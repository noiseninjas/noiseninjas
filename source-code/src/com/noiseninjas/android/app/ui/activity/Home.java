package com.noiseninjas.android.app.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.noiseninjas.android.app.R;
import com.noiseninjas.android.app.globals.NinjaApp;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Home extends BaseActivity {
    private static class TempPlace {
        private String name;
        private double latitude;
        private double longitude;

        public TempPlace(String name, double latitude, double longitude) {
            super();
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

    }

    public static final String SERVER_PLACES_API_KEY = "AIzaSyBdv_q1hNke5sf-z-RoI5OjiWZbwZbqX8o";
    private static final int REQUEST_CHECK_LOCATION_SETTINGS = 101;
    private SupportMapFragment mMapFragment = null;
    private GoogleMap mGoogleMap = null;
    private Location mCurrentLocation = null;
    private MarkerOptions mCurrentLocationMarker = null;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private boolean isGoogleApiConnected = false;
    private boolean isRequestingLocationUpdates = false;
    private Toolbar mToolbar = null;
    private ImageView mImgLevel = null;
    private AnimationDrawable mAnimZone = null;

    private ArrayList<TempPlace> listZones = new ArrayList<Home.TempPlace>();
    private LatLng mLocationBusyArea = new LatLng(28.6355662, 77.361751);
    private LatLng mLocationRemoteArea = new LatLng(23.7998507,85.4321927);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        generateHardCodeData();
        initViews();
        buildGoogleApiClient();

    }

    private void generateHardCodeData() {
        TempPlace school = new TempPlace("Vivekanand Global School", 28.633805, 77.35954);
        TempPlace hospital = new TempPlace("Indirapuram Public Hospital", 28.639544, 77.360691);
        TempPlace currentLocation = new TempPlace("Current Location", mLocationBusyArea.latitude, mLocationBusyArea.longitude);
        listZones.add(school);
        listZones.add(hospital);
        listZones.add(currentLocation);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onPause() {
        stopRequestingLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void initViews() {
        setupMapViews();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mImgLevel = (ImageView) findViewById(R.id.imgLevel);
        mImgLevel.setVisibility(View.INVISIBLE);
        findViewById(R.id.txtRed).setOnClickListener(mOnclickListener);
        findViewById(R.id.txtGreen).setOnClickListener(mOnclickListener);

        initActionBar();
    }

    private void initActionBar() {
        setUpToolbar();

    }

    private void setUpToolbar() {
        if (mToolbar != null) {
//            setSupportActionBar(mToolbar);
//           getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
    private void setupMapViews() {
        mMapFragment = SupportMapFragment.newInstance(getMapOptions());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.flMapFragment, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(mOnMapReadyCallBack);
    }

    private void setupGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(mOnMyLocationButtonClickListener);
    }

    private GoogleMapOptions getMapOptions() {
        GoogleMapOptions mMapOptions = new GoogleMapOptions();
        mMapOptions.compassEnabled(false);
        mMapOptions.mapToolbarEnabled(false);
        mMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapOptions.rotateGesturesEnabled(false);
        mMapOptions.zoomControlsEnabled(false);
        mMapOptions.liteMode(false);
        return mMapOptions;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(mGoogleApiCallback).addOnConnectionFailedListener(mConnectionFailedCallback)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(20000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    private void checkLocationServiceEnabled() {
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(mLocationResultCallback);
    }

    private void startRequestingLocationUpdates() {
        isRequestingLocationUpdates = true;
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        NinjaApp.showGenericToast(getBaseContext(), getString(R.string.waiting_for_location));
        updateCurrentLocationOnMap();
    }

    private void stopRequestingLocationUpdates() {
        if (isRequestingLocationUpdates) {
            isRequestingLocationUpdates = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        }
    }

    private void updateCurrentLocationOnMap() {
        if (mCurrentLocation != null) {
            LatLng clickedLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.clear();
            mCurrentLocationMarker = new MarkerOptions().position(clickedLocation).title("Current Location");
            mGoogleMap.addMarker(mCurrentLocationMarker);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLocation, 18.0f), 1000, null);
        }
    }

    private void updateRedZoneOnMap() {
        List<Marker> mEventsMarker = new ArrayList<Marker>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 100; // offset from edges of the map in pixels
        mGoogleMap.clear();
        mEventsMarker.clear();
        for (TempPlace places : listZones) {
            LatLng location = new LatLng(places.latitude, places.longitude);
            MarkerOptions markerOption = new MarkerOptions().position(location).title(places.name);
            Marker newMarker = mGoogleMap.addMarker(markerOption);
            mEventsMarker.add(newMarker);
            builder.include(newMarker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.animateCamera(cu, 1000, null);
        enableRedZoneInterface();
    }

    private void enableRedZoneInterface() {
        mImgLevel.setVisibility(View.VISIBLE);
        mImgLevel.setBackgroundResource(R.drawable.anim_red);
        mAnimZone = (AnimationDrawable) mImgLevel.getBackground();
        mAnimZone.start();
    }

    private void enableGreenZoneInterface() {
        mImgLevel.setVisibility(View.VISIBLE);
        mImgLevel.setBackgroundResource(R.drawable.anim_green);
        mAnimZone = (AnimationDrawable) mImgLevel.getBackground();
        mAnimZone.start();
    }

    private void updateGreenZoneOnMap() {
        List<Marker> mEventsMarker = new ArrayList<Marker>();
        mGoogleMap.clear();
        mEventsMarker.clear();
        LatLng clickedLocation = new LatLng(mLocationRemoteArea.latitude, mLocationRemoteArea.longitude);
        mGoogleMap.clear();
        mCurrentLocationMarker = new MarkerOptions().position(clickedLocation).title("Current Location");
        mGoogleMap.addMarker(mCurrentLocationMarker);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLocation, 14.0f), 1000, null);
        enableGreenZoneInterface();
    }

    private ResultCallback<LocationSettingsResult> mLocationResultCallback = new ResultCallback<LocationSettingsResult>() {

        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            final LocationSettingsStates states = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can
                    // initialize location
                    // requests here.
                    startRequestingLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed
                    // by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling
                        // startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(Home.this, REQUEST_CHECK_LOCATION_SETTINGS);
                    } catch (SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no
                    // way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        }
    };
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location changedLocation) {
            mCurrentLocation = changedLocation;
            updateCurrentLocationOnMap();
        }
    };

    private OnClickListener mOnclickListener = new OnClickListener() {

        @Override
        public void onClick(View clickedView) {
            switch (clickedView.getId()) {
                case R.id.txtGreen :{
                    updateGreenZoneOnMap();
                }
                break ;
                case R.id.txtRed :{
                    updateRedZoneOnMap();
                }
                break ;
                default:
                    break;
            }
        }
    };

    private void onGoogleApiDisabled() {
        isGoogleApiConnected = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_LOCATION_SETTINGS:
                onCheckLocationResult(resultCode, data);
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onCheckLocationResult(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                // All required changes were successfully made
                startRequestingLocationUpdates();
                break;
            case RESULT_CANCELED:
                // TODO proper finishing required
                finish();
                break;
            default:
                break;
        }

    }

    private void onGoogleApiEnabled(Bundle connectionHint) {
        isGoogleApiConnected = true;
        checkLocationServiceEnabled();
    }

    private OnConnectionFailedListener mConnectionFailedCallback = new OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult arg0) {
            onGoogleApiDisabled();
        }
    };
    private ConnectionCallbacks mGoogleApiCallback = new ConnectionCallbacks() {

        @Override
        public void onConnectionSuspended(int arg0) {
            onGoogleApiDisabled();

        }

        @Override
        public void onConnected(Bundle connectionHint) {
            onGoogleApiEnabled(connectionHint);
        }
    };

    private OnMapReadyCallback mOnMapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            setupGoogleMap(googleMap);
        }
    };

    private OnMyLocationButtonClickListener mOnMyLocationButtonClickListener = new OnMyLocationButtonClickListener() {

        @Override
        public boolean onMyLocationButtonClick() {
            // TODO Auto-generated method stub
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_hardcord1: {
                result = true;
                updateRedZoneOnMap();
            }
                break;
            case R.id.action_hardcord2: {
                result = true;
                updateGreenZoneOnMap();
            }
                break;
            default: {
                result = super.onOptionsItemSelected(item);
                ;
            }
                break;
        }
        return result;
    }

}
