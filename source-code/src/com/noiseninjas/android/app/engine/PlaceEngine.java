/**
 * 
 */
package com.noiseninjas.android.app.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.noiseninjas.android.app.network.NetworkUtils;
import com.noiseninjas.android.app.network.json.JsonHelper;

/**
 * @author vishal gaurav
 *
 */
public class PlaceEngine {
    /**
     * this method will take time. will call server and is synchronous
     * 
     * @param location
     * @return
     */
    public static PlaceIntesity getIntensityAt(LatLng location) {
        PlaceIntesity result = PlaceIntesity.NONE;
        result = calculateIntensity(location, EngineParams.getRadius());
        return result;
    }

    public static List<NoisePlace> getPlacesAt(LatLng location) {
        return getPlacesAt(location, EngineParams.getRadius());
    }

    public static List<NoisePlace> getPlacesAt(LatLng location, int radius) {
        List<NoisePlace> resultList = new ArrayList<NoisePlace>();
        try {
            String url = EngineParams.getPlaceUrl(location, radius);
            String response;
            response = NetworkUtils.downloadUrl(url);
            if (response != null && !response.isEmpty()) {
                resultList.addAll(JsonHelper.parsePlaces(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    private static PlaceIntesity calculateIntensity(LatLng location, int radius) {
        List<NoisePlace> resultList = getPlacesAt(location, radius);
        PlaceIntesity resultIntensity = PlaceIntesity.NONE;
        if (resultList != null && !resultList.isEmpty()) {
            resultIntensity = getMaxIntensityFromPlaces(resultList);
        }

        return resultIntensity;
    }

    private static PlaceIntesity getMaxIntensityFromPlaces(List<NoisePlace> resultList) {
        PlaceIntesity resultIntensity = PlaceIntesity.NONE;
        for (NoisePlace noisePlace : resultList) {
            if (noisePlace.getIntensity().getLevel() > resultIntensity.getLevel()) {
                resultIntensity = noisePlace.getIntensity();
            }
        }
        return resultIntensity;
    }

}
