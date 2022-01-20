package com.matthewcasperson.azuretranscriber.services;

import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TranscribeService {
  @Value("speechSubscriptionKey")
  private String speechSubscriptionKey;

  @Value("speechServiceRegion")
  private String speechServiceRegion;

  public String transcribe(final MultipartFile file, final String language)
      throws IOException, ExecutionException, InterruptedException {
    Path audioFile = null;
    try {
      audioFile = saveFileToDisk(file);

      try (SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(
          speechSubscriptionKey, speechServiceRegion)) {
        config.setSpeechRecognitionLanguage(language);
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFile.toString());
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioConfig);

        Future<SpeechRecognitionResult> task = recognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();
        return result.getText();
      }
    } finally {
      if (audioFile != null) {
        FileUtils.deleteQuietly(audioFile.toFile());
      }
    }
  }

  Path saveFileToDisk(final MultipartFile file) throws IOException {
    final Path tempFile = Files.createTempFile("", ".wav");
    file.transferTo(tempFile);
    return tempFile;
  }
}
