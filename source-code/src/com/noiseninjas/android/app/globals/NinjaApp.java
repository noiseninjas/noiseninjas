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

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static void showGenericToast(Context context,String message){
        Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
    }
   public static NinjaApp getApp(Context context){
       return (NinjaApp)context.getApplicationContext();
   }
}
