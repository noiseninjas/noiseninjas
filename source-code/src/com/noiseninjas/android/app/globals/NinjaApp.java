package com.noiseninjas.android.app.globals;
/**
 * 
 */

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * @author vishal.gaurav@hotmail.com
 *
 */
public class NinjaApp extends Application {
    /**
     * !! Sample request !! 
     * 
     * https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBdv_q1hNke5sf-z-RoI5OjiWZbwZbqX8o&location=28.
     6355662,77.361751&radius=500&type=school|hospital|cemetry|mosque|hindu_temple||church|university|zoo|doctor
     */
    
    
    /**
     * 
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void showGenericToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static NinjaApp getApp(Context context) {
        return (NinjaApp) context.getApplicationContext();
    }
}
