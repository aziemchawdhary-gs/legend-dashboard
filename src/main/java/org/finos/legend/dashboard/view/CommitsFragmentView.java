package org.finos.legend.dashboard.view;

import io.dropwizard.views.common.View;
import org.finos.legend.dashboard.model.CommitInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitsFragmentView extends View {

    private static final Pattern PR_PATTERN = Pattern.compile("#(\\d+)");

    private final List<CommitInfo> commits;
    private final String repo;
    private final String githubUrl;

    public CommitsFragmentView(List<CommitInfo> commits, String repo, String githubUrl) {
        super("commits-fragment.ftl");
        this.commits = commits;
        this.repo = repo;
        this.githubUrl = githubUrl;
    }

    public List<CommitInfo> getCommits() {
        return commits;
    }

    public String getRepo() {
        return repo;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    /**
     * Expose the view itself so templates can call helper methods like linkifyMessage.
     */
    public CommitsFragmentView getView() {
        return this;
    }

    /**
     * HTML-escapes the message, then converts #1234 references into GitHub PR links.
     * Returns pre-escaped HTML safe for use with ?no_esc in templates.
     */
    public String linkifyMessage(String message) {
        String escaped = htmlEscape(message);
        if (githubUrl == null) {
            return escaped;
        }
        Matcher m = PR_PATTERN.matcher(escaped);
        return m.replaceAll("<a href=\"" + htmlEscape(githubUrl) + "/pull/$1\">#$1</a>");
    }

    private static String htmlEscape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
