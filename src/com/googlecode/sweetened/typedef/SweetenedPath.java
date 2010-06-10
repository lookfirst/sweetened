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
//        if (this.getRefid() != null) {
//            // FIXME: this should walk the entire reference tree
//            Object obj = this.getCheckedRef();
//            if (obj instanceof SweetenedPath) {
//                ((SweetenedPath)obj).setScope(scope);
//            }
//        }
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
            Object reference = this.getProject().getReference(this.parent);
            if (reference instanceof SweetenedPath) {
                SweetenedPath sp = (SweetenedPath)reference;

                // Cache the parents scope and then
                // set the parents scope to the current scope
                SweetenedScope tmpScope = null;
                if (this.scope != null) {
                    tmpScope = sp.getScope();
                    sp.setScope(this.scope.name());
                }

                // Recursively call the parent.
                Collection result = sp.getCollection();

                // Return the parents scope back to what it was.
                if (tmpScope != null)
                    sp.setScope(tmpScope.name());

                return result;
            }
        } else {
            resources = this.getSweetenedFileResources();
        }

        Collection<SweetenedFileResource> sfrList = new ArrayList<SweetenedFileResource>();
        for (SweetenedFileResource sfr : resources) {
            if (scope != null && (sfr.getScope() == SweetenedScope.ALL || sfr.getScope() == scope)) {
                sfrList.add(sfr);
            }
        }
        return sfrList;
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
