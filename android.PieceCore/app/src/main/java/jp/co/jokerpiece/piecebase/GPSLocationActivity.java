package jp.co.jokerpiece.piecebase;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.jokerpiece.piecebase.api.GetLocationAPI;
import jp.co.jokerpiece.piecebase.util.AppUtil;

public class GPSLocationActivity extends Activity implements LocationListener {
    private Handler handler = new Handler();
    int loderCount = 0;

    String order_id;
    int type = -1;
    private GoogleMap aMap = null;
    private float zoom;
    LocationManager mLocationManager;
    GPSLocationTimerTask timerTask = null;
    Location senderLocation = null;
    Timer mTimer = null;
    Marker senderMarker;

    boolean getStartPositon;

    View senderPositionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        order_id = getIntent().getStringExtra("order_id");
        type = getIntent().getIntExtra("type", -1);

        if (aMap == null) {
            aMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        }
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });
        zoom = 18.0f;

        findViewById(R.id.MyPosition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMyPosition();
            }
        });

        senderPositionButton = findViewById(R.id.SenderPosition);
        senderPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSenderPosition();
            }
        });
        senderPositionButton.setVisibility(View.INVISIBLE);

        Location location = getLocationByGPS();
        if (location != null) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
            getStartPositon = true;
        }
    }

    private void onClickMyPosition() {
        Location location = aMap.getMyLocation();
        if (location != null) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
    }

    private void onClickSenderPosition() {
        if (senderLocation != null) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(senderLocation.getLatitude(), senderLocation.getLongitude()))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        timerTask = new GPSLocationTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule(timerTask, 0, 60000);
    }

    @Override
    protected void onStop() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onStop();
    }

    class GPSLocationTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    registLocation();
                }
            });
        }
    }

    private void registLocation() {
        final Location location = getLocationByGPS();
        if (location != null) {
            getLoaderManager().initLoader(loderCount++, null, new LoaderManager.LoaderCallbacks<GetLocationAPI.LocationData>() {
                @Override
                public Loader<GetLocationAPI.LocationData> onCreateLoader(int id, Bundle args) {
                    GetLocationAPI getLocationAPI = new GetLocationAPI(GPSLocationActivity.this, order_id, location.getLatitude(), location.getLongitude(), type);
                    getLocationAPI.forceLoad();
                    return getLocationAPI;
                }

                @Override
                public void onLoadFinished(Loader<GetLocationAPI.LocationData> loader, GetLocationAPI.LocationData data) {
//                    data = new GetLocationAPI.LocationData();
//                    data.lat = 35.53133414;
//                    data.lng = 134.69262857;
//                    data.updated = "2015:11:21 12:00:00";
                    if (data != null && data.updated != null) {
                        if (senderMarker != null) {
                            senderMarker.remove();
                            senderMarker = null;
                        }
                        senderLocation = new Location("sender");
                        senderLocation.setLatitude(data.lat);
                        senderLocation.setLongitude(data.lng);

                        MarkerOptions options = new MarkerOptions();
                        options.title(data.updated);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.footprints);
                        options.icon(icon);
                        options.position(new LatLng(data.lat, data.lng));

                        senderMarker = aMap.addMarker(options);
                        senderMarker.showInfoWindow();
                        senderPositionButton.setVisibility(View.VISIBLE);
                    } else {
                        if(data != null && data.error_msg != null){
                            Toast.makeText(GPSLocationActivity.this, data.error_msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(GPSLocationActivity.this, "相手の位置情報を取得できませんでした。", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<GetLocationAPI.LocationData> loader) {
                }
            });
        } else {
            Toast.makeText(GPSLocationActivity.this, "位置情報を取得できませんでした。", Toast.LENGTH_SHORT).show();
        }
    }


    private Location getLocationByGPS() {
        Location result = null;
        if(aMap != null) {
            result = aMap.getMyLocation();
        }
        if (result == null && mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    result = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (result == null) {
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                        result = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
        AppUtil.debugLog("onLocationChanged",location.getLatitude()+":" + location.getLongitude());
        if (!getStartPositon) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
            getStartPositon = true;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        AppUtil.debugLog("onStatusChanged",provider + ":" + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        AppUtil.debugLog("onProviderEnabled",provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        AppUtil.debugLog("onProviderDisabled",provider);
    }
}
