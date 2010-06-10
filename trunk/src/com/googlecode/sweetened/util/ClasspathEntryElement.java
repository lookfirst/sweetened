package com.googlecode.sweetened.util;

/** */
public class ClasspathEntryElement {

    private String kind = null;
    private String path = null;
    private String sourcepath = null;

    /** */
    public ClasspathEntryElement(String kind, String path, String sourcepath) {
        this.kind = kind;
        this.path = path;
        this.sourcepath = sourcepath;
    }

    /** */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /** */
    public String getKind() {
        return kind;
    }

    /** */
    public void setPath(String path) {
        this.path = path;
    }

    /** */
    public String getPath() {
        return path;
    }

    /** */
    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    /** */
    public String getSourcepath() {
        return sourcepath;
    }

    /** */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<classpathentry kind=\"");
        sb.append(this.getKind());
        sb.append("\" path=\"");
        sb.append(this.getPath());
        sb.append("\"");
        if (this.getSourcepath() != null) {
            sb.append(" sourcepath=\"");
            sb.append(this.getSourcepath());
            sb.append("\"");
        }
        sb.append(" />");
        return sb.toString();
    }
}
