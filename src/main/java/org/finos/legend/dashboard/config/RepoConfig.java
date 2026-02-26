package org.finos.legend.dashboard.config;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class RepoConfig {

    private String path;

    @NotEmpty
    private String tagPrefix;

    @NotEmpty
    private String githubUrl;

    private String displayName;

    private List<String> pomProperties;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getPomProperties() {
        return pomProperties;
    }

    public void setPomProperties(List<String> pomProperties) {
        this.pomProperties = pomProperties;
    }
}
