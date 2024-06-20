package de.uftos.repositories.database;

import de.uftos.entities.Server;
import org.springframework.data.repository.ListCrudRepository;

public interface ServerRepository extends ListCrudRepository<Server, String> {
}
