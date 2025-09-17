package com.grup14.luterano.service.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender){
        this.mailSender=javaMailSender;
    }

    @Value("${email.enabled:true}")
    private boolean emailEnabled;

    @Async("emailExecutor")
    public void sendSimpleEmail(String to, String subject, String text) {
        if(!emailEnabled) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Async("emailExecutor")
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        if(!emailEnabled) return;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    @Async("emailExecutor")
    public void sendWelcomeEmail(String to, String password) throws MessagingException {
        String html = String.format("""
        <html><body>
          <h2>Hola, %s!</h2>
          <p>Bienvenido a <strong>Luterano Concordia</strong>.</p>
          <p>Tu usuario se dio de alta correctamente.</p>
          <p><strong>Usuario:</strong> %s</p>
          <p><strong>Contraseña:</strong> %s</p>
        </body></html>
        """, to, to, password);
        sendHtmlEmail(to, "Bienvenido a Luterano Concordia", html);
    }

    @Async("emailExecutor")
    public void sendUserModifiedEmail(String to) throws MessagingException {
        String html = String.format("""
        <html><body>
          <h2>Hola, %s!</h2>
          <p>Tu usuario ha sido modificado con éxito.</p>
          <p>Si no fuiste vos, por favor contacta con soporte.</p>
          <br/>
          <p>Saludos,<br/>Equipo Luterano</p>
        </body></html>
        """, to);
        sendHtmlEmail(to, "Usuario modificado correctamente", html);
    }

    @Async("emailExecutor")
    public void sendUserDeletedEmail(String to, String userName) throws MessagingException {
        String html = String.format("""
        <html><body>
          <h2>Hola, %s!</h2>
          <p>Tu usuario ha sido eliminado.</p>
          <p>Si tienes preguntas o crees que esto es un error, contacta con soporte.</p>
          <br/>
          <p>Saludos,<br/>Equipo Luterano</p>
        </body></html>
        """, userName);
        sendHtmlEmail(to, "Usuario eliminado", html);
    }
}