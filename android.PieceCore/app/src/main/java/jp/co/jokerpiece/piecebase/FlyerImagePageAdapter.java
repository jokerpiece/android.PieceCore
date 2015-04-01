package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

public class FlyerImagePageAdapter extends FragmentPagerAdapter{

	Context context;
	ArrayList<FlyerHeaderData> dataList;

	public FlyerImagePageAdapter(FragmentManager fm ,Context context,
			ArrayList<FlyerHeaderData> dataList) {
		super(fm);
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public Fragment getItem(int i) {
		return new FlyerImageFragment(dataList.get(i));
	}

	@Override
	public int getCount() {
		return dataList.size();
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
	        	e.printStackTrace();
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

	public void addImageView(FlyerHeaderData data){
		dataList.add(data);
		notifyDataSetChanged();
	}

	public void refresh() {
		dataList.clear();
		notifyDataSetChanged();
	}
}
