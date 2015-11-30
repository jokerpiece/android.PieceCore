package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.CategoryListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CategoryListData;
import jp.co.jokerpiece.piecebase.data.CategoryListData.CategoryData;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BitmapCache;
import jp.co.jokerpiece.piecebase.util.BitmapDownloader;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;

public class ShoppingFragment extends BaseFragment implements OnItemClickListener {
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
        AppUtil.debugLog("mAdapter", "6");
        Common.setCurrentFragment(Config.ShoppingFragmentNum);
		View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);

		shoppingListView = (ListView)rootView.findViewById(R.id.shoppingListView);
//        getActionBar().setIcon(R.drawable.icon_shopping);
//        getActionBar().setTitle(R.string.genre_list);
        if(SaveData.Categorydata != null){
            showCategoryView();
        }else {
            getGenreList();
        }
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
        if(SaveData.Categorydata != null) {
            categoryData = SaveData.Categorydata;
        }
//            shoppingListView.setVisibility(View.VISIBLE);
//            shoppingListView.setAdapter(Config.Sadapter);
//            shoppingListView.setOnItemClickListener(this);
//        }else {
            if (categoryData.data_list != null && categoryData.data_list.size() >= 1) {
                shoppingListView.setVisibility(View.VISIBLE);

                ShoppingListAdapter adapter = new ShoppingListAdapter(context,
                        R.layout.adapter_shopping_category_list,
                        categoryData.data_list,
                        ((FragmentActivity) context).getSupportLoaderManager());
                shoppingListView.setAdapter(adapter);
                shoppingListView.setOnItemClickListener(this);
            } else {
                shoppingListView.setVisibility(View.GONE);
            }

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CategoryData data = categoryData.data_list.get(position);
		FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ShoppingGoodsFragment fragment = new ShoppingGoodsFragment();
		Bundle bundle = new Bundle();
		//bundle.putInt("category_id", data.category_id);
        bundle.putString("category_id",data.category_id);
        bundle.putString("category_img_url", data.img_url);
		fragment.setArguments(bundle);
		ft.replace(R.id.fragment, fragment);
        ft.addToBackStack(null);

        ft.commit();
	}

	private void getGenreList(){
        Loader l = ((Activity)context).getLoaderManager().getLoader(Config.loaderCnt);
        if (l != null){
            return;
        }
        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<CategoryListData>(){
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

    @Override
    public void doInSplash(final Activity activity) {
        super.doInSplash(activity);

        Loader l = activity.getLoaderManager().getLoader(Config.loaderCnt);
        if (l != null){
            return;
        }
        activity.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<CategoryListData>() {
            @Override
            public Loader<CategoryListData> onCreateLoader(int id, Bundle args) {
                CategoryListAPI categoryAPI = new CategoryListAPI(activity);
                categoryAPI.forceLoad();
                return categoryAPI;
            }

            @Override
            public void onLoadFinished(Loader<CategoryListData> loader, CategoryListData data) {
                if (data == null) {
                    Common.serverErrorMessage(activity);
                    return;
                }
                SaveData.Categorydata = data;
                for(final CategoryData c : data.data_list) {

                    ((MainBaseActivity) activity).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
                        @Override
                        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
                            BitmapDownloader bmDownloader = new BitmapDownloader( activity, c.img_url);
                            bmDownloader.forceLoad();
                            return bmDownloader;
                        }

                        @Override
                        public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
                            if(data != null) {
                                BitmapCache.newInstance().putBitmap(c.img_url, data);
                            }
                        }

                        @Override
                        public void onLoaderReset(android.support.v4.content.Loader<Bitmap> loader) {

                        }
                    });
                }
            }

            @Override
            public void onLoaderReset(Loader<CategoryListData> loader) {
            }

        });



    }
}
