package com.googlecode.sweetened;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.googlecode.sweetened.git.GitProjectInfo;
import com.googlecode.sweetened.svn.SubversionProjectInfo;

/**
 * Simple task which populates various sweetened properties based on repository.
 * 
 *
 * See the example.xml for an example of the usage.
 *
 * @author jon stevens
 */
public class SweetenedVersionTask extends Task
{
    private String sVersionPath = "sVersionPath";
    private String sVersionBranchName = "sVersionBranchName";
    private String sVersionWorkspaceLoc = "sVersionWorkspaceLoc";
    private String sVersionRevision = "sVersionRevision";
    private String sVersionVersion = "sVersion";

    /**
     * The main deal.
     */
    @Override
    public void execute() throws BuildException
    {
        try
        {
        	ProjectRepositoryInfo info = getProjectInfo();
        	
        	String branch = info.getBranch();
        	String revision = info.getRevision();
        	
            this.getProject().setProperty(sVersionPath, branch);

            // ie: trunk-sweetened or branches-v10-sweetened (for use as eclipse project name, which can't have slashes)
            String branchName = branch.replaceAll("/", "-");
            this.getProject().setProperty(sVersionBranchName, branchName);

            // ie: ${workspace_loc:trunk-sweetened/bin} or ${workspace_loc:branches-v10-sweetened/bin} (for use in eclipse launch files)
            this.getProject().setProperty(sVersionWorkspaceLoc, "${workspace_loc:" + branchName + "/bin}");

            this.getProject().setProperty(sVersionRevision, revision);

            this.getProject().setProperty(sVersionVersion, branch.replace("/", "-") + "-" + revision);
        }
        catch (Exception e)
        {
            throw new BuildException(e);
        }
    }

    /**
     * First try subversion, then try git, then just retrieve base info from the project.
     * 
     * The method catches ClassDefNotFoundErrors so you don't have to include svn task
     * or jgit if you don't want to use them.
     */
    private ProjectRepositoryInfo getProjectInfo() {
    	File baseDir = this.getProject().getBaseDir();
    	
    	ProjectRepositoryInfo info = null;
    	try {
    		info = new SubversionProjectInfo();
        	if (!info.init(baseDir)) {
        		info = null;
        	}
    	} catch (Throwable e) {
    		/** ignore exceptions AND errors */
    	}
    	
    	if (info == null) {
	    	try {
	    		info = new GitProjectInfo();
	        	if (!info.init(baseDir)) {
	        		info = null;
	        	}
	    	} catch (Throwable e) {
	    		/** ignore exceptions AND errors */
	    	}
    	}
    	if (info == null) {
    		/*
    		 * Fall back to null info, which is guaranteed to return *something*
    		 */
    		info = new NullRepositoryInfo(this.getProject());
    		info.init(baseDir);
    	}
    	
    	return info;
	}

	/** */
    public String getPathProperty() {
        return sVersionPath;
    }

    /** */
    public void setBranchNameProperty(String sVersionBranchName) {
        this.sVersionBranchName = sVersionBranchName;
    }

    /** */
    public String getBranchNameProperty() {
        return sVersionBranchName;
    }

    /** */
    public void setWorkspaceLocProperty(String sVersionWorkspaceLoc) {
        this.sVersionWorkspaceLoc = sVersionWorkspaceLoc;
    }

    /** */
    public String getWorkspaceLocProperty() {
        return sVersionWorkspaceLoc;
    }

    /** */
    public void setPathProperty(String sVersionPath) {
        this.sVersionPath = sVersionPath;
    }

    /** */
    public String getRevisionProperty() {
        return sVersionRevision;
    }

    /** */
    public void setRevisionProperty(String sVersionRevision) {
        this.sVersionRevision = sVersionRevision;
    }

    /** */
    public void setVersionProperty(String sVersionVersion) {
        this.sVersionVersion = sVersionVersion;
    }

    /** */
    public String getVersionProperty() {
        return sVersionVersion;
    }
}
