package de.uftos.services;

import de.uftos.dto.ConstraintInstanceRequestDto;
import de.uftos.dto.ResourceType;
import de.uftos.entities.ConstraintArgument;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintParameter;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the constraintInstance database entity.
 */
@Service
public class ConstraintInstanceService {
  private final ConstraintInstanceRepository repository;
  private final ConstraintSignatureRepository signatureRepository;
  private final GradeRepository gradeRepository;
  private final LessonRepository lessonRepository;
  private final RoomRepository roomRepository;
  private final StudentRepository studentRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final SubjectRepository subjectRepository;
  private final TagRepository tagRepository;
  private final TeacherRepository teacherRepository;
  private final TimeslotRepository timeslotRepository;


  /**
   * Creates a constraintInstance service.
   *
   * @param repository the repository for accessing the constraintInstance entity.
   */
  @Autowired
  public ConstraintInstanceService(ConstraintInstanceRepository repository,
                                   ConstraintSignatureRepository signatureRepository,
                                   GradeRepository gradeRepository,
                                   LessonRepository lessonRepository,
                                   RoomRepository roomRepository,
                                   StudentRepository studentRepository,
                                   StudentGroupRepository studentGroupRepository,
                                   SubjectRepository subjectRepository,
                                   TagRepository tagRepository,
                                   TeacherRepository teacherRepository,
                                   TimeslotRepository timeslotRepository) {
    this.repository = repository;
    this.signatureRepository = signatureRepository;
    this.gradeRepository = gradeRepository;
    this.lessonRepository = lessonRepository;
    this.roomRepository = roomRepository;
    this.studentRepository = studentRepository;
    this.studentGroupRepository = studentGroupRepository;
    this.subjectRepository = subjectRepository;
    this.tagRepository = tagRepository;
    this.teacherRepository = teacherRepository;
    this.timeslotRepository = timeslotRepository;
  }

  /**
   * Creates a new constraint instance.
   *
   * @param signatureId the constraint signature id.
   * @param request     the constraint instance request object.
   * @return the newly created constraint instance.
   */
  public ConstraintInstance create(String signatureId, ConstraintInstanceRequestDto request) {
    ConstraintSignature signature = this.signatureRepository.findById(signatureId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    if (signature.getParameters().size() != request.arguments().size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    for (ConstraintParameter parameter : signature.getParameters()) {
      String value = request.arguments().get(parameter.getParameterName());
      if (value == null || value.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Parameter %s could not be found".formatted(parameter.getParameterName()));
      }

      boolean exists = this.getResourceTypeMapping(value).get(parameter.getParameterType());
      if (!exists) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "%s with id %s could not be found".formatted(parameter.getParameterName(),
                parameter.getId()));
      }
    }

    ConstraintInstance instance = new ConstraintInstance();
    List<ConstraintArgument> arguments = request.arguments().entrySet().stream()
        .map(item -> new ConstraintArgument(item.getKey(), item.getValue())).toList();
    instance.setArguments(arguments);
    instance.setSignature(signature);
    instance.setType(request.type());

    return this.repository.save(instance);
  }

  /**
   * Gets a page of entries of the constraintInstance entity.
   *
   * @param signatureId the ID of the signature for the instances.
   * @param pageable    contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<ConstraintInstance> get(String signatureId, Pageable pageable,
                                      Optional<String> argument) {
    Specification<ConstraintInstance> specification = new SpecificationBuilder<ConstraintInstance>()
        .andJoinEquals(signatureId, "signature", "name")
        .optionalAndJoinLike(argument, "arguments", "value")
        .build();

    return this.repository.findAll(specification, pageable);
  }

  /**
   * Gets a constraintInstance by their ID.
   *
   * @param id the ID of the constraintInstance.
   * @return the constraintInstance with the given ID.
   * @throws ResponseStatusException if the ID doesn't have a corresponding constraintInstance.
   */
  public ConstraintInstance getById(String signatureId, String id) {
    ConstraintSignature signature = this.signatureRepository.findById(signatureId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    return this.repository.findBySignatureAndId(signature, id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Updates a constraintInstance by their ID.
   *
   * @param signatureId the ID of the constraint signature.
   * @param id          the ID of the constraintInstance.
   * @param request     the new constraint instance to which the existing instance should be updated
   */
  public ConstraintInstance update(String signatureId, String id,
                                   ConstraintInstanceRequestDto request) {
    ConstraintInstance instance = this.getById(signatureId, id);

    List<ConstraintArgument> arguments = request.arguments().entrySet().stream()
        .map(item -> new ConstraintArgument(item.getKey(), item.getValue())).toList();

    instance.setArguments(arguments);
    instance.setType(request.type());

    return this.repository.save(instance);
  }

  /**
   * Deletes a constraintInstance by their ID.
   *
   * @param signatureId the ID of the constraint signature.
   * @param id          the ID of the constraintInstance.
   */
  public void delete(String signatureId, String id) {
    ConstraintInstance instance = this.getById(signatureId, id);

    this.repository.delete(instance);
  }


  private Map<ResourceType, Boolean> getResourceTypeMapping(String id) {
    return Map.ofEntries(
        new AbstractMap.SimpleEntry<>(ResourceType.GRADE,
            this.gradeRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.LESSON,
            this.lessonRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.ROOM,
            this.roomRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.STUDENT,
            this.studentRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.STUDENT_GROUP,
            this.studentGroupRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.SUBJECT,
            this.subjectRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.TAG,
            this.tagRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.TEACHER,
            this.teacherRepository.findById(id).isPresent()),
        new AbstractMap.SimpleEntry<>(ResourceType.TIMESLOT,
            this.timeslotRepository.findById(id).isPresent())
    );
  }
}
