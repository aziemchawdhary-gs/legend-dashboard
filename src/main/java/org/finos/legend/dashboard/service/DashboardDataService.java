package org.finos.legend.dashboard.service;

import org.finos.legend.dashboard.config.RepoConfig;
import org.finos.legend.dashboard.model.CommitInfo;
import org.finos.legend.dashboard.model.DashboardData;
import org.finos.legend.dashboard.model.DependencyInfo;
import org.finos.legend.dashboard.model.ProjectRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DashboardDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardDataService.class);

    private final String primaryKey;
    private final Map<String, GitRepoReader> readers;
    private final Map<String, RepoConfig> repoConfigs;
    private final PomParser pomParser;
    private final int recentTagCount;

    // Dependency repos: those with a pomProperty, in config insertion order
    private final Map<String, RepoConfig> dependencyConfigs;

    private final ConcurrentHashMap<String, Map<String, String>> depsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<CommitInfo>> commitsCache = new ConcurrentHashMap<>();

    public DashboardDataService(String primaryKey, Map<String, GitRepoReader> readers,
                                 Map<String, RepoConfig> repoConfigs, PomParser pomParser,
                                 int recentTagCount) {
        this.primaryKey = primaryKey;
        this.readers = readers;
        this.repoConfigs = repoConfigs;
        this.pomParser = pomParser;
        this.recentTagCount = recentTagCount;

        // Build ordered map of dependency repos (those with pomProperty set)
        this.dependencyConfigs = new LinkedHashMap<>();
        for (Map.Entry<String, RepoConfig> entry : repoConfigs.entrySet()) {
            if (entry.getValue().getPomProperty() != null) {
                dependencyConfigs.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public DashboardData buildDashboardSummary() throws Exception {
        GitRepoReader primaryReader = readers.get(primaryKey);
        RepoConfig primaryConfig = repoConfigs.get(primaryKey);
        String primaryDisplayName = primaryConfig.getDisplayName() != null
                ? primaryConfig.getDisplayName() : primaryKey;

        // Get N+1 tags so we can show diffs for the most recent N
        List<String> tags = primaryReader.listRecentTags(recentTagCount + 1);

        List<ProjectRelease> releases = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            Map<String, String> deps = getDepsForTag(tag);
            String version = primaryReader.versionFromTag(tag);

            String previousTag = (i + 1 < tags.size()) ? tags.get(i + 1) : null;
            Map<String, String> previousDeps = previousTag != null ? getDepsForTag(previousTag) : null;

            // Skip the last tag (it's only used as "previous" for the one before it)
            if (i < tags.size() - 1) {
                List<DependencyInfo> depInfos = buildDependencyInfos(deps, previousDeps);
                releases.add(new ProjectRelease(tag, version, previousTag, depInfos));
            }
        }

        return new DashboardData(primaryKey, primaryDisplayName, releases);
    }

    private List<DependencyInfo> buildDependencyInfos(Map<String, String> deps,
                                                       Map<String, String> previousDeps) {
        List<DependencyInfo> result = new ArrayList<>();
        for (Map.Entry<String, RepoConfig> entry : dependencyConfigs.entrySet()) {
            String key = entry.getKey();
            RepoConfig config = entry.getValue();
            String displayName = config.getDisplayName() != null ? config.getDisplayName() : key;
            String version = deps.getOrDefault(key, "unknown");
            String previousVersion = previousDeps != null
                    ? previousDeps.getOrDefault(key, "unknown") : null;
            boolean changed = previousVersion != null && !version.equals(previousVersion);
            result.add(new DependencyInfo(key, displayName, version, previousVersion, changed));
        }
        return result;
    }

    private Map<String, String> getDepsForTag(String tag) {
        return depsCache.computeIfAbsent(tag, t -> {
            try {
                GitRepoReader primaryReader = readers.get(primaryKey);
                String pomContent = primaryReader.readFileAtTag(t, "pom.xml");
                Map<String, String> props = pomParser.extractProperties(pomContent);

                Map<String, String> versions = new LinkedHashMap<>();
                for (Map.Entry<String, RepoConfig> entry : dependencyConfigs.entrySet()) {
                    String depKey = entry.getKey();
                    String pomProp = entry.getValue().getPomProperty();
                    versions.put(depKey, props.getOrDefault(pomProp, "unknown"));
                }
                return versions;
            } catch (Exception e) {
                LOG.error("Failed to read dependencies for tag {}", t, e);
                Map<String, String> errorVersions = new LinkedHashMap<>();
                for (String depKey : dependencyConfigs.keySet()) {
                    errorVersions.put(depKey, "error");
                }
                return errorVersions;
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

    public String getGithubUrl(String repo) {
        RepoConfig config = repoConfigs.get(repo);
        return config != null ? config.getGithubUrl() : null;
    }

    private GitRepoReader getReaderForRepo(String repo) {
        GitRepoReader reader = readers.get(repo);
        if (reader == null) {
            throw new IllegalArgumentException("Unknown repo: " + repo);
        }
        return reader;
    }
}
