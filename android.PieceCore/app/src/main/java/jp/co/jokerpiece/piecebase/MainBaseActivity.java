package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.WebViewFragment;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

    public static FragmentTabHost tabHost;

    protected ArrayList<HashMap<String, Object>> settingData = new ArrayList<HashMap<String,Object>>();
    public static ArrayList<TabInfo> tabInfoList;
    protected static HashMap<String, String> titleOfActionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingData = setConfig();
        titleOfActionBar = setTitleOfActionBar();
        tabInfoList = setTabInfoList();

        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this, getSupportFragmentManager(), R.id.real_content);

        for (int i = 0; i < tabInfoList.size(); i++) {
            addTab(tabInfoList.get(i));
        }

        tabHost.setOnTabChangedListener(this);

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null && bundle.getString("type") != null) {
//            switch (bundle.getString("type")) {
//                case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
//                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Infomation"));
////    			FragmentManager fmInfo = getSupportFragmentManager();
////    			FragmentTransaction ftInfo = fmInfo.beginTransaction();
////    			ftInfo.addToBackStack(null);
////    			InfomationSyosaiFragment fragmentInfo = new InfomationSyosaiFragment();
////    			Bundle bundleInfo = new Bundle();
////    			bundleInfo.putString("newsId", bundle.getString("newsId"));
////    			fragmentInfo.setArguments(bundleInfo);
////    			ftInfo.replace(R.id.fragment, fragmentInfo);
////    			ftInfo.commit();
//                    break;
//                case NewsListData.NEWS_DATA_TYPE_FLYER + "":
//                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Flyer"));
////    			FragmentManager fmNews = getSupportFragmentManager();
////    			FragmentTransaction ftNews = fmNews.beginTransaction();
////    			ftNews.addToBackStack(null);
////    			FlyerFragment fragmentNews = new FlyerFragment();
////    			Bundle bundleNews= new Bundle();
////    			bundleNews.putString("flyer_ID", bundle.getString("flyer_ID"));
////    			fragmentNews.setArguments(bundleNews);
////    			ftNews.replace(R.id.fragment, fragmentNews);
////    			ftNews.commit();
//                    break;
//                case NewsListData.NEWS_DATA_TYPE_COUPON + "":
//                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Coupon"));
////    			FragmentManager fmCoupon = getSupportFragmentManager();
////    			FragmentTransaction ftCoupon = fmCoupon.beginTransaction();
////    			ftCoupon.addToBackStack(null);
////    			CouponFragment fragmentCoupon = new CouponFragment();
////    			Bundle bundleCoupon= new Bundle();
////    			bundleCoupon.putString("coupon_code", bundle.getString("coupon_code"));
////    			fragmentCoupon.setArguments(bundleCoupon);
////    			ftCoupon.replace(R.id.fragment, fragmentCoupon);
////    			ftCoupon.commit();
//                    break;
//                default:
//                    break;
//            }
//        }
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
//				new HashMap<String, Object>() {
//					{ put("tabTitle", getString(R.string.shopping1)); }
//					{ put("tabIcon", R.drawable.icon_shopping); }
//					{ put("cls", ShoppingFragment.class); }
//				},
                new HashMap<String, Object>() {
                    { put("tabTitle", getString(R.string.coupon1)); }
                    { put("tabIcon", R.drawable.icon_coupon); }
                    { put("cls", CouponFragment.class); }
                }
        ));
    }

    /**
     * 画面のアクションバータイトルに対応するリソースを設定する。
     */
    public HashMap<String, String> setTitleOfActionBar() {
        return new HashMap<String, String>() {
            { put(FlyerFragment.class.getSimpleName(), getString(R.string.flyer0)); }
            { put(InfomationFragment.class.getSimpleName(), getString(R.string.info0)); }
            { put(InfomationSyosaiFragment.class.getSimpleName(), getString(R.string.info0)); }
            { put(ShoppingFragment.class.getSimpleName(), getString(R.string.shopping0)); }
            { put(ShoppingGoodsFragment.class.getSimpleName(), getString(R.string.item_list)); }
            { put(CouponFragment.class.getSimpleName(), getString(R.string.coupon_get0)); }
            { put(CouponUseFragment.class.getSimpleName(), getString(R.string.coupon_use0)); }
            { put(WebViewFragment.class.getSimpleName(), getString(R.string.webview)); }
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
//        switch (tabId) {
//        case "TAB1":
//            ft.replace(R.id.fragment, Fragment.instantiate(this, InfomationFragment.class.getName()));
//            break;
//        case "TAB2":
//            ft.replace(R.id.fragment, Fragment.instantiate(this, CouponFragment.class.getName()));
//            break;
//        default:
//            break;
//        }
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

        @SuppressLint("InflateParams")
        public CustomTabContentView(Context context, String title, int resId) {
            this(context);
            View childview = inflater.inflate(R.layout.tab_widget, null);
            ImageView iv = (ImageView) childview.findViewById(R.id.iv_tab);
            iv.setImageResource(resId);
            TextView tv = (TextView) childview.findViewById(R.id.tv_tab);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            tv.setText(title);

            addView(childview);
        }
    }

}
