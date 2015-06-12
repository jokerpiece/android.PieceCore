package jp.co.jokerpiece.piecebase.Oauth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import jp.co.jokerpiece.piecebase.R;

/**
 * Created by kaku on 2015/06/08.
 */
public class FacebookFragment extends Fragment {

    LoginButton loginButton;
    CallbackManager callbackManager;
    Context context;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        context = getActivity();
        FacebookSdk.sdkInitialize(context);
        View view = inflater.inflate(R.layout.facebookview, container, false);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(context,"fail",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(context,"error",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
//    public void fbLogin() {
//        token = AccessToken.getCurrentAccessToken();
//        profile = Profile.getCurrentProfile(); // Is null sometimes
//
//        GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject me, GraphResponse response) {
//                if (response.getError() != null) {
//                } else {
//                   // String email = me.optString("email");
//                   // new FBSignup(getActivity()).execute(id, first_name, last_name, email, photo);
//                }
//            }
//        }).executeAsync();
//    }
}
