package jp.co.jokerpiece.piecebase.Oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import jp.co.jokerpiece.piecebase.MainBaseActivity;
import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.SnsFragment;
import jp.co.jokerpiece.piecebase.util.TwitterUtils;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by kaku on 2015/06/10.
 */
public class TwitterActivity extends Activity {
    private String mCallbackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    String contribute;

    FrameLayout tweetpop;
    EditText tweetText;
    ImageView imageView1;
    TextView tweetTextCount;
    Button button3;
    String replies;
//    String sUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallbackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtils.getTwitterInstance(this);
//        mTwitter.setOAuthConsumer(Config.CONSUMER_KEY,Config.CONSUMER_SECRET);

        if (!TwitterUtils.hasAccessToken(this)) {
            startAuthorize();
        }else{
            setContentView(R.layout.twitter_activity);
            tweetpop = (FrameLayout) this.findViewById(R.id.tweetpop);
            tweetText = (EditText) this.findViewById(R.id.tweetText);
            imageView1 = (ImageView) this.findViewById(R.id.imageView1);
            tweetTextCount = (TextView) this.findViewById(R.id.tweetTextCount);
            button3 = (Button) this.findViewById(R.id.button3);

            Bitmap imgBitmap = BitmapFactory.decodeFile(SnsFragment.filePath);
            imageView1.setImageBitmap(imgBitmap);

            // ツイート可能文字数を表示
            int length = 140 - tweetText.length();
            tweetTextCount.setTextColor(Color.GRAY);
            if(SnsFragment.filePath == null){
                replies = "@KuoWenHsin";
                length -= replies.length();
                tweetTextCount.setText( String.valueOf(length));
            }else{
                tweetTextCount.setText(String.valueOf(length));
            }

            // ツイート文字にフォーカスをセット
            tweetText.requestFocus();
            // カーソルは最後の文字に
            tweetText.setSelection(tweetText.length());
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(tweetText, 0);
            if(SnsFragment.filePath == null) {
                replies = "@KuoWenHsin";
                tweetText.setText(replies);
            }
            // ツイート文字列の編集リスナー登録
            tweetText.addTextChangedListener(new TextWatcher() {
                // 編集前の処理
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                // 値が変わったときの処理
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final int textColor;
                    int length = 140 - s.length();
                    if (length < 0) {
                        textColor = Color.RED;
                        button3.setEnabled(false);
                        button3.setTextColor(Color.GRAY);
                    } else {
                        textColor = Color.GRAY;
                        button3.setEnabled(true);
                        button3.setTextColor(Color.rgb(0, 153, 255));
                    }
                    tweetTextCount.setTextColor(textColor);
                    tweetTextCount.setText(String.valueOf(length));
                }

                // 編集後の処理
                public void afterTextChanged(Editable s) {
                }
            });
        }

    }

    private void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);

                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);

                } else {
                    // 失敗。。。
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    // キャンセルボタン対応
                    if(params[0] == null){
                        return null;
                    }
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功！
                    showToast("認証成功！");
                    successOAuth(accessToken);
                } else {
                    // 認証失敗。。。
                    showToast("認証失敗。。。");
                }
            }
        };
        task.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken) {

        if (accessToken != null) {
            // 認証成功時は認証情報を保管
            TwitterUtils.storeAccessToken(this, accessToken);
        }
        BackToMainActivity();
    }
    public void BackToMainActivity(){
        Intent intent = new Intent(this, MainBaseActivity.class);
        startActivity(intent);
        finish();
    }
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    contribute = tweetText.getText().toString();
                    // 本文セット
                    StatusUpdate status = new StatusUpdate(contribute);
                    // 画像セット
                    if(SnsFragment.filePath != null) {
                        File file = new File(SnsFragment.filePath);
                        if (file.exists()) {
                            status.media(file);
                        } else {
                            status.media(null);
                        }

                        // ツイート
                        mTwitter.updateStatus(status);
                    }else{
                        mTwitter.updateStatus(status);
                    }
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Toast.makeText(getApplicationContext(), "ツイート完了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "ツイート失敗", Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();
    }

    // ツイート子画面内ボタンタップ処理
    public void tapPopBtn(View view) {
        switch(Integer.parseInt(String.valueOf(view.getTag()))){
            case 0:
                // 「キャンセル」ボタン
                //tweetpop.setVisibility(View.GONE);
                this.finish();
                break;
            case 1:
                // 「投稿」ボタン
                tweet();
                //tweetpop.setVisibility(View.GONE);
                this.finish();
                break;
            default:
                break;
        }
    }

}
