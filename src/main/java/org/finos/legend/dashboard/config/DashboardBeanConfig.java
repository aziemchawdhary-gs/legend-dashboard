package org.finos.legend.dashboard.config;

import org.finos.legend.dashboard.DashboardConfiguration;
import org.finos.legend.dashboard.health.GitRepoHealthCheck;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.service.GitCloneService;
import org.finos.legend.dashboard.service.GitRepoReader;
import org.finos.legend.dashboard.service.PomParser;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class DashboardBeanConfig {

    @Bean
    public GitCloneService gitCloneService(DashboardConfiguration config) {
        return new GitCloneService(config.getWorkspaceDir());
    }

    @Bean
    public Map<String, GitRepoReader> gitRepoReaders(DashboardConfiguration config,
                                                      GitCloneService cloneService) throws Exception {
        Map<String, GitRepoReader> readers = new LinkedHashMap<>();
        for (Map.Entry<String, RepoConfig> entry : config.getRepos().entrySet()) {
            String key = entry.getKey();
            RepoConfig repoConfig = entry.getValue();
            if (repoConfig.getPath() != null && !repoConfig.getPath().isEmpty()) {
                readers.put(key, new GitRepoReader(repoConfig));
            } else {
                File bareDir = cloneService.ensureRepo(key, repoConfig.getGithubUrl());
                readers.put(key, new GitRepoReader(bareDir, repoConfig));
            }
        }
        return readers;
    }

    @Bean
    public PomParser pomParser() {
        return new PomParser();
    }

    @Bean
    public DashboardDataService dashboardDataService(DashboardConfiguration config,
                                                      Map<String, GitRepoReader> gitRepoReaders,
                                                      PomParser pomParser) {
        return new DashboardDataService(
                config.getPrimaryProject(), gitRepoReaders, config.getRepos(),
                pomParser, config.getRecentTagCount()
        );
    }

    @Bean
    public HealthContributor gitReposHealth(DashboardConfiguration config,
                                            Map<String, GitRepoReader> gitRepoReaders) {
        Map<String, HealthContributor> contributors = new LinkedHashMap<>();
        for (Map.Entry<String, GitRepoReader> entry : gitRepoReaders.entrySet()) {
            String displayName = config.getRepos().get(entry.getKey()).getDisplayName();
            if (displayName == null) {
                displayName = entry.getKey();
            }
            contributors.put(entry.getKey(), new GitRepoHealthCheck(entry.getValue(), displayName));
        }
        return CompositeHealthContributor.fromMap(contributors);
    }
}
