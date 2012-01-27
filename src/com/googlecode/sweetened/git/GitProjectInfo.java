package com.googlecode.sweetened.git;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

import com.googlecode.sweetened.ProjectRepositoryInfo;

/**
 * Uses jgit to determine the head revision of the current
 * working branch.
 *
 * The output would be something like:
 *  master-path/to/basedir or master-myrepo
 *
 * @author jnamnath
 */
public class GitProjectInfo implements ProjectRepositoryInfo {

	private static final String GIT_DIR_NAME = ".git";
	private static final String HEAD_REVISION = "HEAD";

	private String branch = null;
	private String revision = null;

	@Override
	public boolean init(File baseDir) {
		try {

			// find the root repository directory
			File rootFolder = getRootBranchFolder(baseDir);
			if (rootFolder == null) {
				// found no git dir
				return false;
			}

			// now build the last part of the branch name from the path relative to the root dir
			File curr = baseDir;
			String branch = curr.getName();
			while (!rootFolder.equals(curr)) {
				curr = new File(curr.getParent());
				branch = curr.getName() + File.separator + branch;
			}

			Repository repository = new FileRepository(getGitDir(rootFolder));
			this.branch = repository.getBranch() + "-" + branch;

			ObjectId head = repository.resolve(HEAD_REVISION);
			this.revision = head.getName();
		} catch (Exception e) {
			throw new BuildException(e);
		}
		return true;
	}

	private File getRootBranchFolder(File baseDir) {
		File curr = baseDir;
		if (curr == null) {
			return null;
		}

		File gitDir = getGitDir(curr);
		while (!gitDir.exists() || !gitDir.isDirectory()) {
			if (curr.getParent() == null) {
				return null;
			}
			curr = new File(curr.getParent());
			gitDir = getGitDir(curr);
		}
		// found a .git dir somewhere up the tree, so return curr dir
		return curr;
	}

	private File getGitDir(File dir) {
		return new File(dir.getAbsolutePath() + File.separator + GIT_DIR_NAME);
	}

	@Override
	public String getBranch() {
		return this.branch;
	}

	@Override
	public String getRevision() {
		return this.revision;
	}

	@Override
	public void setRevision(String revision) {
		this.revision = revision;
	}
}
