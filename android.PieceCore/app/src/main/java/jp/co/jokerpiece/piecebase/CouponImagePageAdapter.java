package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.data.GetCouponData;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

public class CouponImagePageAdapter extends FragmentPagerAdapter{
	private GetCouponData getCouponData;
	private ArrayList<String> imageUrlList;
	private boolean modeUse;
	public CouponImagePageAdapter(FragmentManager fm ,GetCouponData getCouponData,
			ArrayList<String> imageViewList ,boolean modeUse) {
		super(fm);
		this.getCouponData = getCouponData;
		this.imageUrlList = imageViewList;
		this.modeUse = modeUse;
	}

	@Override
	public Fragment getItem(int i) {
		return new CouponImageFragment(getCouponData, imageUrlList.get(i),modeUse);
	}

	@Override
	public int getCount() {
		return imageUrlList.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void destroyAllItem(ViewPager pager) {
		for (int i = 0; i < getCount() - 1; i++) {
			try {
				Object obj = this.instantiateItem(pager, i);
				if (obj != null) {
					destroyItem(pager, i, obj);
				}
	        } catch (Exception e) {
	        }
		}

		refresh();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);

	    if (position <= getCount()) {
	    	FragmentManager manager = ((Fragment) object).getFragmentManager();
	        FragmentTransaction trans = manager.beginTransaction();
	        trans.remove((Fragment) object);
	        trans.commitAllowingStateLoss();
	    }
	}

	public void addImageView(String imageURL){
		imageUrlList.add(imageURL);
		notifyDataSetChanged();
	}

	public void refresh() {
		imageUrlList.clear();
		notifyDataSetChanged();
	}

	public void setGetCouponData(GetCouponData getCouponData) {
		this.getCouponData = getCouponData;
	}
}
