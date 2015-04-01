package jp.co.jokerpiece.piecebase.config;

import java.util.ArrayList;
import java.util.List;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.data.MenuData;
import android.content.Context;

public class Config {
	public static final String APP_ID = "otonagokoro";
//	public static final String APP_ID = "kodomogokoro";
	//新大人ゴコロ本番
	public static final String SERVER_URL = "http://jokapi.jp/manager/html/xml/";

	//大人ゴコロ本番
	//public static final String SERVER_URL = "http://jokapi.jp/PieceProxy/service/api/shop/kodomogokoro/";
	//大人ゴコロステージング
	//public static final String SERVER_URL = "http://home.noumin.net/PieceProxy/service/api/shop/kodomogokoro/";
	//public static final String SERVER_URL = "http://192.168.77.68/piece/html/xml/";

	//http://192.168.77.68/piece/html/xml/device_token/index.php
	//スタブ
	//public static final String SERVER_URL = "http://192.168.77.83:8888/pieceSever/";

	//デバッグ用UUID デバッグではない場合nullに;
	public static final String getDebugUUID = "jokerpiece-test-uuid";
	//public static final String getDebugUUID = null;

	//サーバー接続までのタイムアウト時間
	public static final int connectionTimeOut = 5000;

	//サーバー接続後の読み込みタイムアウト時間
	public static final int readTimeOut = 30000;

	// プッシュ通知で必要なフィールド(ここから)
	public static final String PROJECT_ID = "367759414941";	//プロジェクトIDを設定する
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // プッシュ通知で必要なフィールド(ここまで)
	//検索機能対応
	public static final boolean SEARCH_PERMISSION = true;
	public static final String USER_NAME = SERVER_URL + "home_list/";
	public static final String SENDID_NEWS_LIST = SERVER_URL + "news/";
	public static final String SENDID_NEWS_INFO = SERVER_URL + "news/index.php?Action=newsDetail";
	public static final String SENDID_FLIYER_LIST = SERVER_URL + "flyer/";
	public static final String SENDID_CTGRY = SERVER_URL + "category/";
	public static final String SENDID_ITEM = SERVER_URL + "item/index.php?Action=itemList";
	public static final String SENDID_ITEM_COUPON = SERVER_URL + "item/index.php?Action=itemList";
	public static final String SENDID_ITEM_BARCODE = SERVER_URL + "item/index.php?Action=itemListBarcode";
	public static final String SENDID_CPN_GIVE = SERVER_URL + "coupon/index.php?Action=giveList";
	public static final String SENDID_CPN_TAKE = SERVER_URL + "coupon/index.php?Action=takedList";
	public static final String SENDID_GET_CPN  = SERVER_URL + "coupon/index.php?Action=get";
	public static final String PUSHSERVER_URL = SERVER_URL + "device_token/";
	public static final String PASS_WORD = null;

	public static final long scrollDelay = 4000;

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
}
