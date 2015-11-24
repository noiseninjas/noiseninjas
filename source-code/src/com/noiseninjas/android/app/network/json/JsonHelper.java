/**
 * 
 */
package com.noiseninjas.android.app.network.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.noiseninjas.android.app.engine.NoisePlace;
import com.noiseninjas.android.app.engine.PlaceIntesity;
import com.noiseninjas.android.app.engine.PlacesMap;
import com.noiseninjas.android.app.tests.TestPlace;

/**
 * @author vishal gaurav
 *
 */
public final class JsonHelper {
    /* JSON KEYS */

    public static final String KEY_LOCATION = "location";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "lng";
    public static final String KEY_GEOMETRY = "geometry";
    public static final String KEY_NAME = "name";
    public static final String KEY_PLACE_ID = "place_id";
    public static final String KEY_TYPES = "types";
    public static final String KEY_RESULTS = "results";
    public static final String KEY_STATUS = "status";
    public static final String RESULT_OK = "OK";

    /* JSON KEYS */

    public static final List<NoisePlace> parsePlaces(String response) {
        List<NoisePlace> resultPlace = new ArrayList<NoisePlace>();
        try {
            JSONObject resultObject = new JSONObject(response);
            if (isValidResponse(resultObject)) {
                JSONArray resultsArray = resultObject.getJSONArray(KEY_RESULTS);
                int arrayLength = resultsArray.length();
                for (int count = 0; count < arrayLength; count++) {
                    JSONObject placeResult = resultsArray.getJSONObject(count);
                    NoisePlace place = getPlaceFromJson(placeResult);
                    resultPlace.add(place);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultPlace;
    }

    public static final NoisePlace getPlaceFromJson(JSONObject placeJSON) throws JSONException {
        String placeId = placeJSON.getString(KEY_PLACE_ID);
        LatLng location = getLocationFromJson(placeJSON.getJSONObject(KEY_GEOMETRY).getJSONObject(KEY_LOCATION));
        String placeName = placeJSON.getString(KEY_NAME);
        JSONArray types = placeJSON.getJSONArray(KEY_TYPES);
        String placeType = getMostApplicableType(types);
        PlaceIntesity intensity = PlacesMap.getPlaceType(placeType);
        return new NoisePlace(placeId, placeName, location, placeType, intensity);
    }

    private static String getMostApplicableType(JSONArray types) throws JSONException {
        int length = types.length();
        String type = PlacesMap.TYPE_NONE;
        PlaceIntesity lastIntensity = PlaceIntesity.NONE;
        for (int count = 0; count < length; count++) {
            String currentType = types.getString(count);
            PlaceIntesity intensity = PlacesMap.getPlaceType(currentType);
            if (intensity != null) {
                if (lastIntensity.getLevel() < intensity.getLevel()) {
                    type = currentType;
                    lastIntensity = intensity;
                }
            }
        }
        return type;
    }

    private static LatLng getLocationFromJson(JSONObject jsonObject) throws JSONException {

        return new LatLng(jsonObject.getDouble(KEY_LAT), jsonObject.getDouble(KEY_LONG));
    }

    private static boolean isValidResponse(JSONObject resultObject) throws JSONException {

        return (resultObject != null && resultObject.has(KEY_STATUS) && resultObject.has(KEY_RESULTS) && resultObject.getString(KEY_STATUS).equals(RESULT_OK)
                && resultObject.getJSONArray(KEY_RESULTS).length() > 0);
    }

    public static List<TestPlace> parseTestPlaces(String jsonString, int fileIndex, String fileName) throws JSONException {
        List<TestPlace> listTests = null;
        JSONObject rootObject = new JSONObject(jsonString);
        if (rootObject.has(TestPlace.KEY_TESTS)) {
            JSONArray testArray = rootObject.getJSONArray(TestPlace.KEY_TESTS);
            if (testArray != null && testArray.length() > 0) {
                int arrayLength = testArray.length();
                listTests = new ArrayList<TestPlace>();
                for (int count = 0; count < arrayLength; count++) {
                    JSONObject testObject = testArray.optJSONObject(count);
                    if (testObject != null) {
                        TestPlace newTest = TestPlace.getNewTest(count,fileIndex, fileName, testObject);
                        listTests.add(newTest);
                    }
                }
            }
        }
        return listTests;
    }
}
