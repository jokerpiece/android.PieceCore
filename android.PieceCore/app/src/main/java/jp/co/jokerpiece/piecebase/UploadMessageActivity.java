package jp.co.jokerpiece.piecebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2015/11/25.
 */
public class UploadMessageActivity extends Activity {
    WebView webView;
    String order_id;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_message);
        webView = (WebView)findViewById(R.id.upload_message);
        Intent i = getIntent();
        order_id = i.getStringExtra("order_id");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String Url = Config.SENDID_MESSAGE_UPLOAD + "?order_id=" + order_id + "&app_id=" + Config.APP_ID + "&app_key=" + Config.APP_KEY;
        AppUtil.debugLog("messageURL", Url);
        webView.loadUrl(Url);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }


}
