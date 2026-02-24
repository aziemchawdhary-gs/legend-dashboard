package org.finos.legend.dashboard.model;

import java.util.List;

public class ProjectRelease {

    private final String tag;
    private final String version;
    private final String previousTag;
    private final List<DependencyInfo> dependencies;

    public ProjectRelease(String tag, String version, String previousTag,
                           List<DependencyInfo> dependencies) {
        this.tag = tag;
        this.version = version;
        this.previousTag = previousTag;
        this.dependencies = dependencies;
    }

    public String getTag() {
        return tag;
    }

    public String getVersion() {
        return version;
    }

    public String getPreviousTag() {
        return previousTag;
    }

    public List<DependencyInfo> getDependencies() {
        return dependencies;
    }

    public boolean hasPreviousRelease() {
        return previousTag != null;
    }

    public List<DependencyInfo> getChangedDependencies() {
        return dependencies.stream().filter(DependencyInfo::changed).toList();
    }
}
