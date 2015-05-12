### Pieceプロジェクトの作成
AndroidStudioでPieceプロジェクト(以下、本プロジェクト)を作成して下さい。
ランチャーアクティビティは、「jp.co.jokerpiece.piece.MainActivity」となります。
ライブラリと重複するリソースは、本プロジェクトが優先されます。
android.PieceCoreライブラリに「drawable/ic_launcher.png」は用意しているので本プロジェクトのic_launcherファイルはすべて削除して下さい。

#### google-play-services_libライブラリのインポート
"Piece/app/build.gradle"内のdependenciesで「compile 'com.google.android.gms:play-services:+'」を実行することでライブラリを読み込んでいます。

#### android.PieceCoreライブラリのインポート
"Piece/app/build.gradle"内のmavenでurlを指定し、dependenciesで「compile 'jp.co.jokerpiece.android.piececore:android.piececore:0.0.+@aar'」を実行することでライブラリを読み込んでいます。

![Sync Project](./mdImage/syncProj.png)ボタンを押下でビルドを実行し、![Run Project](./mdImage/runProj.png)ボタンを押下でアプリを実行して下さい。

### app_idの設定

"Piece/app/src/main/assets"フォルダのsettingFile.txtに本アプリ設定内容を記述して下さい。
下記にsettingFile.txtの記述例を記します。

settingFile.txt

    app_id=pieceSample
    app_key=jokerpiece_appKey
    splash_time=3
    project_id=367759414941
    beacon_isEnabled=false

### 利用方法

MainActivityは、ライブラリにある「jp.co.jokerpiece.piecebase.MainBaseActivity」をextendsする必要があります。
追加するタブは、setConfig()メソッドをOverrideすることで、実装することができます。

setConfig()メソッドでは、ArrayList<HashMap<String, Object>>を戻り値として返す必要があります。
リストの数がタブの数となり、タブの中に設定する設定内容はHashMapで設定することができます。
以下にHashMapで設定できるキーとバリューを示します。

HashMap<String, Object>

| キー | 説明 | バリュー |
| --- | --- | --- |
| tabTitle | タブに表示するタイトル | getString(R.string.flyer1), getString(R.string.info1), getString(R.string.shopping1), getString(R.string.coupon1), getString(R.string.fitting1),  getString(R.string.map1)|
| tabIcon | タブに設定する画像 | R.drawable.icon_flyer, R.drawable.icon_infomation, R.drawable.icon_shopping, R.drawable.icon_coupon, R.drawable.icon_fitting, R.drawable.icon_map |
| cls | タブに設定する画面 | FlyerFragment.class, InfomationFragment.class, ShoppingFragment.class, CouponFragment.class, FittingFragment.class, MapViewFragment.class |

下記にbuild.gradle、AndroidManifest.xml、styles.xml、MainActivity、GcmBroadcastReceiver、GcmIntentServiceの記述例を記します。

build.gradle

    apply plugin: 'com.android.application'

    android {
        compileSdkVersion 21
        buildToolsVersion "21.1.2"

        defaultConfig {
            applicationId "jp.co.jokerpiece.piece"
            minSdkVersion 14
            targetSdkVersion 21
            versionCode 1
            versionName "1.0.0"
        }
        buildTypes {
            release {
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }
    }

    repositories {
        mavenCentral()
        maven {
            url 'https://raw.github.com/jokerpiece/android.PieceCore/master/android.PieceCore/app/repository'
        }
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:21.0.0'
        compile 'com.google.android.gms:play-services:+'
        compile 'jp.co.jokerpiece.android.piececore:android.piececore:0.0.16@aar'
    }

AndroidManifest.xml

    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="jp.co.jokerpiece.piece"
        android:versionCode="1"
        android:versionName="1.0" >

        <uses-sdk
            android:minSdkVersion="14"
            android:targetSdkVersion="21" />

        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

        <!-- プッシュ通知の設定(ここから) -->
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <uses-permission android:name="android.permission.GET_ACCOUNTS" />
        <uses-permission
            android:name="${applicationId}.permission.RECEIVE" />
        <permission
            android:name="${applicationId}.permission.C2D_MESSAGE"
            android:protectionLevel="signature" />
        <uses-permission
            android:name="${applicationId}.permission.C2D_MESSAGE" />
        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <!-- プッシュ通知の設定(ここまで) -->

        <!-- ライブラリのアプリケーションを使用するため -->
        <!-- "jp.co.jokerpiece.piecebase.util.App"を設定する必要があります -->
        <application
            android:allowBackup="true"
            android:icon="@drawable/ic_launcher"
            android:name="jp.co.jokerpiece.piecebase.util.App"
            android:theme="@style/AppTheme"
            android:label="@string/app_name"
            android:launchMode="singleTop">
            
             <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="@string/map_key"/>
            <activity
                android:name=".MainActivity"
                android:screenOrientation="portrait"
                android:launchMode="singleTask">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />
                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>

            <!-- プッシュ通知に必要なアクティビティ(ここから) -->
            <meta-data
                android:name="com.google.android.gms.version"
                android:value="@integer/google_play_services_version"/>
            <service android:name=".GcmIntentService" android:enabled="true"/>
            <receiver
                android:name=".GcmBroadcastReceiver"
                android:permission="com.google.android.c2dm.permission.SEND" >
                <intent-filter>
                    <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                    <category android:name="${applicationId}" />
                </intent-filter>
            </receiver>
            <!-- プッシュ通知に必要なアクティビティ(ここまで) -->

        </application>

    </manifest>

styles.xml

    <resources>

        <!-- Base application theme. -->
        <style name="AppTheme" parent="android:Theme.WithActionBar">
            <!-- Customize your theme here. -->
        </style>

    </resources>


MainActivity.java

    package jp.co.jokerpiece.piece;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.HashMap;

    import jp.co.jokerpiece.piecebase.CouponFragment;
    import jp.co.jokerpiece.piecebase.FlyerFragment;
    import jp.co.jokerpiece.piecebase.InfomationFragment;
    import jp.co.jokerpiece.piecebase.MainBaseActivity;
    import jp.co.jokerpiece.piecebase.R;
    import jp.co.jokerpiece.piecebase.ShoppingFragment;
    import android.os.Bundle;

    public class MainActivity extends MainBaseActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public ArrayList<HashMap<String, Object>> setConfig() {
            return new ArrayList<HashMap<String,Object>>(Arrays.asList(
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.flyer1)); }
                            { put("tabIcon", R.drawable.icon_flyer); }
                            { put("cls", FlyerFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.info1)); }
                            { put("tabIcon", R.drawable.icon_infomation); }
                            { put("cls", InfomationFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.shopping1)); }
                            { put("tabIcon", R.drawable.icon_shopping); }
                            { put("cls", ShoppingFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.coupon1)); }
                            { put("tabIcon", R.drawable.icon_coupon); }
                            { put("cls", CouponFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.fitting1)); }
                            { put("tabIcon", R.drawable.icon_fitting); }
                            { put("cls", FittingFragment.class); }
                    }
            ));
        }

    }

