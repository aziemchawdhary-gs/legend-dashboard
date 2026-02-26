package org.finos.legend.dashboard.health;

import org.finos.legend.dashboard.service.GitRepoReader;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class GitRepoHealthCheck implements HealthIndicator {

    private final GitRepoReader reader;
    private final String repoName;

    public GitRepoHealthCheck(GitRepoReader reader, String repoName) {
        this.reader = reader;
        this.repoName = repoName;
    }

    @Override
    public Health health() {
        if (reader.isAccessible()) {
            return Health.up()
                    .withDetail("repository", repoName)
                    .withDetail("message", "Git repository " + repoName + " is accessible")
                    .build();
        }
        return Health.down()
                .withDetail("repository", repoName)
                .withDetail("message", "Git repository " + repoName + " is not accessible")
                .build();
    }
}
