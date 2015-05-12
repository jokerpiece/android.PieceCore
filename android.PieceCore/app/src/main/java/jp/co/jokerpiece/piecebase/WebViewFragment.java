package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.util.AppUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends Fragment implements View.OnClickListener {
    private WebView webView;
    private ImageView ivBack;
    private ImageView ivNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);

        findViews(rootView);

        webView = (WebView) rootView.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//            	ActionBar ab = getActionBar();
//            	String title = webView.getTitle();
//            	ab.setTitle(title);
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
        if (i == R.id.iv_back) {
            if (webView.canGoBack()) {
                webView.goBack();
            }

        } else if (i == R.id.iv_next) {
            if (webView.canGoForward()) {
                webView.goForward();
            }

        }
    }

    public void findViews(View rootView) {
        ivBack = (ImageView) rootView.findViewById(R.id.iv_back);
        ivNext = (ImageView) rootView.findViewById(R.id.iv_next);

        ivBack.setOnClickListener(this);
        ivNext.setOnClickListener(this);
    }

}