
package com.googlecode.sweetened.typedef;

import java.io.File;

import org.apache.tools.ant.types.resources.FileResource;

/**
 * Allow us to pass in more meta information about FileResource.
 */
public class SweetenedFileResource extends FileResource {

    private String src = null;
    private String scope = null;
    private String nameInner = null;

    /** */
    public void setName(String name) {
        this.nameInner = name;
        // not accurate. will be made accurate later in SweetenedFileList.
        // done here because FileResource.getName() depends on having file not be null.
        this.setFile(new File(name));
    }

    /** */
    public String getNameInner() {
        return nameInner;
    }

    /** */
    public void setSrc(String src) {
        this.src = src;
    }

    /** */
    public String getSrc() {
        return src;
    }

    /** */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /** */
    public String getScope() {
        return scope;
    }
}
