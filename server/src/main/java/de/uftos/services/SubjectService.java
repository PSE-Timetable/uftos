package de.uftos.services;

import de.uftos.entities.Subject;
import de.uftos.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SubjectService {
    private final SubjectRepository repository;

    @Autowired
    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    public Page<Subject> get(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    public Subject getById(String id) {
        var subject = this.repository.findById(id);

        return subject.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    public Subject create(Subject subject) {
        if (subject.getId() != null && this.repository.findById(subject.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }

        return this.repository.save(subject);
    }

    public Subject update(String id, Subject subject) {
        subject.setId(id);

        return this.repository.save(subject);
    }

    public void delete(String id) {
        var subject = this.repository.findById(id);
        if (subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        this.repository.delete(subject.get());
    }
}
