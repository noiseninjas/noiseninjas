package com.noiseninjas.android.app.ui.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.noiseninjas.android.app.R;
import com.noiseninjas.android.app.globals.NinjaApp;
import com.noiseninjas.android.app.service.GeoCoderIntentService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddPlaceActivity extends BaseActivity {

    public static final String EXTRA_LAT_LNG = "extra_lat_lng";
    public static final String EXTRA_PLACE_ID = "place_id";
    public static final String EXTRA_VIEW_TYPE = "view_type";
    private static final int TYPE_ADD = 501;
    private static final int TYPE_UPDATE = 502;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
    private SupportMapFragment mMapFragment = null;
    private GoogleMap mGoogleMap = null;
    private LatLng mCurrentLocation = null;
    private AddreddResultReceiver mAddressReceiver = null;
    private EditText mEdtTitle = null;
    private EditText mEdtMessage = null;
    private TextView mTxtAddress = null;
    private Button mBtnAddPlacce = null;
    private EditText mEdtRadius = null;
    private boolean isRequestingAddress = false;
    private int mGeoCodeResult = GeoCoderIntentService.RESULT_FAILURE;
    private String mGeoCodedAddress = null;
    private Toolbar mToolbar = null;
    private int mCurrentViewType = TYPE_ADD;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean isGoogleApiConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataFromIntent();
        checkDataAndInflate();
    }

    private void initComponents() {
        mAddressReceiver = new AddreddResultReceiver(new Handler());
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(mGoogleApiCallback)
                .addOnConnectionFailedListener(mConnectionFailedCallback).addApi(LocationServices.API).build();
    }

    private void onGoogleApiEnabled(Bundle connectionHint) {
        isGoogleApiConnected = true;
    }

    private void onGoogleApiDisabled() {
        isGoogleApiConnected = false;
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

    private void checkDataAndInflate() {
        if (isValidData()) {
            initComponents();
            setContentView(R.layout.activity_mark_place);
            initViews();
            setUpView();
            checkAndUpdateLocation();
        } else {
            NinjaApp.showGenericToast(getBaseContext(), getString(R.string.failure));
            finish();
        }

    }

    private void checkAndUpdateLocation() {
        if (mCurrentViewType == TYPE_ADD) {
            requestAddressForLocation();
        }

    }

    private void setUpView() {
        updateViewWithPlace();

    }

    private void updateViewWithPlace() {
        mEdtMessage.setText("");
        mEdtTitle.setText("");
    }

    private void requestAddressForLocation() {
        isRequestingAddress = true;
        GeoCoderIntentService.launchGeocodeService(getBaseContext(), mAddressReceiver, mCurrentLocation);

    }

    private void setupLiteMap() {
        mMapFragment = SupportMapFragment.newInstance(getMapOptionsLite());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.flMapContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(mOnMapReadyCallBack);

    }

    private void setupGoogleMapLite(GoogleMap googleMap, LatLng placeLatLng) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.addMarker(new MarkerOptions().position(placeLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 18.0f));
    }

    private GoogleMapOptions getMapOptionsLite() {
        GoogleMapOptions mMapOptions = new GoogleMapOptions();
        mMapOptions.compassEnabled(false);
        mMapOptions.mapToolbarEnabled(false);
        mMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapOptions.rotateGesturesEnabled(false);
        mMapOptions.zoomControlsEnabled(false);
        mMapOptions.liteMode(true);
        return mMapOptions;
    }

    private OnMapReadyCallback mOnMapReadyCallBack = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            setupGoogleMapLite(googleMap, mCurrentLocation);
        }

    };

    private ResultCallback<Status> mGeofenceResultCallback = new ResultCallback<Status>() {

        @Override
        public void onResult(Status arg0) {

        }
    };

    private boolean isValidData() {
        boolean result = false;
        if (mCurrentViewType == TYPE_ADD) {
            result = mCurrentLocation != null;
        } 
        return result;
    }

    private void initViews() {
        mEdtMessage = (EditText) findViewById(R.id.edtMessage);
        mEdtTitle = (EditText) findViewById(R.id.edtTitle);
        mEdtRadius = (EditText) findViewById(R.id.edtRadius);
        mTxtAddress = (TextView) findViewById(R.id.txtAddress);
        mBtnAddPlacce = (Button) findViewById(R.id.btnDone);
        mBtnAddPlacce.setOnClickListener(mOnClickListener);
        mEdtMessage.setOnEditorActionListener(mOnEditorActionListener);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupLiteMap();
        initActionBar();
    }

    private void initActionBar() {
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void updateAddressUI(int resultCode, Bundle resultData) {
        mGeoCodedAddress = resultData.getString(GeoCoderIntentService.EXTRA_RESULT_DATA);
        mTxtAddress.setText(mGeoCodedAddress);
    }

    private void initDataFromIntent() {
        mCurrentLocation = getIntent().getParcelableExtra(EXTRA_LAT_LNG);
        mCurrentViewType = getIntent().getIntExtra(EXTRA_VIEW_TYPE, TYPE_ADD);
        buildGoogleApiClient();

    }

    private boolean isTitleValid() {
        String title = mEdtTitle.getText().toString();
        return isValidString(title);
    }

    private boolean isMessageValid() {
        String title = mEdtMessage.getText().toString();
        return isValidString(title);
    }

    private boolean isValidString(String title) {
        return title != null && !title.trim().isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickAddPlace() {
        if (isTitleValid()) {
            if (isMessageValid()) {
                addOrUpdatePlaceData();

            } else {
                mEdtMessage.setError(getString(R.string.error_enter_data));
            }
        } else {
            mEdtTitle.setError(getString(R.string.error_enter_data));

        }

    }

    private void addOrUpdatePlaceData() {

    }

    private String getAddress() {
        return mGeoCodedAddress;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.edtMessage:
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onClickAddPlace();
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    };
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View clickedView) {
            switch (clickedView.getId()) {
                case R.id.btnDone:
                    onClickAddPlace();
                    break;
                default:
                    break;
            }
        }

    };

    private class AddreddResultReceiver extends ResultReceiver {

        public AddreddResultReceiver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            isRequestingAddress = false;
            mGeoCodeResult = resultCode;
            updateAddressUI(resultCode, resultData);
            super.onReceiveResult(resultCode, resultData);
        }

    }

    public static void launchAddPlace(Context context, LatLng mCurrentLocation) {
        Intent addPlaceIntent = new Intent(context, AddPlaceActivity.class);
        addPlaceIntent.putExtra(AddPlaceActivity.EXTRA_LAT_LNG, mCurrentLocation);
        context.startActivity(addPlaceIntent);
    }

}
