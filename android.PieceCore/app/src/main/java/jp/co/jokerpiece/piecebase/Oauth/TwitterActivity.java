package jp.co.jokerpiece.piecebase.Oauth;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import jp.co.jokerpiece.piecebase.R;

/**
 * Created by kaku on 2015/06/10.
 */
public class TwitterActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterview);
//        AsyncTask<Void,Void,Void> tokenTask = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                getAccessToken();
//                return null;
//            }
//        };
//        tokenTask.execute();
//        finish();
    }

//    public void getAccessToken(){
//        Uri uri = getIntent().getData();
//
//        if(uri != null && uri.toString().startsWith("callback://TwitterActivity")){
//            String verifier = uri.getQueryParameter("oauth_verifier");
//            try {
//                // AccessTokenオブジェクトを取得
//                accessToken = OAuthUtils._oauth.getOAuthAccessToken(OAuthUtils._req,
//                        verifier);
//            } catch (TwitterException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
