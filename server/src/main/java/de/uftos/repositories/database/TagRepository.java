package de.uftos.repositories.database;

import de.uftos.entities.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the tag database table.
 */
public interface TagRepository extends ListPagingAndSortingRepository<Tag, String>,
    ListCrudRepository<Tag, String> {
}
