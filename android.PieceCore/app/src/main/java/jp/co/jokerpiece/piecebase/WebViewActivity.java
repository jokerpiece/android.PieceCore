package jp.co.jokerpiece.piecebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2016/02/01.
 */
public class WebViewActivity extends FragmentActivity {

    WebView webView;
    Button goBack;
    Button goNext;
    Button reload;
    Button close;
    ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = (WebView)findViewById(R.id.activity_webView);
        goBack = (Button)findViewById(R.id.web_goBack);
        goNext = (Button)findViewById(R.id.web_goNext);
        reload = (Button)findViewById(R.id.web_reload);
        close = (Button)findViewById(R.id.closeWeb);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!url.startsWith("file:") && !url.startsWith("http:") && !url.startsWith("https:")){
                    webView.stopLoading();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                if(webView.canGoBack()){
                    goBack.setVisibility(View.VISIBLE);
                }else{
                    goBack.setVisibility(View.INVISIBLE);
                }
                if(webView.canGoForward()){
                    goNext.setVisibility(View.VISIBLE);
                }else{
                    goNext.setVisibility(View.INVISIBLE);
                }
            }
        });

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Intent i = getIntent();
        String strUrl = i.getStringExtra("send_url");
        String cookieString = "uuid="+ Common.getUUID(this);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(Config.COOKIE_DOMAIN, cookieString);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        }else {
            CookieSyncManager.getInstance().sync();
        }

        if(!strUrl.startsWith("file:") && !strUrl.startsWith("http:") && !strUrl.startsWith("https:")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
            this.startActivity(intent);
        }else {
            if (strUrl != null && !strUrl.equals("")) {
                webView.loadUrl(strUrl);
            }
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        });
        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.canGoForward()){
                    webView.goForward();
                }
            }
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

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

}
