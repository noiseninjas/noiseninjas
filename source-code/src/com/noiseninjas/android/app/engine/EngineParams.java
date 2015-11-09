/**
 * 
 */
package com.noiseninjas.android.app.engine;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author visha
 *
 */
public final class EngineParams {
    
    /*
     https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBdv_q1hNke5sf-z-RoI5OjiWZbwZbqX8o&location=28.6355662,77.361751&radius=500&type=school|hospital|cemetry|mosque|hindu_temple||church|university|zoo|doctor
     */
    
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    public static final String RESPONSE_TYPE = "json";
    public static final String PATH_SEPARATOR = "/";
    public static final String LOCATION_API = "/location";
    public static final String QUERY_START = "?";
    public static final String EQUAL = "=";
    public static final String COMMA = ",";
    public static final String AND = "&";
    public static final String URL_COMBINED = BASE_URL  + RESPONSE_TYPE + QUERY_START;
    public static final String KEY_API = "key";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_TYPE = "type";
    public static final int RADIUS_DEFAULT = 500; // int meters
    
    public static final String getApiKey(){
        return "AIzaSyBdv_q1hNke5sf-z-RoI5OjiWZbwZbqX8o";
    }
    public static final int getRadius(){
        // creating method do that we can get it from prefs also. not in current scope now
        return RADIUS_DEFAULT; 
    }
    public static String getPlaceUrl(LatLng location, int radius){
        return URL_COMBINED +
                KEY_API + EQUAL + getApiKey() + AND + 
                KEY_LOCATION + EQUAL + location.latitude + COMMA + location.longitude + AND +
                KEY_RADIUS + EQUAL + radius + AND +
                KEY_TYPE + EQUAL + PlacesMap.getAllPlacesTypeString();
    }
}
