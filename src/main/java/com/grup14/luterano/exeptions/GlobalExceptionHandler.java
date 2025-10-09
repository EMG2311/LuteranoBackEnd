package com.grup14.luterano.exeptions;

import com.grup14.luterano.service.implementation.DocenteServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.TypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));

        logger.warn("Error de validación en {} - Campos: {}", request.getRequestURI(), errores);

        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("status", 400);
        body.put("message", "Error de validación en endpoint " + request.getRequestURI());
        body.put("errors", errores);
        return ResponseEntity.badRequest().body(body);
    }

    // >>> AQUÍ capturás el caso del WARN que mostrás <<<
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String param = ex.getName(); // ej: "alumnoId"
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        Object value = ex.getValue(); // ej: "{2"

        logger.warn("Type mismatch en {} - param='{}', requerido='{}', valor='{}'",
                request.getRequestURI(), param, requiredType, value);

        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("status", 400);
        body.put("message", "Parámetro inválido");
        Map<String, Object> detail = new HashMap<>();
        detail.put("param", param);
        detail.put("requiredType", requiredType);
        detail.put("value", value);
        detail.put("reason", "No se pudo convertir el valor al tipo requerido");
        body.put("error", detail);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({TypeMismatchException.class, ConversionFailedException.class})
    public ResponseEntity<Map<String, Object>> handleGenericMismatch(
            Exception ex, HttpServletRequest request) {

        logger.warn("Type/Conversion mismatch en {} - {}", request.getRequestURI(), ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("status", 400);
        body.put("message", "Dato inválido o con formato incorrecto");
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        logger.warn("JSON inválido en {} - {}", request.getRequestURI(),
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("status", 400);
        body.put("message", "JSON inválido o campo con formato incorrecto");
        body.put("detalle", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
