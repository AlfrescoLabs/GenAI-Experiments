package org.alfresco.aisummarize;

import okhttp3.*;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.RenditionsApi;
import org.alfresco.core.handler.TagsApi;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.core.model.Rendition;
import org.alfresco.core.model.RenditionBodyCreate;
import org.alfresco.core.model.TagBody;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.ResultSetPaging;
import org.alfresco.search.model.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class AiSummarizeApplication implements CommandLineRunner {

    static final Logger LOG = LoggerFactory.getLogger(AiSummarizeApplication.class);

    @Value("${folder}")
    String folder;

    @Value("${genai.summary.url}")
    String genaiSummaryUrl;

    @Value("${genai.request.timeout}")
    Integer genaiTimeout;

    @Autowired
    RenditionsApi renditionsApi;

    @Autowired
    NodesApi nodesApi;

    @Autowired
    TagsApi tagsApi;

    @Autowired
    SearchApi searchApi;

    @Override
    public void run(String... args) throws Exception {

        ResponseEntity<ResultSetPaging> results = searchApi.search(
                new SearchRequest()
                        .query(new RequestQuery()
                                .language(RequestQuery.LanguageEnum.AFTS)
                                .query("PATH:\"" + folder + "//*\"")));

        results.getBody().getList().getEntries().forEach((entry) -> {

            String uuid = entry.getEntry().getId();

            LOG.info("Summarizing document {} ({})", entry.getEntry().getName(), uuid);

            if (renditionsApi.getRendition(uuid, "pdf").getBody().getEntry().getStatus() == Rendition.StatusEnum.CREATED) {

                try {

                    byte[] pdfFileContent =
                            renditionsApi.getRenditionContent(uuid, "pdf",
                                    false, null, null, null).getBody().getContentAsByteArray();

                    File pdfFile = Files.createTempFile(null, null).toFile();
                    Files.write(pdfFile.toPath(), pdfFileContent);

                    String response = getGenAiSummary(pdfFile);
                    JsonParser jsonParser = JsonParserFactory.getJsonParser();
                    Map<String, Object> responseList = jsonParser.parseMap(response);

                    nodesApi.updateNode(uuid,
                            new NodeBodyUpdate().properties(Map.of("cm:description", responseList.get("result"))),
                            null, null);
                    tagsApi.createTagForNode(uuid, new TagBody().tag(responseList.get("model").toString()), null);

                    LOG.info("Document {} has been updated with summary and tag", entry.getEntry().getName());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {

                LOG.info("PDF rendition for document {} was not available, it has been requested", entry.getEntry().getName());
                renditionsApi.createRendition(uuid, new RenditionBodyCreate().id("pdf"));

            }

        });
    }

    private String getGenAiSummary(File pdfFile) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(genaiTimeout, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", pdfFile.getName(),
                        RequestBody.create(MediaType.parse("application/pdf"), pdfFile))
                .build();

        Request request = new Request.Builder()
                .url(genaiSummaryUrl)
                .post(requestBody)
                .build();

        return client.newCall(request).execute().body().string();

    }

    public static void main(String[] args) {
        SpringApplication.run(AiSummarizeApplication.class, args);
    }

}
