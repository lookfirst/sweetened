

&lt;wiki:gadget url="http://sweetened.googlecode.com/svn/wiki/adsense.xml" border="0" width="470" height="60" /&gt;

# Introduction #

Let me start off with a simple statement: I'm not a fan of Maven or Ivy. Period. Ant, by itself, works well enough for me. It is simple, easy to use, easy to train people on, very fast and creates no magic. Best of all, Ant is [K.I.S.S.](http://simple.wikipedia.org/wiki/K.I.S.S.). Yes, this project duplicates a lot of what both Maven and Ivy have been doing for years, but since I choose to not use them, I came up with my own arguably cleaner solution to the problem of declaring your jar files for your build.

Let's start with a brief history lesson. Like you, I'm sure, I've always stored my classpath within my build.xml as something like this:

```
<path id="compile.classpath">
     <fileset dir="${lib.dir}">
          <include name="**/*.jar"/>
     </fileset>
</path>
```

The evolution of this turned into something a bit more powerful:

```
<property file="jar.properties" />
<path id="compile.classpath">
     <fileset dir="${lib.dir}">
          <include name="${abc.jar}"/>
          <include name="${bca.jar}"/>
     </fileset>
</path>

Where jar.properties contains:

abc.jar=abc-1.2.3.jar
bca.jar=bca-1.2.3.jar
```

As you can see, I've declared the jar files in an external file. This has the advantage of code completion in Eclipse when I'm adding jars to the include list. It also means that in order to upgrade a jar file, I only need to change it in one place and not modify the actual build.xml file.

This leads to the next evolution. A centralized repository of jar files. So, I created a project called Alexandria (after the grand library) and started putting all my jar files into there...

```
<property name="alexandria.home" value="../alexandria" />
<property file="${alexandria.home}/jar.properties" />
<path id="compile.classpath">
     <fileset dir="${alexandria.home}">
          <include name="${abc.jar}"/>
          <include name="${bca.jar}"/>
     </fileset>
</path>

Where ${alexandria.home}/jar.properties contains:

abc.jar=abc-1.2.3.jar
bca.jar=bca-1.2.3.jar
```

