package de.uftos.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.uftos.entities.Teacher;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TeacherServiceTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TeacherService teacherService;

  @Test
  public void getPageWithFilter() {
    Teacher teacher1 = new Teacher("Max", "Mustermann", "MM",
        List.of("1", "2", "3"), List.of("1", "2", "3"));
    Teacher teacher2 = new Teacher("James", "Bond", "JB",
        List.of("1", "2"), List.of("1", "2"));
    entityManager.persist(teacher1);
    entityManager.persist(teacher2);
    entityManager.flush();
    Pageable pageable = PageRequest.of(0, 10);

    Page<Teacher> lastNameResult =
        teacherService.get(pageable, Optional.empty(), Optional.of("ster"),
            Optional.empty(), Optional.empty(), Optional.empty());
    assertEquals(1, lastNameResult.getTotalElements());
    Optional<Teacher> resultOpt =  lastNameResult.stream().findFirst();
    assertTrue(resultOpt.isPresent());
    assertThat(resultOpt.get()).isEqualTo(teacher1);
    fail();
  }


}
