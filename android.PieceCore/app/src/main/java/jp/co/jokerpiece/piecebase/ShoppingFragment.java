package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.CategoryListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CategoryListData;
import jp.co.jokerpiece.piecebase.data.CategoryListData.CategoryData;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BitmapCache;
import jp.co.jokerpiece.piecebase.util.BitmapDownloader;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;

public class ShoppingFragment extends BaseFragment implements OnItemClickListener {
    Context context;

    static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    RelativeLayout rl;
    SearchView searchView;
    Button btn_Cancel;
    CategoryListData categoryData = null;
    ArrayList<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();

    InputMethodManager inputMethodManager;
    ListView shoppingListView;
    String strSearch = "";

//	private int loderCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        Common.setCurrentFragment(Config.ShoppingFragmentNum);
        View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);
        rl = (RelativeLayout)rootView.findViewById(R.id.Rl_search);
        shoppingListView = (ListView)rootView.findViewById(R.id.shoppingListView);
//        getActionBar().setIcon(R.drawable.icon_shopping);
//        getActionBar().setTitle(R.string.genre_list);
//        if(!Config.PROPERTY_ID.equals("") && Config.PROPERTY_ID != null){
//            App app = (App)getActivity().getApplication();
//            Tracker t = app.getTracker();
//            t.setScreenName(getString(R.string.shopping0));
//            t.send(new HitBuilders.ScreenViewBuilder().build());
//        }
        if(Config.SEARCHMODE.equals("true")) {
            rl.setVisibility(View.VISIBLE);
            searchView = (SearchView) rootView.findViewById(R.id.searchView);
            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            btn_Cancel = (Button) rootView.findViewById(R.id.search_cancel);
            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
            searchView.setQueryHint("名前で検索");
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    strSearch = query;

                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    FragmentManager fm = ((MainBaseActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ShoppingGoodsFragment fragment = new ShoppingGoodsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("searchKeyword", strSearch);
                    fragment.setArguments(bundle);
                    ft.replace(R.id.fragment, fragment);
                    ft.addToBackStack(null);

                    ft.commit();

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

            });
        }else{
            rl.setVisibility(View.GONE);
        }
        if(SaveData.Categorydata != null){
            showCategoryView();
        }else {
            getGenreList();
        }
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        if(Config.ANALYTICS_MODE.equals("true")){
            App app = (App)getActivity().getApplication();
            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
            t.setScreenName(getString(R.string.shopping0));
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
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

        //カートのURLがセットされている場合のみ
        //カートの画像を表示する。
        if(Config.CARTURL !="" &&   Config.CARTURL != null) {
            inflater.inflate(R.menu.menu_cart, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(Config.CARTURL !="" &&   Config.CARTURL != null) {
            if(Config.WEBVIEW_ACTIVITY_MODE.equals("true")) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("send_url", Config.CARTURL);
                context.startActivity(intent);
            }else {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                WebViewFragment fragment = new WebViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("send_url", Config.CARTURL);
                fragment.setArguments(bundle);
                ft.replace(R.id.fragment, fragment);
                ft.commit();
            }
        }
        return true;
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
        if(Config.SEARCHMODE.equals("true")) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ShoppingGoodsFragment fragment = new ShoppingGoodsFragment();
        Bundle bundle = new Bundle();
        //bundle.putInt("category_id", data.category_id);
        bundle.putString("category_id", data.category_id);
        bundle.putString("category_img_url", data.img_url);
        bundle.putString("category_text", data.category_text);
        bundle.putString("category_name", data.category_name);

        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment);
        ft.addToBackStack(null);

        ft.commit();
    }

    //Get category list
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
