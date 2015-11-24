/**
 * 
 */
package com.noiseninjas.android.app.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.noiseninjas.android.app.utils.FileUtils;

import android.os.AsyncTask;

/**
 * @author vishal gaurav
 *
 */
public class TestGenerator {
    public static class PlaceTestCase {
        public TestPlace placeToTest;
    }

    public static interface OnTestGeneratedListener {
        public void onTestGenerated(int totalTestCaseFiles);

        public void onTestGenerateError();
    }

    private int totalTestCases = 0;
    private int currentTestFileIndex = 0;
    private int currentTestCaseIndex = 0;
    private String currentTestFile = null;
    private String[] totalTestFiles = null;
    private List<TestPlace> listCurrentFileTests = null;
    private OnTestGeneratedListener onTestGeneratedListener = null;
    private ArrayList<ArrayList<TestPlace>> listTotalTestCase = null;
    private boolean isTestGenerationSuccess = false;
    private boolean isNextExists = true;
    private TestPlace currentTest = null;
    
    public boolean isNextTestExists(){
        return isNextExists;
    }
    
    public TestGenerator(OnTestGeneratedListener onTestGeneratedListener) {
        this.onTestGeneratedListener = onTestGeneratedListener;
    }
    public void stopEngine(){
        setTestValuesToDefault();
    }
    public String getCurrentTestFile() {
        return currentTestFile;
    }

    public TestPlace getCurrentTestCase() {
        return currentTest;
    }

    public TestPlace getNextTest() {
        TestPlace resultTest = buildTestForCurrentValue();
        currentTest = resultTest;
        if (resultTest != null) {
            updateNextTest();
        }
        return resultTest;
    }

    private TestPlace buildTestForCurrentValue() {
        TestPlace nextTest = null;
        if (isNextExists) {
            nextTest = listCurrentFileTests.get(currentTestCaseIndex);
        }
        return nextTest;
    }

    private void updateNextTest() {
        if (currentTestCaseIndex + 1 < listCurrentFileTests.size()) {
            currentTestCaseIndex++;
        } else if (currentTestFileIndex + 1 < totalTestFiles.length) {
            currentTestFileIndex++;
            currentTestCaseIndex = 0;
            currentTestFile = totalTestFiles[currentTestCaseIndex];
            listCurrentFileTests = listTotalTestCase.get(currentTestFileIndex);
        } else {
            isNextExists = false;
            // done with the test cases
        }
    }

    public void generateTests() {
        testGeneratorThread.execute();
    }

    private boolean generateAllTestsFromFile() {
        String[] testFiles = FileUtils.getTestFiles();
        if (testFiles != null && testFiles.length > 0) {
            try {
                listTotalTestCase = new ArrayList<ArrayList<TestPlace>>();
                for (int i = 0; i < testFiles.length; i++) {
                    String testFile = FileUtils.getTestFilePath(testFiles[i]);
                    ArrayList<TestPlace> testCases = (ArrayList<TestPlace>) TestPlace.getTestsFromFile(i,testFile);
                    listTotalTestCase.add(testCases);
                    totalTestCases = totalTestCases + testCases.size();
                }
                isTestGenerationSuccess = true;
                totalTestFiles = testFiles;
            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isTestGenerationSuccess;
    }

    private void onTestGenerationFinised(boolean testGenerateResult) {
        if (testGenerateResult) {
            setUpStartTestValues();
            if (onTestGeneratedListener != null) {
                onTestGeneratedListener.onTestGenerated(totalTestFiles.length);
            }
        } else {
            if (onTestGeneratedListener != null) {
                onTestGeneratedListener.onTestGenerateError();
            }
            setTestValuesToDefault();
        }
    }

    private void setUpStartTestValues() {
        isNextExists = true;
        totalTestCases = 0;
        currentTestFileIndex = 0;
        currentTestCaseIndex = 0;
        currentTestFile = totalTestFiles[0];
        listCurrentFileTests = listTotalTestCase.get(0);
    }

    public OnTestGeneratedListener getOnTestGeneratedListener() {
        return onTestGeneratedListener;
    }

    public void setOnTestGeneratedListener(OnTestGeneratedListener onTestGeneratedListener) {
        this.onTestGeneratedListener = onTestGeneratedListener;
    }

    private void setTestValuesToDefault() {
        isNextExists = false;
        totalTestCases = 0;
        currentTestFileIndex = 0;
        currentTestCaseIndex = 0;
        currentTestFile = null;
        totalTestFiles = null;
        listCurrentFileTests = null;
        onTestGeneratedListener = null;
        listTotalTestCase = null;
    }

    private AsyncTask<Void, Void, Boolean> testGeneratorThread = new AsyncTask<Void, Void, Boolean>() {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean testGenerateResult = generateAllTestsFromFile();
            return testGenerateResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            onTestGenerationFinised(result);
            super.onPostExecute(result);
        }
        
        
    };

}
