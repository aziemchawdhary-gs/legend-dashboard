package org.finos.legend.dashboard.view;

import io.dropwizard.views.common.View;
import org.finos.legend.dashboard.model.DashboardData;

public class DashboardView extends View {

    private final DashboardData data;

    public DashboardView(DashboardData data) {
        super("dashboard.ftl");
        this.data = data;
    }

    public DashboardData getData() {
        return data;
    }
}
