package de.uftos.controller;

import de.uftos.dto.ConstraintInstanceRequestDto;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import de.uftos.services.ConstraintInstanceService;
import de.uftos.services.ConstraintSignatureService;
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
  public Page<ConstraintSignature> getConstraintSignatures(Pageable pageable) {
    return this.constraintSignatureService.get(pageable);
  }

  @GetMapping("/{id}")
  public ConstraintSignature getConstraintSignatures(@PathVariable String id) {
    return this.constraintSignatureService.getById(id);
  }

  @PostMapping("/{id}/instances")
  public ConstraintInstance createConstraintInstance(@PathVariable String id,
                                                     ConstraintInstanceRequestDto request) {
    return this.constraintInstanceService.create(id, request);
  }

  @GetMapping("/{id}/instances")
  public Page<ConstraintInstance> getConstraintInstances(@PathVariable String id,
                                                         Pageable pageable) {
    return this.constraintInstanceService.get(id, pageable);
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
