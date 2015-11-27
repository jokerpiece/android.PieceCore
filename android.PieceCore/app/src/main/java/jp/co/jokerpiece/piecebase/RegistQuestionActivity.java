package jp.co.jokerpiece.piecebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.config.Config;

public class RegistQuestionActivity extends Activity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_only);
        String order_id = getIntent().getStringExtra("order_id");

        WebView wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        String url = Config.SENDID_REGIST_QUESTION + "?order_id=" + order_id + "&app_id=" + Config.APP_ID + "&app_key=" + Config.APP_KEY;
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(url.endsWith(Config.QUESTION_FINISH)){
                    finish();
                }
            }
        });
        wv.loadUrl(url);
    }
}
