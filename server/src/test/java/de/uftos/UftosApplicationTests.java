package de.uftos;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Suite
@SelectPackages({ "de.uftos.e2e" })
class UftosApplicationTests {

  static Network network = Network.newNetwork();

  static final GenericContainer<?> postgres = new GenericContainer<>(
      DockerImageName.parse("postgres:16.3-alpine3.18"))
      .withNetwork(network)
      .withNetworkAliases("db")
      .withExposedPorts(5432)
      .withEnv("POSTGRES_USER", "uftos")
      .withEnv("POSTGRES_PASSWORD", "superSecurePassword")
      .withEnv("POSTGRES_DB", "uftos");

  static final GenericContainer<?> server = new GenericContainer<>("uftos-api-dev")
      .dependsOn(postgres)
      .withNetwork(network)
      .withAccessToHost(true)
      .withEnv("DB_HOST", "db")
      .withEnv("DB_PORT", "5432")
      .withEnv("DB_NAME", "uftos")
      .withEnv("DB_USER", "uftos")
      .withEnv("DB_PASSWORD", "superSecurePassword")
      .withEnv("MAIL_HOST", "mail.example.org")
      .withEnv("MAIL_USER", "myUser")
      .withEnv("MAIL_PASSWORD", "highlySecurePassword")
      .withEnv("MAIL_FROM", "uftos@example.com")
      .withExposedPorts(8080);

  @BeforeAll
  static void setup() {
    postgres.start();
    server.start();
    RestAssured.port = server.getMappedPort(8080);
    RestAssured.baseURI = "http://%s".formatted(server.getHost());
  }

  // if this doesn't exist surefire will ignore the file
  // don't ask me....
  @Test
  void foo() {
  }
}
