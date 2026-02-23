package org.finos.legend.dashboard.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.finos.legend.dashboard.model.DashboardData;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.view.DashboardView;

@Path("/")
public class DashboardResource {

    private final DashboardDataService dataService;

    public DashboardResource(DashboardDataService dataService) {
        this.dataService = dataService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public DashboardView dashboard() throws Exception {
        DashboardData data = dataService.buildDashboardSummary();
        return new DashboardView(data);
    }
}
