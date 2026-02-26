package org.finos.legend.dashboard.controller;

import org.finos.legend.dashboard.model.CommitInfo;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.view.CommitsFragmentView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/commits")
public class CommitsController {

    private final DashboardDataService dataService;

    public CommitsController(DashboardDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/{repo}")
    public ModelAndView commits(@PathVariable String repo,
                                @RequestParam("from") String fromTag,
                                @RequestParam("to") String toTag) {
        List<CommitInfo> commits = dataService.getCommitsForRange(repo, fromTag, toTag);
        String githubUrl = dataService.getGithubUrl(repo);
        CommitsFragmentView view = new CommitsFragmentView(commits, repo, githubUrl);

        ModelAndView mav = new ModelAndView("commits-fragment");
        mav.addObject("commits", commits);
        mav.addObject("githubUrl", githubUrl);
        mav.addObject("view", view);
        return mav;
    }
}
