package org.finos.legend.dashboard.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

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

    @JsonProperty("pomProperties")
    @JsonAlias("pomProperty")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
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
