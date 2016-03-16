package jp.co.jokerpiece.piecebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2016/03/15.
 */
public class SettingFragment extends BaseFragment {

    View rootView;
    Switch swNotification;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_setting,container,false);
//        Config.soundFlg = AppUtil.getPrefBoolean(getActivity(), "notifiFlg", false);
        swNotification = (Switch)rootView.findViewById(R.id.notifi_switch);
        Common.setCurrentFragment(Config.SettingFragmentNum);
        swNotification.setChecked(AppUtil.getPrefBoolean(getActivity(), "notifiFlg", true));
        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppUtil.setPrefBoolean(getActivity(),"notifiFlg",true);
                    Config.soundFlg = true;
                }else{
                    AppUtil.setPrefBoolean(getActivity(),"notifiFlg",false);
                    Config.soundFlg = false;
                }
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(SettingFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();

    }
}
