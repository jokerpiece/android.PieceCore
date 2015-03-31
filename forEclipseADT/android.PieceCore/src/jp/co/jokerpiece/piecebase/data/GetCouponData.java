package jp.co.jokerpiece.piecebase.data;

import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;

public class GetCouponData {
	public Context context;
	public Handler handler;
	public ViewPager viewPager;
	public ViewPagerIndicator viewPagerIndicator;
	public CouponListData couponData;

	public GetCouponData(
			Context context,
			Handler handler,
			ViewPager viewPager,
			ViewPagerIndicator viewPagerIndicator,
			CouponListData couponData) {
		super();
		this.context = context;
		this.handler = handler;
		this.viewPager = viewPager;
		this.viewPagerIndicator = viewPagerIndicator;
		this.couponData = couponData;
	}

}
