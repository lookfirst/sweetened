package com.googlecode.sweetened;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Reference;

import com.googlecode.sweetened.typedef.SweetenedFileResource;
import com.googlecode.sweetened.typedef.SweetenedPath;
import com.googlecode.sweetened.typedef.SweetenedScope;
import com.googlecode.sweetened.typedef.SweetenedXML;
import com.googlecode.sweetened.util.ListEntryElement;

/**
 * Controller for the Sweetened ant Task. Sweetened generates
 * .classpath files for Eclipse.
 *
 * @author jon stevens
 */
public class SweetenedLaunchTask extends MatchingTask
{
    private String file;
    private String var;
    private String varpath;
    private SweetenedXML xml;
    private String sourcepath;
    private List<SweetenedPath> classpaths = new ArrayList<SweetenedPath>();
    private boolean autoGen = true;
    private boolean debug = false;
    public static String SWEETENEDENTRIES_ELEMENT = "sweetenedEntries";

    /**
     * Make sure everything is setup correctly.
     */
    protected void validate() throws BuildException
    {
        if (getSweetenedBits().size() == 0)
        {
            throw new BuildException("Missing classpath element within a sweetenedLaunch element");
        }
    }

    /**
     * The main deal.
     */
    @Override
    public void execute() throws BuildException
    {
        try
        {
            this.validate();
            String data = this.getFormattedListEntries(getListEntries());

            this.getData().execute(data, new File(this.getFile()), this.isAutoGen(), this.isDebug());
        }
        catch (Exception e)
        {
            throw new BuildException(e);
        }
    }

    /**
    * Deals with formatting the actual elements so they show up nicely in the file.
    */
    protected String getFormattedListEntries(List<ListEntryElement> ceeList) {
        StringBuilder sb = new StringBuilder();
        for (ListEntryElement cee : ceeList) {
            sb.append("    ");
            sb.append(cee.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Override this method if you would like to format things
     * differently.
     */
    protected List<ListEntryElement> getListEntries()
    {
        List<ListEntryElement> ceeList = new ArrayList<ListEntryElement>();
        try
        {
            for (SweetenedFileResource jar : this.getJars(SweetenedScope.ALL))
            {
                File jarFile = jar.getFile();

                String jarCanonicalPath = jarFile.getCanonicalPath();
                String outputSourcePath = null;

                if (jar.getSrc() != null)
                {
                    File filePath = null;
                    if (jar.getSrc().startsWith("/"))
                        filePath = new File(jar.getSrc());
                    else
                        filePath = new File(this.getProject().getBaseDir(), jar.getSrc());

                    if (filePath.exists())
                    {
                        outputSourcePath = filePath.getCanonicalPath();
                    }
                }
                else if (sourcepath != null)
                {
                    String filename = sourcepath + "/" + jarFile.getName();
                    outputSourcePath = jarCanonicalPath.replace(jarFile.getName(), filename);
                    if (!new File(outputSourcePath).exists())
                    {
                        outputSourcePath = null;
                    }
                }
                ListEntryElement cee = new ListEntryElement(jarCanonicalPath);
                ceeList.add(cee);
            }
        }
        catch (IOException ex)
        {
            // Really shouldn't happen
            throw new BuildException(ex);
        }
        return ceeList;
    }

    /**
     * The output file location.
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * The output file location.
     */
    public String getFile() {
        return file;
    }

    /**
     * The Eclipse variable that is used to locate the classpath resource.
     * You must also define the varpath attribute.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * The Eclipse variable that is used to locate the classpath resource.
     * You must also define the varpath attribute.
     */
    public String getVar() {
        return var;
    }

    /**
     * The path that is defined to the variable in Eclipse. This is required
     * if the var attribute is defined.
     */
    public void setVarpath(String varpath) {
        this.varpath = varpath;
    }

    /**
     * The path that is defined to the variable in Eclipse. This is required
     * if the var attribute is defined.
     */
    public String getVarpath() {
        return varpath;
    }

    /**
     * The inner xml within the sweetenedClasspath element.
     */
    public void addConfiguredData(SweetenedXML xml) {
        this.xml = xml;
    }


    /**
     * The inner xml within the sweetenedClasspath element.
     */
    public SweetenedXML getData() {
        return xml;
    }

    /**
     * The list of ant &lt;sweetenedBits&gt; elements.
     */
    public void addConfiguredSweetenedBits(SweetenedPath classpath) {
        // need to resolve nested parent paths by hand.
        Reference ref = classpath.getRefid();
        if (ref != null) {
            Object obj = ref.getReferencedObject();
            if (obj instanceof SweetenedPath) {
                addConfiguredSweetenedBits((SweetenedPath)obj);
                return;
            }
        }
        SweetenedPath path = null;
        if (classpath.getParent() != null) {
            path = (SweetenedPath)this.getProject().getReference(classpath.getParent());
            classpaths.add(path);
        }
    }

    /**
     * The list of ant &lt;sweetenedBits&gt; elements.
     */
    public List<SweetenedPath> getSweetenedBits() {
        return classpaths;
    }

    /**
     * Combines all the sweetenedBits elements into
     * a List of Strings. Only retreives jars of
     * a specific scope and ALL.
     */
    protected List<SweetenedFileResource> getJars(SweetenedScope scope) {
        List<SweetenedFileResource> jars = new ArrayList<SweetenedFileResource>();
        for (SweetenedPath path : getSweetenedBits())
        {
            for (SweetenedFileResource sfr : path.getSweetenedFileResources())
            {
                SweetenedScope sfrScope = sfr.getScope();
                if (sfrScope != null && (sfrScope == scope || sfrScope == SweetenedScope.ALL))
                    jars.add(sfr);
            }
        }
        return jars;
    }

    /**
     * Directory name which is appended before the filename of the
     * jar file being referenced. So, if you have /Users/jon/lib/foo.jar
     * and sourcepath="src", then the sourcepath attribute will be set
     * to /Users/jon/lib/src/foo.jar and that should be where the source
     * code to foo.jar lives.
     */
    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    /**
     * Directory name which is appended before the filename of the
     * jar file being referenced. So, if you have /Users/jon/lib/foo.jar
     * and sourcepath="src", then the sourcepath attribute will be set
     * to /Users/jon/lib/src/foo.jar and that should be where the source
     * code to foo.jar lives.
     */
    public String getSourcepath() {
        return sourcepath;
    }

    /**
     * Whether or not you want the generated
     * notice printed out or not. Default is true.
     */
    public void setAutoGen(boolean autoGen) {
        this.autoGen = autoGen;
    }

    /**
     * Whether or not you want the generated
     * notice printed out or not. Default is true.
     */
    public boolean isAutoGen() {
        return autoGen;
    }

    /**
     * Prints the output to stdout.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Prints the output to stdout.
     */
    public boolean isDebug() {
        return debug;
    }
}
