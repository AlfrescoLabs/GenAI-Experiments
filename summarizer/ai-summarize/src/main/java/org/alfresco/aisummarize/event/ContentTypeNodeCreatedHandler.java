package org.alfresco.aisummarize.event;

import org.alfresco.aisummarize.event.filter.ParentFolderFilter;
import org.alfresco.core.handler.RenditionsApi;
import org.alfresco.core.model.RenditionBodyCreate;
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
import org.springframework.stereotype.Component;

@Component
public class ContentTypeNodeCreatedHandler extends AbstractCreatedHandler implements OnNodeCreatedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ContentTypeNodeCreatedHandler.class);

    @Autowired
    RenditionsApi renditionsApi;

    @Override
    public void handleEvent(final RepoEvent<DataAttributes<Resource>> repoEvent) {
        LOG.info("PDF Rendition has been requested for document {}", ((NodeResource) repoEvent.getData().getResource()).getId());
        renditionsApi.createRendition(((NodeResource) repoEvent.getData().getResource()).getId(), new RenditionBodyCreate().id("pdf"));
    }

    @Override
    public EventFilter getEventFilter() {
        return ParentFolderFilter.of(folderId)
                .and(NodeTypeFilter.of("cm:content"));
    }

}