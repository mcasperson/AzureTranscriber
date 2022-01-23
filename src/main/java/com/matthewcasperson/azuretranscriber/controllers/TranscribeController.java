package com.matthewcasperson.azuretranscriber.controllers;

import com.matthewcasperson.azuretranscriber.services.TranscribeService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TranscribeController {

  @Autowired
  TranscribeService transcribeService;

  @PostMapping("/recordings")
  @CrossOrigin()
  public String transcribe(
      @RequestParam("file") final MultipartFile file)
      throws IOException, ExecutionException, InterruptedException {
    return transcribeService.transcribe(file);
  }
}
