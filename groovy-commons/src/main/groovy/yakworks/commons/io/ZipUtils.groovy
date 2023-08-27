/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.io

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import net.lingala.zip4j.ZipFile

/*
 * Util methods for file processing such as deleting temp files or zipping
 */

@CompileStatic
@Slf4j
class ZipUtils {

    /**
     * Like "Extract here" functionality in windows or on mac.
     * Extracts contents into dir with same name
     * create a new directory with same name as zip (minus the .zip) beneath the targetDir directory ( if sepcified)
     * if targetDir not specifed then will use the dir the zip is currently in.
     * @return the new Path with the extracted contents
     */
    static Path extractHere(Path sourceZip, Path targetDir = null) {
        targetDir ?= sourceZip.getParent()
        Path unzipDir = targetDir.resolve(PathTools.getBaseName(sourceZip.fileName))
        Files.createDirectories(unzipDir)
        unzip(sourceZip, unzipDir)
        return unzipDir
    }

    /**
     * Unzip files to targetDir directory
     */
    static void unzip(Path sourceZip, Path targetDir) {
        new ZipFile(sourceZip.toFile()).extractAll(targetDir.toAbsolutePath().toString());
    }

    /**
     * Returns input stream for a specific file inside zip if exists
     */
    static InputStream getZipEntryInputStream(File zip, String entryName) {
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zip)
        ZipEntry entry = zipFile.getEntry(entryName)
        if(entry) return zipFile.getInputStream(entry)
        return null
    }

    // static InputStream getZipEntryInputStream(InputStream inputStream, String entryName) {
    //     ZipInputStream zip = new ZipInputStream(inputStream);
    //     var zipis = new java.util.zip.ZipInputStream(inputStream)
    //     zipis.get
    //     FileHeader fileHeader = zip.getFileHeader("entry_name_in_zip.txt");
    //     InputStream inputStream = zipFile.getInputStream(fileHeader);
    //
    //     getZipEntryInputStream(zip.toFile(), entryName)
    // }

    /**
     * Zips multiple files into single zip
     */
    static File zip(String zipName, File destinationDir, File[] files) {
        if(!files) return

        if (!destinationDir) destinationDir = files[0].parentFile
        File zip = new File(destinationDir, zipName)
        FileOutputStream fout = new FileOutputStream(zip)
        ZipOutputStream zout = new ZipOutputStream(fout)
        zout.setLevel(Deflater.BEST_COMPRESSION)

        Closure addZipEntry
        addZipEntry = { ZipOutputStream zoutStream, File fileToZip, String parent ->
            if (fileToZip == null || !fileToZip.exists()) return
            String zipEntryName = fileToZip.getName()
            if (parent!=null && !parent.isEmpty()) {
                zipEntryName = parent + "/" + fileToZip.getName()
            }

            if (fileToZip.isDirectory()) {
                for (File file : fileToZip.listFiles()) {
                    addZipEntry(zoutStream, file, zipEntryName);
                }
            } else {
                ZipEntry entry = new ZipEntry(zipEntryName)
                zoutStream.putNextEntry(entry)
                fileToZip.withInputStream { fin ->
                    zoutStream << fin
                }
                zoutStream.closeEntry()
            }
        }


        files.each { File f ->
            addZipEntry(zout, f, null)
        }
        zout.close()
        return zip
    }

    /**
     * Zips given file
     */
    static File zip(File file, File destDir = null) {
        assert file.exists()
        if (!destDir) destDir = file.parentFile
        String name = PathTools.changeExtension(file.name, 'zip')
        return zip(name, destDir, file)
    }
}
