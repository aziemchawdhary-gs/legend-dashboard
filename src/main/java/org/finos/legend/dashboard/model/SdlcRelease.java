package org.finos.legend.dashboard.model;

public class SdlcRelease {

    private final String tag;
    private final String version;
    private final String previousTag;
    private final DependencyVersions deps;
    private final DependencyVersions previousDeps;

    public SdlcRelease(String tag, String version, String previousTag,
                        DependencyVersions deps, DependencyVersions previousDeps) {
        this.tag = tag;
        this.version = version;
        this.previousTag = previousTag;
        this.deps = deps;
        this.previousDeps = previousDeps;
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

    public DependencyVersions getDeps() {
        return deps;
    }

    public DependencyVersions getPreviousDeps() {
        return previousDeps;
    }

    public boolean hasPreviousRelease() {
        return previousTag != null;
    }

    public boolean engineChanged() {
        return previousDeps != null && !deps.engineVersion().equals(previousDeps.engineVersion());
    }

    public boolean pureChanged() {
        return previousDeps != null && !deps.pureVersion().equals(previousDeps.pureVersion());
    }

    public boolean sharedChanged() {
        return previousDeps != null && !deps.sharedVersion().equals(previousDeps.sharedVersion());
    }
}
