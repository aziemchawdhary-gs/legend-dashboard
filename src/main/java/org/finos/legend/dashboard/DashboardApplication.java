package org.finos.legend.dashboard;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.views.common.ViewBundle;
import org.finos.legend.dashboard.health.GitRepoHealthCheck;
import org.finos.legend.dashboard.resource.CommitsResource;
import org.finos.legend.dashboard.resource.DashboardResource;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.service.GitRepoReader;
import org.finos.legend.dashboard.service.PomParser;

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
        GitRepoReader sdlcReader = new GitRepoReader(config.getSdlc());
        GitRepoReader engineReader = new GitRepoReader(config.getEngine());
        GitRepoReader pureReader = new GitRepoReader(config.getPure());
        GitRepoReader sharedReader = new GitRepoReader(config.getShared());

        PomParser pomParser = new PomParser();

        DashboardDataService dataService = new DashboardDataService(
                sdlcReader, engineReader, pureReader, sharedReader,
                pomParser, config.getRecentTagCount()
        );

        environment.jersey().register(new DashboardResource(dataService));
        environment.jersey().register(new CommitsResource(dataService));

        environment.healthChecks().register("sdlc-repo",
                new GitRepoHealthCheck(sdlcReader, "legend-sdlc"));
        environment.healthChecks().register("engine-repo",
                new GitRepoHealthCheck(engineReader, "legend-engine"));
        environment.healthChecks().register("pure-repo",
                new GitRepoHealthCheck(pureReader, "legend-pure"));
        environment.healthChecks().register("shared-repo",
                new GitRepoHealthCheck(sharedReader, "legend-shared"));
    }
}
