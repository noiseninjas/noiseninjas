/**
 * 
 */
package com.noiseninjas.android.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

import com.noiseninjas.android.app.engine.EngineParams;
import com.noiseninjas.android.app.engine.PlaceEngine;
import com.noiseninjas.android.app.engine.PlaceIntesity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author vishal.gaurav@hotmail.com
 *
 */
public final class NetworkUtils {

    public static boolean isNetworkConnected(Context context) {
        boolean result = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            result = true;
        }
        return result;
    }

    /**
     * should not use string for response object
     * 
     * @param myurl
     * @return
     * @throws IOException
     */
    // TODO improve this method not looking good right now
    public static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String contentAsString = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if (response == 200) {
                is = conn.getInputStream();
                // Convert the InputStream into a string
                contentAsString = readIt(is);
            }
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
    
    public static boolean sendIntensityToPi(PlaceIntesity intensity) {
        boolean result = false;
        Socket piSocket = null;
        try {
            piSocket = new Socket(EngineParams.PI_IP_ADDRESS, EngineParams.PI_PORT);
            String msg1 = "pi";
            OutputStream out = piSocket.getOutputStream();
            out.write(msg1.getBytes());
            String msg2 = "/join";
            out.write(msg2.getBytes());
            String msg3 = PlaceEngine.getLevelStringForPi(intensity);
            out.write(msg3.getBytes());
            piSocket.close();
        } catch (SocketException ex) {
        } catch (IOException ex) {
        } finally {
            closeSilently(piSocket);
        }
        return result;
    }

    private static void closeSilently(Socket piSocket) {
        if(piSocket!=null){
            try {
                piSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
