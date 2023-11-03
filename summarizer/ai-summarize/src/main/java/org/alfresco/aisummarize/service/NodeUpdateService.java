package org.alfresco.aisummarize.service;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.TagsApi;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.core.model.TagBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NodeUpdateService {

    @Autowired
    NodesApi nodesApi;

    @Autowired
    TagsApi tagsApi;

    public void updateNode(String uuid, Map<String, Object> aiResponse) {

        nodesApi.updateNode(uuid,
                new NodeBodyUpdate().properties(Map.of("cm:description", aiResponse.get("result"))),
                null, null);
        tagsApi.createTagForNode(uuid, new TagBody().tag(aiResponse.get("model").toString()), null);

    }
}
