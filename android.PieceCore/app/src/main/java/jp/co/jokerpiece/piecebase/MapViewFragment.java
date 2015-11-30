package jp.co.jokerpiece.piecebase;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.MapPositionListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.ShopListData;
import jp.co.jokerpiece.piecebase.data.ShopListData.ShopData;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by kaku on 2015/04/13.
 */
public class MapViewFragment extends Fragment implements OnItemClickListener ,LocationListener{
    private static final String TAG = MapViewFragment.class.getSimpleName();

    Context context;
    ShopListData shopData ;
    ShopListAdapter adapter;
    //    MapController m_controller;
    LocationManager mLocationManager;
    LocationProvider provider;
    private float zoom;

    private GoogleMap aMap;
    private static View rootView;

    private int providerNum = 0;

    ArrayList<ShopData> addshop = null;

    double longitude;
    double latitude;

    double old_latitude;
    double old_longitude;

    private ListView lv;

//    private LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            AppUtil.debugLog(TAG, "latitude: " + location.getLatitude());
//            AppUtil.debugLog(TAG, "longitude: " + location.getLongitude());
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };

    public MapViewFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

//        if(Config.MapFragmentNum == 0) {
//            if (Config.Savelist.size() == 1) {
//                Config.Savelist.clear();
//                Config.Savelist.add(0);
//            }
//            if (!Config.Backflg) {
//                if (Config.FragmentCurrentNum != 0) {
//                    Config.Savelist.add(Config.MapFragmentNum);
//                    Config.FragmentCurrentNum += 1;
//                }
//            }
//        }else{
//            if(!Config.Backflg) {
//                Config.Savelist.add(Config.MapFragmentNum);
//                Config.FragmentCurrentNum += 1;
//            }
//        }
        Common.setCurrentFragment(Config.MapFragmentNum);
        if(rootView != null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            // ListViewの設定
            if(parent != null){
                parent.removeView(rootView);
            }
        }
        try{
            rootView = inflater.inflate(R.layout.fragment_map, container,false);
        }catch (InflateException e){
        }

        context = getActivity();
        lv = (ListView) rootView.findViewById(R.id.shop_list_view);
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if(aMap == null){
            aMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();

        }
        aMap.setMyLocationEnabled(true);
        zoom = 16.0f;
//        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (location != null) {
//
//            old_latitude=location.getLatitude();
//            old_longitude=location.getLongitude();
//            AppUtil.debugLog("old","lat :  "+old_latitude);
//            AppUtil.debugLog("old","long :  "+old_longitude);
//
//            this.onLocationChanged(location);
//        }

//        LatLng lat = new LatLng(35, 135);
//        if(aMap == null){
//            aMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
//        }
//
//        if(aMap != null){
//            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lat, 16, 0, 0)));
//        }

//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        provider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);

        getMapPositionListData();

        return rootView;
    }



    public void getMapPositionListData(){

        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<ShopListData>() {
            @Override
            public Loader<ShopListData> onCreateLoader(int id, Bundle args) {
                // shop_idの取得
                String shopId = "";
                if (args != null) {
                    shopId = args.getString("shopId");
                }

                MapPositionListAPI mapAPI = new MapPositionListAPI(context, shopId);
                mapAPI.forceLoad();
                return mapAPI;
            }
            @Override
            public void onLoadFinished(Loader<ShopListData> loader, ShopListData data) {
                if(data == null){
                    Common.serverErrorMessage(context);
                    return;
                }
                if(data != null){
                    if(shopData == null){
                        shopData = data;
                    }else{
                        shopData.more_flg = data.more_flg;
                        //itemData.data_list.addAll(data.data_list);
                        addshop = data.data_list;

                    }
                }
                showShopView();

            }
            @Override
            public void onLoaderReset(Loader<ShopListData> loader) {

            }
        });
    }

    private void showShopView() {

        if(shopData.data_list != null && shopData.data_list.size() >= 1){
            if(adapter == null){
                adapter = new ShopListAdapter(context,
                        R.layout.adapter_shop_list,shopData.data_list);
                lv.setAdapter(adapter);
                lv.setVisibility(View.VISIBLE);
                lv.setOnItemClickListener(this);
//                lv.setOnScrollListener(this);
            }else{
                adapter.addAll(addshop);
                addshop = null;
            }

        }else{
            //lv.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        criteria.setCostAllowed(false);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);	// 低消費電力
//        String providerName = mLocationManager.getBestProvider(criteria, true);

        if(mLocationManager != null) {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }
        Location location = getLocationByGPS();
        if(location == null) {
            location = getLocationByNetwork();
        }
        if(location == null) {
            // Location が取得できなかった旨の Toast を出力するとか
        } else {
            onLocationChanged(location);
        }
        if(location != null) {
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
//        if (mLocationManager != null) {
//            // ネットワークから取得を開始する
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        }

//        if (mLocationManager != null) {
//            mLocationManager.requestLocationUpdates(
////                    providerName,
//                LocationManager.GPS_PROVIDER,
//                    10000,
//                    10,
//                    this);
//        }


        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(MapViewFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();
//		if(infoListData == null){

//		}
    }

    @Override
    public void onPause() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }

        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ShopListData.ShopData data = shopData.data_list.get(position);

        longitude = Double.parseDouble(data.longitude);
        latitude = Double.parseDouble(data.latitude);
        LatLng latLng = new LatLng(latitude, longitude);

        if(aMap != null){
            // aMap.addMarker(new MarkerOptions().position(latLng).title("INFLOAT Co.,Ltd."));
            aMap.addMarker(new MarkerOptions().position(latLng));
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(latitude,longitude))
                    .zoom(zoom)
                    .bearing(0).build();
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
//            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 16, 0, 0)));
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        AppUtil.debugLog("----------", "----------");
        AppUtil.debugLog("Latitude", String.valueOf(location.getLatitude()));
        AppUtil.debugLog("Longitude", String.valueOf(location.getLongitude()));
        AppUtil.debugLog("Accuracy", String.valueOf(location.getAccuracy()));
        AppUtil.debugLog("Altitude", String.valueOf(location.getAltitude()));
        AppUtil.debugLog("Time", String.valueOf(location.getTime()));
        AppUtil.debugLog("Speed", String.valueOf(location.getSpeed()));
        AppUtil.debugLog("Bearing", String.valueOf(location.getBearing()));


        LatLng lat = new LatLng(location.getLatitude(), location.getLongitude());

        if(aMap != null){
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(lat)
                    .zoom(zoom)
                    .bearing(0).build();
            //aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                AppUtil.debugLog("Status", "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                AppUtil.debugLog("Status", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                AppUtil.debugLog("Status", "TEMPORARILY_UNAVAILABLE");
                break;
        }


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Location getLocationByGPS() {
        if(mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        return null;
    }


    private Location getLocationByNetwork() {
        if(mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return null;
    }

}
