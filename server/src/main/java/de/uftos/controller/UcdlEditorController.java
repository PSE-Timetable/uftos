package de.uftos.controller;

import de.uftos.dto.SuccessResponse;
import de.uftos.services.UcdlEditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  @PutMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public SuccessResponse validateUcdlFile(@RequestParam(value = "file") MultipartFile file) {
    return this.editorService.validate(file);
  }

  /**
   * Maps the HTTP PUT request, to set the UCDL code, to the
   * {@link UcdlEditorService#setUcdl(MultipartFile, boolean)} function of the editor service.
   *
   * @param file  the file which contains the new UCDL code.
   * @param force if the ucdl file should be forcefully updated, ignoring inconsistencies. Defaults to false.
   * @return a response whether the file could be parsed and saved successfully or not.
   */
  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public SuccessResponse setUcdlFile(@RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "force", required = false, defaultValue = "false")
                                     boolean force) {
    return this.editorService.setUcdl(file, force);
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
