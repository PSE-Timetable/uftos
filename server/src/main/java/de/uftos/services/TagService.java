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

@Service
public class TagService {
  private final TagRepository repository;

  @Autowired
  public TagService(TagRepository repository) {
    this.repository = repository;
  }

  public Page<Tag> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Tag getById(String id) {
    var tag = this.repository.findById(id);

    return tag.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Tag create(TagRequestDto tag) {
    return this.repository.save(tag.map());
  }

  public Tag update(String id, TagRequestDto tagRequest) {
    Tag tag = tagRequest.map();
    tag.setId(id);

    return this.repository.save(tag);
  }

  public void delete(String id) {
    var tag = this.repository.findById(id);
    if (tag.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(tag.get());
  }
}
