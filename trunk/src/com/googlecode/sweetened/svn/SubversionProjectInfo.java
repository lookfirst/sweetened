package com.googlecode.sweetened.svn;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.googlecode.sweetened.ProjectRepositoryInfo;

/*
 * Simple bit which uses svnkit to determine the version of a project
 * that is stored in svn with a path like this:
 *
 * http://svn/trunk/project
 * http://svn/branches/v10/project
 *
 * The output would be something like:
 *  trunk/project-23423 or branches/v10/project-23432 or
 *  trunk-23534
 *
 */
public class SubversionProjectInfo implements ProjectRepositoryInfo {

	/** */
    private SVNClientManager manager = null;

    private String branch = null;
    private String revision = null;

	public boolean init(File baseDir) {
        /*
         * Initializes the library to work with a repository via different
	     * protocols.
	     */

		/*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();

        /*
         * Create the client manager with defaults
         */
        this.manager = SVNClientManager.newInstance();

        /*
         * Now fetch the branch name and revision from svn info
         */

        // Get the WC Client
        SVNWCClient client = this.getSvnClient().getWCClient();
        SVNInfo info = null;
	    try {
	        // Execute svn info
	        info = client.doInfo(baseDir, SVNRevision.WORKING);
        } catch (SVNException e) {
        	// not an svn working dir
        	return false;
        }
        // Get the urls
        String url = info.getURL().toDecodedString();
        String repositoryRootUrl = info.getRepositoryRootURL().toDecodedString();

        // we have a top level project, ie: http://foo.com/help
        if (url.equals(repositoryRootUrl)) {
            this.branch = url.substring(url.lastIndexOf('/') + 1);
        } else {
            // ie: trunk/sweetened or branches/v10/sweetened
            this.branch = url.substring(repositoryRootUrl.length() + 1);
        }

        this.revision = new Long(info.getRevision().getNumber()).toString();

        return true;
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

	/** */
    private SVNClientManager getSvnClient() {
        return this.manager;
    }

}
