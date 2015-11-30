package jp.co.jokerpiece.piecebase;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import jp.co.jokerpiece.piecebase.config.Config;

/**
 * Created by wenHsin on 2015/11/25.
 */
public class UploadMessageActivity extends FragmentActivity {
    WebView webView;
    String order_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_message);
        webView = (WebView)findViewById(R.id.upload_message);
        Intent i = getIntent();
        String action = i.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = i.getData();
            if (uri != null) {
                order_num = uri.getQueryParameter("order_num");
            }
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String Url = Config.SENDID_MESSAGE_UPLOAD + "?order_id=" + order_num + "&app_id=" + Config.APP_ID + "&app_key=" + Config.APP_KEY;
        webView.loadUrl(Config.SENDID_MESSAGE_UPLOAD + "?order_id=" + order_num + "&app_id=" + Config.APP_ID + "&app_key=" + Config.APP_KEY );
        webView.setWebChromeClient(new WebChromeClient(){
        });
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                webView.loadUrl(Config.SENDID_MESSAGE_UPLOAD_DONE);
//            }
//        });
    }


}
