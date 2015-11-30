package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jp.co.jokerpiece.piecebase.api.CheckDataAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CheckData;
import jp.co.jokerpiece.piecebase.util.AppUtil;


/**
 * Created by wenHsin on 2015/11/30.
 */
public class LoginActivity extends Activity {


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
            context = this;
        }

        @Override
        protected void onResume() {
            super.onResume();

            if(!MainBaseActivity.startFromSchemeFlg) {
                MainBaseActivity.startFromSchemeFlg = true;

                Intent intent = getIntent();
                order_num = intent.getStringExtra("order_num");
                getData();
            }
//            String action = intent.getAction();
//            if (Intent.ACTION_VIEW.equals(action)) {
//                Uri uri = intent.getData();
//                if (uri != null) {
//                    order_num = uri.getQueryParameter("order_num");
//                    mailAddress = "";
//                    AppUtil.debugLog("checkData",order_num);
//                    getData();
//                }
//            }else{
//                if(sbMail != null && sbOrderNum != null) {
//                    order_num = sbOrderNum.toString();
//                    mailAddress = sbMail.toString();
//                    getData();
//                }else{
//                    mailAddress = "";
//                }
//            }
        }
        public void makeView(){

        }
        public void getData(){
            ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt, null, new LoaderManager.LoaderCallbacks<CheckData>() {
                @Override
                public Loader<CheckData> onCreateLoader(int id, Bundle args) {
                    AppUtil.debugLog("checkData", "order" + order_num);
                    AppUtil.debugLog("checkData", "mail" + mailAddress);
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
                        if (data.status_code.equals("01")) {

//                            setContentView(R.layout.activity_loginfailed);
//                            Button backBtn = (Button) findViewById(R.id.loginfailed_back);
//                            backBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setContentView(R.layout.activity_login);
//                                }
//                            });
                            Intent i = new Intent(context,LoginFailedActivity.class);
                            context.startActivity(i);
                            setContentView(R.layout.activity_login);
                            etMailAddress = (EditText)findViewById(R.id.login_address);
                            etOrderNum = (EditText)findViewById(R.id.login_ordernum);
                            deciedBtn = (Button)findViewById(R.id.login_decide);

                            sbMail = (SpannableStringBuilder)etMailAddress.getText();
                            sbOrderNum = (SpannableStringBuilder)etOrderNum.getText();
                            deciedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppUtil.debugLog("checkData", "decied");
                                    sbMail = (SpannableStringBuilder) etMailAddress.getText();
                                    sbOrderNum = (SpannableStringBuilder) etOrderNum.getText();
                                    mailAddress = sbMail.toString();
                                    order_num = sbOrderNum.toString();
                                    AppUtil.debugLog("checkData", "order" + order_num);
                                    AppUtil.debugLog("checkData", "mail" + mailAddress);
                                    if (!mailAddress.equals("") && !order_num.equals("")) getData();
                                }
                            });
                        } else {
                            Intent i = new Intent(context,UploadVideoActivity.class);
                            i.putExtra("account_id", data.account_id);
                            i.putExtra("order_id", data.order_id);
                            i.putExtra("token", data.token);
                            i.putExtra("upload_token", data.upload_token);
                            context.startActivity(i);
                            finish();
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<CheckData> loader) {

                }
            });
        }


}
