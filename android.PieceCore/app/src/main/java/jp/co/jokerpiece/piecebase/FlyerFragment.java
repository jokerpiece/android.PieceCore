package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.jokerpiece.piecebase.api.FlyerListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.FlyerData;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BitmapCache;
import jp.co.jokerpiece.piecebase.util.BitmapDownloader;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;
import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;

public class FlyerFragment extends BaseFragment implements OnPageChangeListener{
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

	protected FlyerData flyerData;
    int flyer_ID = -1;


	int headerNowPage = 0;
	public FlyerImagePageAdapter pageFlagment;

	public void setFragmentPagerAdapter(){
		pageFlagment = new FlyerImagePageAdapter(getChildFragmentManager(),
				context,
				new ArrayList<FlyerHeaderData>());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();
        Common.setCurrentFragment(Config.FlyerFragmentNum);
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
//        pageFlagment = new FlyerImagePageAdapter(getChildFragmentManager(),
//        		context,
//        		new ArrayList<FlyerHeaderData>());
		setFragmentPagerAdapter();

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
        if(SaveData.Flyerdata != null){
            flyerData = SaveData.Flyerdata;
            SaveData.Flyerdata = null;
        }
		if(llFlyerBase != null){
			llFlyerBase.removeAllViews();
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


		if(!MainBaseActivity.startFromSchemeFlg){
			Intent intent = getActivity().getIntent();
			String action = intent.getAction();
			if (Intent.ACTION_VIEW.equals(action)) {
				Uri uri = intent.getData();
				if (uri != null) {
					String order_num = "1";
					order_num = uri.getQueryParameter("order_num");
					Intent i = new Intent(context,LoginActivity.class);
					i.putExtra("order_num",order_num);
					context.startActivity(i);
//					FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
//					FragmentTransaction ft = fm.beginTransaction();
//					LoginFragment fragment = new LoginFragment();
//					Bundle bundle = new Bundle();
//					bundle.putString("order_num", order_num);
//					fragment.setArguments(bundle);
//					ft.replace(R.id.fragment, fragment);
//					ft.addToBackStack(null);
//					ft.commit();
				}
			}
		}
		//		if(alImageViewList != null && alImageViewList.size() >= 1){
//			for(DownloadImageView iv : alImageViewList){
//				if(!iv.loadImageView()){
//					getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++,null,iv);
//				}
//			}
//		}else{
//			getHomeFlyerID();
//		}

        if(AppUtil.getPrefString(context, "FLYERID", "").equals("0") || AppUtil.getPrefString(context, "FLYERID", "").equals("") ) {
            getFlyerWithID(0);
        }else{
            flyer_ID = Integer.parseInt((AppUtil.getPrefString(context, "FLYERID", "")));
            getFlyerWithID(flyer_ID);
        }

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
//        inflater.inflate(R.menu.menu_coupon, menu);
        super.onCreateOptionsMenu(menu, inflater);
	}

	public void onClickFlyer(String url) {
        if (url != null && !url.equals("") && !url.equals("null")) {
            FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            WebViewFragment fragment = new WebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("send_url", url);
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment);
            ft.addToBackStack(null);
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
		AppUtil.debugLog("FlyerActivity", "onDestroy");
	}

    @Override
    public void doInSplash(final Activity activity) {
        super.doInSplash(activity);
        Loader l = activity.getLoaderManager().getLoader(Config.loaderCnt);
        if (l != null){
            return;
        }
        activity.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<FlyerData>() {
            @Override
            public Loader<FlyerData> onCreateLoader(int id, Bundle args) {
                FlyerListAPI flyerAPI = new FlyerListAPI(activity, 0);
                flyerAPI.forceLoad();
                return flyerAPI;
            }

            @Override
            public void onLoadFinished(Loader<FlyerData> loader, FlyerData data) {
                if (data == null) {
                    Common.serverErrorMessage(activity);
                    return;
                }

                SaveData.Flyerdata = data;

                for (final FlyerHeaderData c : data.header_list) {
                    ((MainBaseActivity) activity).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
                        @Override
                        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
                            BitmapDownloader bmDownloader = new BitmapDownloader(activity, c.img_url);
                            bmDownloader.forceLoad();
                            return bmDownloader;
                        }

                        @Override
                        public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
                            if(data != null) {
                                BitmapCache.newInstance().putBitmap(c.img_url, data);
                            }
                        }
                        @Override
                        public void onLoaderReset(android.support.v4.content.Loader<Bitmap> loader) {
                        }
                    });
                }
                for (final FlyerData.FlyerBodyData c : data.body_list) {
                    ((MainBaseActivity) activity).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
                        @Override
                        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
                            BitmapDownloader bmDownloader = new BitmapDownloader(activity, c.img_url);
                            bmDownloader.forceLoad();
                            return bmDownloader;
                        }
                        @Override
                        public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
                            if(data != null) {
                                BitmapCache.newInstance().putBitmap(c.img_url, data);
                            }
                        }
                        @Override
                        public void onLoaderReset(android.support.v4.content.Loader<Bitmap> loader) {
                        }
                    });
                }
            }

            @Override
            public void onLoaderReset(Loader<FlyerData> loader) {
            }
        });
    }

}
