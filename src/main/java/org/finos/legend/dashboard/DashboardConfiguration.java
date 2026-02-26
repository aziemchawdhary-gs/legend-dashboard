package org.finos.legend.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.finos.legend.dashboard.config.RepoConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(prefix = "dashboard")
@Validated
public class DashboardConfiguration {

    @NotEmpty
    private String primaryProject;

    @Valid
    @NotNull
    private Map<String, RepoConfig> repos;

    private int recentTagCount = 10;

    private String workspaceDir = "./repos";

    public String getPrimaryProject() {
        return primaryProject;
    }

    public void setPrimaryProject(String primaryProject) {
        this.primaryProject = primaryProject;
    }

    public Map<String, RepoConfig> getRepos() {
        return repos;
    }

    public void setRepos(Map<String, RepoConfig> repos) {
        this.repos = repos;
    }

    public int getRecentTagCount() {
        return recentTagCount;
    }

    public void setRecentTagCount(int recentTagCount) {
        this.recentTagCount = recentTagCount;
    }

    public String getWorkspaceDir() {
        return workspaceDir;
    }

    public void setWorkspaceDir(String workspaceDir) {
        this.workspaceDir = workspaceDir;
    }
}
