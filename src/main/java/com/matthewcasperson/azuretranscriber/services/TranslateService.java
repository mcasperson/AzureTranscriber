package com.matthewcasperson.azuretranscriber.services;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TranslateService {

  @Value("${speechSubscriptionKey}")
  private String speechSubscriptionKey;

  @Value("${speechServiceRegion}")
  private String speechServiceRegion;

  public String translate(final String input, final String sourceLanguage,
      final String targetLanguage) throws IOException {

    final HttpUrl url = new HttpUrl.Builder()
        .scheme("https")
        .host("api.cognitive.microsofttranslator.com")
        .addPathSegment("/translate")
        .addQueryParameter("api-version", "3.0")
        .addQueryParameter("from", sourceLanguage)
        .addQueryParameter("to", targetLanguage)
        .build();

    final OkHttpClient client = new OkHttpClient();

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create("[{\"Text\": \""
        + StringEscapeUtils.escapeJson(input) + "\"}]", mediaType);
    Request request = new Request.Builder().url(url).post(body)
        .addHeader("Ocp-Apim-Subscription-Key", speechSubscriptionKey)
        .addHeader("Ocp-Apim-Subscription-Region", speechServiceRegion)
        .addHeader("Content-type", "application/json")
        .build();
    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}
