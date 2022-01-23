package com.matthewcasperson.azuretranscriber.services;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpeechService {

  @Value("${speechSubscriptionKey}")
  private String speechSubscriptionKey;

  @Value("${speechServiceRegion}")
  private String speechServiceRegion;

  public byte[] translateText(final String input, final String targetLanguage) throws IOException {
    final Path tempFile = Files.createTempFile("", ".wav");

    try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey,
        speechServiceRegion)) {
      speechConfig.setSpeechSynthesisLanguage(targetLanguage);
      speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);

      SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, null);
      SpeechSynthesisResult result = synthesizer.SpeakText(input);
      AudioDataStream stream = AudioDataStream.fromResult(result);
      stream.saveToWavFile(tempFile.toString());
      return Files.readAllBytes(tempFile);
    } finally {
      FileUtils.deleteQuietly(tempFile.toFile());
    }

  }

}
