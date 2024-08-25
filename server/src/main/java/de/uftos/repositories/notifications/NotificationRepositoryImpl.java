package de.uftos.repositories.notifications;

import de.uftos.repositories.database.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;

/**
 * Provides an implementation for notifications in uftos.
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
  private final JavaMailSender emailSender;
  private final ServerRepository serverRepository;

  @Autowired
  public NotificationRepositoryImpl(JavaMailSender emailSender, ServerRepository serverRepository) {
    this.emailSender = emailSender;
    this.serverRepository = serverRepository;
  }

  @Override
  public void notify(NotificationType type, String subject, String message) {
    switch (type) {
      case EMAIL -> sendEmail(subject, message);
      default -> throw new IllegalStateException();
    }
  }

  private void sendEmail(String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(System.getenv("MAIL_FROM"));
    message.setTo(serverRepository.findAll().getFirst().getEmail());
    message.setSubject(subject);
    message.setText(text);

    emailSender.send(message);
  }
}
