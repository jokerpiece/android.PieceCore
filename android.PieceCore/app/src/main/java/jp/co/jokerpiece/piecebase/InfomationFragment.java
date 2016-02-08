package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.api.NewsListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class InfomationFragment extends Fragment  {
	Context context;

	ViewPager viewPager;
//	PagerTabStrip viewPagerTab;
	NewsListData infoListData;

    Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();

        Common.setCurrentFragment(Config.InfoFragmentNum);
		View rootView = inflater.inflate(R.layout.fragment_infomation, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
//        viewPagerTab = (PagerTabStrip) rootView.findViewById(R.id.tab);

        //viewPager.setOnPageChangeListener(this);
		if(!Config.PROPERTY_ID.equals("") && Config.PROPERTY_ID != null){
			App app = (App)getActivity().getApplication();
			Tracker t = app.getTracker();
			t.setScreenName(getString(R.string.info0));
			t.send(new HitBuilders.ScreenViewBuilder().build());
		}
        getInfomation();

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
				MainBaseActivity.titleOfActionBar.get(InfomationFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
//		if(infoListData == null){
			getInfomation();;
//		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

//	@Override
//	public void onPageScrollStateChanged(int arg0) {
//	}
//	@Override
//	public void onPageScrolled(int arg0, float arg1, int arg2) {
//	}
//	@Override
//	public void onPageSelected(int arg0) {
//		switch (arg0) {
//		case 0:
//			viewPagerTab.setBackgroundColor(getResources().getColor(R.color.info_all_color));
//			break;
//		case 1:
//			viewPagerTab.setBackgroundColor(getResources().getColor(R.color.info_notice_color));
//			break;
//		case 2:
//			viewPagerTab.setBackgroundColor(getResources().getColor(R.color.info_flyer_color));
//			break;
//		case 3:
//			viewPagerTab.setBackgroundColor(getResources().getColor(R.color.info_coupon_color));
//			break;
//		default:
//			break;
//		}
//	}

	public void getInfomation(){
        viewPager.setVisibility(View.INVISIBLE);

        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<NewsListData>() {
			@Override
			public Loader<NewsListData> onCreateLoader(int id, Bundle args) {
				 NewsListAPI newsAPI = new NewsListAPI(context);
				 newsAPI.forceLoad();
				 return newsAPI;
			}
			@Override
			public void onLoadFinished(Loader<NewsListData> loader, NewsListData data) {
				if(data == null){
					Common.serverErrorMessage(context);
                    viewPager.setVisibility(View.INVISIBLE);
					return;
				}
				infoListData = data;
				if(getActivity() == null) return;
				// ここにデータ取得時の処理を書く
		        viewPager.setAdapter(
		                new InfomationListPageAdapter(
		                  getChildFragmentManager(), getActivity(), data));
                viewPager.setVisibility(View.VISIBLE);
			}
			@Override
			public void onLoaderReset(Loader<NewsListData> loader) {

			}
		});
	}
}
