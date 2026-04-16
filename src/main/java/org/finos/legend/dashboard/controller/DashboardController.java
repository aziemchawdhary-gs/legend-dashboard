package org.finos.legend.dashboard.controller;

import org.finos.legend.dashboard.DashboardConfiguration;
import org.finos.legend.dashboard.config.RepoConfig;
import org.finos.legend.dashboard.model.DashboardData;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.service.GitCloneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class DashboardController {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardDataService dataService;
    private final GitCloneService gitCloneService;
    private final Map<String, RepoConfig> repoConfigs;

    public DashboardController(DashboardDataService dataService,
                               GitCloneService gitCloneService,
                               DashboardConfiguration dashboardConfig) {
        this.dataService = dataService;
        this.gitCloneService = gitCloneService;
        this.repoConfigs = dashboardConfig.getRepos();
    }

    @GetMapping("/")
    public ModelAndView dashboard() throws Exception {
        DashboardData data = dataService.buildDashboardSummary();
        return new ModelAndView("dashboard", "data", data);
    }

    @PostMapping("/refresh")
    public String refresh() {
        LOG.info("Refreshing data: fetching all repos and clearing caches");
        for (Map.Entry<String, RepoConfig> entry : repoConfigs.entrySet()) {
            try {
                gitCloneService.ensureRepo(entry.getKey(), entry.getValue().getGithubUrl());
            } catch (Exception e) {
                LOG.error("Failed to fetch repo {}", entry.getKey(), e);
            }
        }
        dataService.clearCaches();
        LOG.info("Refresh complete");
        return "redirect:/";
    }
}
