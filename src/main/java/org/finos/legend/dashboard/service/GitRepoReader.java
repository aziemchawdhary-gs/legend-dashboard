package org.finos.legend.dashboard.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.finos.legend.dashboard.config.RepoConfig;
import org.finos.legend.dashboard.model.CommitInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitRepoReader {

    private final Repository repository;
    private final String tagPrefix;
    private final String githubUrl;
    private final Pattern versionPattern;

    public GitRepoReader(RepoConfig config) throws IOException {
        File gitDir = new File(config.getPath(), ".git");
        this.repository = new FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .build();
        this.tagPrefix = config.getTagPrefix();
        this.githubUrl = config.getGithubUrl();
        this.versionPattern = Pattern.compile("^" + Pattern.quote(tagPrefix) + "(\\d+\\.\\d+\\.\\d+)$");
    }

    public GitRepoReader(File bareRepoDir, RepoConfig config) throws IOException {
        this.repository = new FileRepositoryBuilder()
                .setGitDir(bareRepoDir)
                .readEnvironment()
                .build();
        this.tagPrefix = config.getTagPrefix();
        this.githubUrl = config.getGithubUrl();
        this.versionPattern = Pattern.compile("^" + Pattern.quote(tagPrefix) + "(\\d+\\.\\d+\\.\\d+)$");
    }

    public List<String> listRecentTags(int count) throws IOException {
        List<Ref> tagRefs = repository.getRefDatabase().getRefsByPrefix("refs/tags/");

        record TagVersion(String tagName, int major, int minor, int patch) {}

        List<TagVersion> matching = new ArrayList<>();
        for (Ref ref : tagRefs) {
            String shortName = ref.getName().replace("refs/tags/", "");
            Matcher m = versionPattern.matcher(shortName);
            if (m.matches()) {
                String[] parts = m.group(1).split("\\.");
                matching.add(new TagVersion(
                        shortName,
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])
                ));
            }
        }

        matching.sort(Comparator
                .comparingInt(TagVersion::major)
                .thenComparingInt(TagVersion::minor)
                .thenComparingInt(TagVersion::patch)
                .reversed());

        return matching.stream()
                .limit(count)
                .map(TagVersion::tagName)
                .toList();
    }

    public String readFileAtTag(String tagName, String filePath) throws IOException {
        Ref tagRef = repository.exactRef("refs/tags/" + tagName);
        if (tagRef == null) {
            throw new IOException("Tag not found: " + tagName);
        }

        try (RevWalk revWalk = new RevWalk(repository)) {
            ObjectId objectId = tagRef.getObjectId();
            RevCommit commit;

            // Handle annotated tags
            try {
                RevTag tag = revWalk.parseTag(objectId);
                commit = revWalk.parseCommit(tag.getObject());
            } catch (Exception e) {
                // Lightweight tag, points directly to commit
                commit = revWalk.parseCommit(objectId);
            }

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(filePath));

                if (!treeWalk.next()) {
                    throw new IOException("File not found at tag " + tagName + ": " + filePath);
                }

                ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
                return new String(loader.getBytes(), StandardCharsets.UTF_8);
            }
        }
    }

    public List<CommitInfo> commitsBetween(String fromTag, String toTag) throws Exception {
        try (Git git = new Git(repository)) {
            ObjectId fromId = resolveTagToCommit(fromTag);
            ObjectId toId = resolveTagToCommit(toTag);

            Iterable<RevCommit> commits = git.log().addRange(fromId, toId).call();
            List<CommitInfo> result = new ArrayList<>();
            for (RevCommit commit : commits) {
                result.add(new CommitInfo(
                        commit.getName(),
                        commit.getShortMessage()
                ));
            }
            return result;
        }
    }

    private ObjectId resolveTagToCommit(String tagName) throws IOException {
        Ref tagRef = repository.exactRef("refs/tags/" + tagName);
        if (tagRef == null) {
            throw new IOException("Tag not found: " + tagName);
        }

        try (RevWalk revWalk = new RevWalk(repository)) {
            ObjectId objectId = tagRef.getObjectId();
            try {
                RevTag tag = revWalk.parseTag(objectId);
                return revWalk.parseCommit(tag.getObject()).getId();
            } catch (Exception e) {
                return revWalk.parseCommit(objectId).getId();
            }
        }
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String versionFromTag(String tag) {
        Matcher m = versionPattern.matcher(tag);
        return m.matches() ? m.group(1) : tag;
    }

    public boolean isAccessible() {
        try {
            return repository.getObjectDatabase().exists();
        } catch (Exception e) {
            return false;
        }
    }
}
