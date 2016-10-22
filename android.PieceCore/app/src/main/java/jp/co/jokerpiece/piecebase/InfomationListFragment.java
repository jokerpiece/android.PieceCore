package jp.co.jokerpiece.piecebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.data.NewsListData.NewsData;
import jp.co.jokerpiece.piecebase.util.AppUtil;

public class InfomationListFragment extends Fragment implements OnItemClickListener {
	public static final String TAG = "InfomationListFragment";

	private ListView lv;

	private InfoListAdapter adapter;
	private NewsListData data;
	private int type;
	/**
	 * コンストラクタ
	 */
	public InfomationListFragment() {
		super();
	}

	/**
	 * コンストラクタ
	 */
	public InfomationListFragment(NewsListData data, int type) {
		super();
		this.data = data;
		this.type = type;
	}

	@SuppressLint("InflateParams")
	@Override
	  public View onCreateView(LayoutInflater inflater,
	    ViewGroup container,
	    Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_infomation_list, null);

		lv = (ListView) rootView.findViewById(R.id.infomation_list_view);
		if(savedInstanceState != null){
			data = (NewsListData)savedInstanceState.getSerializable("InfomationDataList");
			type = savedInstanceState.getInt("InfomationType");
		}
		if (data != null) {
			adapter = new InfoListAdapter(getActivity(), getDataWithType(data.data_list));
			lv.setAdapter(adapter);
		}
		lv.setOnItemClickListener(this);

	    return rootView;
	}
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("InfomationDataList", data);
        outState.putInt("InfomationType", type);
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (data == null) { return; }

		ArrayList<NewsData> newsList = getDataWithType(data.data_list);
		//　表示順番を逆にするため
		NewsData newsData = newsList.get(data.data_list.size() - 1 - position);

		switch (newsData.type) {
		case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
			// お知らせ詳細画面に遷移
			FragmentManager fmInfo = getParentFragment().getFragmentManager();
			FragmentTransaction ftInfo = fmInfo.beginTransaction();
			ftInfo.addToBackStack(null);
			InfomationSyosaiFragment fragmentInfo = new InfomationSyosaiFragment();
			Bundle bundleInfo = new Bundle();
			bundleInfo.putString("newsId", newsData.news_id);
			fragmentInfo.setArguments(bundleInfo);
			ftInfo.replace(R.id.fragment, fragmentInfo);
			ftInfo.commit();
			break;
		case NewsListData.NEWS_DATA_TYPE_FLYER + "":
			// フライヤー画面に遷移
			AppUtil.debugLog("newsData.id",newsData.id);
			if(newsData.id != "null") {
				AppUtil.setPrefString(getActivity(), "FLYERID", newsData.id);
				MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Flyer"));
			}
//			FragmentManager fmFlyer = getParentFragment().getFragmentManager();
//			FragmentTransaction ftFlyer = fmFlyer.beginTransaction();
//			ftFlyer.addToBackStack(null);
//			FlyerFragment fragmentFlyer = new FlyerFragment();
//			try {
//				Bundle bundleFlyer = new Bundle();
//				bundleFlyer.putInt("flyer_ID", Integer.parseInt(newsData.id));
//				fragmentFlyer.setArguments(bundleFlyer);
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			}
//			ftFlyer.replace(R.id.fragment, fragmentFlyer);
//			ftFlyer.commit();
			break;
		case NewsListData.NEWS_DATA_TYPE_COUPON + "":
			// クーポン画面に遷移
			MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Coupon"));
//			FragmentManager fmCoupon = getParentFragment().getFragmentManager();
//			FragmentTransaction ftCoupon = fmCoupon.beginTransaction();
//			ftCoupon.addToBackStack(null);
//			CouponFragment fragmentCoupon = new CouponFragment();
//			Bundle bundleCoupon = new Bundle();
//			bundleCoupon.putString("coupon_id", newsData.id);
//			fragmentCoupon.setArguments(bundleCoupon);
//			ftCoupon.replace(R.id.fragment, fragmentCoupon);
//			ftCoupon.commit();
			break;
		}
	}

	/**
	 * タイプによってデータを選別する。
	 */
	public ArrayList<NewsData> getDataWithType(ArrayList<NewsData> list) {
		ArrayList<NewsData> ret = new ArrayList<NewsData>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				NewsData newsData = list.get(i);
				switch (type) {
				case NewsListData.NEWS_DATE_TYPE_ALL:
					ret.add(newsData);
					break;
				case NewsListData.NEWS_DATA_TYPE_INFOMATION:
					if (newsData.type.equals(NewsListData.NEWS_DATA_TYPE_INFOMATION + "")) {
						ret.add(newsData);
					}
					break;
				case NewsListData.NEWS_DATA_TYPE_FLYER:
					if (newsData.type.equals(NewsListData.NEWS_DATA_TYPE_FLYER + "")) {
						ret.add(newsData);
					}
					break;
				case NewsListData.NEWS_DATA_TYPE_COUPON:
					if (newsData.type.equals(NewsListData.NEWS_DATA_TYPE_COUPON + "")) {
						ret.add(newsData);
					}
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * アダプタ
	 */
	public class InfoListAdapter extends BaseAdapter {
		protected Context context;
		protected ArrayList<NewsData> list;

		public InfoListAdapter(Context context, ArrayList<NewsData> list) {
			this.context = context;
			setList(list);
		}

		public Context getContext() {
			return this.context;
		}

		public void setList(ArrayList<NewsData> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tvTitle, tvDate, tvNew;
			ImageView ivInfo;
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater =
						(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_informationlist, null);
			}

			//NewsData data = (NewsData)getItem(position);
			//　表示順番を逆にするため
			NewsData data = (NewsData)getItem(getCount() - 1 - position);

			if (data != null) {
				tvTitle = (TextView) v.findViewById(R.id.tv_title);
				ivInfo = (ImageView) v.findViewById(R.id.iv_info);
				tvDate = (TextView) v.findViewById(R.id.tv_date);

				String title = data.title;
				String type = data.type;
				String date = data.delivered_datetime;

				if (title != null) {
					tvTitle.setText(title);
				}

				if (type != null) {
					switch (type) {
						case "1":
							setImageResource(ivInfo, R.drawable.news_infomation);
							break;
						case "2":
							setImageResource(ivInfo, R.drawable.news_shopping);
							break;
						case "3":
							setImageResource(ivInfo, R.drawable.news_coupon);
							break;
					}
				}

				tvDate.setText(date);

				Log.d("position",Integer.toString(position));
			}

			tvNew = (TextView) v.findViewById(R.id.tv_new);

			if(position == 0)
			{
				tvNew.setText("new!");
			}
			else
			{
				tvNew.setText("");
			}


			return v;
		}

		public void setImageResource(ImageView iv, int resId) {
			iv.setVisibility(View.VISIBLE);
			iv.setImageResource(resId);
		}

	}

}
