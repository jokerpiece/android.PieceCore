package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.api.NewsInfoAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsInfoData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfomationSyosaiFragment extends Fragment {
	Context context;

	private TextView tvTitle;
	private TextView tvText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();
		View rootView = inflater.inflate(R.layout.fragment_info_syosai, container, false);

		// findViews
		tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
		tvText = (TextView) rootView.findViewById(R.id.tv_text);

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
				if(data == null){
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
			}
			@Override
			public void onLoaderReset(Loader<NewsInfoData> loader) {

			}
		});
	}

}
