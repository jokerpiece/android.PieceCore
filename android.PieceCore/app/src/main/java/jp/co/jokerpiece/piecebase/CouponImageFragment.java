package jp.co.jokerpiece.piecebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import jp.co.jokerpiece.piecebase.api.GetCouponAPI;
import jp.co.jokerpiece.piecebase.data.CouponListData;
import jp.co.jokerpiece.piecebase.data.CouponListData.CouponData;
import jp.co.jokerpiece.piecebase.data.GetCouponData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync.DownloadImageSyncCallback;
import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;

public class CouponImageFragment extends Fragment implements OnClickListener, DownloadImageSyncCallback {
	public static final String TAG = "CouponImageFragment";

	private Context context;
	private Handler handler;
	private ViewPager viewPager;
	private ViewPagerIndicator viewPagerIndicator;
	private CouponListData couponData;
    private View viewMask;

	private RelativeLayout view;
	private ImageView imageView;
	private LinearLayout llGetCoupon;

	private String imageURL;

	private static int loderCount = 1000;
	private boolean modeUse;
	public CouponImageFragment() {
		super();
	}
	public CouponImageFragment(GetCouponData getCouponData, String imageURL,boolean modeUse) {
		 this.context = getCouponData.context;
		 this.handler = getCouponData.handler;
		 this.viewPager = getCouponData.viewPager;
		 this.viewPagerIndicator = getCouponData.viewPagerIndicator;
		 this.couponData = getCouponData.couponData;
		 this.imageURL = imageURL;

		 this.modeUse = modeUse;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			    ViewGroup container,
			    Bundle savedInstanceState) {
		view = (RelativeLayout) inflater.inflate(R.layout.fragment_coupon_image, null);

        viewMask = (View)view.findViewById(R.id.view_mask);
		imageView = (ImageView)view.findViewById(R.id.header_image);
		getLoaderManager();

		if(savedInstanceState != null){
			imageURL = savedInstanceState.getString("imageURL");
		}
		if(imageURL != null){
			//view.addView(imageView);
			DownloadImageSync sync = new DownloadImageSync(getActivity(), imageURL, imageView,this);
			if(!sync.loadImageView()){
				getActivity().getSupportLoaderManager().initLoader(loderCount++, null, sync);
			}

		}

		llGetCoupon = (LinearLayout) view.findViewById(R.id.ll_getcoupon);
		if(!modeUse){
			llGetCoupon.setOnClickListener(this);

			Display display = getActivity().getWindowManager().getDefaultDisplay();
			Point point = new Point();
			display.getSize(point);
			int scale = point.y * 16 / 100;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llGetCoupon.getLayoutParams();
			params.setMargins(0, 0, 0, scale);
			params.height = scale * 8 / 10;
			llGetCoupon.setLayoutParams(params);
		}else{
            viewMask.setVisibility(View.INVISIBLE);
			llGetCoupon.setVisibility(View.INVISIBLE);
		}
		if(modeUse){
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    CouponData currentCouponData = couponData.data_list.get(viewPager.getCurrentItem());
                    //クリップボードにアイテムコードを格納
                    ClipData.Item item = new ClipData.Item(currentCouponData.coupon_code);
                    String[] mimeType = new String[1];
                    mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;
                    ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
                    ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(cd);


                    if ( currentCouponData.coupon_url != null && !currentCouponData.coupon_url.equals("")) {
                        FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        WebViewFragment fragment = new WebViewFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("send_url", currentCouponData.coupon_url);
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment);
                        //ft.addToBackStack(null);
                        ft.commit();
                    } else {

                        Toast.makeText(context, getString(R.string.coupon_copy_clipboard), Toast.LENGTH_LONG).show();
                        if (currentCouponData.item_url != null) {
                            //item_urlが存在する場合は該当ページに遷移
                            FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();

                            WebViewFragment fragment = new WebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("send_url", currentCouponData.item_url);
                            fragment.setArguments(bundle);
                            ft.replace(R.id.fragment, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        } else if (currentCouponData.category_id != null) {
                            // category_idが存在する場合は該当の商品一覧に遷移
                            MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
//						FragmentManager fm = getParentFragment().getFragmentManager();
//						FragmentTransaction ft = fm.beginTransaction();
//						ft.addToBackStack(null);
//						ShoppingGoodsFragment fragment = new ShoppingGoodsFragment();
//						Bundle bundle = new Bundle();
//						bundle.putString("category_id", currentCouponData.category_id);
//						bundle.putString("coupon_id", currentCouponData.coupon_id);
//						fragment.setArguments(bundle);
//						ft.replace(R.id.fragment, fragment);
//						ft.commit();
                        } else {
                            //どちらもない場合はカテゴリ一覧に遷移
                            MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
//						FragmentManager fm = getParentFragment().getFragmentManager();
//						FragmentTransaction ft = fm.beginTransaction();
//						ft.addToBackStack(null);
//						ShoppingFragment fragment = new ShoppingFragment();
//						Bundle bundle = new Bundle();
//						bundle.putString("coupon_id", currentCouponData.coupon_id);
//						fragment.setArguments(bundle);
//						ft.replace(R.id.fragment, fragment);
//						ft.commit();
                        }
                    }
                }
			});
		}
		return view;
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageURL", imageURL);
    }

	@Override
	public void onDestroyView() {
		view.removeAllViews();
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_getcoupon) {
			if (context != null) {
				registCoupon();
			}
		}
	}

	private void registCoupon(){
		((Activity) context).getLoaderManager().initLoader(loderCount++, null, new LoaderCallbacks<Boolean>(){
			@Override
			public Loader<Boolean> onCreateLoader(int id, Bundle args) {
				GetCouponAPI getCouponAPI = new GetCouponAPI(context, couponData.data_list.get(viewPager.getCurrentItem()).coupon_id);
				getCouponAPI.forceLoad();
				return getCouponAPI;
			}

			@Override
			public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
				// ここにデータ取得時の処理を書く
				if (data) {
				    Animation outToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.right_out);
					llGetCoupon.setAnimation(outToRightAnimation);
					llGetCoupon.setVisibility(View.GONE);

					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AppUtil.debugLog(TAG, "CouponUseActivityにintentを投げます。(couponId: " + couponData.data_list.get(viewPager.getCurrentItem()).coupon_id + ")");

                            viewPager.setVisibility(View.GONE);
                            viewPagerIndicator.setVisibility(View.GONE);

                            FragmentManager fm = getParentFragment().getFragmentManager();
//							FragmentManager fm = getActivity().getSupportFragmentManager();
							FragmentTransaction ft = fm.beginTransaction();

							CouponUseFragment fragment = new CouponUseFragment();
							Bundle bundle = new Bundle();
							bundle.putString("couponId", couponData.data_list.get(viewPager.getCurrentItem()).coupon_id);
							fragment.setArguments(bundle);
							ft.replace(R.id.fragment, fragment);
                            ft.addToBackStack(null);
							ft.commit();
					}
					}, 1000);
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "クーポンを取得できませんでした。", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}

			@Override
			public void onLoaderReset(Loader<Boolean> loader) {
			}
        });
	}

	@Override
	public void setImageCallbackWithURL(Bitmap bitmap, String url) {
		if(imageView != null && bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
	}

}
