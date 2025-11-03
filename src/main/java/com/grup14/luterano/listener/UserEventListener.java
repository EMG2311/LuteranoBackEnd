package com.grup14.luterano.listener;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.event.UserEvent;
import com.grup14.luterano.service.implementation.EmailServiceImpl;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {
    @Autowired
    private EmailServiceImpl emailService;

    @EventListener
    public void handleUserEvent(UserEvent event) throws MessagingException {
        User user = event.getUser();
        switch (event.getTipo()) {
            case CREAR:
                emailService.sendWelcomeEmail(user.getEmail(), event.getPassword());
                break;
            case ACTUALIZAR:
                emailService.sendUserModifiedEmail(user.getEmail());
                break;
        }
        System.out.println("Mail enviado a " + user.getEmail() + " para acción " + event.getTipo());
    }
}
