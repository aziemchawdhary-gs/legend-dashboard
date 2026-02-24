package org.finos.legend.dashboard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class RepoConfig {

    @JsonProperty
    private String path;

    @NotEmpty
    @JsonProperty
    private String tagPrefix;

    @NotEmpty
    @JsonProperty
    private String githubUrl;

    @JsonProperty
    private String displayName;

    @JsonProperty
    private String pomProperty;

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

    public String getPomProperty() {
        return pomProperty;
    }

    public void setPomProperty(String pomProperty) {
        this.pomProperty = pomProperty;
    }
}
