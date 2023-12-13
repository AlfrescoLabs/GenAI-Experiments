package org.alfresco.aisummarize.service;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.TagsApi;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.core.model.TagBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NodeUpdateService {

    static final String TAG_PROPERTY = "TAG";

    @Value("${content.service.summary.property}")
    String summaryProperty;

    @Value("${content.service.llm.property}")
    String llmProperty;

    @Value("${content.service.answer.property}")
    private String answerProperty;

    @Autowired
    NodesApi nodesApi;

    @Autowired
    TagsApi tagsApi;

    public void updateNodeSummary(String uuid, Map<String, Object> aiResponse) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(summaryProperty, aiResponse.get("summary").toString().trim());
        if (!llmProperty.equals(TAG_PROPERTY)) {
            properties.put(llmProperty, aiResponse.get("model").toString());
        }
        nodesApi.updateNode(uuid,
                new NodeBodyUpdate().properties(properties),
                null, null);

        if (llmProperty.equals(TAG_PROPERTY)) {
            tagsApi.createTagForNode(uuid, new TagBody().tag(aiResponse.get("model").toString()), null);
        }

        List<String> tags = Arrays.asList(aiResponse.get("tags").toString().split(",", -1));
        tags.forEach(tag -> {
            tagsApi.createTagForNode(uuid, new TagBody().tag(tag.replace('.', ' ').trim()), null);
        });

    }

    public void updateNodeAnswer(String uuid, String answer) {
        nodesApi.updateNode(uuid,
                new NodeBodyUpdate().properties(Map.of(answerProperty, answer)),
                null, null);
    }

}
