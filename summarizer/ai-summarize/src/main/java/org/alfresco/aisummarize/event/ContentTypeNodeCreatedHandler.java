package org.alfresco.aisummarize.event;

import org.alfresco.core.handler.RenditionsApi;
import org.alfresco.core.model.RenditionBodyCreate;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.handling.filter.NodeTypeFilter;
import org.alfresco.event.sdk.handling.handler.OnNodeCreatedEventHandler;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentTypeNodeCreatedHandler extends AbstractCreatedHandler implements OnNodeCreatedEventHandler {

    @Autowired
    RenditionsApi renditionsApi;

    @Override
    public void handleEvent(final RepoEvent<DataAttributes<Resource>> repoEvent) {
        boolean parentFound = ((NodeResource) repoEvent.getData().getResource()).getPrimaryHierarchy().get(0).equals(folderId);
        if (parentFound) {
            renditionsApi.createRendition(((NodeResource) repoEvent.getData().getResource()).getId(), new RenditionBodyCreate().id("pdf"));
        }
    }

    @Override
    public EventFilter getEventFilter() {
        return NodeTypeFilter.of("cm:content");
    }

}