package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.CouponListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CouponListData;
import jp.co.jokerpiece.piecebase.data.CouponListData.CouponData;
import jp.co.jokerpiece.piecebase.data.GetCouponData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CouponUseFragment extends Fragment implements OnPageChangeListener {
	Context context;
	private Activity activity;
	private ViewPager viewPager;
	private ViewPagerIndicator viewPagerIndicator;
	private TextView tvNoCoupon;
	private Handler handler = new Handler();

	private CouponImagePageAdapter pageFlagment;

//	private ArrayList<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();
	private CouponListData couponData = null;
//	private int loderCount = 0;

	private String couponId = "";
	private int initPos = -1;

//	private static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
//	private static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();
		View rootView = inflater.inflate(R.layout.fragment_coupon_use, container, false);

		activity = getActivity();
        viewPager = (ViewPager) rootView.findViewById(R.id.couponpager);
        viewPagerIndicator = (ViewPagerIndicator) rootView.findViewById(R.id.indicator);
        tvNoCoupon = (TextView) rootView.findViewById(R.id.tv_nocoupon);
        viewPager.setOnPageChangeListener(this);
        pageFlagment = new CouponImagePageAdapter(getChildFragmentManager(),
        		new GetCouponData(activity, handler, viewPager, viewPagerIndicator, couponData),
        		new ArrayList<String>(),true);

        Bundle bundle = getArguments();
        if (bundle != null) {
        	couponId = bundle.getString("couponId");
        }

        getCouponList();


		return rootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();

        inflater.inflate(R.menu.menu_coupon_use, menu);

//		MenuItem haveCoupon = menu.add(0 , Menu.FIRST, Menu.NONE ,"閉じる");
//		haveCoupon.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		haveCoupon.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int itemId = item.getItemId();
		if (itemId == R.id.action_coupon_use) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.addToBackStack(null);
			CouponFragment fragment = new CouponFragment();
			ft.replace(R.id.fragment, fragment);
			ft.commit();
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		AppUtil.setTitleOfActionBar(
				getActivity().getActionBar(),
				MainBaseActivity.titleOfActionBar.get(CouponUseFragment.class.getSimpleName()));
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

	private void getCouponList(){
        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<CouponListData>(){
			@Override
			public Loader<CouponListData> onCreateLoader(int id, Bundle args) {
				CouponListAPI couponAPI = new CouponListAPI(context, CouponListData.COUPON_DATA_TYPE_GIVEN);
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

		ArrayList<CouponData> couponList = couponData.data_list;
		if (couponList == null || couponList.size() == 0) {
			viewPager.setVisibility(View.GONE);
			tvNoCoupon.setVisibility(View.VISIBLE);
			return;
		}

		pageFlagment.destroyAllItem(viewPager);
		for (int i = 0; i < couponList.size(); i++) {
			CouponData data = couponList.get(i);
//			DownloadImageView dlIv = new DownloadImageView(getApplicationContext());
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
//			alImageViewList.add(dlIv);
			pageFlagment.addImageView(data.img_url);

			if ((data.coupon_id + "").equals(couponId)) {
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


}
