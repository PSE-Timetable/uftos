package de.uftos.repositories.database;

import de.uftos.entities.Tag;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

/**
 * The repository for accessing the tag database table.
 */
public interface TagRepository
    extends ListCrudRepository<Tag, String>, JpaSpecificationExecutor<Tag> {
}
