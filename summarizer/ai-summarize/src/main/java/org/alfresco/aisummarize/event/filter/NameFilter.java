package org.alfresco.aisummarize.event.filter;

import org.alfresco.event.sdk.handling.filter.AbstractEventFilter;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;

import java.util.Objects;

public class NameFilter extends AbstractEventFilter {

    private final String name;

    private NameFilter(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public static NameFilter of(final String name) {
        return new NameFilter(name);
    }

    @Override
    public boolean test(RepoEvent<DataAttributes<Resource>> event) {
        return ((NodeResource) event.getData().getResource()).getName().equals(name);
    }
}
