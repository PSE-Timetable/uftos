package de.uftos.controller;


import de.uftos.dto.CurriculumRequestDto;
import de.uftos.dto.CurriculumResponseDto;
import de.uftos.services.CurriculumService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the curriculum entity.
 * This controller handles /curriculum HTTP requests.
 */
@RestController
@RequestMapping("/curriculum")
public class CurriculumController {
  private final CurriculumService curriculumService;

  /**
   * Creates the curriculum controller.
   *
   * @param curriculumService the service for the curriculum entity.
   */
  @Autowired
  public CurriculumController(CurriculumService curriculumService) {
    this.curriculumService = curriculumService;
  }

  /**
   * Maps the HTTP POST request, to create a new curriculum in the database, to the
   * {@link CurriculumService#create(CurriculumRequestDto) create} function of the
   * curriculum service.
   *
   * @param curriculum the curriculum which is to be created.
   * @return the created curriculum with the assigned ID.
   */
  @PostMapping()
  public CurriculumResponseDto createCurriculum(@RequestBody CurriculumRequestDto curriculum) {
    return this.curriculumService.create(curriculum);
  }

  /**
   * Maps the HTTP GET request for a set of curriculums from the database, to the
   * {@link CurriculumService#get(Pageable, Optional) get} function of the curriculum service.
   *
   * @param name the name filter
   * @param pageable contains the parameters for the page.
   * @return the page of curriculums fitting the parameters.
   */
  @GetMapping()
  public Page<CurriculumResponseDto> getCurriculums(Pageable pageable, Optional<String> name) {
    return this.curriculumService.get(pageable, name);
  }

  /**
   * Maps the HTTP GET request for a curriculum with the given ID to the
   * {@link CurriculumService#getById(String) getById} function of the curriculum service.
   *
   * @param id the ID of the curriculum.
   * @return the curriculum with the given ID.
   */
  @GetMapping("/{id}")
  public CurriculumResponseDto getCurriculum(@PathVariable String id) {
    return this.curriculumService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a curriculum to the
   * {@link CurriculumService#update(String, CurriculumRequestDto) update} function of
   * the curriculum service.
   *
   * @param id         the ID of the curriculum which is to be updated.
   * @param curriculum the updated information of the curriculum.
   * @return the updated curriculum.
   */
  @PutMapping("/{id}")
  public CurriculumResponseDto updateCurriculum(@PathVariable String id,
                                                @RequestBody CurriculumRequestDto curriculum) {
    return this.curriculumService.update(id, curriculum);
  }

  /**
   * Maps the HTTP DELETE request to the {@link CurriculumService#delete(String) delete} function of
   * the curriculum service.
   *
   * @param id the ID of the curriculum which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteCurriculum(@PathVariable String id) {
    this.curriculumService.delete(id);
  }
}
