package de.uftos.repositories;

import de.uftos.entities.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface TagRepository extends ListPagingAndSortingRepository<Tag, String>,
    ListCrudRepository<Tag, String> {
}
