package com.grup14.luterano.event;

import com.grup14.luterano.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Getter
public class UserEvent extends ApplicationEvent {

    public enum Tipo {
        CREAR, ACTUALIZAR
    }

    private final Tipo tipo;
    private final User user;
    private final String password;

    public UserEvent(Object source, Tipo tipo, User usuario) {
        super(source);
        this.tipo = tipo;
        this.user=usuario;
        password=null;
    }

    public UserEvent(Object source, Tipo tipo, User usuario,String password) {
        super(source);
        this.tipo = tipo;
        this.user = usuario;
        this.password = password;
    }
    }


