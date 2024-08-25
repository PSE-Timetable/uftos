package de.uftos.repositories.notifications;

/**
 * The repository for handling notifications.
 */
public interface NotificationRepository {
  /**
   * Notifies the admin using the specified notification type.
   *
   * @param type    How the admin should get notified.
   * @param subject The subject of the notification.
   * @param message The content of the notification.
   */
  void notify(NotificationType type, String subject, String message);
}
