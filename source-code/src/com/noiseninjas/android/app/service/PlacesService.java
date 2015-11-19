package com.noiseninjas.android.app.service;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.noiseninjas.android.app.R;
import com.noiseninjas.android.app.engine.NoisePlace;
import com.noiseninjas.android.app.engine.PlaceEngine;
import com.noiseninjas.android.app.engine.PlaceIntesity;
import com.noiseninjas.android.app.network.NetworkUtils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class PlacesService extends IntentService {
    public static final String TAG = "PlaceService";

    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCEL = 0;
    public static final int QUERY_GET_PLACES = 1;
    public static final int QUERY_SEND_INTENSITY = 2;
    public static final int QUERY_NONE = 0;

    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_MAX_INTENSITY = "max_intensity";
    public static final String EXTRA_PLACES = "place_results";
    public static final String EXTRA_INTENSITY = "intensity";
    public static final String EXTRA_QUERY_TYPE = "query_type";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_RESULT_RECEIVER = "receiver";
    public static final String EXTRA_QUERY_TAG = "query_tag";
    public static final String EXTRA_UPDATE_INTENSITY_RESULT = "update_intensity_result";

    public PlacesService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent queryIntent) {
        Log.e("VVV", "onHandleIntent  Called :");
        Bundle result = null;
        switch (getQueryTypeFromIntent(queryIntent)) {
            case QUERY_GET_PLACES: {
                Log.e("VVV", "QUERY_GET_PLACES :");
                result = onQueryPlaces(queryIntent);
            }
            break;
            case QUERY_SEND_INTENSITY: {
                Log.e("VVV", "QUERY_SEND_INTENSITY :");

                result = onQuerySendIntensity(queryIntent);
            }
                break;
            case QUERY_NONE:
            default:
                result = onQueryImproper(queryIntent, getBaseContext().getString(R.string.improper_query));
                break;
        }
        deliverResultToReceiver(queryIntent, result);
        Log.e("VVV", "onHandleIntent ended :");
    }

    private void deliverResultToReceiver(Intent queryIntent, Bundle bundle) {
        updateBundleFromIntent(queryIntent, bundle);
        ResultReceiver resultReceiver = getResultReceiverFromIntent(queryIntent);
        Log.e("VVV", "deliverResultToReceiver ");
        if (resultReceiver != null) {
            Log.e("VVV", "deliverResultToReceiver not null :");
            resultReceiver.send(bundle.getInt(EXTRA_RESULT), bundle);
        }
    }

    private ResultReceiver getResultReceiverFromIntent(Intent intent) {

        return intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
    }

    private Bundle onQueryPlaces(Intent queryIntent) {
        Bundle resultBundle = null;
        if (queryIntent.hasExtra(EXTRA_LOCATION)) {
            resultBundle = new Bundle();
            resultBundle.putInt(EXTRA_RESULT, RESULT_OK);
            LatLng location = queryIntent.getParcelableExtra(EXTRA_LOCATION);
            ArrayList<NoisePlace> places = (ArrayList<NoisePlace>) PlaceEngine.getPlacesAt(location);
            PlaceIntesity intensity = PlaceEngine.getMaxIntensityFromPlaces(places);
            resultBundle.putInt(EXTRA_INTENSITY, intensity.getLevel());
            resultBundle.putParcelableArrayList(EXTRA_PLACES, places);
        } else {
            resultBundle = onQueryImproper(queryIntent, getBaseContext().getString(R.string.no_location_input));
        }
        return resultBundle;
    }
    private Bundle onQuerySendIntensity(Intent queryIntent) {
        Bundle resultBundle = null;
        if (queryIntent.hasExtra(EXTRA_INTENSITY)) {
            int level = queryIntent.getIntExtra(EXTRA_INTENSITY, PlaceIntesity.NONE.getLevel());
            PlaceIntesity intensity = PlaceIntesity.getIntensityFromLevel(level);
            resultBundle = new Bundle();
            resultBundle.putInt(EXTRA_RESULT, RESULT_OK);
            String myIpAddress = NetworkUtils.getWiFiIpAddress(getBaseContext());
            resultBundle.putBoolean(EXTRA_UPDATE_INTENSITY_RESULT, NetworkUtils.sendIntensityToPi(myIpAddress,intensity));
        } else {
            resultBundle = onQueryImproper(queryIntent, getBaseContext().getString(R.string.no_location_input));
        }
        return resultBundle;
    }
    private Bundle onQueryImproper(Intent queryIntent, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_RESULT, RESULT_CANCEL);
        bundle.putString(EXTRA_MESSAGE, getBaseContext().getString(R.string.improper_query));
        return bundle;

    }

    private int getQueryTypeFromIntent(Intent intent) {
        return intent.getIntExtra(EXTRA_QUERY_TYPE, QUERY_NONE);
    }
    private int getQueryTagFromIntent(Intent intent) {
        return intent.getIntExtra(EXTRA_QUERY_TAG, QUERY_NONE);
    }
    private void updateBundleFromIntent(Intent intent,Bundle resultBundle) {
       resultBundle.putInt(EXTRA_QUERY_TYPE, getQueryTypeFromIntent(intent));
       resultBundle.putInt(EXTRA_QUERY_TAG, getQueryTagFromIntent(intent));
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
