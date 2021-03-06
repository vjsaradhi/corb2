/*
  * * Copyright (c) 2004-2016 MarkLogic Corporation
  * *
  * * Licensed under the Apache License, Version 2.0 (the "License");
  * * you may not use this file except in compliance with the License.
  * * You may obtain a copy of the License at
  * *
  * * http://www.apache.org/licenses/LICENSE-2.0
  * *
  * * Unless required by applicable law or agreed to in writing, software
  * * distributed under the License is distributed on an "AS IS" BASIS,
  * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * * See the License for the specific language governing permissions and
  * * limitations under the License.
  * *
  * * The use of the Apache License does not indicate that this project is
  * * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb.util;

import static com.marklogic.developer.corb.util.IOUtils.closeQuietly;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;

/**
 * Common file manipulation utilities
 *
 * @author Mads Hansen, MarkLogic Corporation
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Delete a file or folder and all of it's contents.
     *
     * @param file The file to be deleted.
     * @throws IOException
     */
    public static void deleteFile(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        boolean success;
        if (!file.isDirectory()) {
            success = file.delete();
            if (!success) {
                throw new IOException("error deleting " + file.getCanonicalPath());
            }
            return;
        }
        // directory, so recurse
        File[] children = file.listFiles();
        if (children != null) {
            for (File children1 : children) {
                // recurse
                deleteFile(children1);
            }
        }
        // now this directory should be empty
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Delete a file.
     *
     * @param path Path to the file to be deleted.
     * @throws IOException
     */
    public static void deleteFile(String path) throws IOException {
        deleteFile(new File(path));
    }

    /**
     * Moves a file. If the destination already exists, deletes before moving
     * source.
     *
     * @param source The file to be moved.
     * @param dest The destination file.
     */
    public static void moveFile(final File source, final File dest) {
        if (!source.getAbsolutePath().equals(dest.getAbsolutePath()) && source.exists()) {
            if (dest.exists()) {
                dest.delete();
            }
            source.renameTo(dest);
        }
    }

    /**
     * Determine how many lines are in the file. Returns 0 if the file is null
     * or does not exist.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static int getLineCount(final File file) throws IOException {
        if (file != null && file.exists()) {
            LineNumberReader lnr = null;
            try {
                lnr = new LineNumberReader(new FileReader(file));
                lnr.skip(Long.MAX_VALUE);
                return lnr.getLineNumber();
            } finally {
                closeQuietly(lnr);
            }
        }
        return 0;
    }

    /**
     * Find the file with the given name. First checking for resources on the
     * classpath, then constructing a new File object.
     *
     * @param filename
     * @return File
     */
    public static File getFile(final String filename) {
        File file;
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource != null) {
            file = new File(resource.getFile());
        } else {
            file = new File(filename);
        }
        return file;
    }
}
