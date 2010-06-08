package com.googlecode.sweetened;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

import com.googlecode.sweetened.typedef.SweetenedFileList;
import com.googlecode.sweetened.typedef.SweetenedFileResource;
import com.googlecode.sweetened.typedef.SweetenedPath;
import com.googlecode.sweetened.typedef.SweetenedScope;

/**
 * Controller for the Sweetened ant Task. Sweetened generates
 * .classpath files for Eclipse.
 *
 * @author jon stevens
 */
public class SweetenedClasspathTask extends MatchingTask
{
    private String file;
    private String var;
    private String text;
    private String sourcepath;
    private String varpath;
    private List<SweetenedPath> classpaths = new ArrayList<SweetenedPath>();
    private String property = "sweetened";
    private boolean autoGen = true;
    private boolean debug = false;

    /**
     * Make sure everything is setup correctly.
     */
    protected void validate() throws BuildException
    {
        if (getSweetenedBits().size() == 0)
        {
            throw new BuildException("Missing classpath element within a sweetened element");
        }
        if (getProperty() == null)
        {
            throw new BuildException("Missing the 'property' attribute within the sweetened element definition.");
        }

        if (getVar() != null)
        {
            if (getVarpath() == null)
            {
                throw new BuildException("Attibute 'varpath' cannot be null when the 'var' attribute is declared.");
            }

            this.getProject().setProperty(this.getProperty(), this.getFormattedVarPath());
        }
        else
        {
            this.getProject().setProperty(this.getProperty(), this.getFormattedLibPath());
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

		    String results = this.getProject().replaceProperties(this.getText());
            results = results.trim();

            if (isAutoGen())
            {
                String note = "\n<!-- Generated by Sweetened http://sweetened.googlecode.com/ (" + new Date() + ") -->\n";
                results = results.replace("\"?>", "\"?>" + note);
            }
            if (isDebug())
                log(results);

            FileOutputStream fos = new FileOutputStream(new File(this.getFile()));
            fos.write(results.getBytes("UTF-8"));
            fos.close();
		}
		catch (Exception e)
		{
			throw new BuildException(e);
		}
	}

    /**
     * Override this method if you would like to format things
     * differently.
     * @throws IOException if there is a problem with getCanonicalPath to a file
     */
	protected String getFormattedVarPath()
	{
	    StringBuilder sb = new StringBuilder();

	    try
	    {
	        // Get the absolute path to the Eclipse variable.
            File varpath = new File(this.getVarpath());
            String canocialPath = varpath.getCanonicalPath();

    	    for (SweetenedFileResource jar : this.getJars(SweetenedScope.COMPILE))
    	    {
    	        File jarFile = jar.getFile();
    	        if (jar.getFile().exists())
    	        {
        	        sb.append("<classpathentry kind=\"var\" ");
        	        sb.append("path=\"");
        	        // /Users/jon/alexandria/thirdparty/junit.jar -> ALEXANDRIA_HOME/thirdparty/junit.jar
        	        sb.append(jarFile.getCanonicalPath().replace(canocialPath, getVar()));
        	        sb.append("\"");
        	        if (jar.getSrc() != null)
        	        {
        	            File filePath = null;
        	            if (jar.getSrc().startsWith("/"))
        	                filePath = new File(jar.getSrc());
        	            else
        	                filePath = new File(this.getProject().getBaseDir(), jar.getSrc());

        	            if (filePath.exists())
        	            {
                            String path = filePath.getCanonicalPath().replace(varpath.getCanonicalPath(), getVar());
                            sb.append(" sourcepath=\"/");
                            sb.append(path);
                            sb.append("\"");
        	            }
        	        }
        	        else if (sourcepath != null)
        	        {
                        String filename = sourcepath + "/" + jarFile.getName();
                        // Add in the sourcepath before the filename
                        // thirdparty/junit.jar -> thirdparty/src/junit.jar
                        String path = jarFile.getCanonicalPath().replace(jarFile.getName(), filename);
                        if (new File(path).exists())
                        {
                            // Chop off the prefix path and replace it with the variable.
                            // /Users/jon/alexandria/thirdparty/src/junit.jar -> /ALEXANDRIA_HOME/thirdparty/src/junit.jar
                            path = path.replace(varpath.getCanonicalPath(), getVar());
                            sb.append(" sourcepath=\"/");
                            sb.append(path);
                            sb.append("\"");
                        }
        	        }
        	        sb.append("/>\n");
    	        }
    	    }
	    }
	    catch (IOException ex)
	    {
	        // Really shouldn't happen
	        throw new BuildException(ex);
	    }
	    return sb.toString();
	}

    /**
     * Override this method if you would like to format things
     * differently.
     */
	protected String getFormattedLibPath()
	{
	    StringBuffer sb = new StringBuffer();
        try
        {
            for (SweetenedFileResource jar : this.getJars(SweetenedScope.COMPILE))
            {
                File jarFile = jar.getFile();

                sb.append("<classpathentry kind=\"lib\" ");
                sb.append("path=\"");
                sb.append(jarFile.getCanonicalPath());
                sb.append("\"");
                if (jar.getSrc() != null)
                {
                    File filePath = null;
                    if (jar.getSrc().startsWith("/"))
                        filePath = new File(jar.getSrc());
                    else
                        filePath = new File(this.getProject().getBaseDir(), jar.getSrc());

                    if (filePath.exists())
                    {
                        sb.append(" sourcepath=\"");
                        sb.append(filePath.getCanonicalPath());
                        sb.append("\"");
                    }
                }
                else if (sourcepath != null)
                {
                    String filename = sourcepath + "/" + jarFile.getName();
                    String path = jarFile.getCanonicalPath().replace(jarFile.getName(), filename);
                    if (new File(path).exists())
                    {
                        sb.append(" sourcepath=\"");
                        sb.append(path);
                        sb.append("\"");
                    }
                }
                sb.append("/>\n");
            }
        }
        catch (IOException ex)
        {
            // Really shouldn't happen
            throw new BuildException(ex);
        }
        return sb.toString();
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
     * The inner text within the sweetened element.
     */
    public void addText(String text) {
        this.text = text;
    }

    /**
     * The inner text within the sweetened element.
     */
    public String getText() {
        return text;
    }

    /**
     * The list of ant &lt;classpath&gt; elements.
     */
    public void addConfiguredSweetenedBits(SweetenedPath classpath) {
        // Lame... but seems to work. At this point, we just want to
        // resolve by reference.
        SweetenedPath path = (SweetenedPath)this.getProject().getReference(classpath.getRefid().getRefId());
        classpaths.add(path);
    }

    /**
     * The list of ant &lt;classpath&gt; elements.
     */
    public List<SweetenedPath> getSweetenedBits() {
        return classpaths;
    }

    /**
     * Combines all the classpath elements into
     * a List of Strings. Only retreives jars of
     * a specific scope.
     */
    protected List<SweetenedFileResource> getJars(SweetenedScope scope) {
        List<SweetenedFileResource> jars = new ArrayList<SweetenedFileResource>();
        for (SweetenedPath path : getSweetenedBits())
        {
            for (SweetenedFileList sfl : path.getFileList())
            {
                for (SweetenedFileResource sfr : sfl.getFileResources())
                {
                    SweetenedScope sfrScope = SweetenedScope.safeValueOf(sfr.getScope());
                    if (sfrScope != null && (sfrScope == scope || sfrScope == SweetenedScope.ALL))
                        jars.add(sfr);
                }
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
     * Used for overriding the substitution property
     * in the CDATA section.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Used for overriding the substitution property
     * in the CDATA section.
     */
    public String getProperty() {
        return property;
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
