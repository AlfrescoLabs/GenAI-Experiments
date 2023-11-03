package org.alfresco.aisummarize.event;

import org.alfresco.aisummarize.service.GenAiClient;
import org.alfresco.aisummarize.service.NodeUpdateService;
import org.alfresco.aisummarize.service.RenditionService;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.RenditionsApi;
import org.alfresco.core.handler.TagsApi;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.core.model.RenditionBodyCreate;
import org.alfresco.core.model.TagBody;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.handling.filter.NodeTypeFilter;
import org.alfresco.event.sdk.handling.handler.OnNodeCreatedEventHandler;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class RenditionCreatedHandler implements OnNodeCreatedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RenditionCreatedHandler.class);

    @Autowired
    GenAiClient genAiClient;

    @Autowired
    RenditionService renditionService;

    @Autowired
    NodeUpdateService nodeUpdateService;

    @Override
    public void handleEvent(final RepoEvent<DataAttributes<Resource>> repoEvent) {
        if (((NodeResource) repoEvent.getData().getResource()).getName().equals("pdf")) {

            String uuid = ((NodeResource) repoEvent.getData().getResource()).getPrimaryHierarchy().get(0);

            String response = null;
            try {
                response = genAiClient.getSummary(renditionService.getRenditionContent(uuid));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            Map<String, Object> aiResponse = jsonParser.parseMap(response);

            nodeUpdateService.updateNode(uuid, aiResponse);

            LOG.info("Document {} has been updated with summary and tag", uuid);

        }
    }

    @Override
    public EventFilter getEventFilter() {
        return NodeTypeFilter.of("cm:thumbnail");
    }

}