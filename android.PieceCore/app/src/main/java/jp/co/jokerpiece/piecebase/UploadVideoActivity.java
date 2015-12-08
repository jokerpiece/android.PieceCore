package jp.co.jokerpiece.piecebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ClientCertRequest;
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

    private final static int FILECHOOSER_RESULTCODE = 1;

    private String account_id;
    private String token;
    private String upload_token;
    private String order_id;
    private ValueCallback<Uri[]> uploadMessages;
    private ValueCallback<Uri> mUploadMessage;
    final private int INTENT_CODE = 101;
    Context context;
    WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        account_id = i.getStringExtra("account_id");
        order_id = i.getStringExtra("order_id");
        token = i.getStringExtra("token");
        upload_token = i.getStringExtra("upload_token");

        String url = Config.SENDID_YOUTUBE_UPLOAD + "?order_id=" + order_id + "&app_id=" + Config.APP_ID + "&app_key=" + Config.APP_KEY + "&token=" + token;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_uploadvideo);
            webView = (WebView) findViewById(R.id.webview_youtubeUpload);

            WebSettings webSettings = webView.getSettings();
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setJavaScriptEnabled(true);

            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Log.d("onPageStarted", url);
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                    Log.d("onLoadResource", url);

                }

                @Override
                public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                    super.onReceivedClientCertRequest(view, request);
                    Log.d("onCertRequest", request.toString());
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {

                public void ShowFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                    openFileChooser(uploadFile, acceptType, capture);
                }

                public void showFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                    openFileChooser(uploadFile, acceptType, capture);
                }

                public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                    mUploadMessage = uploadFile;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("video/*");
                    startActivityForResult(Intent.createChooser(i, "video"), INTENT_CODE);
                }


                @Override
                public boolean onShowFileChooser(WebView webView,
                                                 ValueCallback<Uri[]> filePathCallback,
                                                 WebChromeClient.FileChooserParams fileChooserParams) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (uploadMessages != null) {
                            uploadMessages.onReceiveValue(null);
                            uploadMessages = null;
                        }
                        uploadMessages = filePathCallback;
                        Intent intent = fileChooserParams.createIntent();
                        intent.setType("video/*");
                        try {
                            UploadVideoActivity.this.startActivityForResult(intent,
                                    FILECHOOSER_RESULTCODE);
                        } catch (ActivityNotFoundException e) {
                            uploadMessages = null;
                            return false;
                        }
                    }
                    return true;
                }
            });
        }else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (uploadMessages == null) {
                    return;
                }
                uploadMessages.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploadMessages = null;
            }
        }else {
            if (requestCode == INTENT_CODE) {
                if (null == mUploadMessage) return;

                Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

}
