package com.schacherbauer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = "spring.liquibase.change-log=combined-changelog.yml")
public class ChangeNlsSemanticsIntegrationTest {

  private static final DockerImageName imageName =
      DockerImageName.parse("gvenzl/oracle-xe").withTag("21-slim-faststart");

  @ServiceConnection @Container
  private static final OracleContainer c =
      new OracleContainer(imageName).withUsername("testuser").withPassword("testpassword");

  @Autowired PersonRepository personRepository;

  @Test
  public void testInsert_bytesOfStringExceedNumberOfCharacters() {
    var person = new Person();
    var testString = "Ö".repeat(50);
    person.setLastname(testString);

    assertThat(testString).hasSize(50);
    assertThat(testString.getBytes()).hasSize(100);
    assertThatNoException().isThrownBy(() -> personRepository.save(person));
  }
}
