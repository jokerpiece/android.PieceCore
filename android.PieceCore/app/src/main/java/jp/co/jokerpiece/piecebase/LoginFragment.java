package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import jp.co.jokerpiece.piecebase.api.CheckDataAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CheckData;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2015/11/16.
 */
public class LoginFragment extends Fragment {
    View rootView;
    View loginView;
    //View failedView;
    EditText etMailAddress;
    EditText etOrderNum;
    Button deciedBtn;
    //Button backBtn;
    Context context;
    SpannableStringBuilder sbMail;
    SpannableStringBuilder sbOrderNum;
    CheckData checkData;
    String order_num;
    String mailAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();

        rootView = inflater.inflate(R.layout.activity_login, container, false);
        etMailAddress = (EditText)rootView.findViewById(R.id.login_address);
        etOrderNum = (EditText)rootView.findViewById(R.id.login_ordernum);
        deciedBtn = (Button)rootView.findViewById(R.id.login_decide);
//        backBtn = (Button) failedView.findViewById(R.id.loginfailed_back);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        deciedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checkData", "decied");
                sbMail = (SpannableStringBuilder)etMailAddress.getText();
                sbOrderNum = (SpannableStringBuilder)etOrderNum.getText();
                mailAddress = sbMail.toString();
                order_num = sbOrderNum.toString();
                Log.d("checkData", "order" + order_num);
                Log.d("checkData","mail"+mailAddress);
                if(!mailAddress.equals("") && !order_num.equals("")) getData();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(LoginFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();
        if(!MainBaseActivity.startFromSchemeFlg){
            MainBaseActivity.startFromSchemeFlg = true;
            mailAddress = "";
            Bundle bundle = getArguments();
            if (bundle != null) {
                order_num = bundle.getString("order_num");
            }
            getData();
        }
    }

    public void getData(){
        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt, null, new LoaderManager.LoaderCallbacks<CheckData>() {
            @Override
            public Loader<CheckData> onCreateLoader(int id, Bundle args) {
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
                Log.d("token","1"+data.token);
                Log.d("token","status"+data.status_code);
                Log.d("token","status"+data.account_id);
                if(!data.status_code.equals("") && data.status_code != null) {
                    if (data.status_code.equals("00")) {
                        FragmentManager fm =((MainBaseActivity)context).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        UploadVideoFragment fragment = new UploadVideoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("account_id", data.account_id);
                        bundle.putString("order_id", data.order_id);
                        bundle.putString("token", data.token);
                        bundle.putString("upload_token", data.upload_token);
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        FailedViewFragment fragment = new FailedViewFragment();
                        ft.replace(R.id.fragment, fragment);
                        ft.addToBackStack("LoginFragment");
                        ft.commit();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<CheckData> loader) {

            }
        });
    }
    public static class FailedViewFragment extends Fragment{
        View failedView;
        Button backBtn;
        Context context;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            context = getActivity();
            failedView = inflater.inflate(R.layout.activity_loginfailed,container,false);
            backBtn = (Button) failedView.findViewById(R.id.loginfailed_back);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
                    fm.popBackStack("LoginFragment",FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    FragmentTransaction ft = fm.beginTransaction();
//                    ft.replace(R.id.fragment, Fragment.instantiate(context, String.valueOf(LoginFragment.class)));
//                    ft.addToBackStack(null);
//                    ft.commit();

                }
            });
            return failedView;
        }

        @Override
        public void onResume() {
            super.onResume();
            AppUtil.setTitleOfActionBar(
                    getActivity().getActionBar(),
                    MainBaseActivity.titleOfActionBar.get(FailedViewFragment.class.getSimpleName()));
            getActivity().invalidateOptionsMenu();
        }
    }
}
