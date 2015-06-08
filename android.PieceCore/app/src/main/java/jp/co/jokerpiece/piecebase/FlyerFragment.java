package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.jokerpiece.piecebase.api.FlyerListAPI;
import jp.co.jokerpiece.piecebase.api.NewsListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.FlyerData;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.data.NewsListData.NewsData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;
import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FlyerFragment extends Fragment implements OnPageChangeListener{
	Context context;
	FlyerTimerTask timerTask = null;
	Timer   mTimer   = null;
	Handler mHandler = new Handler();

	static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
	static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	ArrayList<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();
	ViewPager viewPagerScroll;
	private ViewPagerIndicator viewPagerIndicator;
//	private int loderCount = 0;
	LinearLayout llFlyerBase;

	public Button btnSendOtherGoods;

	FlyerData flyerData;
	int flyer_ID = -1;

	int headerNowPage = 0;
	private FlyerImagePageAdapter pageFlagment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();

        if(Config.FlyerFragmentNum == 0) {
            if (Config.Savelist.size() == 1) {
                Config.Savelist.clear();
                Config.Savelist.add(0);
            }
            if (!Config.Backflg) {
                if (Config.FragmentCurrentNum != 0) {
                    Config.Savelist.add(Config.FlyerFragmentNum);
                    Config.FragmentCurrentNum += 1;
                }
            }
        }else{
            if(!Config.Backflg) {
                Config.Savelist.add(Config.FlyerFragmentNum);
                Config.FragmentCurrentNum += 1;
            }
        }
		View rootView = inflater.inflate(R.layout.fragment_flyer, container, false);

		if(pageFlagment != null){
			pageFlagment.notifyDataSetChanged();
		}

        llFlyerBase = (LinearLayout)rootView.findViewById(R.id.flyer_base);
        btnSendOtherGoods = (Button)rootView.findViewById(R.id.buttonSendShopping);
        btnSendOtherGoods.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendView(v);
			}
		});
        viewPagerScroll = (ViewPager) rootView.findViewById(R.id.headerScroll);
        viewPagerScroll.setVisibility(View.GONE);
        viewPagerIndicator = (ViewPagerIndicator) rootView.findViewById(R.id.indicator);
        viewPagerIndicator.setCurrentPosition(viewPagerScroll.getCurrentItem());
        //インテントなどでflyerIDを受け取っていない場合は
        Bundle bundle = getArguments();
        if(bundle != null){
        	flyer_ID = bundle.getInt("flyer_ID");
        }
