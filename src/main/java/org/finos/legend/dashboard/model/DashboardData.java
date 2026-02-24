package org.finos.legend.dashboard.model;

import java.util.List;

public record DashboardData(String primaryKey, String primaryDisplayName,
                              List<ProjectRelease> releases) {
}
