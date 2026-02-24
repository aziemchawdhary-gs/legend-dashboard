package org.finos.legend.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.finos.legend.dashboard.config.RepoConfig;

import java.util.Map;

public class DashboardConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String primaryProject;

    @Valid
    @NotNull
    @JsonProperty
    private Map<String, RepoConfig> repos;

    @JsonProperty
    private int recentTagCount = 10;

    @JsonProperty
    private String workspaceDir = "./repos";

    public String getPrimaryProject() {
        return primaryProject;
    }

    public Map<String, RepoConfig> getRepos() {
        return repos;
    }

    public int getRecentTagCount() {
        return recentTagCount;
    }

    public String getWorkspaceDir() {
        return workspaceDir;
    }
}
