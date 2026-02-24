package org.finos.legend.dashboard.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GitCloneService {

    private static final Logger LOG = LoggerFactory.getLogger(GitCloneService.class);

    private final File workspaceDir;

    public GitCloneService(String workspacePath) {
        this.workspaceDir = new File(workspacePath);
        if (!workspaceDir.exists()) {
            workspaceDir.mkdirs();
        }
    }

    public File ensureRepo(String repoKey, String githubUrl) throws IOException, GitAPIException {
        File bareDir = new File(workspaceDir, repoKey + ".git");

        if (bareDir.exists() && isValidBareRepo(bareDir)) {
            LOG.info("Fetching updates for {} from {}", repoKey, githubUrl);
            try (Git git = Git.open(bareDir)) {
                git.fetch().setRemote("origin").call();
            }
            LOG.info("Fetch complete for {}", repoKey);
        } else {
            LOG.info("Cloning {} from {}", repoKey, githubUrl);
            Git.cloneRepository()
                    .setURI(githubUrl)
                    .setDirectory(bareDir)
                    .setBare(true)
                    .call()
                    .close();
            LOG.info("Clone complete for {}", repoKey);
        }

        return bareDir;
    }

    private boolean isValidBareRepo(File dir) {
        try {
            Repository repo = new FileRepositoryBuilder()
                    .setGitDir(dir)
                    .readEnvironment()
                    .build();
            boolean valid = repo.getObjectDatabase().exists();
            repo.close();
            return valid;
        } catch (IOException e) {
            return false;
        }
    }
}
