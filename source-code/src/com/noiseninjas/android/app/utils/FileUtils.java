/**
 * 
 */
package com.noiseninjas.android.app.utils;

import java.io.File;

import android.os.Environment;

/**
 * @author vishal gaurav
 *
 */
public final class FileUtils {

    public static final String TEST_DIRECTORY = "ninja-test";

    public static String getTestDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + TEST_DIRECTORY;
    }
    
    public static String getTestFilePath(String fileName){
        return getTestDirectory() + "/" + fileName;
    }
    
    public static final String[] getTestFiles() {
        String[] testFilesList = null;
        String testDir = getTestDirectory();
        if (testDir != null && !testDir.isEmpty()) {
            File testDirFile = new File(testDir);
            if (testDirFile.exists() && testDirFile.isDirectory()) {
                testFilesList = testDirFile.list();
            }
        }
        return testFilesList;
    }
}
