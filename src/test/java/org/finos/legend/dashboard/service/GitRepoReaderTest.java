package org.finos.legend.dashboard.service;

import org.eclipse.jgit.api.Git;
import org.finos.legend.dashboard.config.RepoConfig;
import org.finos.legend.dashboard.model.CommitInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GitRepoReaderTest {

    private Path tempDir;
    private Git git;
    private GitRepoReader reader;

    @BeforeEach
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("git-reader-test");
        git = Git.init().setDirectory(tempDir.toFile()).setInitialBranch("main").call();

        // Create initial commit with a file
        Path pomFile = tempDir.resolve("pom.xml");
        Files.writeString(pomFile, "<project><properties><version>1.0</version></properties></project>");
        git.add().addFilepattern("pom.xml").call();
        git.commit().setMessage("Initial commit").call();
        git.tag().setName("test-repo-1.0.0").call();

        // Second commit and tag
        Files.writeString(pomFile, "<project><properties><version>1.1</version></properties></project>");
        git.add().addFilepattern("pom.xml").call();
        git.commit().setMessage("Update version to 1.1").call();
        git.tag().setName("test-repo-1.1.0").call();

        // Third commit and tag
        Files.writeString(pomFile, "<project><properties><version>2.0</version></properties></project>");
        git.add().addFilepattern("pom.xml").call();
        git.commit().setMessage("Major version bump").call();
        git.tag().setName("test-repo-2.0.0").call();

        // Stray tag that should be filtered out
        git.tag().setName("some-other-tag").call();

        RepoConfig config = new RepoConfig();
        config.setPath(tempDir.toString());
        config.setTagPrefix("test-repo-");
        reader = new GitRepoReader(config);
    }

    @AfterEach
    public void tearDown() throws Exception {
        git.close();
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void listRecentTagsSortedDescending() throws IOException {
        List<String> tags = reader.listRecentTags(10);

        assertThat(tags).containsExactly("test-repo-2.0.0", "test-repo-1.1.0", "test-repo-1.0.0");
    }

    @Test
    public void listRecentTagsLimitsCount() throws IOException {
        List<String> tags = reader.listRecentTags(2);

        assertThat(tags).containsExactly("test-repo-2.0.0", "test-repo-1.1.0");
    }

    @Test
    public void readFileAtTag() throws IOException {
        String content = reader.readFileAtTag("test-repo-1.0.0", "pom.xml");

        assertThat(content).contains("<version>1.0</version>");
    }

    @Test
    public void commitsBetweenTags() throws Exception {
        List<CommitInfo> commits = reader.commitsBetween("test-repo-1.0.0", "test-repo-2.0.0");

        assertThat(commits).hasSize(2);
        assertThat(commits.stream().map(CommitInfo::message).toList())
                .contains("Update version to 1.1", "Major version bump");
    }

    @Test
    public void versionFromTag() {
        assertThat(reader.versionFromTag("test-repo-1.2.3")).isEqualTo("1.2.3");
        assertThat(reader.versionFromTag("bad-tag")).isEqualTo("bad-tag");
    }

    @Test
    public void isAccessible() {
        assertThat(reader.isAccessible()).isTrue();
    }
}
