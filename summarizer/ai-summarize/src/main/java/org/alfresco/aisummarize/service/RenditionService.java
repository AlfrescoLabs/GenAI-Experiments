package org.alfresco.aisummarize.service;

import org.alfresco.core.handler.RenditionsApi;
import org.alfresco.core.model.Rendition;
import org.alfresco.core.model.RenditionBodyCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class RenditionService {

    @Autowired
    RenditionsApi renditionsApi;

    public File getRenditionContent(String uuid) throws IOException {
        byte[] pdfFileContent =
                renditionsApi.getRenditionContent(uuid, "pdf",
                        false, null, null, null).getBody().getContentAsByteArray();
        File pdfFile = Files.createTempFile(null, null).toFile();
        Files.write(pdfFile.toPath(), pdfFileContent);
        return pdfFile;
    }

    public boolean pdfRenditionIsCreated(String uuid) {
        return renditionsApi.getRendition(uuid, "pdf").getBody().getEntry().getStatus() == Rendition.StatusEnum.CREATED;
    }

    public void createPdfRendition(String uuid) {
        renditionsApi.createRendition(uuid, new RenditionBodyCreate().id("pdf"));
    }

}
