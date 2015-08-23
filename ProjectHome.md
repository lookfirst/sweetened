&lt;wiki:gadget url="https://sweetened.googlecode.com/svn/wiki/adsense.xml" border="0" width="475" height="60" /&gt;

Sweetened is a system that centralizes your Ant build dependencies within Ant itself. The beauty of Sweetened is the simplicity it offers over using Maven or Ivy to manage your dependencies. When you have a project that has a well defined set of jar files, it is often the case that Maven and Ivy are complete overkill. Consider this my alternative to those projects.

Sweetened is implemented as a set of Ant tasks and typedefs that have only a negligible effect on your build times. The typedefs are used to define your classpaths and the tasks are used to generate your Eclipse .classpath and launch configurations.

The UserGuide goes into more details with examples of how to use Sweetened to make your builds better.