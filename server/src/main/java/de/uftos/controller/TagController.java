package de.uftos.controller;

import de.uftos.entities.Tag;
import de.uftos.services.TagService;
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

@RestController
@RequestMapping("/tags")
public class TagController {
  private final TagService tagsService;

  @Autowired
  public TagController(TagService tagsService) {
    this.tagsService = tagsService;
  }

  @PostMapping()
  public Tag createTag(@RequestBody Tag tags) {
    return this.tagsService.create(tags);
  }

  @GetMapping()
  public Page<Tag> getTags(Pageable pageable) {
    return this.tagsService.get(pageable);
  }

  @GetMapping("/{id}")
  public Tag getTag(@PathVariable long id) {
    return this.tagsService.getById(id);
  }

  @PutMapping("/{id}")
  public Tag updateTag(@PathVariable long id, @RequestBody Tag tags) {
    return this.tagsService.update(id, tags);
  }

  @DeleteMapping("/{id}")
  public void deleteTag(@PathVariable long id) {
    this.tagsService.delete(id);
  }
}
