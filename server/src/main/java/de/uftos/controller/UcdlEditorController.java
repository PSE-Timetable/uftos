package de.uftos.controller;

import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.services.UcdlEditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The REST controller for the UCDL editor.
 * This controller handles /editor HTTP requests.
 */
@RestController
@RequestMapping("/editor")
public class UcdlEditorController {
  private final UcdlEditorService editorService;

  /**
   * Creates an editor controller.
   *
   * @param editorService the service for the UCDL editor.
   */
  @Autowired
  public UcdlEditorController(UcdlEditorService editorService) {
    this.editorService = editorService;
  }

  /**
   * Maps the HTTP POST request, to validate the UCDL code, to the
   * {@link UcdlEditorService#validate(MultipartFile)} function of the editor service.
   *
   * @param file the file which contains the new UCDL code.
   * @return a response whether the file could be parsed successfully or not.
   */
  @PutMapping("/validate")
  public ParsingResponse validate(@RequestBody MultipartFile file) {
    return this.editorService.validate(file);
  }

  /**
   * Maps the HTTP PUT request, to set the UCDL code, to the
   * {@link UcdlEditorService#setUcdl(MultipartFile)} function of the editor service.
   *
   * @param file the file which contains the new UCDL code.
   * @return a response whether the file could be parsed and saved successfully or not.
   */
  @PutMapping()
  public ParsingResponse setUcdlFile(@RequestBody MultipartFile file) {
    return this.editorService.setUcdl(file);
  }

  /**
   * Maps the HTTP GET request for the current UCDL code, to the
   * {@link UcdlEditorService#getUcdl()} function of the editor service.
   *
   * @return a file containing the current UCDL code.
   */
  @GetMapping(produces = "text/yaml")
  public Resource getUcdlFile() {
    return this.editorService.getUcdl();
  }

}
