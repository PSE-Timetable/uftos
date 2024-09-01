package de.uftos.repositories.notifications;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import de.uftos.entities.Server;
import de.uftos.repositories.database.ServerRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NotificationRepositoryImplTest {

  @Mock
  private JavaMailSender sender;

  @Mock
  private ServerRepository serverRepository;

  @BeforeEach
  void setup() {
    Server server = new Server();
    server.setEmail("test@example.org");
    when(serverRepository.findAll()).thenReturn(List.of(server));
  }

  @Test
  void notifyTest() {
    NotificationRepository repo = new NotificationRepositoryImpl(sender, serverRepository);
    assertDoesNotThrow(() -> repo.notify(NotificationType.EMAIL, "test", "test"));
  }
}
