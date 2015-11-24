/**
 * 
 */
package com.noiseninjas.android.app.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.noiseninjas.android.app.network.NetworkUtils;
import com.noiseninjas.android.app.network.json.JsonHelper;

/**
 * @author vishal gaurav
 *
 */
public class TestPlace {
    public static final String KEY_TESTS = "tests";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";
    public static final String KEY_EXPECTED_VALUE = "value";
    
    public int testId ;
    public LatLng mTestLocation;
    public int expectedValue; 
    public String testFile;
    public int testFileIndex;
    public boolean isTestPassed;
    
    public static TestPlace getNewTest(int testId,int fileIndex,String fileName, JSONObject jsonObject) throws JSONException{
        TestPlace newTest = new TestPlace();
        newTest.testFileIndex = fileIndex;
        newTest.testFile = fileName;
        newTest.testId = testId;
        newTest.mTestLocation  = new LatLng(jsonObject.getDouble(KEY_LAT), jsonObject.getDouble(KEY_LONG));
        newTest.expectedValue = jsonObject.getInt(KEY_EXPECTED_VALUE);
        return newTest;
    }
    
    public static List<TestPlace> getTestsFromFile(int index, String fileName) throws UnsupportedEncodingException, IOException, JSONException{
        List<TestPlace> listTests = null;
        FileInputStream fileInput = new FileInputStream(fileName);
        String jsonString = NetworkUtils.readIt(fileInput);
        listTests  = JsonHelper.parseTestPlaces(jsonString,index,fileName);
        return listTests;
    }
}
