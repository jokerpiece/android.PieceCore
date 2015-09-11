package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.api.NewsInfoAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsInfoData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfomationSyosaiFragment extends Fragment implements DownloadImageSync.DownloadImageSyncCallback {
	Context context;

	private TextView tvTitle;
	private TextView tvText;
	private TextView tvUrlTitle;
	private TextView tvUrl;
	private ImageView imageView;
	//View listView;
	LinearLayout llListView;
	LayoutInflater inflater;
	ViewGroup container;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();
		this.inflater = inflater;
		this.container = container;
		View rootView = inflater.inflate(R.layout.fragment_info_syosai, container, false);
		// findViews
		tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
		tvText = (TextView) rootView.findViewById(R.id.tv_text);

		imageView = (ImageView) rootView.findViewById(R.id.InfoImgView);
		llListView = (LinearLayout)rootView.findViewById(R.id.LinkListView);

		// データの取得
		getInfoSyosai();

		return rootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

	@Override
	public void onResume() {
		super.onResume();
		AppUtil.setTitleOfActionBar(
				getActivity().getActionBar(),
				MainBaseActivity.titleOfActionBar.get(InfomationSyosaiFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void getInfoSyosai(){
        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, getArguments(), new LoaderCallbacks<NewsInfoData>() {
			@Override
			public Loader<NewsInfoData> onCreateLoader(int id, Bundle args) {
				// news_idの取得
				String newsId = "";
				if (args != null) {
					newsId = args.getString("newsId");
				}

				NewsInfoAPI infoAPI = new NewsInfoAPI(context, newsId);
				infoAPI.forceLoad();
				return infoAPI;
			}

			@Override
			public void onLoadFinished(Loader<NewsInfoData> loader, NewsInfoData data) {
				if (data == null) {
					Common.serverErrorMessage(context);
					return;
				}
				// ここにデータ取得時の処理を書く
				if (data.title != null) {
					tvTitle.setText(data.title);
				}
				if (data.text != null) {
					tvText.setText(data.text);
				}

				makeView(data);

			}

			@Override
			public void onLoaderReset(Loader<NewsInfoData> loader) {

			}
		});
	}

	public void makeView(NewsInfoData data){
		if(data.data_list == null){
			return;
		}
		if(llListView!= null){
			llListView.removeAllViews();
		}
		DownloadImageSync sync = new DownloadImageSync(context, data.img_url, imageView,this);
		if(!sync.loadImageView()){
			getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++, null, sync);
		}
		LinearLayout llBase = null;
		if(data.data_list != null) {
			for (final NewsInfoData.LinkListData linkListData : data.data_list) {
				llBase = new LinearLayout(context);
				View listView = inflater.inflate(R.layout.infomationsyosai_list_view, container, false);
				tvUrlTitle = (TextView) listView.findViewById(R.id.InfoUrlTitleTv);
				tvUrlTitle.setText(linkListData.link_title);
				Pattern pattern = Pattern.compile(linkListData.link_title);
				Linkify.TransformFilter filter = new Linkify.TransformFilter() {
					@Override
					public String transformUrl(Matcher match, String url) {
						return linkListData.link_url;
					}
				};
				Linkify.addLinks(tvUrlTitle, pattern, linkListData.link_url, null, filter);
				llBase.addView(listView);
				llListView.addView(llBase);

			}
		}
	}

	@Override
	public void setImageCallbackWithURL(Bitmap bitmap, String url) {
		if(imageView != null && bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
	}
}
