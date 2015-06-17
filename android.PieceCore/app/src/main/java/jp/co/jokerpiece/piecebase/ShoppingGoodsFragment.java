package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;
import java.util.List;

import jp.co.jokerpiece.piecebase.api.ItemListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.ItemListData;
import jp.co.jokerpiece.piecebase.data.ItemListData.ItemData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class ShoppingGoodsFragment extends Fragment implements OnItemClickListener, OnScrollListener, OnQueryTextListener {
	final Context context = App.getContext();

	static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
	static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	ItemListData itemData = null;
	ArrayList<ItemData> addItem = null;
	List<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();

	ListView goodsListView;
	ShoppingGoodsListAdapter adapter = null;
	FrameLayout categoryBase;
	TextView tvItemCount;
	//int categoryID= -1;
    String categoryID;
	String searchKeyword = null;
	String couponID = null;
    String categoryImgUrl = null;

	boolean connecting = false;
	int nowPageCount = 1;
    int quantity = -1;

//	private int loderCount = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_shopping_goods_list, container, false);

		goodsListView = (ListView)rootView.findViewById(R.id.goodsListView);
        categoryBase = (FrameLayout)rootView.findViewById(R.id.categoryImageBase);
        tvItemCount = (TextView)rootView.findViewById(R.id.itemCount);

//        getActionBar().setIcon(R.drawable.icon_shopping);
//        getActionBar().setTitle(R.string.goods_list);
        Bundle bundle = getArguments();
        if (bundle != null) {
	        //categoryID = bundle.getInt("category_id", -1);
            categoryID = bundle.getString("category_id");
	        searchKeyword = bundle.getString("searchKeyword");
	        couponID = bundle.getString("coupon_ID");
            categoryImgUrl = bundle.getString("category_img_url");
        }

//        getItemList();

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
				MainBaseActivity.titleOfActionBar.get(ShoppingGoodsFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
//		if(itemData == null){
			connecting = false;
			getItemList();
//		}else{
//			for(DownloadImageView iv : alImageViewList){
//				if(!iv.loadImageView()){
//					getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++,null,iv);
//				}
//			}
//		}
	}

	@Override
	public void onPause() {
		super.onPause();
		adapter = null;
	}

	private void showItemView() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		float scale = (float)point.x / 640.0f;
		if(categoryImgUrl != null){
			DownloadImageView categoryImage = new DownloadImageView(context);
            categoryImage.setTag(categoryImgUrl);
			if(!categoryImage.setImageURL(categoryImgUrl)){
				getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++,null,categoryImage);
			}
			categoryImage.setLayoutParams(new LayoutParams((int)(640 * scale),(int)(160 * scale)));
			categoryBase.addView(categoryImage);
			alImageViewList.add(categoryImage);
			categoryBase.setVisibility(View.VISIBLE);
		}else{
			categoryBase.setVisibility(View.GONE);
		}
        try {
            quantity = Integer.valueOf(itemData.quantity);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
		tvItemCount.setText(getResources().getString(R.string.item_count) + itemData.quantity);
		if(itemData.data_list != null && itemData.data_list.size() >= 1){
			if(adapter == null){
			adapter = new ShoppingGoodsListAdapter(context,
					R.layout.adapter_shopping_goods_list,itemData.data_list,
					getActivity().getSupportLoaderManager());
				goodsListView.setAdapter(adapter);
				goodsListView.setVisibility(View.VISIBLE);
				goodsListView.setOnItemClickListener(this);
				goodsListView.setOnScrollListener(this);
			}else{
				adapter.addAll(addItem);
				addItem = null;
			}

		}else{
			goodsListView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ItemData data = itemData.data_list.get(position);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);
		WebViewFragment fragment = new WebViewFragment();
		Bundle bundle = new Bundle();
		bundle.putString("send_url", data.item_url);
		fragment.setArguments(bundle);
		ft.replace(R.id.fragment, fragment);
		ft.commit();
	}
	private void getItemList(){
		connecting = true;
        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<ItemListData>(){
			@Override
			public Loader<ItemListData> onCreateLoader(int id, Bundle args) {
				 ItemListAPI itemAPI = null;
				 if(couponID == null){
					 itemAPI =new ItemListAPI(context, categoryID, searchKeyword, getListNum(nowPageCount, quantity));
				 }else{
					 itemAPI =new ItemListAPI(context, couponID, getListNum(nowPageCount, quantity));
				 }
				 itemAPI.forceLoad();
				 return itemAPI;
				}

			@Override
			public void onLoadFinished(Loader<ItemListData> loader, ItemListData data) {
				if(data == null){
					Common.serverErrorMessage(context);
					return;
				}
				if(data != null){
					if(itemData == null){
						itemData = data;
					}else{
						itemData.more_flg = data.more_flg;
						//itemData.data_list.addAll(data.data_list);
						addItem = data.data_list;
					}
					nowPageCount++;
					connecting = false;
					showItemView();
				}

			}

			@Override
			public void onLoaderReset(Loader<ItemListData> loader) {
			}
        });
	}

    public int getListNum(int page, int quantity) {
        if (quantity < 0) {
            return 0;
        } else {
            return (quantity > ((page - 1) * 10) ? ((page - 1) * 10) : quantity);
        }
    }

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (totalItemCount != 0 && totalItemCount - visibleItemCount / 2 <= firstVisibleItem + visibleItemCount) {
			if(itemData.more_flg && !connecting){
				getItemList();
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
        // サーチは実装できています
        // サーチを実装する場合は以下のコメントを外してください。
        // (注)サーチ後のquantityが正しく返っておりません。
//		if(Config.SEARCH_PERMISSION){
//	        inflater.inflate(R.menu.search_menu, menu);
//
//	        // SearchViewを取得する
//	        final MenuItem searchItem = menu.findItem(R.id.searchView);
//	        final SearchView searchView = (SearchView) searchItem.getActionView();
//	        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//	            @Override
//	            public void onFocusChange(View view, boolean queryTextFocused) {
//	                if(!queryTextFocused) {
//	                	searchItem.collapseActionView();
//	                }
//	            }
//	        });
//	        AutoCompleteTextView queryText = (AutoCompleteTextView) searchView.findViewById(this.getResources().getIdentifier("android:id/search_src_text", null, null));
//	        queryText.setTextColor(getResources().getColor(R.color.theme_text_color));
//
//	        searchView.setOnQueryTextListener(this);
//		}
        // ここまで
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

    @Override
    public boolean onQueryTextSubmit(String query) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    	if(!query.isEmpty()){
    		itemData = null;
            couponID = null;
    		adapter = null;
    		searchKeyword = query;
    		nowPageCount = 1;

    		getItemList();
    	}
        return false;
    }
}
