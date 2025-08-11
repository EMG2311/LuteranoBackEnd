package com.grup14.luterano.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final ObjectMapper mapper = new ObjectMapper();
    @Before("execution(* com.grup14.luterano.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : "desconocido";
        MDC.put("usuario", email);
        for (Object arg : joinPoint.getArgs()) {
            try {
                String json = mapper.writeValueAsString(arg);
                json = json.replaceAll("(?i)(\"password\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
                logger.info("[{}] invocó {} con argumento JSON: {}", email, joinPoint.getSignature().toShortString(), json);
            } catch (JsonProcessingException e) {
                logger.warn("No se pudo serializar argumento para loguear", e);
            }
        }
    }

    @After("execution(* com.grup14.luterano.service..*(..))")
    public void clearMDC(JoinPoint joinPoint) {
        MDC.clear();
    }

    @AfterThrowing(pointcut = "execution(* com.grup14.luterano.service..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : "desconocido";

        logger.error(" [{}] El método {} lanzó la excepción: {}",
                email,
                joinPoint.getSignature().toShortString(),
                ex.getMessage());
    }
}