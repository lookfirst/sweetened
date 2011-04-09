package com.googlecode.sweetened;

import java.io.File;

/**
 * @author jnamnath
 *
 */
public interface ProjectRepositoryInfo {
	
	/**
	 * 
	 * @return true if the repository info is available.
	 */
	public boolean init(File baseDir);
	
	public String getBranch();
	
	public String getRevision();
	
}
