package org.alfresco.aisummarize.event;

import org.alfresco.aisummarize.service.GenAiClient;
import org.alfresco.aisummarize.service.NodeUpdateService;
import org.alfresco.aisummarize.service.RenditionService;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.handling.filter.PropertyAddedFilter;
import org.alfresco.event.sdk.handling.filter.PropertyChangedFilter;
import org.alfresco.event.sdk.handling.handler.OnNodeUpdatedEventHandler;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class PropertyUpdatedHandler implements OnNodeUpdatedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyUpdatedHandler.class);

    @Value("${content.service.question.property}")
    private String questionProperty;

    @Autowired
    GenAiClient genAiClient;

    @Autowired
    RenditionService renditionService;

    @Autowired
    NodeUpdateService nodeUpdateService;

    @Override
    public void handleEvent(RepoEvent<DataAttributes<Resource>> repoEvent) {

        String uuid = ((NodeResource) repoEvent.getData().getResource()).getId();
        String question = ((NodeResource) repoEvent.getData().getResource()).getProperties().get(questionProperty).toString();

        LOG.info("Answering question '{}' for document {}", question, uuid);

        String response;
        try {
            response = genAiClient.getAnswer(renditionService.getRenditionContent(uuid), question);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String, Object> aiResponse = jsonParser.parseMap(response);
        String answer = aiResponse.get("answer").toString();

        nodeUpdateService.updateNodeAnswer(uuid, answer);

        LOG.info("Document {} has been updated with answer '{}'", uuid, answer);

    }

    @Override
    public EventFilter getEventFilter() {
        return PropertyAddedFilter.of(questionProperty)
                .or(PropertyChangedFilter.of(questionProperty));
    }

}
