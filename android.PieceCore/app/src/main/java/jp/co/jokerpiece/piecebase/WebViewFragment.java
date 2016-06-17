package jp.co.jokerpiece.piecebase;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends BaseFragment implements View.OnClickListener {
    private WebView webView;
    private ImageView ivBack;
    private ImageView ivNext;
    private ImageView ivBackCenter;
    private ImageView ivNextCenter;
    private ImageView ivBackBottom;
    private ImageView ivNextBottom;
    //ImageBackとNextを表示するかどうか
    private ImageView imgBackFlg;
    private ImageView imgNextFlg;
    private ImageButton imgReload;
    private TextView tv;
    private TextView error_Msg;
    public boolean ConnectFailed = false;

    String S_Url;
    LinearLayout ll;
    public WebViewFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //Google Analytics
        if(Config.ANALYTICS_MODE.equals("true")){
            App app = (App)getActivity().getApplication();
            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
            t.setScreenName("WEB VIEW");
            t.send(new HitBuilders.ScreenViewBuilder().build());

        }

            View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
            ll = (LinearLayout) rootView.findViewById(R.id.base_webview);
            //conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            setMode();
            findViews(rootView);
            webView = (WebView) rootView.findViewById(R.id.webview);
            Bundle bundle = getArguments();
            if (bundle != null) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        ConnectFailed = false;

                        //Google Analytics
                        if(Config.ANALYTICS_MODE.equals("true")){
                            App app = (App)getActivity().getApplication();
                            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
                            t.setScreenName(webView.getUrl());
                            t.send(new HitBuilders.ScreenViewBuilder().build());

                        }


                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
//                        if (ConnectFailed) {
//                            //error_Msg.setVisibility(View.VISIBLE);
//                            imgReload.setVisibility(View.VISIBLE);
//                            tv.setVisibility(View.VISIBLE);
//                        } else {
                        //error_Msg.setVisibility(View.GONE);
//                            imgReload.setVisibility(View.GONE);
//                            tv.setVisibility(View.GONE);
//                        }
                        if (!webView.canGoBack()) {
                            imgBackFlg.setVisibility(View.GONE);
                        } else {
                            imgBackFlg.setVisibility(View.VISIBLE);
                        }
                        if (!webView.canGoForward()) {
                            imgNextFlg.setVisibility(View.GONE);
                        } else {
                            imgNextFlg.setVisibility(View.VISIBLE);
                        }

//            	ActionBar ab = getActionBar();
//            	String title = webView.getTitle();
//            	ab.setTitle(title);
                    }
//
//                    @Override
//                    public void onReceivedError(final WebView webview, int errorCode, String description, String failingUrl) {
//                        ConnectFailed = true;
//                        if (Common.CheckNetWork(getActivity())) {
//                            String s = getErrorMsg(errorCode);
//                            error_Msg.setText(s);
//                        } else {
//                            error_Msg.setText("通信できませんでした。\n電波状態をお確かめ下さい。");
//                        }
//                        imgReload.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                webView.reload();
//                                imgReload.setVisibility(View.GONE);
//                                tv.setVisibility(View.GONE);
//                            }
//                        });
//                    }
                });

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                String strUrl = bundle.getString("send_url");
                String cookieString = "uuid="+ Common.getUUID(getContext());

                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.setCookie(Config.COOKIE_DOMAIN, cookieString);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }else {
                    CookieSyncManager.getInstance().sync();
                }

                if (strUrl != null && !strUrl.equals("")) {
                    webView.loadUrl(strUrl);
                }
            } else {

                if(SaveData.ConnectFlg){
                    SaveData.ConnectFailed = SaveData.ConnectFlg;
                }
                setWebView(SaveData.Wb, getActivity(), SaveData.ConnectFailed);
                SaveData.ConnectFailed = SaveData.ConnectFlg;
                ViewGroup parent = (ViewGroup) webView.getParent();
                if (parent != null) {
                    parent.removeView(webView);
                }
                ViewGroup parent2 = (ViewGroup) SaveData.Wb.getParent();
                if (parent2 != null) {
                    parent2.removeView(SaveData.Wb);
                }
                ll.addView(SaveData.Wb);

            }
//        rootView.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // KeyEvent.ACTION_DOWN以外のイベントを無視する
//                // （これがないとKeyEvent.ACTION_UPもフックしてしまう）
//                if(event.getAction() != KeyEvent.ACTION_DOWN) {
//                    return false;
//                }
//
//                switch(keyCode) {
//    	        case KeyEvent.KEYCODE_BACK:
//   				 	if(webView.canGoBack()){
//   				 		webView.goBack();
//   				 		return false;
//   				 	}else{
//   				 		return true;
//   				 	}
//   				 default:
//   					 return false;
//                }
//            }
//        });
//        // View#setFocusableInTouchModeでtrueをセットしておくこと
//        rootView.setFocusableInTouchMode(true);

            return rootView;

    }
