package de.uftos.repositories.database;

import de.uftos.entities.Server;
import org.springframework.data.repository.ListCrudRepository;

/**
 * The repository for accessing the server database entity.
 */
public interface ServerRepository extends ListCrudRepository<Server, String> {
}
