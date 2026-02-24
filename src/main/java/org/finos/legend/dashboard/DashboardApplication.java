package org.finos.legend.dashboard;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.views.common.ViewBundle;
import org.finos.legend.dashboard.config.RepoConfig;
import org.finos.legend.dashboard.health.GitRepoHealthCheck;
import org.finos.legend.dashboard.resource.CommitsResource;
import org.finos.legend.dashboard.resource.DashboardResource;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.service.GitCloneService;
import org.finos.legend.dashboard.service.GitRepoReader;
import org.finos.legend.dashboard.service.PomParser;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardApplication extends Application<DashboardConfiguration> {

    public static void main(String[] args) throws Exception {
        new DashboardApplication().run(args);
    }

    @Override
    public String getName() {
        return "legend-versions-dashboard";
    }

    @Override
    public void initialize(Bootstrap<DashboardConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets", null, "assets"));
    }

    @Override
    public void run(DashboardConfiguration config, Environment environment) throws Exception {
        GitCloneService cloneService = new GitCloneService(config.getWorkspaceDir());

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

        PomParser pomParser = new PomParser();

        DashboardDataService dataService = new DashboardDataService(
                config.getPrimaryProject(), readers, config.getRepos(),
                pomParser, config.getRecentTagCount()
        );

        environment.jersey().register(new DashboardResource(dataService));
        environment.jersey().register(new CommitsResource(dataService));

        for (Map.Entry<String, GitRepoReader> entry : readers.entrySet()) {
            String displayName = config.getRepos().get(entry.getKey()).getDisplayName();
            if (displayName == null) {
                displayName = entry.getKey();
            }
            environment.healthChecks().register(entry.getKey() + "-repo",
                    new GitRepoHealthCheck(entry.getValue(), displayName));
        }
    }
}
