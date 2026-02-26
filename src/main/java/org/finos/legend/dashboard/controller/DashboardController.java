package org.finos.legend.dashboard.controller;

import org.finos.legend.dashboard.model.DashboardData;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    private final DashboardDataService dataService;

    public DashboardController(DashboardDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/")
    public ModelAndView dashboard() throws Exception {
        DashboardData data = dataService.buildDashboardSummary();
        return new ModelAndView("dashboard", "data", data);
    }
}
