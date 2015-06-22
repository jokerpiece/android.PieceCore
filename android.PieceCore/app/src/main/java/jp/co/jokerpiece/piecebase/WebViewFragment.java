package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends Fragment implements View.OnClickListener {
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
    private boolean ConnectFailed;
    ConnectivityManager conMan;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectFailed = false;
        setMode();
        findViews(rootView);
        //設定しないと最初の読み込みとき出てしまう
        imgBackFlg.setVisibility(View.GONE);
        imgNextFlg.setVisibility(View.GONE);
        imgReload.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        error_Msg.setVisibility(View.GONE);

        webView = (WebView) rootView.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ConnectFailed = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(ConnectFailed) {
                    error_Msg.setVisibility(View.VISIBLE);
                    imgReload.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                }else{
                    error_Msg.setVisibility(View.GONE);
                    imgReload.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                }
                if (!webView.canGoBack()) {
                    imgBackFlg.setVisibility(View.GONE);
                }else{
                    imgBackFlg.setVisibility(View.VISIBLE);
                }
                if (!webView.canGoForward()) {
                    imgNextFlg.setVisibility(View.GONE);
                }else{
                    imgNextFlg.setVisibility(View.VISIBLE);
                }

//            	ActionBar ab = getActionBar();
//            	String title = webView.getTitle();
//            	ab.setTitle(title);
            }

            @Override
            public void onReceivedError(final WebView webview,int errorCode, String description,String failingUrl){
                 ConnectFailed = true;
                if(checkNetWork()) {
                    String s = getErrorMsg(errorCode);
                    error_Msg.setText(s);
                }else{
                    error_Msg.setText("通信できませんでした。\n電波状態をお確かめ下さい。");
                }

                imgReload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.reload();
                        imgReload.setVisibility(View.GONE);
                        tv.setVisibility(View.GONE);
                    }
                });
            }
        });


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Bundle bundle = getArguments();
        if(bundle != null){
            String strUrl = bundle.getString("send_url");
            if (strUrl != null && !strUrl.equals("")) {
                webView.loadUrl(strUrl);
            }
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

        if (i == R.id.iv_back || i == R.id.iv_backCenter || i == R.id.iv_backBottom) {
            if (webView.canGoBack()) {
                webView.goBack();
            }

        } else if (i == R.id.iv_next || i == R.id.iv_nextCenter || i == R.id.iv_nextBottom) {
            if (webView.canGoForward()) {
                webView.goForward();
            }

        }
    }
    //ivBackはどこに設置するか
    public void setMode(){
        Config.PositionForWebView = Config.Center;
    }
    public void findViews(View rootView) {
        ivBack = (ImageView) rootView.findViewById(R.id.iv_back);
        ivNext = (ImageView) rootView.findViewById(R.id.iv_next);
        ivBackCenter = (ImageView) rootView.findViewById(R.id.iv_backCenter);
        ivNextCenter = (ImageView) rootView.findViewById(R.id.iv_nextCenter);
        ivBackBottom = (ImageView) rootView.findViewById(R.id.iv_backBottom);
        ivNextBottom = (ImageView) rootView.findViewById(R.id.iv_nextBottom);
        imgReload = (ImageButton) rootView.findViewById(R.id.reload);
        tv = (TextView)rootView.findViewById(R.id.tv);
        error_Msg = (TextView) rootView.findViewById(R.id.errorMsg);


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
            case -1:
                Msg = "エラー";
                break;
            case -2:
                Msg = "サーバーまたはプロキシのホスト名の検索に失敗しました";
                break;
            case -3:
                Msg = "サポートされていない認証方式";
                break;
            case -4:
                Msg = "ユーザー認証はサーバー上で失敗しました";
                break;
            case -5:
                Msg = "ユーザー認証はプロキシ上で失敗しました";
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
            case -9:
                Msg = "リダイレクトが多過ぎ";
                break;
            case -10:
                Msg = "サポートされていないURIスキーム";
                break;
            case -11:
                Msg = "SSLハンドシェイクの実行に失敗しました";
                break;
            case -12:
                Msg = "不正な形式のURL";
                break;
            case -13:
                Msg = "ファイルエラー";
                break;
            case -14:
                Msg = "ファイルは見つかりません";
                break;
            case -15:
                Msg = "リクエストが多過ぎ";
                break;

        }
        return Msg;
    }
    public boolean checkNetWork(){
        // モバイル回線（３G）の接続状態を取得
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        // wifiの接続状態を取得
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        // 3Gデータ通信／wifi共に接続状態じゃない場合
        if ( (mobile != NetworkInfo.State.CONNECTED) && (wifi != NetworkInfo.State.CONNECTED) ) {
            // ネットワーク未接続
            return false;
        }

        // ネットワークに接続している
        return true;
    }
}