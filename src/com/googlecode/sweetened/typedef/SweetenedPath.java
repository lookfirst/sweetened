package com.googlecode.sweetened.typedef;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;


/**
 * Wrapper around an ant path element.
 */
public class SweetenedPath extends Path {

    private List<SweetenedFileList> rc = new ArrayList<SweetenedFileList>();

    /** */
    public SweetenedPath(Project project) {
        super(project);
    }

    /** */
    public void addConfigured(SweetenedFileList sfl) {
        add(sfl);
        rc.add(sfl);
    }

    /** */
    public List<SweetenedFileList> getFileList() {
        return rc;
    }
}
