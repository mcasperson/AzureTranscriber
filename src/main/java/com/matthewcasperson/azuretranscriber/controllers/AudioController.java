package com.matthewcasperson.azuretranscriber.controllers;

import com.matthewcasperson.azuretranscriber.services.TranscribeService;
import com.matthewcasperson.azuretranscriber.services.SpeechService;
import com.matthewcasperson.azuretranscriber.services.TranslateService;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AudioController {

  @Autowired
  TranscribeService transcribeService;

  @Autowired
  SpeechService speechService;

  @Autowired
  TranslateService translateService;

  @PostMapping("/transcribe")
  @CrossOrigin()
  public String transcribe(
      @RequestParam("file") final MultipartFile file, final String sourceLanguage)
      throws IOException, ExecutionException, InterruptedException {
    return transcribeService.transcribe(file, sourceLanguage);
  }

  @PostMapping("/translate")
  @CrossOrigin()
  public String translate(final String input, final String sourceLanguage,
      final String targetLanguage) throws IOException {
    return translateService.translate(input, sourceLanguage, targetLanguage);
  }

  @PostMapping("/speech")
  @CrossOrigin()
  public byte[] speech(final String input) throws IOException {
    return speechService.translateText(input);
  }
}