The advantage here is that multiple projects can now take advantage of the jar files without having to copy them into each projects lib directory. I also check alexandria (and all of the jars) into subversion. With the [svntask ant task](http://code.google.com/p/svntask/), I can automatically update the local alexandria directory so that I always have the latest jar files.

This works really well in a group development environment. Not only is this system really simple, but it is very fast. It is easy for everyone to understand and keeps the build working perfectly over many years. No need for 'local' repositories, extra software, dealing with failed mirrors, broken pom files, etc.

This system has worked for me just peachy except for one detail. I'd like to associate more information with each jar file and how it is used so that I can also generate Eclipse .classpath and launch files. In my current office environment, checking these files into subversion is difficult because everyone uses different directory structures and unfortunately, Eclipse doesn't do a very good job of making these files use relative paths. That is where Sweetened comes into play.

# Details #

## Requirements ##

Sweetened requires Ant 1.8.1 or greater and probably JDK 1.5 or greater.

## Usage ##

In order to use Sweetened, you must tell Ant to use it by using a typedef near the top of your Ant build file (and specifying the path to the sweetened.jar file:

```
    <typedef resource="com/googlecode/sweetened/sweetened.xml" classpath="sweetened.jar" />
```

## Union ##

As I mentioned earlier, Sweetened is comprised of a few ant typedefs and tasks. Let's go over the typedefs first. In order to add more information to the jars, I had to come up with my own classes which define Ant Resources.

```
<sunion id="filelist.classpath">
     <sfilelist dir="${alexandria.home}">
          <sfile name="${junit.jar}" scope="unit" src="${junit.src}" />
          <sfile name="${abc.jar}" scope="compile" src="${abc.src}" />
          <sfile name="${bca.jar}" scope="runtime" src="${bca.src}" />
     </sfilelist>
</sunion>
```

This is a very basic example. As you can see, I'm defining a special [Union resource](http://ant.apache.org/manual/Types/resources.html#union) called 'filelist.classpath'. The nested filelist is based in the alexandria home directory that I discussed above and it defines three jar files, the scope of those jar files and the location to the source code. The location can be either a jar file, zip file or directory and is the absolute location to the source code. In other words, the sfilelist directory is not appended to the beginning of the src attribute on the sfile. Yes, this is a bit confusing, but the reality is that the source code could exist outside of the sfilelist directory structure so I wanted to enable that possibility.

## Scope ##

The scope of the jar files is an important part of the requirements of Sweetened. When using javac to compile your classes, you only want to include the dependencies which are necessary to do the compile. However, when building a launch configuration, you will want all of the jar files. That is where scope comes into play and there is an optional parent attribute to sunion which allows you to extend a base sunion. To build on the example above:

```
<sunion id="filelist.classpath">
     <sfilelist dir="${alexandria.home}">
          <sfile name="${junit.jar}" scope="unit" src="${junit.src}" />
          <sfile name="${abc.jar}" scope="compile" src="${abc.src}" />
          <sfile name="${bca.jar}" scope="runtime" src="${bca.src}" />
     </sfilelist>
</sunion>
<sunion id="javac.classpath" scope="compile" parent="filelist.classpath" />
<sunion id="runtime.classpath" scope="runtime" parent="filelist.classpath" />
<sunion id="all.classpath" scope="all" parent="filelist.classpath" />
```

The scopes are defined as:

```
    all > unit|runtime > compile
```

Therefore, all includes everything. unit or runtime includes everything in compile and compile just includes compile. If you don't define a scope, the default is all.

Now that we have an annotated classpath, we can use this for all sorts of fun.

## Generating .classpath files ##

If you have ever looked at a .classpath file, it basically is a simple xml file that contains a list of jar files and a few other details in order to tell Eclipse how to build your project. I've implemented things so that you can either define a global variable in Eclipse and all of the jar paths will be relative to that variable or you can generate a .classpath where all paths are absolute.

This is an example of building a .classpath that just uses absoulte paths. This is the preferred way of doing things because it doesn't require all your developers to manually define a variable in Eclipse:

```
<target name=".eclipse">
        <sweetenedClasspath file=".classpath" debug="true">
            <sweetenedBits refid="filelist.classpath" />
            <data>
                <classpath>
                    <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
                    <classpathentry kind="lib" path="bin/log4j/dev"/>
                    <classpathentry kind="lib" path="etc"/>
                    <sweetenedEntries />
                    <classpathentry kind="lib" path="resources/publisher"/>
                    <classpathentry kind="src" path="src"/>
                    <classpathentry kind="src" path="test/java"/>
                    <classpathentry kind="output" path="_eclipse"/>
                </classpath>
            </data>
        </sweetenedClasspath>
</target>
```

This example uses a variable in Eclipse called ALEXANDRIA\_HOME with a path of ${alexandria.home} and the same filelist.classpath defined in the examples above:

```
<target name=".eclipse">
        <sweetenedClasspath file=".classpath"
            var="ALEXANDRIA_HOME" varpath="${alexandria.home}" sourcepath="src" debug="true">
            <sweetenedBits refid="filelist.classpath" />
            <data>
                <classpath>
                    <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
                    <sweetenedEntries />
                    <classpathentry kind="lib" path="bin/log4j/dev"/>
                    <classpathentry kind="lib" path="etc"/>
                    <classpathentry kind="lib" path="resources/publisher"/>
                    <classpathentry kind="src" path="src"/>
                    <classpathentry kind="src" path="test/java"/>
                    <classpathentry kind="output" path="_eclipse"/>
                </classpath>
            </data>
        </sweetenedClasspath>
</target>
```

If you don't define a src attribute in the sfile element and you define the optional sourcepath attribute, then it will get appended to the jar filename to generate the convention of pointing to the source jar files. This way you can have an alexandria directory structure that looks like this:

```
    /alexandria
        jar.properties
        /thirdparty/junit.jar
        /src/junit.jar

And jar.properties is:

junit.jar=thirdparty/junit.jar
```

debug=true will cause the .classpath output to be printed to the ant logging console.

Probably the best way to define the initial generated .classpath in Ant is to generate one using Eclipse and then copy the output into the sweetenedClasspath element. Add a `<sweetenedEntries />` element where you want your jar files to get replaced.

## Generating .project files ##

The .project file doesn't have any real magic in it, so I generate it with a standard ant echoxml element:

```
<target name=".eclipse">

    <!-- I've removed the sweetenedClasspath for brevity -->

    <echoxml file=".project">
        <projectDescription>
            <name>${project.name}</name>
            <comment></comment>
            <projects>
            </projects>
            <buildSpec>
                <buildCommand>
                    <name>org.eclipse.jdt.core.javabuilder</name>
                    <arguments>
                    </arguments>
                </buildCommand>
            </buildSpec>
            <natures>
                <nature>org.eclipse.jdt.core.javanature</nature>
            </natures>
        </projectDescription>
    </echoxml>
</target>
```

## Using sunion to compile your code ##

In order to use your sunion to compile your code, you need to use a bit of a hacky ant feature. Assume you have defined a sunion with an id of javac.classpath, you would reference it like this:

```
<target name="compile">
    <javac>
        <classpath path="${toString:javac.classpath}" />
    </javac>
</target>
```

## Using sunion to create a tar file ##

```
<tar destfile="${target.dir}/${project.name}.tgz" compression="gzip" longfile="gnu">
    <mappedresources>
        <resources refid="all.classpath" />
        <globmapper from="*" to="lib/*"/>
    </mappedresources>
</tar>
```

## Using sunion to create a war file ##

```
<war>
    <mappedresources>
        <resources refid="all.classpath" />
        <globmapper from="*" to="WEB-INF/lib/*"/>
    </mappedresources>
</war>

or

<war>
    <mappedresources>
        <resources refid="all.classpath" />
        <chainedmapper>
            <flattenmapper/>
            <globmapper from="*" to="WEB-INF/lib/*"/>
        </chainedmapper>
    </mappedresources>
</war>
```

## Generating Launch Configs ##

For those of us who need to generate launch configurations with the classpath all filled out correctly, you can also use Sweetened for that as well. I'm not going to include the full example here in the documentation because it is rather long. Instead I'm going to point you at the example build.xml file that I've included in the [Sweetened repository](http://code.google.com/p/sweetened/source/browse/trunk/example-launch.xml). It functions very similar to the sweetenedClasspath element, so it should be pretty easy to figure out just by looking at the example.

## Generating Versions ##

I've also included another sweet little feature to Sweetened. The ability to generate a nice version for your product based on information from subversion. In order to do this, you will need to put [svnkit](http://svnkit.org) into sweetened's classpath.

```
    <typedef resource="com/googlecode/sweetened/sweetened.xml" classpath="sweetened.jar:svnkit.jar" />
```

Now, you can execute this task:

```
<target name="sVersion">
    <sweetenedVersion />
    <echo>
        ${sVersionPath}-${sVersionRevision} or ${sVersion}
    </echo>
</target>
```

This will output something that looks like either `trunk-23423423` or `branches-mybranch-23423423`. Basically, it removes the base path from your repository and leaves you with a useful version number that you can include in your jar files manifest:

```
<target name="jar">
    <tstamp/>
     <jar jarfile="foo.jar">
        <manifest>
            <attribute name="Specification-Version" value="${sVersion}" />
            <attribute name="Implementation-Version" value="${TODAY}" />
        </manifest>
    </jar>
</target>
```