//    public void setUrl(String url){
//        S_Url = url;
//    }


    public void setWebView(final WebView wv, final Activity activity,boolean con){
        SaveData.ConnectFlg = con;
//        if(con){
//            wv.reload();
//        }
//        wv.setVisibility(View.VISIBLE);
        //スプラッシュが終わってからSaveData.Wbの情報をチェックする
        if(SaveData.SplashIsFinished) {
            if (!wv.canGoBack()) {
                imgBackFlg.setVisibility(View.GONE);
            } else {
                imgBackFlg.setVisibility(View.VISIBLE);
            }
            if (!wv.canGoForward()) {
                imgNextFlg.setVisibility(View.GONE);
            } else {
                imgNextFlg.setVisibility(View.VISIBLE);
            }
//            if (SaveData.ConnectFlg) {
//                if (Common.CheckNetWork(activity)) {
//                    String s = getErrorMsg(SaveData.ErrorCode);
//                    error_Msg.setText(s);
//                } else {
//                    error_Msg.setText("通信できませんでした。\n電波状態をお確かめ下さい。");
//                }
//                imgReload.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        wv.reload();
//                        imgReload.setVisibility(View.GONE);
//                        tv.setVisibility(View.GONE);
//                    }
//                });
//                error_Msg.setVisibility(View.VISIBLE);
//                imgReload.setVisibility(View.VISIBLE);
//                tv.setVisibility(View.VISIBLE);
//            } else {
//                error_Msg.setVisibility(View.GONE);
//                imgReload.setVisibility(View.GONE);
//                tv.setVisibility(View.GONE);
//            }
        }
//        else {
//
//            if(SaveData.ConnectFlg) {
//                wv.setVisibility(View.GONE);
//                wv.reload();
//            }
//            //wv.onFinishTemporaryDetach();
//        }
//        if(SaveData.ConnectFlg) {
//            wv.setVisibility(View.GONE);
//            SaveData.ConnectFlg = false;
//            wv.reload();
//
//        }
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if(SaveData.ConnectFlg) {
                        //wv.setVisibility(View.GONE);
                        SaveData.ConnectFlg = false;
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (SaveData.ConnectFlg) {
//                        error_Msg.setVisibility(View.VISIBLE);
//                        imgReload.setVisibility(View.VISIBLE);
//                        tv.setVisibility(View.VISIBLE);
                        //SaveData.SplashIsFinished = false;
                        wv.setVisibility(View.VISIBLE);

                    } else {
//                        error_Msg.setVisibility(View.GONE);
//                        imgReload.setVisibility(View.GONE);
//                        tv.setVisibility(View.GONE);
                        //SaveData.SplashIsFinished = false;
                        wv.setVisibility(View.VISIBLE);

                    }
                    if (!wv.canGoBack()) {
                        imgBackFlg.setVisibility(View.GONE);
                    } else {
                        imgBackFlg.setVisibility(View.VISIBLE);
                    }
                    if (!wv.canGoForward()) {
                        imgNextFlg.setVisibility(View.GONE);
                    } else {
                        imgNextFlg.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
                    //ConnectFailed = SaveData.ConnectFailed;
                    SaveData.ConnectFlg = true;
                    if (SaveData.ConnectFlg) {
//                        if (Common.CheckNetWork(activity)) {
//                            String s = getErrorMsg(SaveData.ErrorCode);
//                            error_Msg.setText(s);
//                        } else {
//                            error_Msg.setText("通信できませんでした。\n電波状態をお確かめ下さい。");
//                        }
//                        imgReload.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                wv.reload();
//                                //SaveData.ConnectFlg = false;
//                                imgReload.setVisibility(View.GONE);
//                                tv.setVisibility(View.GONE);
//                            }
//                        });
                    }
                    SaveData.ErrorCode = errorCode;
                }
            });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(WebViewFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//		MenuItem haveCoupon = menu.add(0 , Menu.FIRST, Menu.NONE ,"閉じる");
//		haveCoupon.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		haveCoupon.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
////        inflater.inflate(R.menu.menu_coupon, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		switch (item.getItemId()) {
//		case Menu.FIRST://閉じる
//			//バックスタックの登録数をチェックして0であればPopUpは存在しない
//		     if (getFragmentManager().getBackStackEntryCount() != 0) {
//		    	 getFragmentManager().popBackStack();
//		     }
//			break;
//		}
//		return true;
//	}

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Bundle bundle = getArguments();

        if (i == R.id.iv_back || i == R.id.iv_backCenter || i == R.id.iv_backBottom) {
            if (bundle != null) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }else {
                if (SaveData.Wb.canGoBack()) {
                    SaveData.Wb.goBack();
                }
            }
        } else if (i == R.id.iv_next || i == R.id.iv_nextCenter || i == R.id.iv_nextBottom) {
            if (bundle != null) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }else {
                if (SaveData.Wb.canGoForward()) {
                    SaveData.Wb.goForward();
                }
            }
        }
    }
    //ivBackはどこに設置するか
    public void setMode(){
        Config.PositionForWebView = Config.Center;
    }
    public void setReload(WebView wv){
        wv.reload();
    }
    public void findViews(View rootView) {

        ivBack = (ImageView) rootView.findViewById(R.id.iv_back);
        ivNext = (ImageView) rootView.findViewById(R.id.iv_next);
        ivBackCenter = (ImageView) rootView.findViewById(R.id.iv_backCenter);
        ivNextCenter = (ImageView) rootView.findViewById(R.id.iv_nextCenter);
        ivBackBottom = (ImageView) rootView.findViewById(R.id.iv_backBottom);
        ivNextBottom = (ImageView) rootView.findViewById(R.id.iv_nextBottom);
//        imgReload = (ImageButton) rootView.findViewById(R.id.reload);
//        tv = (TextView)rootView.findViewById(R.id.tv);
//        error_Msg = (TextView) rootView.findViewById(R.id.errorMsg);


        switch(Config.PositionForWebView){
            case Config.Top:
                ivBackCenter.setVisibility(View.GONE);
                ivNextCenter.setVisibility(View.GONE);
                ivBackBottom.setVisibility(View.GONE);
                ivNextBottom.setVisibility(View.GONE);
                imgBackFlg = ivBack;
                imgNextFlg = ivNext;
                break;
            case Config.Center:
                ivBack.setVisibility(View.GONE);
                ivNext.setVisibility(View.GONE);
                ivBackBottom.setVisibility(View.GONE);
                ivNextBottom.setVisibility(View.GONE);
                imgBackFlg = ivBackCenter;
                imgNextFlg = ivNextCenter;
                break;
            case Config.Bottom:
                ivBackCenter.setVisibility(View.GONE);
                ivNextCenter.setVisibility(View.GONE);
                ivBack.setVisibility(View.GONE);
                ivNext.setVisibility(View.GONE);
                imgBackFlg = ivBackBottom;
                imgNextFlg = ivNextBottom;
                break;
        }
        //設定しないと最初の読み込みとき出てしまう
        imgBackFlg.setVisibility(View.GONE);
        imgNextFlg.setVisibility(View.GONE);
//        imgReload.setVisibility(View.GONE);
//        tv.setVisibility(View.GONE);
//        error_Msg.setVisibility(View.GONE);

        ivBack.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivBackCenter.setOnClickListener(this);
        ivNextCenter.setOnClickListener(this);
        ivBackBottom.setOnClickListener(this);
        ivNextBottom.setOnClickListener(this);
    }
    public String getErrorMsg(int ErrorCode){

        String Msg = "";
        switch(ErrorCode){
            case -2:
                Msg = "サーバーまたはプロキシのホスト名の検索に失敗しました";
                break;
            case -6:
                Msg = "サーバーへの接続に失敗しました";
                break;
            case -7:
                Msg = "読み取りまたはサーバへの書き込みに失敗しました";
                break;
            case -8:
                Msg = "接続がタイムアウトしました";
                break;
            default:
                Msg = "サーバーへの接続に失敗しました";
                break;

        }
        return Msg;
    }
//    public boolean checkNetWork(){
//        // モバイル回線（３G）の接続状態を取得
//        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//        // wifiの接続状態を取得
//        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//
//        // 3Gデータ通信／wifi共に接続状態じゃない場合
//        if ( (mobile != NetworkInfo.State.CONNECTED) && (wifi != NetworkInfo.State.CONNECTED) ) {
//            // ネットワーク未接続
//            return false;
//        }
//        // ネットワークに接続している
//        return true;
//    }

    @Override
    public void doInSplash(Activity activity) {
        super.doInSplash(activity);
        //Common.setUrl( "http://jokerpiece.co.jp/");
        S_Url = "http://jokerpiece.co.jp/";

        SaveData.Wb = new WebView(activity);
        SaveData.Wb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                SaveData.ConnectFailed = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                SaveData.SplashIsFinished = true;
            }

            @Override
            public void onReceivedError(final WebView webview, int errorCode, String description, String failingUrl) {
                SaveData.ConnectFailed = true;
                AppUtil.debugLog("t", "test");
            }
        });
        WebSettings webSettings = SaveData.Wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        SaveData.Wb.loadUrl(S_Url);
    }
}