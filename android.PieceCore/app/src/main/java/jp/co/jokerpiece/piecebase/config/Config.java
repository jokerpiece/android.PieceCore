package jp.co.jokerpiece.piecebase.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.jokerpiece.piecebase.MainBaseActivity;
import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.data.MenuData;
import jp.co.jokerpiece.piecebase.util.App;

import android.content.Context;

public class Config {
    // --- staticイニシャライザで定数を設定(ここから) --- //
    public static final String APP_ID;
    public static final String APP_KEY;
    public static final int SPLASH_TIME;
    public static final String SERVER_URL;
    public static final String PAYPAL_CLIENT_ID;
    public static final String PAYPAL_ENVIRONMENT;
    public static final String PAYPAL_BNCODE;
    public static final String PAY_SELECT_KBN;
    public static final String DELIVERY;
//  public static String APP_ID = "pieceSample";
//  public static final String APP_ID = "pushColor";
//	public static final String APP_ID = "otonagokoro";
//	public static final String APP_ID = "kodomogokoro";
    // --- staticイニシャライザで定数を設定(ここまで) --- //

    /**
     * ビーコン処理を有効にするかどうか
     * (true:有効、false:無効)
     */
    public static final boolean IS_BEACON_ENABLED;
    public static boolean changeToDevFlg = false;

	//新大人ゴコロ本番

   // public  static final String SERVER_URL = "http://192.168.77.200/piece_dev/";

	//大人ゴコロ本番
	//public static final String SERVER_API_URL = "http://jokapi.jp/PieceProxy/service/api/shop/kodomogokoro/";
	//大人ゴコロステージング
	//public static final String SERVER_API_URL = "http://home.noumin.net/PieceProxy/service/api/shop/kodomogokoro/";
	//public static final String SERVER_API_URL = "http://192.168.77.68/piece/html/xml/";

	//http://192.168.77.68/piece/html/xml/device_token/index.php
	//スタブ
	//public static final String SERVER_API_URL = "http://192.168.77.83:8888/pieceSever/";

	//デバッグ用UUID デバッグではない場合nullに;
	//public static final String getDebugUUID = "jokerpiece-test-uuid";
	public static final String getDebugUUID = null;

    //OSタイプ (1:iOS, 2:android)
    public static final String OS_TYPE = "2";

	//サーバー接続までのタイムアウト時間
	public static final int connectionTimeOut = 5000;

	//サーバー接続後の読み込みタイムアウト時間
	public static final int readTimeOut = 30000;

	// プッシュ通知で必要なフィールド(ここから)
//	public static final String PROJECT_ID = "367759414941";	//プロジェクトIDを設定する
    public static final String PROJECT_ID;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // プッシュ通知で必要なフィールド(ここまで)
	//検索機能対応
	public static final boolean SEARCH_PERMISSION = true;

    public  static final String SERVER_API_URL;
	public static final String USER_NAME;
	public static final String SENDID_NEWS_LIST;
	public static final String SENDID_NEWS_INFO;
	public static final String SENDID_FLIYER_LIST;
	public static final String SENDID_CTGRY;
	public static final String SENDID_ITEM;
	public static final String SENDID_ITEM_COUPON;
	public static final String SENDID_ITEM_BARCODE;
	public static final String SENDID_CPN_GIVE;
	public static final String SENDID_CPN_TAKE;
	public static final String SENDID_GET_CPN;
    public static final String SENDID_GET_QUES;
	public static final String PUSHSERVER_URL;
    public static final String SENDID_MAP_LIST;
    public static final String SENDID_STAMP_LIST;
    public static final String SENDID_CHECKDATA;
    public static final String SENDID_GETFILEDATA;

    public static final String SENDID_GET_LOCATION;

    public static final String SENDID_REGIST_QUESTION;
    public static final String SENDID_GET_QUESTION;
    public static final String QUESTION_FINISH;

    public static final String SENDID_YOUTUBE_UPLOAD;
    public static final String SENDID_YOUTUBE_UPLOAD_DONE;

    public static final String SENDID_MESSAGE_UPLOAD;
    public static final String SENDID_MESSAGE_UPLOAD_DONE;
    public static final String SENDID_REGUST_APP_PAYMENT;

    public static final String YOUTUBE_APIV3 = "https://www.googleapis.com/upload/youtube/v3/videos?part=snippet,status";


	public static final String PASS_WORD = null;

	public static final long scrollDelay = 4000;

