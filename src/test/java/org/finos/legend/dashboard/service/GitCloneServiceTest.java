package org.finos.legend.dashboard.service;

import org.eclipse.jgit.api.Git;
import org.finos.legend.dashboard.config.RepoConfig;
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

public class GitCloneServiceTest {

    private Path remoteDir;
    private Path workspaceDir;
    private Git remoteGit;

    @BeforeEach
    public void setUp() throws Exception {
        remoteDir = Files.createTempDirectory("clone-test-remote");
        workspaceDir = Files.createTempDirectory("clone-test-workspace");

        remoteGit = Git.init().setDirectory(remoteDir.toFile()).setInitialBranch("main").call();

        Path pomFile = remoteDir.resolve("pom.xml");
        Files.writeString(pomFile, "<project><properties><v>1.0</v></properties></project>");
        remoteGit.add().addFilepattern("pom.xml").call();
        remoteGit.commit().setMessage("Initial commit").call();
        remoteGit.tag().setName("test-repo-1.0.0").call();

        Files.writeString(pomFile, "<project><properties><v>2.0</v></properties></project>");
        remoteGit.add().addFilepattern("pom.xml").call();
        remoteGit.commit().setMessage("Version 2").call();
        remoteGit.tag().setName("test-repo-2.0.0").call();
    }

    @AfterEach
    public void tearDown() throws Exception {
        remoteGit.close();
        deleteDir(remoteDir);
        deleteDir(workspaceDir);
    }

    @Test
    public void cloneFromLocalRemoteAndReadTags() throws Exception {
        GitCloneService service = new GitCloneService(workspaceDir.toString());
        File bareDir = service.ensureRepo("myrepo", remoteDir.toUri().toString());

        assertThat(bareDir).exists();
        assertThat(new File(bareDir, "HEAD")).exists();

        RepoConfig config = new RepoConfig();
        config.setTagPrefix("test-repo-");
        config.setGithubUrl(remoteDir.toUri().toString());
        GitRepoReader reader = new GitRepoReader(bareDir, config);

        List<String> tags = reader.listRecentTags(10);
        assertThat(tags).containsExactly("test-repo-2.0.0", "test-repo-1.0.0");

        String content = reader.readFileAtTag("test-repo-1.0.0", "pom.xml");
        assertThat(content).contains("<v>1.0</v>");
    }

    @Test
    public void fetchUpdatesExistingBareClone() throws Exception {
        GitCloneService service = new GitCloneService(workspaceDir.toString());
        service.ensureRepo("myrepo", remoteDir.toUri().toString());

        // Add a new tag to the remote
        Path pomFile = remoteDir.resolve("pom.xml");
        Files.writeString(pomFile, "<project><properties><v>3.0</v></properties></project>");
        remoteGit.add().addFilepattern("pom.xml").call();
        remoteGit.commit().setMessage("Version 3").call();
        remoteGit.tag().setName("test-repo-3.0.0").call();

        // Fetch updates
        File bareDir = service.ensureRepo("myrepo", remoteDir.toUri().toString());

        RepoConfig config = new RepoConfig();
        config.setTagPrefix("test-repo-");
        config.setGithubUrl(remoteDir.toUri().toString());
        GitRepoReader reader = new GitRepoReader(bareDir, config);

        List<String> tags = reader.listRecentTags(10);
        assertThat(tags).containsExactly("test-repo-3.0.0", "test-repo-2.0.0", "test-repo-1.0.0");
    }

    private void deleteDir(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
