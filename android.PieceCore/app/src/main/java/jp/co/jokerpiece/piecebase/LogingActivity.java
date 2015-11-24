package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import jp.co.jokerpiece.piecebase.api.CheckDataAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CheckData;

/**
 * Created by wenHsin on 2015/11/13.
 */
public class LogingActivity extends FragmentActivity {

    View rootView;
    EditText etMailAddress;
    EditText etOrderNum;
    Button deciedBtn;
    Context context;
    SpannableStringBuilder sbMail;
    SpannableStringBuilder sbOrderNum;
    CheckData checkData;
    String order_num;
    String mailAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        etMailAddress = (EditText)findViewById(R.id.login_address);
        etOrderNum = (EditText)findViewById(R.id.login_ordernum);
        deciedBtn = (Button)findViewById(R.id.login_decide);

        sbMail = (SpannableStringBuilder)etMailAddress.getText();
        sbOrderNum = (SpannableStringBuilder)etOrderNum.getText();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        order_num = intent.getStringExtra("order_num");
        getData();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                order_num = uri.getQueryParameter("order_num");
                mailAddress = "";
                Log.d("checkData",order_num);
                getData();
            }
        }else{
            if(sbMail != null && sbOrderNum != null) {
                order_num = sbOrderNum.toString();
                mailAddress = sbMail.toString();
                getData();
            }else{
                mailAddress = "";
            }
        }
    }
    public void makeView(){

    }
    public void getData(){
        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt, null, new LoaderManager.LoaderCallbacks<CheckData>() {
            @Override
            public Loader<CheckData> onCreateLoader(int id, Bundle args) {
                Log.d("checkData","order"+order_num);
                Log.d("checkData","mail"+mailAddress);
                CheckDataAPI checkDataAPI = new CheckDataAPI(context,mailAddress,order_num);
                checkDataAPI.forceLoad();
                return checkDataAPI;
            }

            @Override
            public void onLoadFinished(Loader<CheckData> loader, CheckData data) {
                if(data == null){
                    Common.serverErrorMessage(context);
                    return;
                }
                checkData = data;
                if(!data.status_code.equals("") && data.status_code != null) {
                    if (data.status_code.equals("1")) {
                        setContentView(R.layout.activity_loginfailed);
                        Button backBtn = (Button) findViewById(R.id.loginfailed_back);
                        backBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setContentView(R.layout.activity_login);
                            }
                        });
                    } else {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        UploadVideoFragment fragment = new UploadVideoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("account_id", data.account_id);
                        bundle.putString("order_id", data.order_id);
                        bundle.putString("token", data.token);
                        bundle.putString("upload_token", data.upload_token);
                        ft.replace(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<CheckData> loader) {

            }
        });
    }
}
