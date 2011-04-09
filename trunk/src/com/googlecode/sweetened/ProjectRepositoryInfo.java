package com.googlecode.sweetened;

import java.io.File;

public interface ProjectRepositoryInfo {
	
	public boolean init(File baseDir);
	
	public String getBranch();
	
	public String getRevision();
	
}
