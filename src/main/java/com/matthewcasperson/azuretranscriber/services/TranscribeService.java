package com.matthewcasperson.azuretranscriber.services;

import com.matthewcasperson.azuretranscriber.readers.BinaryAudioStreamReader;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamContainerFormat;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;
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

  @Value("${speechSubscriptionKey}")
  private String speechSubscriptionKey;

  @Value("${speechServiceRegion}")
  private String speechServiceRegion;

  public String transcribe(final MultipartFile file, final String language)
      throws IOException, ExecutionException, InterruptedException {
    Path audioFile = null;
    try {
      audioFile = saveFileToDisk(file);

      try (SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(
          speechSubscriptionKey, speechServiceRegion)) {
        config.setSpeechRecognitionLanguage(language);
        final PullAudioInputStream pullAudio = PullAudioInputStream.create(
            new BinaryAudioStreamReader(audioFile.toString()),
            AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.ANY));
        final AudioConfig audioConfig = AudioConfig.fromStreamInput(pullAudio);
        final SpeechRecognizer reco = new SpeechRecognizer(config, audioConfig);
        final Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
        final SpeechRecognitionResult result = task.get();
        return result.getText();
      }
    } catch (final Exception ex) {
      System.out.println(ex);
      throw ex;
    } finally {
      if (audioFile != null) {
        FileUtils.deleteQuietly(audioFile.toFile());
      }
    }
  }

  Path saveFileToDisk(final MultipartFile file) throws IOException {
    final Path tempFile = Files.createTempFile("", ".webm");
    file.transferTo(tempFile);
    return tempFile;
  }
}