    //使ったフラグメントを記録するための設定
    public static int FragmentCurrentNum;
    public static int FlyerFragmentNum;
    public static int InfoFragmentNum;
    public static int ShoppingFragmentNum;
    public static int CouponFragmentNum;
    public static int FittingFragmentNum;
    public static int MapFragmentNum;
    public static int BarcodeFragmentNum;
    public static int StampFragmentNum;
    public static int SnsFragmentNum;
    public static int WebViewFragmentNum;
    public static int TwitterFragmentNum;
    public static int PaypalFragmentNum;
    public static int ResoultFragmentNum;

    public static List Savelist = new ArrayList<>();
    public static  Class<?> TwitterBackToMain = MainBaseActivity.class;

    public static boolean Backflg = false;
    //クーポンURLを持ってるかどうかのフラグ
    public static boolean haveUrlFlg = false;
    public static final String COOKIE_DOMAIN;

    public static String CONSUMER_KEY = "N1qd3k7y8mT73CZ2hAVxh4JTr";
    public static String CONSUMER_SECRET = "uQqLoh6lV9KQn6F7o6JyRiQkDueNNPIlrR8GwsmW7P95Q8OZ8d";
    public static String ACCESS_TOKEN = "3245559282-ncb4SSrwITCEPkgFscWpewRopIBRMQWE2cbj0eU";
    public static String ACCESS_SECRET = "CpcWqrWVR3E0fbDPxLJs5GRpGM2HUg7i6iDYruH1L9HXX";
    public static String URL;


    public static final String PREF_KEY = "SETTINGS";
    public static final String PREF_URL = "url";

    // ImageBackとnextの位置をどこに配置する設定
    public static int PositionForWebView;
    final public static int Top = 0;
    final public static int Center = 1;
    final public static int Bottom = 2;

    /** loaderCount */
	public static int loaderCnt = 0;
	/** タブ画面で戻るボタン押下時の動作 */
	public static final String BACK_STACK_KEY = "BACK_STACK";

	static public List<MenuData> getMenuList(Context context) {
		ArrayList<MenuData> dataList = new ArrayList<>();
		dataList.add(new MenuData(0,R.drawable.icon_flyer,context.getResources().getString(R.string.flyer0)));
		dataList.add(new MenuData(1,R.drawable.icon_infomation,context.getResources().getString(R.string.info)));
		dataList.add(new MenuData(2,R.drawable.icon_shopping,context.getResources().getString(R.string.shopping)));
		dataList.add(new MenuData(3,R.drawable.icon_coupon,context.getResources().getString(R.string.coupon)));
		return dataList;
	}