//        if(flyer_ID < 0){
//        	getHomeFlyerID();
//        }else{
//        	getFlyerWithID(flyer_ID);
//        }

        pageFlagment = new FlyerImagePageAdapter(getChildFragmentManager(),
        		context,
        		new ArrayList<FlyerHeaderData>());

        return rootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

	private void ShowFlyerView() {
		if(flyerData == null){
			return;
		}
		alImageViewList.clear();
		pageFlagment.refresh();
		pageFlagment.destroyAllItem(viewPagerScroll);
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		float scale = (float)point.x / 640.0f;
		RelativeLayout.LayoutParams vplp = new RelativeLayout.LayoutParams(MP,(int)(388 * scale));
		viewPagerScroll.setLayoutParams(vplp);
		viewPagerScroll.setOnPageChangeListener(this);

		if(flyerData.header_list != null && flyerData.header_list.size() >= 1){
			for(FlyerData.FlyerHeaderData data :flyerData.header_list){
				pageFlagment.addImageView(data);
			}
			viewPagerScroll.setVisibility(View.VISIBLE);
			viewPagerScroll.setAdapter(pageFlagment);
			viewPagerIndicator.setCount(pageFlagment.getCount());
	        viewPagerIndicator.setCurrentPosition(viewPagerScroll.getCurrentItem());

			pageFlagment.notifyDataSetChanged();
			autoScroll();

		}else{
			viewPagerScroll.setVisibility(View.GONE);
		}
		int count = 0;
		LinearLayout llBase = null;
		for(FlyerData.FlyerBodyData data :flyerData.body_list){
			if(count % 2 == 0){
				llBase = new LinearLayout(context);
				MarginLayoutParams baseLp = new MarginLayoutParams(MP,WC);
				baseLp.setMargins(0, 4, 0, 0);
				llFlyerBase.addView(llBase,baseLp);
			}
			DownloadImageView dlIv = new DownloadImageView(context);
			if(!dlIv.setImageURL(data.img_url)){
                ((FragmentActivity) context).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, dlIv);
			}

			LayoutParams lp = new LinearLayout.LayoutParams((int)(320 * scale),(int)(320 * scale));
			dlIv.setScaleType(ScaleType.CENTER_CROP);
			final String itemURL = data.item_url;
			dlIv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickFlyer(itemURL);
				}
			});
			llBase.addView(dlIv,lp);
			alImageViewList.add(dlIv);
			count++;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AppUtil.setTitleOfActionBar(
				getActivity().getActionBar(),
				MainBaseActivity.titleOfActionBar.get(FlyerFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
//		if(alImageViewList != null && alImageViewList.size() >= 1){
//			for(DownloadImageView iv : alImageViewList){
//				if(!iv.loadImageView()){
//					getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++,null,iv);
//				}
//			}
//		}else{
//			getHomeFlyerID();
//		}
        if(flyer_ID < 0){
            getHomeFlyerID();
        }else{
            getFlyerWithID(flyer_ID);
        }
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
//        inflater.inflate(R.menu.menu_coupon, menu);
        super.onCreateOptionsMenu(menu, inflater);
	}

	private void onClickFlyer(String url) {
        if (url != null && !url.equals("") && !url.equals("null")) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(null);
            WebViewFragment fragment = new WebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("send_url", url);
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        }
	}

	private void sendView(View v){
		if(v == btnSendOtherGoods){
			MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
//			FragmentManager fm = getFragmentManager();
//			FragmentTransaction ft = fm.beginTransaction();
//			ft.addToBackStack(null);
//			ShoppingFragment fragment = new ShoppingFragment();
//			ft.replace(R.id.fragment, fragment);
//			ft.commit();
		}
	}

	private void getHomeFlyerID(){
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
					return;
				}
				int flyerID = -1;
				ArrayList<NewsData> data_list = data.data_list;
				for (int i = 0; i < data_list.size(); i++) {
					NewsListData.NewsData newsData = data_list.get(i);
					try{
						if(Integer.parseInt(newsData.type) == NewsListData.NEWS_DATA_TYPE_FLYER){
							if(flyerID < Integer.parseInt(newsData.id)){
								flyerID = Integer.parseInt(newsData.id);
							}
						}
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
				getFlyerWithID(flyerID);
			}
			@Override
			public void onLoaderReset(Loader<NewsListData> loader) {

			}
		});
	}

	private void getFlyerWithID(final int flyerID){
		this.flyer_ID = flyerID;
        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<FlyerData>(){
			@Override
			public Loader<FlyerData> onCreateLoader(int id, Bundle args) {
				 FlyerListAPI flyerAPI = new FlyerListAPI(context, flyerID);
				 flyerAPI.forceLoad();
				 return flyerAPI;
			}

			@Override
			public void onLoadFinished(Loader<FlyerData> loader, FlyerData data) {
				if(data == null){
					Common.serverErrorMessage(context);
					return;
				}
				flyerData = data;
				ShowFlyerView();
			}

			@Override
			public void onLoaderReset(Loader<FlyerData> loader) {
			}
        });
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (ViewPager.SCROLL_STATE_DRAGGING == state) {
			if(mTimer != null){
				mTimer.cancel();
				mTimer = null;
			}
		}else{
			autoScroll();
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		headerNowPage = position;
		viewPagerIndicator.setCurrentPosition(position);
	}

	private void autoScroll(){
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
        timerTask = new FlyerTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule(timerTask,Config.scrollDelay);
	}

	class FlyerTimerTask extends TimerTask{
	     @Override
	     public void run() {
	         mHandler.post( new Runnable() {
	             public void run() {
	         		headerNowPage++;
	         		if(headerNowPage == pageFlagment.getCount()){
	         			headerNowPage = 0;
	         		}
	         		viewPagerScroll.setCurrentItem(headerNowPage, true);
	            	 autoScroll();
	             }
	         });
	     }
	 }

	@Override
	public void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
		Log.d("FlyerActivity", "onDestroy");
	}

}
