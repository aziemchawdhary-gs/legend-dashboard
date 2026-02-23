package org.finos.legend.dashboard.service;

import org.finos.legend.dashboard.model.CommitInfo;
import org.finos.legend.dashboard.model.DashboardData;
import org.finos.legend.dashboard.model.DependencyVersions;
import org.finos.legend.dashboard.model.SdlcRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DashboardDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardDataService.class);

    private final GitRepoReader sdlcReader;
    private final GitRepoReader engineReader;
    private final GitRepoReader pureReader;
    private final GitRepoReader sharedReader;
    private final PomParser pomParser;
    private final int recentTagCount;

    private final ConcurrentHashMap<String, DependencyVersions> depsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<CommitInfo>> commitsCache = new ConcurrentHashMap<>();

    public DashboardDataService(GitRepoReader sdlcReader, GitRepoReader engineReader,
                                 GitRepoReader pureReader, GitRepoReader sharedReader,
                                 PomParser pomParser, int recentTagCount) {
        this.sdlcReader = sdlcReader;
        this.engineReader = engineReader;
        this.pureReader = pureReader;
        this.sharedReader = sharedReader;
        this.pomParser = pomParser;
        this.recentTagCount = recentTagCount;
    }

    public DashboardData buildDashboardSummary() throws Exception {
        // Get N+1 tags so we can show diffs for the most recent N
        List<String> tags = sdlcReader.listRecentTags(recentTagCount + 1);

        List<SdlcRelease> releases = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            DependencyVersions deps = getDepsForTag(tag);
            String version = sdlcReader.versionFromTag(tag);

            String previousTag = (i + 1 < tags.size()) ? tags.get(i + 1) : null;
            DependencyVersions previousDeps = previousTag != null ? getDepsForTag(previousTag) : null;

            // Skip the last tag (it's only used as "previous" for the one before it)
            if (i < tags.size() - 1) {
                releases.add(new SdlcRelease(tag, version, previousTag, deps, previousDeps));
            }
        }

        return new DashboardData(releases);
    }

    private DependencyVersions getDepsForTag(String tag) {
        return depsCache.computeIfAbsent(tag, t -> {
            try {
                String pomContent = sdlcReader.readFileAtTag(t, "pom.xml");
                Map<String, String> props = pomParser.extractProperties(pomContent);
                return new DependencyVersions(
                        props.getOrDefault("legend.engine.version", "unknown"),
                        props.getOrDefault("legend.pure.version", "unknown"),
                        props.getOrDefault("legend.shared.version", "unknown")
                );
            } catch (Exception e) {
                LOG.error("Failed to read dependencies for tag {}", t, e);
                return new DependencyVersions("error", "error", "error");
            }
        });
    }

    public List<CommitInfo> getCommitsForRange(String repo, String fromTag, String toTag) {
        GitRepoReader reader = getReaderForRepo(repo);
        String resolvedFrom = resolveTag(reader, fromTag);
        String resolvedTo = resolveTag(reader, toTag);
        String cacheKey = repo + ":" + resolvedFrom + ".." + resolvedTo;
        return commitsCache.computeIfAbsent(cacheKey, k -> {
            try {
                return reader.commitsBetween(resolvedFrom, resolvedTo);
            } catch (Exception e) {
                LOG.error("Failed to get commits for {} from {} to {}", repo, resolvedFrom, resolvedTo, e);
                return Collections.emptyList();
            }
        });
    }

    private String resolveTag(GitRepoReader reader, String tagOrVersion) {
        if (tagOrVersion.startsWith(reader.getTagPrefix())) {
            return tagOrVersion;
        }
        return reader.getTagPrefix() + tagOrVersion;
    }


    private GitRepoReader getReaderForRepo(String repo) {
        return switch (repo) {
            case "sdlc" -> sdlcReader;
            case "engine" -> engineReader;
            case "pure" -> pureReader;
            case "shared" -> sharedReader;
            default -> throw new IllegalArgumentException("Unknown repo: " + repo);
        };
    }
}
