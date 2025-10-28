package com.grup14.luterano.utils;

import com.grup14.luterano.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private static final String USUARIO_NO_AUTENTICADO = "El usuario no está autenticado o la información  es incorrecta.";

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException(USUARIO_NO_AUTENTICADO);
        }

        try {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return ((User) principal).getId();
            } else if (principal instanceof Long) { // Si el ID se guarda directamente
                return (Long) principal;
            } else {
                throw new SecurityException("Formato de principal desconocido.");
            }
        } catch (Exception e) {
            throw new SecurityException(USUARIO_NO_AUTENTICADO + " Error: " + e.getMessage());
        }
    }

}
