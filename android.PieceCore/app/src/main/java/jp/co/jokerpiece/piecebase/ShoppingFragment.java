package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.CategoryListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CategoryListData;
import jp.co.jokerpiece.piecebase.data.CategoryListData.CategoryData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ShoppingFragment extends Fragment implements OnItemClickListener {
	Context context;

	static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
	static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	CategoryListData categoryData = null;
	ArrayList<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();

	ListView shoppingListView;
//	private int loderCount = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context = getActivity();
        if(Config.ShoppingFramentNum == 0) {
            if (Config.Savelist.size() == 1) {
                Config.Savelist.clear();
                Config.Savelist.add(0);
            }
            if (!Config.Backflg) {
                if (Config.FragmentCurrentNum != 0) {
                    Config.Savelist.add(Config.ShoppingFramentNum);
                    Config.FragmentCurrentNum += 1;
                }
            }
        }else{
            if(!Config.Backflg) {
                Config.Savelist.add(Config.ShoppingFramentNum);
                Config.FragmentCurrentNum += 1;
            }
        }
		View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);

		shoppingListView = (ListView)rootView.findViewById(R.id.shoppingListView);
//        getActionBar().setIcon(R.drawable.icon_shopping);
//        getActionBar().setTitle(R.string.genre_list);

        getGenreList();

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
				MainBaseActivity.titleOfActionBar.get(ShoppingFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
//		if(categoryData == null){
			getGenreList();
//		}else{
//			for(DownloadImageView iv : alImageViewList){
//				if(!iv.loadImageView()){
//					getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++,null,iv);
//				}
//			}
//		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
//        inflater.inflate(R.menu.menu_coupon, menu);
        super.onCreateOptionsMenu(menu, inflater);
	}

	private void showCategoryView() {
		if(categoryData.data_list != null && categoryData.data_list.size() >= 1){
			shoppingListView.setVisibility(View.VISIBLE);

			ShoppingListAdapter adapter = new ShoppingListAdapter(context,
					R.layout.adapter_shopping_category_list,
					categoryData.data_list,
					 getActivity().getSupportLoaderManager());
			shoppingListView.setAdapter(adapter);
			shoppingListView.setOnItemClickListener(this);
		}else{
			shoppingListView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CategoryData data = categoryData.data_list.get(position);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);
		ShoppingGoodsFragment fragment = new ShoppingGoodsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("category_id", data.category_id);
        bundle.putString("category_img_url", data.img_url);
		fragment.setArguments(bundle);
		ft.replace(R.id.fragment, fragment);
		ft.commit();
	}

	private void getGenreList(){
        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<CategoryListData>(){
			@Override
			public Loader<CategoryListData> onCreateLoader(int id, Bundle args) {
				 CategoryListAPI categoryAPI = new CategoryListAPI(context);
				 categoryAPI.forceLoad();
				 return categoryAPI;
				}

			@Override
			public void onLoadFinished(Loader<CategoryListData> loader, CategoryListData data) {
				if(data == null){
					Common.serverErrorMessage(context);
					return;
				}
				categoryData = data;
				showCategoryView();
			}

			@Override
			public void onLoaderReset(Loader<CategoryListData> loader) {
			}
        });
	}
}
