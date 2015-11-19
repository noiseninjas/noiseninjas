/**
 * 
 */
package com.noiseninjas.android.app.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.android.gms.maps.model.LatLng;

import android.os.Environment;
import android.util.Log;

/**
 * @author visha
 *
 */
public final class EngineParams {
    
    /*
    https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBQcbQbmf8C0BOmY3bvj6qBxiZ3fuc7eRg&location=28.6355662,77.361751&radius=500&type=school|hospital|cemetry|mosque|hindu_temple||church|university|zoo|doctor
     */
    
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    public static final String PI_IP_ADDRESS = "192.168.43.212";
    public static final int PI_PORT = 9906;
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
        return "AIzaSyBQcbQbmf8C0BOmY3bvj6qBxiZ3fuc7eRg";
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
    public static String getIpAddressFromFile() throws FileNotFoundException,IOException{
       String result = PI_IP_ADDRESS;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ip.txt")));
            result =  br.readLine();
            Log.e("VVV", "ip address found from file = " + result);
            br.close();
        }
        
        catch (FileNotFoundException e) {
           e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result.isEmpty() ? PI_IP_ADDRESS : result;
    }
}
