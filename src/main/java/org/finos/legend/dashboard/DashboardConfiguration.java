package org.finos.legend.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.finos.legend.dashboard.config.RepoConfig;

public class DashboardConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private RepoConfig sdlc;

    @Valid
    @NotNull
    @JsonProperty
    private RepoConfig engine;

    @Valid
    @NotNull
    @JsonProperty
    private RepoConfig pure;

    @Valid
    @NotNull
    @JsonProperty("shared")
    private RepoConfig shared;

    @JsonProperty
    private int recentTagCount = 10;

    public RepoConfig getSdlc() {
        return sdlc;
    }

    public RepoConfig getEngine() {
        return engine;
    }

    public RepoConfig getPure() {
        return pure;
    }

    public RepoConfig getShared() {
        return shared;
    }

    public int getRecentTagCount() {
        return recentTagCount;
    }
}
