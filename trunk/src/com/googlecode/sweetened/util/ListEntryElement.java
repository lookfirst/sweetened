package com.googlecode.sweetened.util;

/**
 * <listEntry value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;runtimeClasspathEntry
 * externalArchive=&quot;/Users/jon/checkout/trunk/ftpserver/lib/acegi-security-1.0.6.jar&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#10;"/>
 */
public class ListEntryElement {

    private int path = 3;
    private int type = 2;
    private String jarPath = null;

    /** */
    public ListEntryElement(String jarPath) {
        this.setJarPath(jarPath);
    }

    /** */
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    /** */
    public String getJarPath() {
        return jarPath;
    }

    /** */
    public void setType(int type) {
        this.type = type;
    }

    /** */
    public int getType() {
        return type;
    }

    /** */
    public void setPath(int path) {
        this.path = path;
    }

    /** */
    public int getPath() {
        return path;
    }

    /** */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<listEntry value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;runtimeClasspathEntry ");
        sb.append("externalArchive=&quot;");
        sb.append(this.getJarPath());
        sb.append("&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#10;\" />");
        return sb.toString();
    }
}