GcmBroadcastReceiver.java

    package jp.co.jokerpiece.piece;
    
    import android.app.Activity;
    import android.content.ComponentName;
    import android.content.Context;
    import android.content.Intent;
    import android.support.v4.content.WakefulBroadcastReceiver;
    
    public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    
        @Override
        public void onReceive(Context context, Intent intent) {
    
            // Explicitly specify that GcmMessageHandler will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmIntentService.class.getName());
    
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }

GcmIntentService.java

    package jp.co.jokerpiece.piece;
    
    import android.content.Intent;
    import android.util.Log;
    
    import java.util.HashMap;
    
    import jp.co.jokerpiece.piecebase.data.NewsListData;
    
    public class GcmIntentService extends jp.co.jokerpiece.piecebase.GcmIntentService {
        public static final String TAG = GcmIntentService.class.getSimpleName();
    
        @Override
        public Intent getSelectedIntent(HashMap<String, String> map) {
            Intent intent = null;
            switch (getBlankIfNull(map.get("type"))) {
                case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", getBlankIfNull(map.get("type")));
                    intent.putExtra("newsId", getBlankIfNull(map.get("id")));
                    Log.d(TAG, "type: Infomation, newsId: " + getBlankIfNull(map.get("id")));
                    break;
                case NewsListData.NEWS_DATA_TYPE_FLYER + "":
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", getBlankIfNull(map.get("type")));
                    intent.putExtra("flyer_ID", getBlankIfNull(map.get("id")));
                    Log.d(TAG, "type: Flyer, flyer_ID: " + getBlankIfNull(map.get("id")));
                    break;
                case NewsListData.NEWS_DATA_TYPE_COUPON + "":
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("type", getBlankIfNull(map.get("type")));
                    intent.putExtra("coupon_code", getBlankIfNull(map.get("id")));
                    Log.d(TAG, "type: Coupon, coupon_code: " + getBlankIfNull(map.get("id")));
                    break;
                default:
                    intent = new Intent(this, MainActivity.class);
                    break;
            }
            return intent;
        }
    }

### 機能クラス

Pieceで提供している機能と紐づくクラス名は下記の通りです。

|名前 | 説明 |
| --- | --- |
|FlyerFragment.class | フライヤー |
|InfomationFragment.class | お知らせ一覧 |
|ShoppingFragment.class | ショッピング |
|CouponFragment.class | クーポン |
|FittingFragment.class | フィッティング |
|MapViewFragment.class | マップ |


### PieceSampleプロジェクト

github(<https://github.com/jokerpiece/android.PieceCore>)からPieceSampleプロジェクトをコピーして下さい。
PieceSampeプロジェクトはPieceアプリの最小構成となっています。
ランチャーアクティビティは、「jp.co.jokerpiece.piece.MainActivity」となります。
AndroidStudioからPieceSampleプロジェクトを起動して下さい。
![Sync Project](./mdImage/syncProj.png)ボタンを押下でビルドを実行し、![Run Project](./mdImage/runProj.png)ボタンを押下でアプリを実行して下さい。

### プッシュ通知のアイコン設定

drawable-ldpi、drawable-mdpi、drawable-hdpi、drawable-xhdpiにstatus_icon.pngという名前の画像ファイルを設定して下さい。
画像サイズは下記URLを参考にして下さい。

* 4.1.4 ステータスバー アイコン<br>(<http://www.techdoctranslator.com/android/practices/ui_guidelines/icon_design/icon_design_status_bar>)
