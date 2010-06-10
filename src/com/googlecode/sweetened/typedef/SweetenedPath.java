package com.googlecode.sweetened.typedef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Union;


/**
 * Wrapper around an ant union element.
 */
public class SweetenedPath extends Union {

    private List<SweetenedFileList> rc = new ArrayList<SweetenedFileList>();
    private SweetenedScope scope = null;

    /** */
    public void addConfigured(SweetenedFileList sfl) {

        // the ant path cache
        add(sfl);

        // our internal cache
        rc.add(sfl);
    }

    /** */
    public List<SweetenedFileList> getFileList() {
        return rc;
    }

    /** */
    public void setScope(String scope) {
        System.out.println("path: " + this.getRefid() + " scope: " + scope);
        this.scope = SweetenedScope.safeValueOf(scope);
    }

    public SweetenedScope getScope() {
        return scope;
    }


    @Override
    protected Collection getCollection() {
        for (SweetenedFileList sfl : getFileList()) {
            // Remove all resources not in the correct scope
            List<SweetenedFileResource> fileResources = sfl.getFileResources();
            for (SweetenedFileResource sfr : fileResources) {
                System.out.println("sfr.getScope:" + sfr.getScope());
                System.out.println("path.getScope:" + this.scope);
                if (this.scope != null && (sfr.getScope() != SweetenedScope.ALL || sfr.getScope() != this.scope)) {
                    System.out.println("removing resource: " + sfr.getName());
                    sfl.removeResource(sfr);
                }
            }
        }
        return super.getCollection();
    }
    @Override
    public String[] list() {
        String[] theList = super.list();
        System.out.println("here: " + theList);
        return super.list();
    }
    @Override
    public Resource[] listResources() {
        String[] theList = super.list();
        System.out.println("here: " + theList);
        return super.listResources();
    }
}