    static {
        boolean isSuccess = true;
        HashMap<String, String> map = new HashMap<>();
        try {
            ArrayList<String> lineList = new ArrayList<>();

            InputStream is = App.getContext().getResources().getAssets().open(
                    "settingFile.txt");
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String s;
            while ((s = br.readLine()) != null) {
                lineList.add(s);
            }
            br.close();
            isr.close();
            is.close();

            for (String line : lineList) {
                if (line.length() == 0) { continue; }
                String[] values = line.split("=");
                if (values[0] != null && !values[0].equals("") && values.length >= 2) {
                    map.put(values[0], values[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;

        }
        if (isSuccess) {
            String serverUrl = map.get("server_url");
            if (serverUrl != null && !serverUrl.equals("")) {
                SERVER_URL = serverUrl;
            } else {
                SERVER_URL = "http://jokapi.jp/";
            }

            String appId = map.get("app_id");
            if (appId != null && !appId.equals("")) {
                APP_ID = appId;
            } else {
                APP_ID = "pieceSample";
            }

            String appKey = map.get("app_key");
            if (appKey != null && !appKey.equals("")) {
                APP_KEY = appKey;
            } else {
                APP_KEY = "jokerpiece_appKey";
            }
            String cookieDomain = map.get("cookie_domain");
            if (cookieDomain != null && !cookieDomain.equals("")) {
                COOKIE_DOMAIN = cookieDomain;
            } else {
                COOKIE_DOMAIN = "t.otonagokoro.com";
            }
            int splashTime;
            try {
                splashTime = Integer.valueOf(map.get("splash_time"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                splashTime = 2;
            }
            SPLASH_TIME = splashTime;

            String projectId = map.get("project_id");
            if (projectId != null && !projectId.equals("")) {
                PROJECT_ID = projectId;
            } else {
                PROJECT_ID = "367759414941";
            }

            String beaconEnabled = map.get("beacon_isEnabled");
            if (beaconEnabled != null && !beaconEnabled.equals("")) {
                IS_BEACON_ENABLED = Boolean.valueOf(beaconEnabled).booleanValue();
            } else {
                IS_BEACON_ENABLED = false;
            }

            String paypalClientId = map.get("paypal_clientId");
            if (paypalClientId != null && !paypalClientId.equals("")) {
                PAYPAL_CLIENT_ID = paypalClientId;
            } else {
                PAYPAL_CLIENT_ID = "credential from developer.paypal.com";
            }

            String paypalEnvironment = map.get("paypal_environment");
            if (paypalEnvironment != null && !paypalEnvironment.equals("")) {
                PAYPAL_ENVIRONMENT = paypalEnvironment;
            } else {
                PAYPAL_ENVIRONMENT = "mock";
            }

            String paypalBncode = map.get("paypal_bncode");
            if (paypalBncode != null && !paypalBncode.equals("")) {
                PAYPAL_BNCODE = paypalEnvironment;
            } else {
                PAYPAL_BNCODE = "";
            }

            String pay_select_kbn = map.get("pay_select_kbn");
            if (pay_select_kbn != null && !pay_select_kbn.equals("")) {
                PAY_SELECT_KBN = pay_select_kbn;
            } else {
                PAY_SELECT_KBN = "";
            }

            String delivery = map.get("delivery");
            if (delivery != null && !delivery.equals("") ) {
                DELIVERY = delivery;
            } else {
                DELIVERY = "0";
            }


        } else {
            SERVER_URL = "http://jokapi.jp/";
            APP_ID = "pieceSample";
            APP_KEY = "jokerpiece_appKey";
            COOKIE_DOMAIN = "t.otonagokoro.com";
            SPLASH_TIME = 2;
            PROJECT_ID = "367759414941";
            IS_BEACON_ENABLED = false;
            PAYPAL_CLIENT_ID =  "credential from developer.paypal.com";
            PAYPAL_ENVIRONMENT =  "mock";
            PAYPAL_BNCODE = "";
            PAY_SELECT_KBN = "";
            DELIVERY = "0";
        }

        SERVER_API_URL = SERVER_URL + "manager/html/xml/";
        USER_NAME = SERVER_API_URL + "home_list/";
        SENDID_NEWS_LIST = SERVER_API_URL + "news/";
        SENDID_NEWS_INFO = SERVER_API_URL + "news/index.php?Action=newsDetail";
        SENDID_FLIYER_LIST = SERVER_API_URL + "flyer/";
        SENDID_CTGRY = SERVER_API_URL + "category/";
        SENDID_ITEM = SERVER_API_URL + "item/index.php?Action=itemList";
        SENDID_ITEM_COUPON = SERVER_API_URL + "item/index.php?Action=itemList";
        SENDID_ITEM_BARCODE = SERVER_API_URL + "item/index.php?Action=itemListBarcode";
        SENDID_CPN_GIVE = SERVER_API_URL + "coupon/index.php?Action=giveList";
        SENDID_CPN_TAKE = SERVER_API_URL + "coupon/index.php?Action=takedList";
        SENDID_GET_CPN  = SERVER_API_URL + "coupon/index.php?Action=get";
        SENDID_GET_QUES  = SERVER_API_URL + "fitting/";
        PUSHSERVER_URL = SERVER_API_URL + "device_token/";
        SENDID_MAP_LIST = SERVER_API_URL + "shop/";
        SENDID_STAMP_LIST = SERVER_API_URL + "stamp/";
        SENDID_CHECKDATA = SERVER_API_URL + "order/?Action=check";
        SENDID_GETFILEDATA = SERVER_API_URL + "order/?Action=getFileData";

        SENDID_GET_LOCATION = SERVER_API_URL + "order/?Action=getLocation";

        SENDID_REGIST_QUESTION = SERVER_URL + "manager/html/webview/padlock_regist.html";
        SENDID_GET_QUESTION = SERVER_URL + "manager/html/webview/padlock_answer.html";
        QUESTION_FINISH = "/manager/html/webview/padlock_finish.html";

        SENDID_YOUTUBE_UPLOAD = SERVER_URL +"manager/html/webview/youtube_upload/index.php";
        SENDID_YOUTUBE_UPLOAD_DONE = SERVER_URL + "manager/html/webview/youtube_upload/done.html";

        SENDID_MESSAGE_UPLOAD = SERVER_URL + "manager/html/webview/message/index.php";
        SENDID_MESSAGE_UPLOAD_DONE = SERVER_URL +"manager/html/webview/message/done.html";
        SENDID_REGUST_APP_PAYMENT = SERVER_API_URL + "regist_app_payment";
    }

}
