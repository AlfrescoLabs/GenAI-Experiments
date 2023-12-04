package org.alfresco.aisummarize.event;

import jakarta.annotation.PostConstruct;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.ResultSetRowEntry;
import org.alfresco.search.model.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AbstractCreatedHandler {

    String folderId;

    @Value("${content.service.folder}")
    private String folder;

    @Autowired
    SearchApi searchApi;

    @PostConstruct
    public void init() {
        ResultSetRowEntry folderEntry =
                searchApi.search(
                                new SearchRequest().query(
                                        new RequestQuery().query("PATH:'" + folder + "'")))
                        .getBody().getList().getEntries().get(0);
        folderId = folderEntry.getEntry().getId();
    }

}