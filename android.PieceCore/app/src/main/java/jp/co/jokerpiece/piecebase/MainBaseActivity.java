package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BeaconUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebViewFragment;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * (注意)
 * MainActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
 * で設定している箇所はタブの遷移のみを行っています。
 * データの受け渡しに関しては未実装です。
 * "Shopping"の部分はタブに設定しているクラスのクラス名を含む文字列を設定すればOK。
 * AppUtil.getPositionメソッドが-1で返ってくる場合は何も起こりません。
 */
public class MainBaseActivity extends FragmentActivity implements OnTabChangeListener {
    private static final String TAG = MainBaseActivity.class.getSimpleName();

    private Context context;
    public static FragmentTabHost tabHost;

    public String myTheme = "";
    public TabColorState tabColorState;

    public ArrayList<HashMap<String, Object>> settingData = new ArrayList<HashMap<String,Object>>();
    public static ArrayList<TabInfo> tabInfoList;
    public static HashMap<String, Integer> titleOfActionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setTheme();
        setContentView(R.layout.activity_main);

        settingData = setConfig();
        titleOfActionBar = setTitleOfActionBar();
        tabInfoList = setTabInfoList();
        tabColorState = setTabColorState();

        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this, getSupportFragmentManager(), R.id.real_content);

        for (int i = 0; i < tabInfoList.size(); i++) {
            addTab(tabInfoList.get(i));
        }

        tabHost.setOnTabChangedListener(this);

        // クリックイベントを設定する
        Log.d("numberOfTabs=", "" + tabHost.getTabWidget().getChildCount());
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN
                            && v.equals(tabHost.getCurrentTabView())) {
                        getCurrentRootFragment().getChildFragmentManager()
                                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment, Fragment.instantiate(
                                context,
                                tabInfoList.get(tabHost.getCurrentTab()).cls.getName()));
                        ft.commit();
                        return true;
                    }
                    return false;
                }
            });
        }

        /**
         * IS_BEACON_ENABLEDがtrueの場合はビーコン処理を実行する
         */
        if (Config.IS_BEACON_ENABLED) {
            // ビーコン処理の初期化
            BeaconUtil.init(this);
            // ビーコン検索処理
            BeaconUtil.startScan();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        runIfGetIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runIfGetIntent(getIntent());

        // プッシュ通知処理の初期化
        Common.setupGcm(context, (Activity) context, Config.loaderCnt++);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                //スタックを戻る。
                if(!getCurrentRootFragment().popBackStack()){
                    return true;
                }else{
                    return false;
                }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BeaconUtil.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetoothの自動ONが成功した場合呼ばれる
                    Log.d(TAG, "Bluetoothの自動ONに成功しました。");
                    BeaconUtil.isGetBluetoothAdapter = true;
                    BeaconUtil.startScan();
                }
                break;
        }
    }

    /**
     * アプリのテーマを設定する。
     * overrideする場合は、以下の通りに実装する。
     *      1.myTheme = "[default/cute]";
     *      2.super.setTheme();
     * super.setThemeはmyThemeを設定してから呼んでください。
     */
    public void setTheme() {
        // Theme setting
        switch (myTheme) {
            case "cute":
                setTheme(R.style.AppTheme_cute);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    /**
     * タブに設定する内容を宣言する。
     */
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
                },
                new HashMap<String, Object>() {
                    { put("tabTitle", getString(R.string.map1)); }
                    { put("tabIcon", R.drawable.icon_map); }
                    { put("cls", MapViewFragment.class); }
                },
                new HashMap<String, Object>() {
                    { put("tabTitle", getString(R.string.barcode1)); }
                    { put("tabIcon", R.drawable.icon_map); }
                    { put("cls", BarcodeFragment.class); }
                }
        ));
    }

    /**
     * 画面のアクションバータイトルに対応するリソースを設定する。
     */
    public HashMap<String, Integer> setTitleOfActionBar() {
        return new HashMap<String, Integer>() {
            { put(FlyerFragment.class.getSimpleName(), R.string.flyer0); }
            { put(InfomationFragment.class.getSimpleName(), R.string.info0); }
            { put(InfomationSyosaiFragment.class.getSimpleName(), R.string.info0); }
            { put(ShoppingFragment.class.getSimpleName(), R.string.shopping0); }
            { put(ShoppingGoodsFragment.class.getSimpleName(), R.string.item_list); }
            { put(CouponFragment.class.getSimpleName(), R.string.coupon_get0); }
            { put(CouponUseFragment.class.getSimpleName(), R.string.coupon_use0); }
            { put(WebViewFragment.class.getSimpleName(), R.string.webview); }
            { put(FittingFragment.class.getSimpleName(), R.string.fitting0); }
            { put(MapViewFragment.class.getSimpleName(), R.string.map0); }
            { put(BarcodeFragment.class.getSimpleName(), R.string.barcode0); }
        };
    }

    /**
     * tabに設定する情報を設定する。
     */
    @SuppressWarnings("unchecked")
    public ArrayList<TabInfo> setTabInfoList() {
        ArrayList<TabInfo> tabInfoList = new ArrayList<TabInfo>();
        for (int i = 0; i < settingData.size(); i++) {
            HashMap<String, Object> data = settingData.get(i);
            tabInfoList.add(new TabInfo(
                    "TAB" + i,
                    (String) data.get("tabTitle"),
                    (Integer) data.get("tabIcon"),
                    (Class<? extends Fragment>) data.get("cls")));
        }
        return tabInfoList;
    }

    /**
     * タブに設定するTabColorStateを取得する。<br>
     * (TabColorStateのコンストラクタ)<br>
     * (1)bgSelected: 選択時の背景<br>
     * (2)bgUnselected: 非選択時の背景<br>
     * (3)ftSelected: 選択時のフォント色<br>
     * (4)ftUnselected: 非選択時のフォント色<br>
     * (例)<br>
     * return new TabColorState(<br>
     *      Color.argb(0xff, 0xcc, 0xcc, 0xcc),<br>
     *      Color.argb(0xff, 0xff, 0xff, 0xff),<br>
     *      Color.argb(0xff, 0x66, 0x66, 0x66),<br>
     *      Color.argb(0xff, 0x66, 0x66, 0x66));<br>
     * (注意)<br>
     * 設定しない場合は-1を設定する。<br>
     * 但し、「(1)と(2)」「(3)と(4)」はセットです。
     * @return タブに設定するTabColorState
     */
    public TabColorState setTabColorState() {
        return new TabColorState(
                -1,
                -1,
                -1,
                -1);
    }

        /**
         * タブを追加する。
         **/
        public void addTab(TabInfo tabInfo) {
            View childView = new CustomTabContentView(this, tabInfo.title, tabInfo.resId);
            TabSpec tabSpec = tabHost.newTabSpec(tabInfo.tag).setIndicator(childView);
            Bundle args = new Bundle();
            args.putString("root", tabInfo.cls.getName());
            tabHost.addTab(tabSpec, RootFragment.class, args);
        }

        /**
         * タブ情報を保持しておくクラス。
         */
        public class TabInfo {
            public String tag;
            public String title;
            public int resId;
            public Class<? extends Fragment> cls;

            public TabInfo(String tag, String title, int resId, Class<? extends Fragment> cls) {
                this.tag = tag;
                this.title = title;
                this.resId = resId;
                this.cls = cls;
            }
        }

        /**
         * 現在タブに設定されているフラグメントを取得する。
         **/
        private RootFragment getCurrentRootFragment() {
            return (RootFragment) getSupportFragmentManager().findFragmentById(R.id.real_content);
        }

        @Override
        public void onTabChanged(String tabId) {
//		// バックスタックのクリーンアップ
//		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.fragment, Fragment.instantiate(
//                context,
//                tabInfoList.get(tabHost.getCurrentTab()).cls.getName()));
//        ft.commit();
        }

        /**
         * TabWidget用の独自Viewを作ります。
         */
        public class CustomTabContentView extends FrameLayout {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public CustomTabContentView(Context context) {
            super(context);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public CustomTabContentView(Context context, String title, int resId) {
            this(context);
            View childview = inflater.inflate(R.layout.tab_widget, null);
            LinearLayout ll = (LinearLayout) childview.findViewById(R.id.ll_tab);
            ImageView iv = (ImageView) childview.findViewById(R.id.iv_tab);
            iv.setImageResource(resId);
            TextView tv = (TextView) childview.findViewById(R.id.tv_tab);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            tv.setText(title);

            // Theme setting
            switch (myTheme) {
                case "cute":
                    ll.setBackgroundResource(R.drawable.shape_tabbtn_cute);
                    tv.setTextColor(getResources().getColor(R.color.tab_text_color_cute));
                    break;
                default:
                    ll.setBackgroundResource(R.drawable.shape_tabbtn);
                    tv.setTextColor(getResources().getColor(R.color.tab_text_color_cute));
                    break;
            }

            // Tab colorstate
            if (tabColorState != null) {
                // 背景
                if (tabColorState.bgSelected != -1 && tabColorState.bgUnselected != -1) {
                    Drawable pressed = new ColorDrawable(Color.argb(0x90, 0x8e, 0xb7, 0xff));
                    Drawable selected = new ColorDrawable(tabColorState.bgSelected);
                    Drawable unselected = new ColorDrawable(tabColorState.bgUnselected);

                    StateListDrawable d = new StateListDrawable();
                    d.addState(new int[]{android.R.attr.state_pressed}, pressed);
                    d.addState(new int[]{android.R.attr.state_selected}, selected);
                    d.addState(new int[]{-android.R.attr.state_focused}, unselected);

                    int sdkVersion = android.os.Build.VERSION.SDK_INT;
                    if (sdkVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        ll.setBackgroundDrawable(d);
                    } else {
                        ll.setBackground(d);
                    }
                }

                // フォント色
                if (tabColorState.ftSelected != -1 && tabColorState.ftUnselected != -1) {
                    ColorStateList c = new ColorStateList(
                            new int[][]{
                                    new int[]{android.R.attr.state_selected},
                                    new int[]{-android.R.attr.state_focused}
                            },
                            new int[]{
                                    tabColorState.ftSelected,
                                    tabColorState.ftUnselected
                            });
                    tv.setTextColor(c);
                }
            }

            addView(childview);
        }
    }

    /**
     * タブに設定するcolorstateを保持する。<br>
     * (1)bgSelected: 選択時の背景<br>
     * (2)bgUnselected: 非選択時の背景<br>
     * (3)ftSelected: 選択時のフォント色<br>
     * (4)ftUnselected: 非選択時のフォント色<br>
     */
    public class TabColorState {
        /** 選択時の背景 */
        public int bgSelected;
        /** 非選択時の背景 */
        public int bgUnselected;
        /** 選択時のフォント色 */
        public int ftSelected;
        /** 非選択時のフォント色 */
        public int ftUnselected;

        /**
         * コンストラクタ
         */
        public TabColorState(int bgSelected, int bgUnselected,
                              int ftSelected, int ftUnselected) {
            this.bgSelected = bgSelected;
            this.bgUnselected = bgUnselected;
            this.ftSelected = ftSelected;
            this.ftUnselected = ftUnselected;
        }
    }

    /**
     * getIntent()で取得できた場合の処理を記述します。
     * 本メソッドをonResume()で呼んでいるのは、launchModeがsingleTaskの場合、
     * メインアクティビティを起動中はonCreate()は呼ばれずonResume()から呼ばれるため。
     */
    public void runIfGetIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.getString("type") != null) {
            switch (bundle.getString("type")) {
                case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Infomation"));
//    			FragmentManager fmInfo = getSupportFragmentManager();
//    			FragmentTransaction ftInfo = fmInfo.beginTransaction();
//    			ftInfo.addToBackStack(null);
//    			InfomationSyosaiFragment fragmentInfo = new InfomationSyosaiFragment();
//    			Bundle bundleInfo = new Bundle();
//    			bundleInfo.putString("newsId", bundle.getString("newsId"));
//    			fragmentInfo.setArguments(bundleInfo);
//    			ftInfo.replace(R.id.fragment, fragmentInfo);
//    			ftInfo.commit();
                    break;
                case NewsListData.NEWS_DATA_TYPE_FLYER + "":
                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Flyer"));
//    			FragmentManager fmNews = getSupportFragmentManager();
//    			FragmentTransaction ftNews = fmNews.beginTransaction();
//    			ftNews.addToBackStack(null);
//    			FlyerFragment fragmentNews = new FlyerFragment();
//    			Bundle bundleNews= new Bundle();
//    			bundleNews.putString("flyer_ID", bundle.getString("flyer_ID"));
//    			fragmentNews.setArguments(bundleNews);
//    			ftNews.replace(R.id.fragment, fragmentNews);
//    			ftNews.commit();
                    break;
                case NewsListData.NEWS_DATA_TYPE_COUPON + "":
                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Coupon"));
//    			FragmentManager fmCoupon = getSupportFragmentManager();
//    			FragmentTransaction ftCoupon = fmCoupon.beginTransaction();
//    			ftCoupon.addToBackStack(null);
//    			CouponFragment fragmentCoupon = new CouponFragment();
//    			Bundle bundleCoupon= new Bundle();
//    			bundleCoupon.putString("coupon_code", bundle.getString("coupon_code"));
//    			fragmentCoupon.setArguments(bundleCoupon);
//    			ftCoupon.replace(R.id.fragment, fragmentCoupon);
//    			ftCoupon.commit();
                    break;
                default:
                    break;
            }
        }
    }

}
