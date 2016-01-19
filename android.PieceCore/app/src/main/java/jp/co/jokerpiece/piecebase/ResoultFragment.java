package jp.co.jokerpiece.piecebase;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;

public class ResoultFragment extends Fragment {


    Context context;

    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        context = getActivity();

        Common.setCurrentFragment(Config.ResoultFragmentNum);
        //Paypal購入画面を取得
        rootView = inflater.inflate(R.layout.fragment_resolut, container, false);

        return rootView;
    }


}



