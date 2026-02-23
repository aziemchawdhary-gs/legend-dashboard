package org.finos.legend.dashboard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class RepoConfig {

    @NotEmpty
    @JsonProperty
    private String path;

    @NotEmpty
    @JsonProperty
    private String tagPrefix;

    @JsonProperty
    private String githubUrl;

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
}
