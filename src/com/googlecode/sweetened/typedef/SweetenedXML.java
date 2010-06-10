/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.googlecode.sweetened.typedef;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.XMLFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Represents the xml fragment within a SweetenedClasspath
 */
public class SweetenedXML extends XMLFragment {

    public void execute(File file, boolean autoGen, boolean debug) {
        DOMElementWriter writer = new DOMElementWriter(true, NamespacePolicy.DEFAULT.getPolicy());
        OutputStream os = new ByteArrayOutputStream();
        try {
            Node n = getFragment().getFirstChild();
            if (n == null) {
                throw new BuildException("No nested XML specified");
            }
            writer.write((Element) n, os);
        } catch (BuildException e) {
            throw e;
        } catch (Exception e) {
            throw new BuildException(e);
        }
        String results = this.getProject().replaceProperties(os.toString());

        if (autoGen)
        {
            String note = "\n<!-- Generated by Sweetened http://sweetened.googlecode.com/ (" + new Date() + ") -->";
            results = results.replace("\"?>", "\"?>" + note);
        }
        if (debug)
            log(results);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(results.getBytes("UTF-8"));
            fos.close();
        } catch (FileNotFoundException ex) {
            throw new BuildException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new BuildException(ex);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    public static class NamespacePolicy extends EnumeratedAttribute {
        private static final String IGNORE = "ignore";
        private static final String ELEMENTS = "elementsOnly";
        private static final String ALL = "all";

        public static final NamespacePolicy DEFAULT
            = new NamespacePolicy(IGNORE);

        public NamespacePolicy() {}

        public NamespacePolicy(String s) {
            setValue(s);
        }
        /** {@inheritDoc}. */
        public String[] getValues() {
            return new String[] {IGNORE, ELEMENTS, ALL};
        }

        public DOMElementWriter.XmlNamespacePolicy getPolicy() {
            String s = getValue();
            if (IGNORE.equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.IGNORE;
            } else if (ELEMENTS.equalsIgnoreCase(s)) {
                return
                    DOMElementWriter.XmlNamespacePolicy.ONLY_QUALIFY_ELEMENTS;
            } else if (ALL.equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.QUALIFY_ALL;
            } else {
                throw new BuildException("Invalid namespace policy: " + s);
            }
        }
    }
}