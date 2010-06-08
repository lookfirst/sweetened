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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.FileList;


/**
 * FileList represents an explicitly named list of files.  FileLists
 * are useful when you want to capture a list of files regardless of
 * whether they currently exist.  By contrast, FileSet operates as a
 * filter, only returning the name of a matched file if it currently
 * exists in the file system.
 */
public class SweetenedFileList extends FileList {

    List<SweetenedFileResource> sfrs = new ArrayList<SweetenedFileResource>();

    /** */
    public void addConfigured(SweetenedFileResource sfr) {
        File baseDir = this.getDir(this.getProject());
        sfr.setBaseDir(baseDir);
        sfr.setFile(new File(baseDir, sfr.getNameInner()));

        // Need to also 'convert' to FileList by configuring it
        FileName fileName = new FileName();
        fileName.setName(sfr.getName());
        addConfiguredFile(fileName);

        sfrs.add(sfr);
    }

    /** */
    public List<SweetenedFileResource> getFileResources() {
        return this.sfrs;
    }
}
