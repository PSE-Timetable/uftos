package de.uftos.controller;

import de.uftos.dto.parser.ParsingResponse;
import de.uftos.services.UcdlEditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
   * Creates an editor service.
   *
   * @param editorService the service for the UCDL editor.
   */
  @Autowired
  public UcdlEditorController(UcdlEditorService editorService) {
    this.editorService = editorService;
  }

  /**
   * Maps the HTTP POST request, to set the UCDL code, to the
   * {@link UcdlEditorService#setUcdl(MultipartFile)} function of the editor service.
   *
   * @param file the file which contains the new UCDL code.
   * @return a response whether the file could be parsed successfully or not.
   */
  @PostMapping()
  public ParsingResponse set(@RequestBody MultipartFile file) {
    return this.editorService.setUcdl(file);
  }

  /**
   * Maps the HTTP GET request for the current UCDL code, to the
   * {@link UcdlEditorService#getUcdl()} function of the editor service.
   *
   * @return a file containing the current UCDL code.
   */
  @GetMapping()
  public MultipartFile getTimetables() {
    return this.editorService.getUcdl();
  }

}
