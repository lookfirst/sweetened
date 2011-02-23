package com.googlecode.sweetened.typedef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.tools.ant.types.resources.Union;


/**
 * Wrapper around an ant union element. Contains the logic which includes or excludes
 * resources using the scope attribute of the resource and the ref'd path.
 * 
 */
public class SweetenedPath extends Union {

    private List<SweetenedFileList> fileListList = new ArrayList<SweetenedFileList>();

    // default to the most strict - compile
    private SweetenedScope scope = null;
    private String parent = null;

    /** */
    public void addConfigured(SweetenedFileList sfl) {

        // the ant path cache
        add(sfl);

        // our internal cache
        getFileList().add(sfl);
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
    	return this.scope;
    }
    
    /**
     * This method applies the current path scope to the objects it returns.
     * Any sfile's with the scope of ALL are included.
     */
    @SuppressWarnings("rawtypes")
	@Override
    protected Collection getCollection() {
    	
        // use a LinkedHashSet to de-dupe, but preserve order
    	LinkedHashSet<SweetenedFileResource> resources = new LinkedHashSet<SweetenedFileResource>();

        resources.addAll(getSweetenedFileResources(getScope()));

        return resources;
    }

    /**
     * Override this method to change the behavior for dealing with scope.
     * all > unit|runtime > compile
     */
    protected static boolean isInScope(SweetenedScope pathScope, SweetenedScope jarScope) {
        if (jarScope == null) {
        	return pathScope == null || pathScope == SweetenedScope.ALL;
        }
        // If either the passed in scope or this path's scope is all or equal to each other
        if (jarScope == SweetenedScope.ALL || pathScope == SweetenedScope.ALL || jarScope == pathScope) {
            return true;
        // If this path's scope is either unit or runtime and the passed in scope is compile, then we are in scope.
        } else if ((pathScope == SweetenedScope.UNIT || pathScope == SweetenedScope.RUNTIME) && jarScope == SweetenedScope.COMPILE) {
            return true;
        }
        return false;
    }

    /**
     * Gets the list of SweetenedFileResource objects that are contained within this path at the desired scope.
     * 
     * If pathScope is null, all of the resources are returned.
     */
    private List<SweetenedFileResource> getSweetenedFileResources(SweetenedScope pathScope) {
    	
    	List<SweetenedFileResource> resources = getAllFileResources();
    	
        // if the path is marked with a scope other than all, then filter down the sweetened bits
        if (pathScope != SweetenedScope.ALL) {
	        for (Iterator<SweetenedFileResource> itr = resources.iterator(); itr.hasNext(); ) {
	        	SweetenedFileResource resource = itr.next();
	        	SweetenedScope resourceScope = resource.getScope();
	            if (!isInScope(pathScope, resourceScope)) {
	            	itr.remove();
	            }
	        }
        }
        return resources;
    }

    /**
     * Gets all the file resources ref'd by this element, including parent elements.
     */
    private List<SweetenedFileResource> getAllFileResources() {
        List<SweetenedFileResource> resources = new ArrayList<SweetenedFileResource>();
        
        // add in parent resources
        if (getParent() != null) {
            Object parentRefObj = getProject().getReference(getParent());
            if (parentRefObj instanceof SweetenedPath) {
                SweetenedPath parentRef = (SweetenedPath)parentRefObj;
                
                // add all the parent resources that should be included with the current path's scope
                resources.addAll(parentRef.getAllFileResources());
            }
        }
        
        for (SweetenedFileList sfl : getFileList()) {
        	resources.addAll(sfl.getFileResources());
        }
        
        return resources;
    }
    
    /**
     * Gets the list of SweetenedFileResource objects that are contained within this path at the path's designated scope.
     */
    public List<SweetenedFileResource> getSweetenedFileResources() {
    	return getSweetenedFileResources(getScope());
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
