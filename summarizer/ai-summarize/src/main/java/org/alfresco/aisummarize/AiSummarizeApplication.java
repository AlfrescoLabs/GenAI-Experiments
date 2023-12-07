package org.alfresco.aisummarize;

import org.alfresco.aisummarize.service.GenAiClient;
import org.alfresco.aisummarize.service.NodeUpdateService;
import org.alfresco.aisummarize.service.RenditionService;
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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
public class AiSummarizeApplication implements CommandLineRunner {

    static final Logger LOG = LoggerFactory.getLogger(AiSummarizeApplication.class);

    @Value("${content.service.folder}")
    String folder;

    @Autowired
    GenAiClient genAiClient;

    @Autowired
    RenditionService renditionService;

    @Autowired
    NodeUpdateService nodeUpdateService;

    @Autowired
    SearchApi searchApi;

    @Override
    public void run(String... args) {

        ResponseEntity<ResultSetPaging> results = searchApi.search(
                new SearchRequest()
                        .query(new RequestQuery()
                                .language(RequestQuery.LanguageEnum.AFTS)
                                .query("PATH:\"" + folder + "//*\"")));

        Objects.requireNonNull(results.getBody()).getList().getEntries().forEach((entry) -> {

            String uuid = entry.getEntry().getId();

            LOG.info("Summarizing document {} ({})", entry.getEntry().getName(), uuid);

            if (renditionService.pdfRenditionIsCreated(uuid)) {

                try {

                    String response = genAiClient.getSummary(renditionService.getRenditionContent(uuid));
                    JsonParser jsonParser = JsonParserFactory.getJsonParser();
                    Map<String, Object> aiResponse = jsonParser.parseMap(response);

                    nodeUpdateService.updateNode(uuid, aiResponse);

                    LOG.info("Document {} has been updated with summary and tag", entry.getEntry().getName());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {

                LOG.info("PDF rendition for document {} was not available, it has been requested", entry.getEntry().getName());
                renditionService.createPdfRendition(uuid);

            }

        });
    }

    public static void main(String[] args) {
        SpringApplication.run(AiSummarizeApplication.class, args);
    }

}
