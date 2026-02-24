package org.finos.legend.dashboard.model;

public record DependencyInfo(String key, String displayName, String version,
                              String previousVersion, boolean changed) {
}
