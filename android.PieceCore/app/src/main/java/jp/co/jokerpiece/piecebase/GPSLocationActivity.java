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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class GPSLocationActivity extends Activity implements LocationListener {
    private Handler handler = new Handler();
    int loderCount = 0;
    private GoogleMap aMap = null;
    private float zoom;
    LocationManager mLocationManager;
    GPSLocationTimerTask timerTask = null;
    Location senderLocation = null;
    Timer mTimer = null;
    Marker senderMarker;

    View senderPositionButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

        Location location = getLocationByGPS();
        if (location != null) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        client.connect();
        timerTask = new GPSLocationTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule(timerTask, 0, 60000);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GPSLocation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://jp.co.jokerpiece.piecebase/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GPSLocation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://jp.co.jokerpiece.piecebase/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
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
                    GetLocationAPI getLocationAPI = new GetLocationAPI(GPSLocationActivity.this, "", location.getLatitude(), location.getLongitude(), 0);
                    getLocationAPI.forceLoad();
                    return getLocationAPI;
                }

                @Override
                public void onLoadFinished(Loader<GetLocationAPI.LocationData> loader, GetLocationAPI.LocationData data) {
//                    data = new GetLocationAPI.LocationData();
//                    data.lat = 35.53133414;
//                    data.lng = 134.69262857;
//                    data.updated = "2015:11:21 12:00:00";
                    if (data != null) {
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
                        Toast.makeText(GPSLocationActivity.this, "相手の位置情報を取得できませんでした。", Toast.LENGTH_SHORT).show();
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
        if (mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
