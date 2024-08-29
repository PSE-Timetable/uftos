package de.uftos.controller;

import de.uftos.dto.requestdtos.TagRequestDto;
import de.uftos.entities.Tag;
import de.uftos.services.TagService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the tag entity.
 * This controller handles /tags HTTP requests.
 */
@RestController
@RequestMapping("/tags")
public class TagController {
  private final TagService tagsService;

  /**
   * Creates a tag controller.
   *
   * @param tagsService the service for the teacher entity.
   */
  @Autowired
  public TagController(TagService tagsService) {
    this.tagsService = tagsService;
  }

  /**
   * Maps the HTTP POST request, to create a new tag in the database, to the
   * {@link TagService#create(TagRequestDto) create} function of the tag service.
   *
   * @param tag the tag which is to be created.
   * @return the created tag with the assigned ID.
   */
  @PostMapping()
  public Tag createTag(@RequestBody TagRequestDto tag) {
    return this.tagsService.create(tag);
  }

  /**
   * Maps the HTTP GET request for a set of tags from the database to the
   * {@link TagService#get(Sort, Optional) get} function of the tag service.
   *
   * @param sort   contains the sort parameters.
   * @param search the search filter.
   * @return the page of tags fitting the parameters.
   */
  @GetMapping()
  public List<Tag> getTags(Sort sort, Optional<String> search) {
    return this.tagsService.get(sort, search);
  }

  /**
   * Maps the HTTP GET request for a tag with the given ID to the
   * {@link TagService#getById(String) getById} function of the tag service.
   *
   * @param id the ID of the tag.
   * @return the tag with the given ID.
   */
  @GetMapping("/{id}")
  public Tag getTag(@PathVariable String id) {
    return this.tagsService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a tag to the
   * {@link TagService#update(String, TagRequestDto) update} function of the tag service.
   *
   * @param id  the ID of the tag which is to be updated.
   * @param tag the updated information of the tag.
   * @return the updated tag.
   */
  @PutMapping("/{id}")
  public Tag updateTag(@PathVariable String id, @RequestBody TagRequestDto tag) {
    return this.tagsService.update(id, tag);
  }

  /**
   * Maps the HTTP DELETE request to the {@link TagService#delete(String) delete} function of the
   * tag service.
   *
   * @param id the ID of the tag which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteTag(@PathVariable String id) {
    this.tagsService.delete(id);
  }

  /**
   * Maps the HTTP DELETE request to the {@link TagService#deleteTags(String[]) delete} function of the
   * tag service.
   *
   * @param ids the IDs of the tag which is to be deleted.
   */
  @DeleteMapping()
  public void deleteTags(@RequestBody String[] ids) {
    this.tagsService.deleteTags(ids);
  }
}
