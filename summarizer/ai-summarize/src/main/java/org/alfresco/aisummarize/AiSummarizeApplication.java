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

    public static void main(String[] args) {
        SpringApplication.run(AiSummarizeApplication.class, args);
    }

    @Override
    public void run(String... args) {}

}
