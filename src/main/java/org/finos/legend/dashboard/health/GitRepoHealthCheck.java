package org.finos.legend.dashboard.health;

import com.codahale.metrics.health.HealthCheck;
import org.finos.legend.dashboard.service.GitRepoReader;

public class GitRepoHealthCheck extends HealthCheck {

    private final GitRepoReader reader;
    private final String repoName;

    public GitRepoHealthCheck(GitRepoReader reader, String repoName) {
        this.reader = reader;
        this.repoName = repoName;
    }

    @Override
    protected Result check() {
        if (reader.isAccessible()) {
            return Result.healthy("Git repository %s is accessible", repoName);
        }
        return Result.unhealthy("Git repository %s is not accessible", repoName);
    }
}
