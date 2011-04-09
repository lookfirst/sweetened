package com.googlecode.sweetened;

import java.io.File;

import org.apache.tools.ant.Project;

/**
 * Returns empty-ish branch results.
 *
 */
public class NullRepositoryInfo implements ProjectRepositoryInfo {

	private static final String NULL_REVISION = "head";
	
	private String branch;
	private Project project;
	
	public NullRepositoryInfo(Project project) {
		this.project = project;
	}
	
	@Override
	public boolean init(File baseDir) {
		// default branch is just the project name
		this.branch = this.project.getName();
		return true;
	}

	@Override
	public String getBranch() {
		return this.branch;
	}

	@Override
	public String getRevision() {
		return NULL_REVISION;
	}

}
