package jp.co.jokerpiece.piecebase.config;

import java.io.IOException;
import java.util.UUID;

import jp.co.jokerpiece.piecebase.MainBaseActivity;
import jp.co.jokerpiece.piecebase.api.DeviceTokenAPI;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Common {
	private static Context context = null;
	public static String getUUID(Context context) {

		String uuid = Config.getDebugUUID;
		if(uuid != null){
			return uuid;
		}
		SharedPreferences defaultPre = PreferenceManager.getDefaultSharedPreferences(context);
		uuid = defaultPre.getString("uuid", null);
		if(uuid == null){
			uuid = UUID.randomUUID().toString();
			Editor editor = defaultPre.edit();
			editor.putString("uuid", uuid);
			editor.commit();
		}
		return uuid;
	}


	public static void errorMessage(final Context context, final int errorCode, final String errorMes) {
		if (Thread.currentThread().equals(context.getMainLooper().getThread())) {
			errorMessage(context,errorCode,null,errorMes);
		} else {
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					errorMessage(context,errorCode,null,errorMes);
				}
			});
		}
	}

	public static void errorMessage(Context context,int errorCode,String errorTitle,String errorMes) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	if(errorTitle != null){
    		builder.setTitle(errorTitle);
    	}
    	if(errorMes != null){
    		builder.setMessage(errorMes);
    	}
        builder.setPositiveButton("OK", null);
        builder.create().show();
	}

	public static void serverErrorMessage(final Context context) {
		if (Thread.currentThread().equals(context.getMainLooper().getThread())) {
			errorMessage(context,0,"通信エラー","サーバーとの通信に失敗しました");
		} else {
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					errorMessage(context,0,"通信エラー","サーバーとの通信に失敗しました");
				}
			});
		}
	}


    // プッシュ通知で必要な内容(ここから)
    // Play serviceが有効かチェック
    private static GoogleCloudMessaging gcm;
    private static String regid = "";
    private static Activity activity = null;
    private static int loaderID = -1;
	public static void setupGcm(Context context,Activity activity,int loaderID){
		Common.context = context;
		Common.activity = activity;
		Common.loaderID = loaderID;
        if (checkPlayServices()) {

            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            if(regid.equals("")){
                regist_id();
            } else {
            	registDeviceToken();
            }

        } else {
            Log.i("", "Google Play Services は無効");
        }
	}

	private static boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                		Config.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("", "Play Service not support");
            }
            return false;
        }
        return true;
    }


    private static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Config.PROPERTY_REG_ID, "");
        if (regid.equals("")) {
            return "";
        }
        // アプリケーションがバージョンアップされていた場合、レジストレーションIDをクリア
        int registeredVersion = prefs.getInt(Config.PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }

        return registrationId;
    }


    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(MainBaseActivity.class.getSimpleName(),
        		Context.MODE_PRIVATE);
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
            		.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("package not found : " + e);
        }
    }


    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.PROPERTY_REG_ID, regId);
        editor.putInt(Config.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

	public static AsyncTask<Void, Void, String> registtask = null;

    private static void regist_id(){
        if (regid.equals("")) {
            registtask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    try {
                        //GCMサーバーへ登録
                        regid = gcm.register(Config.PROJECT_ID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!regid.equals("")) {
	                    //取得したレジストレーションIDを自分のサーバーへ送信して記録しておく
	                    //サーバーサイドでは、この レジストレーションIDを使ってGCMに通知を要求します
	                    registDeviceToken();

	                    // レジストレーションIDを端末に保存
	                    storeRegistrationId(context, regid);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    registtask = null;
                }
            };
            registtask.execute(null, null, null);
        }

    }

	private static void registDeviceToken(){
		activity.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<Boolean>(){
			@Override
			public Loader<Boolean> onCreateLoader(int id, Bundle args) {
				DeviceTokenAPI registAPI = new DeviceTokenAPI(context, regid);
				registAPI.forceLoad();
				 return registAPI;
				}

			@Override
			public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
				if(data == false){
					Common.serverErrorMessage(context);
					return;
				}
				// ここにデータ取得時の処理を書く
			}

			@Override
			public void onLoaderReset(Loader<Boolean> loader) {
			}
        });
	}
    // プッシュ通知で必要なメソッド(ここまで)
}
