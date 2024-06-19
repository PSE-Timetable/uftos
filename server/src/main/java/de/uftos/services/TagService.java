package de.uftos.services;

import de.uftos.dto.TagRequestDto;
import de.uftos.entities.Tag;
import de.uftos.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the tag database entity.
 */
@Service
public class TagService {
  private final TagRepository repository;

  /**
   * Creates a tag service.
   *
   * @param repository the repository for accessing the tag entity.
   */
  @Autowired
  public TagService(TagRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the tag entity.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of entries fitting the parameters.
   */
  public Page<Tag> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a tag from their ID.
   *
   * @param id the ID of the tag.
   * @return the tag with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding tag.
   */
  public Tag getById(String id) {
    var tag = this.repository.findById(id);

    return tag.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new tag in the database.
   *
   * @param tag the information about the tag which is to be created.
   * @return the created tag which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the tag parameter is
   *                                 already present in the database.
   */
  public Tag create(TagRequestDto tag) {
    return this.repository.save(tag.map());
  }

  /**
   * Updates the tag with the given ID.
   *
   * @param id         the ID of the tag which is to be updated.
   * @param tagRequest the updated tag information.
   * @return the updated tag.
   */
  public Tag update(String id, TagRequestDto tagRequest) {
    Tag tag = tagRequest.map();
    tag.setId(id);

    return this.repository.save(tag);
  }

  /**
   * Deletes the tag with the given ID.
   *
   * @param id the ID of the tag which is to be deleted.
   * @throws ResponseStatusException is thrown if no tag exists with the given ID.
   */
  public void delete(String id) {
    var tag = this.repository.findById(id);
    if (tag.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(tag.get());
  }
}
