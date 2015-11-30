package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.co.jokerpiece.piecebase.config.Config;

/**
 * Created by wenHsin on 2015/11/25.
 */
public class UploadVideoActivity extends Activity {

    private String account_id;
    private String token;
    private String upload_token;
    private String order_id;
    private ValueCallback<Uri> mUploadMessage;
    final private int INTENT_CODE = 101;
    Context context;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadvideo);
        webView = (WebView)findViewById(R.id.webview_youtubeUpload);

        Intent i = getIntent();
        account_id = i.getStringExtra("account_id");
        order_id = i.getStringExtra("order_id");
        token = i.getStringExtra("token");
        upload_token = i.getStringExtra("upload_token");

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(Config.SENDID_YOUTUBE_UPLOAD + "?order_id=" + order_id + "&app_id=" + Config.APP_ID + "&app_key=" + Config.APP_KEY + "&token=" + token);

        webView.setWebChromeClient(new WebChromeClient() {
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                mUploadMessage = uploadFile;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("video/*");
                startActivityForResult(Intent.createChooser(i, "video"), INTENT_CODE);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl(Config.SENDID_YOUTUBE_UPLOAD_DONE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == INTENT_CODE){
            if(null == mUploadMessage)return;

            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
}
