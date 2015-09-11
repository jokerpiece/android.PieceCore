package jp.co.jokerpiece.piecebase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RootFragment extends Fragment {

    private static boolean StartActivity = true;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.root_fragment, container, false);
        return view;
	}

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        String rootClass = args.getString("root");

        initFragment(rootClass);
	}

    /**
     * バックスタックを取り出す。ある場合はtrue、ない場合はfalseを返す。
     **/
    public boolean popBackStack() {
        return getChildFragmentManager().popBackStackImmediate();
    }

    /**
     * クラス名からフラグメントを設置する。すでに設置済みなら何もしない。
     **/
    private void initFragment(String fragmentClassName) {
        FragmentManager fm = getChildFragmentManager();

        if(fm.findFragmentById(R.id.fragment) != null){
            return;
        }
        if(StartActivity) {
            StartActivity = false;
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = Fragment.instantiate(getActivity(), fragmentClassName);
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        }
    }
}
