package org.finos.legend.dashboard.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.finos.legend.dashboard.model.CommitInfo;
import org.finos.legend.dashboard.service.DashboardDataService;
import org.finos.legend.dashboard.view.CommitsFragmentView;

import java.util.List;

@Path("/commits")
public class CommitsResource {

    private final DashboardDataService dataService;

    public CommitsResource(DashboardDataService dataService) {
        this.dataService = dataService;
    }

    @GET
    @Path("/{repo}")
    @Produces(MediaType.TEXT_HTML)
    public CommitsFragmentView commits(@PathParam("repo") String repo,
                                        @QueryParam("from") String fromTag,
                                        @QueryParam("to") String toTag) {
        List<CommitInfo> commits = dataService.getCommitsForRange(repo, fromTag, toTag);
        String githubUrl = dataService.getGithubUrl(repo);
        return new CommitsFragmentView(commits, repo, githubUrl);
    }
}
