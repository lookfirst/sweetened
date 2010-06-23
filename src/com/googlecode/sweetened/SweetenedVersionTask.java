package com.googlecode.sweetened;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * Simple task which uses svnkit to determine the version of a project
 * that is stored in svn with a path like this:
 *
 * http://svn/trunk/project
 * http://svn/branches/v10/project
 *
 * The output would be something like:
 *  trunk/project-23423 or branches/v10/project-23432 or
 *  trunk-23534
 *
 * See the example.xml for an example of the usage.
 *
 * @author jon stevens
 */
public class SweetenedVersionTask extends Task
{
    private String sVersionPath = "sVersionPath";
    private String sVersionRevision = "sVersionRevision";
    private String sVersionVersion = "sVersion";

    /** */
    private SVNClientManager manager = null;

    /**
     * The main deal.
     */
    @Override
    public void execute() throws BuildException
    {
        try
        {
            setupSvnKit();

            // Get the WC Client
            SVNWCClient client = this.getSvnClient().getWCClient();

            // Execute svn info
            SVNInfo info = client.doInfo(new File("."), SVNRevision.WORKING);

            // Get the urls
            String url = info.getURL().toDecodedString();
            String repositoryRootUrl = info.getRepositoryRootURL().toDecodedString();

            // ie: trunk/sweetened or branches/v10/sweetened
            String branch = url.substring(repositoryRootUrl.length() + 1);
            this.getProject().setProperty(sVersionPath, branch);

            String revision = new Long(info.getRevision().getNumber()).toString();
            this.getProject().setProperty(sVersionRevision, revision);

            this.getProject().setProperty(sVersionVersion, branch + "-" + revision);
        }
        catch (Exception e)
        {
            throw new BuildException(e);
        }
    }

    /**
     * Initializes the library to work with a repository via different
     * protocols.
     */
    private void setupSvnKit() {
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
    }

    /** */
    public SVNClientManager getSvnClient() {
        return this.manager;
    }

    /** */
    public String getPathProperty() {
        return sVersionPath;
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
