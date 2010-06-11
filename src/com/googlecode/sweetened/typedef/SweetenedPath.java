package com.googlecode.sweetened.typedef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tools.ant.types.resources.Union;


/**
 * Wrapper around an ant union element.
 */
public class SweetenedPath extends Union {

    private List<SweetenedFileList> fileListList = new ArrayList<SweetenedFileList>();
    private SweetenedScope scope = null;
    private String parent = null;

    /** */
    public void addConfigured(SweetenedFileList sfl) {

        // the ant path cache
        add(sfl);

        // our internal cache
        fileListList.add(sfl);
    }

    /** */
    public List<SweetenedFileList> getFileList() {
        return fileListList;
    }

    /** */
    public void setScope(String scope) {
        this.scope = SweetenedScope.valueOf(scope.toUpperCase());
    }

    /**
     * The scope for this path.
     */
    public SweetenedScope getScope() {
        return scope;
    }

    /**
     * This method applies the current path scope to the objects it returns.
     * Any sfile's with the scope of ALL are included.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Collection getCollection() {
        List<SweetenedFileResource> resources = null;
        if (this.parent != null) {
            Object parentRefObj = this.getProject().getReference(this.parent);
            if (parentRefObj instanceof SweetenedPath) {
                SweetenedPath parentRef = (SweetenedPath)parentRefObj;

                // Cache the parents scope and then
                // set the parents scope to the current scope
                SweetenedScope tmpScope = null;
                if (this.scope != null) {
                    tmpScope = parentRef.getScope();
                    parentRef.setScope(this.scope.name());
                }

                // Recursively call the parent.
                Collection result = parentRef.getCollection();

                // Return the parents scope back to what it was.
                if (tmpScope != null)
                    parentRef.setScope(tmpScope.name());

                return result;
            }
        } else {
            resources = this.getSweetenedFileResources();
        }

        Collection<SweetenedFileResource> sfrList = new ArrayList<SweetenedFileResource>();

        // Filter out the items from the scopes we care about.
        for (SweetenedFileResource sfr : resources) {
            if (isInScope(sfr.getScope())) {
                sfrList.add(sfr);
            }
        }

        return sfrList;
    }

    /**
     * Override this method to change the behavior for dealing with scope.
     * all > unit|runtime > compile
     */
    protected boolean isInScope(SweetenedScope scope) {
        if (scope == null) return false;
        if (scope == SweetenedScope.ALL || scope == this.getScope()) {
            return true;
        } else if ((this.getScope() == SweetenedScope.UNIT || this.getScope() == SweetenedScope.RUNTIME) && scope == SweetenedScope.COMPILE) {
            return true;
        }
        return false;
    }

    /**
     * Gets the list of SweetenedFileResource objects that are contained within this path.
     */
    public List<SweetenedFileResource> getSweetenedFileResources() {
        List<SweetenedFileResource> sfrList = new ArrayList<SweetenedFileResource>();
        for (SweetenedFileList sfl : this.getFileList()) {
            List<SweetenedFileResource> fileResources = sfl.getFileResources();
            sfrList.addAll(fileResources);
        }

        return sfrList;
    }

    /**
     * The parent path that this path refers to.
     */
    public String getParent() {
        return this.parent;
    }

    /**
     * The parent path that this path refers to.
     */
    public void setParent(String parent) {
        this.parent = parent;
    }
}
