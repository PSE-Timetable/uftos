package de.uftos.services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.uftos.entities.Student;
import de.uftos.entities.Tag;
import de.uftos.repositories.database.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties="spring.main.banner-mode=off")
@Transactional
class StudentServiceTest {

  @Autowired
  private StudentRepository studentRepository;
   @Autowired
  private StudentService studentService;
  private Student max;
  private Student alex;

  @BeforeEach
  void setup() {
    studentRepository.deleteAll();

    Tag stupid = mockTag("stupid");
    Tag intelligent = mockTag("intelligent");
    max = mockStudent("Max", "Musterman", "123", List.of(stupid));
    alex = mockStudent("Alex", "Musterman", "123", List.of(intelligent));

    studentRepository.save(max);
    studentRepository.save(alex);
  }

  @Test
  void testGetStudentByName() {
    Page<Student> resultPage =
        studentService.get(PageRequest.of(0, 5), Optional.of("Max"), Optional.empty(),
            Optional.empty());
    assertEquals(1, resultPage.getTotalElements());
    assertTrue(resultPage.get().findFirst().isPresent());
    assertEquals(max, resultPage.get().findFirst().get());
    System.out.println(max);
  }

  private Student mockStudent(String firstName, String lastName, String id, List<Tag> tags) {
    Student student = mock(Student.class);
    doReturn(firstName).when(student).getFirstName();
    doReturn(lastName).when(student).getLastName();
    doReturn(id).when(student).getId();
    doReturn(tags).when(student).getTags();
    return student;
  }

  private Tag mockTag(String name) {
    Tag tag = mock(Tag.class);
    doReturn(name).when(tag).getName();
    return tag;
  }
}