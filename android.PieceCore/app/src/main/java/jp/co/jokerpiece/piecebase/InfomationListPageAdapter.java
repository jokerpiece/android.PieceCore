package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.data.NewsListData;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class InfomationListPageAdapter extends FragmentPagerAdapter {
	Context context;
	NewsListData data;

	public InfomationListPageAdapter(FragmentManager fm ,Context context, NewsListData data) {
		super(fm);
		this.context = context;
		this.data = data;
	}

	@Override
	  public Fragment getItem(int i) {

	    switch(i){
	    case 0:
	    	return new InfomationListFragment(data, NewsListData.NEWS_DATE_TYPE_ALL);
	    case 1:
	    	return new InfomationListFragment(data, NewsListData.NEWS_DATA_TYPE_INFOMATION);
	    case 2:
	    	return new InfomationListFragment(data, NewsListData.NEWS_DATA_TYPE_FLYER);
	    case 3:
	    	return new InfomationListFragment(data, NewsListData.NEWS_DATA_TYPE_COUPON);
	    default:
	    	return new InfomationListFragment();
	    }

	  }

	  @Override
	  public int getCount() {
	    return 4;
	  }

	  @Override
	  public CharSequence getPageTitle(int position) {
		  String title = "";
		  switch (position) {
		case 0:
			title = context.getResources().getString(R.string.all);
			break;
		case 1:
			title = context.getResources().getString(R.string.info);
			break;
		case 2:
			title = context.getResources().getString(R.string.flyer);
			break;
		case 3:
			title = context.getResources().getString(R.string.coupon);
			break;
		default:
			break;
		}
	    return title;
	  }
}
