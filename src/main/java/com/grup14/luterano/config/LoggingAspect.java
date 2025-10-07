package com.grup14.luterano.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private final ObjectMapper mapper;

    public LoggingAspect(ObjectMapper baseMapper) {
        this.mapper = baseMapper.copy()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Before("execution(* com.grup14.luterano.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : "desconocido";
        MDC.put("usuario", email);

        Object[] safeArgs = Arrays.stream(joinPoint.getArgs())
                .map(this::sanitizeArg)
                .toArray();

        try {
            String json = mapper.writeValueAsString(safeArgs);
            json = maskSensitiveStrings(json);
            logger.info("[{}] invocó {} con args: {}", email, joinPoint.getSignature().toShortString(), json);
        } catch (JsonProcessingException e) {
            logger.info("[{}] invocó {} con args: {}", email, joinPoint.getSignature().toShortString(),
                    Arrays.toString(safeArgs));
        }
    }

    @After("execution(* com.grup14.luterano.service..*(..))")
    public void clearMDC(JoinPoint joinPoint) {
        MDC.clear();
    }

    @AfterThrowing(pointcut = "execution(* com.grup14.luterano.service..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint jp, Throwable ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : "desconocido";
        String msg = (ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName());

        logger.error("[{}] El método {} lanzó la excepción: {}",
                email, jp.getSignature().toShortString(), msg);
    }

    private Object sanitizeArg(Object o) {
        if (o == null) return null;

        if (o instanceof MultipartFile mf) {
            return Map.of(
                    "type", "MultipartFile",
                    "name", mf.getName(),
                    "originalFilename", mf.getOriginalFilename(),
                    "size", mf.getSize(),
                    "contentType", mf.getContentType()
            );
        }
        if (o instanceof InputStream) {
            return Map.of("type", "InputStream");
        }
        if (o instanceof byte[] bytes) {
            return Map.of("type", "byte[]", "length", bytes.length);
        }
        if (o instanceof char[] chars) {
            return Map.of("type", "char[]", "length", chars.length);
        }
        if (o instanceof File f) {
            return Map.of("type", "File", "path", f.getAbsolutePath(), "length", f.length());
        }
        if (o instanceof Path p) {
            return Map.of("type", "Path", "path", p.toString());
        }
        if (o instanceof Resource r) {
            return Map.of("type", "Resource", "desc", r.description());
        }
        if (o instanceof HttpServletRequest req) {
            return Map.of("type", "HttpServletRequest", "method", req.getMethod(), "uri", req.getRequestURI());
        }
        if (o instanceof HttpServletResponse resp) {
            return Map.of("type", "HttpServletResponse", "status", resp.getStatus());
        }
        if (o instanceof BindingResult br) {
            return Map.of("type", "BindingResult", "errors", br.getErrorCount());
        }
        if (o instanceof Throwable t) {
            return Map.of("type", "Throwable", "class", t.getClass().getName(), "message", t.getMessage());
        }

        if (o instanceof Map<?, ?> map) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                String key = String.valueOf(e.getKey());
                out.put(key, isSensitiveKey(key) ? "***" : sanitizeArg(e.getValue()));
            }
            return out;
        }

        if (o instanceof Collection<?> col) {
            return col.stream().limit(100).map(this::sanitizeArg).collect(Collectors.toList());
        }

        if (o.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(o);
            int n = Math.min(len, 100);
            List<Object> sample = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                sample.add(sanitizeArg(java.lang.reflect.Array.get(o, i)));
            }
            return Map.of("type", "array", "length", len, "sample", sample);
        }

        return o; // deja que Jackson lo serialice
    }

    private boolean isSensitiveKey(String key) {
        String k = key == null ? "" : key.toLowerCase(Locale.ROOT);
        return k.contains("password") || k.contains("pass") ||
                k.contains("secret") || k.contains("token") ||
                k.contains("authorization") || k.contains("auth");
    }

    private String maskSensitiveStrings(String json) {
        if (json == null) return null;
        json = json.replaceAll("(?i)(\"password\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
        json = json.replaceAll("(?i)(\"pass\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
        json = json.replaceAll("(?i)(\"token\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
        json = json.replaceAll("(?i)(\"authorization\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
        json = json.replaceAll("(?i)(\"secret\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
        return json;
    }

}