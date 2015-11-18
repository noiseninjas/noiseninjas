package com.noiseninjas.android.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;
import com.noiseninjas.android.app.R;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

public class GeoCoderIntentService extends IntentService {
    
    public static final String TAG = "GeocoderIntentService" ;
    
    public static final int RESULT_SUCCESS = 301;
    public static final int RESULT_FAILURE = 302;
    public static final int QUERY_TYPE_FETCH_ADDRESS = 501 ;
    public static final int QUERY_TYPE_NONE = 502 ;

    
    public static final String EXTRA_RESULT_RECEIVER = "extra_result_receiver";
    public static final String EXTRA_RESULT_CODE = "extra_result_code";
    public static final String EXTRA_LOCATION = "extra_location";
    public static final String EXTRA_RESULT_DATA = "extra_result_data";
    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";
    public static final String EXTRA_TYPE_FETCH_ADRESS = "extra_fetch_address";
    
    /**
     * 
     * private class to fetch results query
     *
     */
     private static class QueryResult{
        
       
        private int resultCode; 
        private String queryAddress;
        private String errorMesage ; 
        
        public QueryResult(int resultCode) {
            this.resultCode = resultCode ;
        }
        
    }
    
    /**
     * constructor for background thread name
     */
    public GeoCoderIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent queryIntent) {
        QueryResult result = null ;
        switch(getQueryTypeFromIntent(queryIntent)){
            
            case QUERY_TYPE_FETCH_ADDRESS:{
                result = onQueryAddress(queryIntent);
//                addTempDelay(10000);
            }
            break ;
            default:{
                result = onQueryImproper(queryIntent);
            }
            break;
        }
        deliverResult(queryIntent, result);
        
    }

    @SuppressWarnings("unused")
    private void addTempDelay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    private void deliverResult(Intent queryIntent, QueryResult result) {
       String resultMessage = (result.resultCode == RESULT_SUCCESS) ? result.queryAddress : result.errorMesage ;
       deliverResultToReceiver(queryIntent, result.resultCode, resultMessage) ;
        
    }
    private boolean isValidAddress(List<Address> addresses){
        return addresses != null && !addresses.isEmpty();
    }
    private QueryResult onQueryAddress(Intent queryIntent) {
        LatLng queryLocation = queryIntent.getParcelableExtra(EXTRA_LOCATION);
        QueryResult result = new QueryResult(RESULT_FAILURE) ;
        if(queryLocation != null ){
            List<Address> addresses = null;
            Geocoder addressFetcher = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                // fetch only one address
                addresses = addressFetcher.getFromLocation(queryLocation.latitude,queryLocation.longitude,1);
                if(isValidAddress(addresses)){
                    result.resultCode = RESULT_SUCCESS ;
                    result.queryAddress = getAddressString(addresses);
                }else{
                    result.errorMesage = getBaseContext().getString(R.string.no_location_data);
                }
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                result.errorMesage = getString(R.string.no_geocoder);
                Log.e(TAG, result.errorMesage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                result.errorMesage = getString(R.string.invalid_lat_lng);
                Log.e(TAG, result.errorMesage + ". " + "Latitude = " + queryLocation.latitude + ", " + "Longitude = " + queryLocation.longitude, illegalArgumentException);
            }
        }else{
            result.errorMesage = getBaseContext().getString(R.string.no_location_data);
        }
        return result ;
    }
    
    private String getAddressString(List<Address> addresses){
        Address address = addresses.get(0);
        ArrayList<String> addressFragments = new ArrayList<String>();
        // Fetch the address lines using getAddressLine,
        // join them, and send them to the thread.
        for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        return TextUtils.join(System.getProperty("line.separator"),addressFragments) ;
    }
    private QueryResult onQueryImproper(Intent queryIntent) {
       QueryResult result = new QueryResult(RESULT_FAILURE);
       result.errorMesage = getBaseContext().getString(R.string.improper_geocoder_query);
       return result;
    }
    
    private void deliverResultToReceiver(Intent queryIntent, int resultCode, String message) {
        ResultReceiver resultReceiver = getResultReceiverFromIntent(queryIntent);
        if (resultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_RESULT_CODE, resultCode);
            bundle.putString(EXTRA_RESULT_DATA, message);
            bundle.putInt(EXTRA_TYPE_FETCH_ADRESS, getQueryTypeFromIntent(queryIntent));
            resultReceiver.send(resultCode, bundle);
        }
    }
    private ResultReceiver getResultReceiverFromIntent(Intent intent) {
        
        return intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
    }

    private int getQueryTypeFromIntent(Intent intent) {
       
        return intent.getIntExtra(EXTRA_TYPE_FETCH_ADRESS, QUERY_TYPE_NONE);
    }
    
    public static void launchGeocodeService(Context context, ResultReceiver resultReceiver,LatLng queryLocation){
        Intent geoCodeIntent = new Intent(context,GeoCoderIntentService.class);
        geoCodeIntent.putExtra(GeoCoderIntentService.EXTRA_LOCATION, queryLocation);
        geoCodeIntent.putExtra(GeoCoderIntentService.EXTRA_RESULT_RECEIVER, resultReceiver);
        geoCodeIntent.putExtra(GeoCoderIntentService.EXTRA_TYPE_FETCH_ADRESS, GeoCoderIntentService.QUERY_TYPE_FETCH_ADDRESS);
        context.startService(geoCodeIntent);
    }
}
