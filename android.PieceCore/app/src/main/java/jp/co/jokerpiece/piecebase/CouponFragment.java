package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.CouponListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CategoryListData;
import jp.co.jokerpiece.piecebase.data.CouponListData;
import jp.co.jokerpiece.piecebase.data.CouponListData.CouponData;
import jp.co.jokerpiece.piecebase.data.GetCouponData;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BitmapCache;
import jp.co.jokerpiece.piecebase.util.BitmapDownloader;
import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CouponFragment extends BaseFragment implements OnPageChangeListener {
	Context context;

	final static int GET_COUPON = 100;
	private static final String TAG = "CouponActivity";

	private Activity activity;
	private ViewPager viewPager;
	private ViewPagerIndicator viewPagerIndicator;
	private TextView tvNoCoupon;
	private Handler handler = new Handler();

	private CouponImagePageAdapter pageFlagment;
    //クーポンURLを持ってるかどうかのフラグ
   // public boolean haveUrlFlg = false;

//	private ArrayList<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();
	private CouponListData couponData = null;

//	private static int loderCount = 0;

	private String couponCode = "";
	private int initPos = -1;

//	private static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
//	private static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();
        Common.setCurrentFragment(Config.CouponFragmentNum);
		View rootView = inflater.inflate(R.layout.fragment_coupon, container, false);

        activity = getActivity();
        viewPager = (ViewPager) rootView.findViewById(R.id.couponpager);
        viewPagerIndicator = (ViewPagerIndicator) rootView.findViewById(R.id.indicator);
        tvNoCoupon = (TextView) rootView.findViewById(R.id.tv_nocoupon);
        viewPager.setOnPageChangeListener(this);

        pageFlagment = new CouponImagePageAdapter(getChildFragmentManager(),
        		new GetCouponData(activity, handler, viewPager, viewPagerIndicator, couponData),
        		new ArrayList<String>(),Config.haveUrlFlg);

        Bundle bundle = getArguments();
        if (bundle != null) {
        	couponCode = bundle.getString("coupon_code");
        }

        Log.d(TAG, "uuid: " + Common.getUUID(context));

        if (SaveData.Cdata != null){
            displayCouponAfterGetData();
        }else {
            getCouponList();
        }

        return rootView;
	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		switch (requestCode) {
//		case GET_COUPON:
//			couponData = null;
//		}
//	}

	@Override
	public void onResume() {
		super.onResume();
		AppUtil.setTitleOfActionBar(
				getActivity().getActionBar(),
				MainBaseActivity.titleOfActionBar.get(CouponFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
//		if(couponData == null){
			getCouponList();
//		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		viewPagerIndicator.setCurrentPosition(position);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
     //   if(!Config.isGetUrl) {
        if(!Config.haveUrlFlg){
            inflater.inflate(R.menu.menu_coupon, menu);
        }
//		MenuItem haveCoupon = menu.add(0 , Menu.FIRST, Menu.NONE ,"取得済みクーポン");
//		haveCoupon.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		haveCoupon.setIcon(R.drawable.icon_coupon);
        super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Log.d("itemID", "itemID"+item.getItemId());
		int itemId = item.getItemId();
		if (itemId == R.id.action_coupon) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			CouponUseFragment fragment = new CouponUseFragment();
			ft.replace(R.id.fragment, fragment);
            ft.addToBackStack(null);
            ft.commit();
		}
		return true;
	}

	public void getCouponList(){
        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<CouponListData>(){
			@Override
			public Loader<CouponListData> onCreateLoader(int id, Bundle args) {
				CouponListAPI couponAPI = new CouponListAPI(context, CouponListData.COUPON_DATA_TYPE_NOT_GIVE);
				couponAPI.forceLoad();
				 return couponAPI;
				}

			@Override
			public void onLoadFinished(Loader<CouponListData> loader, CouponListData data) {
				if(data == null){
					Common.serverErrorMessage(context);
					return;
				}
				couponData = data;

				// ここにデータ取得時の処理を書く
				displayCouponAfterGetData();

			}

			@Override
			public void onLoaderReset(Loader<CouponListData> loader) {
			}
        });
	}
	/**
	 * クーポンデータ取得後、クーポン情報を表示する。
	 */
	public void displayCouponAfterGetData() {
		if (couponData == null) { return; }
        if(SaveData.Cdata != null){
            couponData = SaveData.Cdata;
        }
		ArrayList<CouponData> couponList = couponData.data_list;

		if (couponList == null || couponList.size() == 0) {
			viewPager.setVisibility(View.GONE);
			viewPagerIndicator.setVisibility(View.GONE);
			tvNoCoupon.setVisibility(View.VISIBLE);
			return;
		}

		pageFlagment.destroyAllItem(viewPager);
		for (int i = 0; i < couponList.size(); i++) {
			CouponData data = couponList.get(i);
//			ForegroundImageView dlIv = new ForegroundImageView(getApplicationContext());
//			dlIv.setScaleType(ScaleType.FIT_XY);
//			final String itemURL = data.img_url;
//			dlIv.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					onClickFlyer(itemURL);
//				}
//
//			});
//			if(!dlIv.setImageURL(data.img_url)){
//				getSupportLoaderManager().initLoader(loderCount++,null,dlIv);
//			}
//			dlIv.setLayoutParams(new LayoutParams(MP, MP));
//			dlIv.setForegroundResource(R.drawable.shape_mask_imageview);
//			alImageViewList.add(dlIv);
			pageFlagment.addImageView(data.img_url);

			if ((data.coupon_code).equals(couponCode)) {
				initPos = i;
			}
		}

		pageFlagment.setGetCouponData(new GetCouponData(activity, handler, viewPager, viewPagerIndicator, couponData));

		viewPager.setAdapter(pageFlagment);
		viewPagerIndicator.setCount(pageFlagment.getCount());
		viewPager.setVisibility(View.VISIBLE);
		viewPagerIndicator.setVisibility(View.VISIBLE);
		tvNoCoupon.setVisibility(View.GONE);

		if (initPos > -1) {
			viewPager.setCurrentItem(initPos, true);
		}
	}

    @Override
    public void doInSplash(final Activity activity) {
        super.doInSplash(activity);

        activity.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<CouponListData>() {
            @Override
            public Loader<CouponListData> onCreateLoader(int id, Bundle args) {
                CouponListAPI couponAPI = new CouponListAPI(activity, CouponListData.COUPON_DATA_TYPE_NOT_GIVE);
                couponAPI.forceLoad();
                return couponAPI;
            }

            @Override
            public void onLoadFinished(Loader<CouponListData> loader, CouponListData data) {
                if (data == null) {
                    Common.serverErrorMessage(activity);
                    return;
                }
                SaveData.Cdata = data;
                for (final CouponData c : data.data_list) {
                    ((MainBaseActivity) activity).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
                        @Override
                        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
                            BitmapDownloader bmDownloader = new BitmapDownloader(activity, c.img_url);
                            bmDownloader.forceLoad();
                            return bmDownloader;
                        }
                        @Override
                        public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
                            BitmapCache.newInstance().putBitmap(c.img_url, data);
                        }
                        @Override
                        public void onLoaderReset(android.support.v4.content.Loader<Bitmap> loader) {
                        }
                    });
                }
            }
            @Override
            public void onLoaderReset(Loader<CouponListData> loader) {
            }
        });

    }

}
