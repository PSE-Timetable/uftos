package de.uftos.controller;

import de.uftos.dto.ConstraintInstanceRequestDto;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import de.uftos.services.ConstraintInstanceService;
import de.uftos.services.ConstraintSignatureService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constraints")
public class ConstraintController {
  private final ConstraintSignatureService constraintSignatureService;
  private final ConstraintInstanceService constraintInstanceService;

  @Autowired
  public ConstraintController(ConstraintSignatureService constraintSignatureService,
                              ConstraintInstanceService constraintInstanceService) {
    this.constraintSignatureService = constraintSignatureService;
    this.constraintInstanceService = constraintInstanceService;
  }

  @GetMapping()
  public Page<ConstraintSignature> getConstraintSignatures(Pageable pageable,
                                                           Optional<String> name) {
    return this.constraintSignatureService.get(pageable);
  }

  @GetMapping("/{id}")
  public ConstraintSignature getConstraintSignatures(@PathVariable String id) {
    return this.constraintSignatureService.getById(id);
  }

  @PostMapping("/{signatureId}/instances")
  public ConstraintInstance createConstraintInstance(@PathVariable String signatureId,
                                                     ConstraintInstanceRequestDto request) {
    return this.constraintInstanceService.create(signatureId, request);
  }

  @GetMapping("/{signatureId}/instances")
  public Page<ConstraintInstance> getConstraintInstances(@PathVariable String signatureId,
                                                         Pageable pageable,
                                                         Optional<String> argument) {
    return this.constraintInstanceService.get(signatureId, pageable);
  }

  @GetMapping("/{signatureId}/instances/{id}")
  public ConstraintInstance getConstraintInstanceById(@PathVariable String signatureId,
                                                      @PathVariable String id) {
    return this.constraintInstanceService.getById(signatureId, id);
  }

  @PutMapping("/{signatureId}/instances/{id}")
  public ConstraintInstance updateConstraintInstanceById(@PathVariable String signatureId,
                                                         @PathVariable String id,
                                                         ConstraintInstanceRequestDto request) {
    return this.constraintInstanceService.update(signatureId, id, request);
  }

  @DeleteMapping("/{signatureId}/instances/{id}")
  public void deleteConstraintInstance(@PathVariable String signatureId,
                                       @PathVariable String id
  ) {
    this.constraintInstanceService.delete(signatureId, id);
  }

}
