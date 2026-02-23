package org.finos.legend.dashboard.view;

import io.dropwizard.views.common.View;
import org.finos.legend.dashboard.model.CommitInfo;

import java.util.List;

public class CommitsFragmentView extends View {

    private final List<CommitInfo> commits;
    private final String repo;

    public CommitsFragmentView(List<CommitInfo> commits, String repo) {
        super("commits-fragment.ftl");
        this.commits = commits;
        this.repo = repo;
    }

    public List<CommitInfo> getCommits() {
        return commits;
    }

    public String getRepo() {
        return repo;
    }
}
