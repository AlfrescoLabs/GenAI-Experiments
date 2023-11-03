package org.alfresco.aisummarize.service;

import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GenAiClient {

    @Value("${genai.summary.url}")
    String genaiSummaryUrl;

    @Value("${genai.request.timeout}")
    Integer genaiTimeout;

    OkHttpClient client;

    @PostConstruct
    public void init() {
        client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(genaiTimeout, TimeUnit.SECONDS)
                .build();
    }

    public String getSummary(File pdfFile) throws IOException {

        RequestBody requestBody = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", pdfFile.getName(), RequestBody.create(MediaType.parse("application/pdf"), pdfFile))
                .build();

        Request request = new Request
                .Builder()
                .url(genaiSummaryUrl)
                .post(requestBody)
                .build();

        return client.newCall(request).execute().body().string();

    }

}
