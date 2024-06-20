package de.uftos.controller;


import de.uftos.dto.CurriculumRequestDto;
import de.uftos.dto.CurriculumResponseDto;
import de.uftos.services.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController {
  private final CurriculumService curriculumService;

  @Autowired
  public CurriculumController(CurriculumService curriculumService) {
    this.curriculumService = curriculumService;
  }

  @PostMapping()
  public CurriculumResponseDto createCurriculum(@RequestBody CurriculumRequestDto curriculum) {
    return this.curriculumService.create(curriculum);
  }

  @GetMapping()
  public Page<CurriculumResponseDto> getCurriculums() {
    return this.curriculumService.get();
  }

  @GetMapping("/{id}")
  public CurriculumResponseDto getCurriculum(@PathVariable String id) {
    return this.curriculumService.getById(id);
  }

  @PutMapping("/{id}")
  public CurriculumResponseDto updateCurriculum(@PathVariable String id,
                                                @RequestBody CurriculumRequestDto curriculum) {
    return this.curriculumService.update(id, curriculum);
  }

  @DeleteMapping("/{id}")
  public void deleteCurriculum(@PathVariable String id) {
    this.curriculumService.delete(id);
  }
}
