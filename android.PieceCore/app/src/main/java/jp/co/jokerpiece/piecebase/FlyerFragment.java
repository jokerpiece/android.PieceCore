package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.jokerpiece.piecebase.api.FlyerListAPI;
import jp.co.jokerpiece.piecebase.api.ItemListAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.FlyerData;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import jp.co.jokerpiece.piecebase.data.ItemListData;
import jp.co.jokerpiece.piecebase.data.SaveData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.BitmapCache;
import jp.co.jokerpiece.piecebase.util.BitmapDownloader;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;
import jp.co.jokerpiece.piecebase.util.ViewPagerIndicator;

public class FlyerFragment extends BaseFragment implements OnPageChangeListener {
    Context context;
    FlyerTimerTask timerTask = null;
    Timer mTimer = null;
    Handler mHandler = new Handler();

    static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    ArrayList<DownloadImageView> alImageViewList = new ArrayList<DownloadImageView>();
    ViewPager viewPagerScroll;
    private ViewPagerIndicator viewPagerIndicator;
    //	private int loderCount = 0;
    LinearLayout llFlyerBase;

    public Button btnSendOtherGoods;

    protected FlyerData flyerData;
    int flyer_ID = -1;


    int headerNowPage = 0;
    public FlyerImagePageAdapter pageFlagment;

    //ItemListAPI
    public static ItemListData itemData = null;

    //String item_id_flyer;


    public void setFragmentPagerAdapter() {
        pageFlagment = new FlyerImagePageAdapter(getChildFragmentManager(),
                context,
                new ArrayList<FlyerHeaderData>());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        Common.setCurrentFragment(Config.FlyerFragmentNum);
        View rootView = inflater.inflate(R.layout.fragment_flyer, container, false);

        if (pageFlagment != null) {
            pageFlagment.notifyDataSetChanged();
        }

        llFlyerBase = (LinearLayout) rootView.findViewById(R.id.flyer_base);
        btnSendOtherGoods = (Button) rootView.findViewById(R.id.buttonSendShopping);
        btnSendOtherGoods.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendView(v);
            }
        });
        viewPagerScroll = (ViewPager) rootView.findViewById(R.id.headerScroll);
        viewPagerScroll.setVisibility(View.GONE);
        viewPagerIndicator = (ViewPagerIndicator) rootView.findViewById(R.id.indicator);
        viewPagerIndicator.setCurrentPosition(viewPagerScroll.getCurrentItem());
        //インテントなどでflyerIDを受け取っていない場合は
        Bundle bundle = getArguments();
        if (bundle != null) {
            flyer_ID = bundle.getInt("flyer_ID");
        }
        if(Config.ANALYTICS_MODE.equals("true")){
            App app = (App)getActivity().getApplication();
            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
            t.setScreenName(getString(R.string.flyer0));
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
//        if(flyer_ID < 0){
//        	getHomeFlyerID();
//        }else{
//        	getFlyerWithID(flyer_ID);
//        }
//        pageFlagment = new FlyerImagePageAdapter(getChildFragmentManager(),
//        		context,
//        		new ArrayList<FlyerHeaderData>());

        setFragmentPagerAdapter();


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

    private void ShowFlyerView() {
        if (flyerData == null) {
            return;
        }
        if (SaveData.Flyerdata != null) {
            flyerData = SaveData.Flyerdata;
            SaveData.Flyerdata = null;
        }
        if (llFlyerBase != null) {
            llFlyerBase.removeAllViews();
        }

        alImageViewList.clear();
        pageFlagment.refresh();
        pageFlagment.destroyAllItem(viewPagerScroll);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        float scale = (float) point.x / 640.0f;
        RelativeLayout.LayoutParams vplp = new RelativeLayout.LayoutParams(MP, (int) (388 * scale));
        viewPagerScroll.setLayoutParams(vplp);
        viewPagerScroll.setOnPageChangeListener(this);

        if (flyerData.header_list != null && flyerData.header_list.size() >= 1) {
            for (FlyerData.FlyerHeaderData data : flyerData.header_list) {
                pageFlagment.addImageView(data);
            }
            viewPagerScroll.setVisibility(View.VISIBLE);
            viewPagerScroll.setAdapter(pageFlagment);
            viewPagerIndicator.setCount(pageFlagment.getCount());
            viewPagerIndicator.setCurrentPosition(viewPagerScroll.getCurrentItem());

            pageFlagment.notifyDataSetChanged();
            autoScroll();

        } else {
            viewPagerScroll.setVisibility(View.GONE);
        }
        int count = 0;
        LinearLayout llBase = null;

        //flyerData.body_list
        for (FlyerData.FlyerBodyData data : flyerData.body_list)
        {
            if (count % 2 == 0)
            {
                llBase = new LinearLayout(context);
                MarginLayoutParams baseLp = new MarginLayoutParams(MP, WC);
                baseLp.setMargins(0, 4, 0, 0);
                llFlyerBase.addView(llBase, baseLp);
            }
            DownloadImageView dlIv = new DownloadImageView(context);
            if (!dlIv.setImageURL(data.img_url))
            {
                ((FragmentActivity) context).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, dlIv);
            }

            LayoutParams lp = new LinearLayout.LayoutParams((int) (320 * scale), (int) (320 * scale));
            dlIv.setScaleType(ScaleType.CENTER_CROP);

            //get data from API
            final String itemURL = data.item_url;
            final String imgURL = data.img_url;
            final String itemId = data.item_id;//Flyer Date's item_id
            //item_id_flyer=data.item_id;


            //body list on click
            dlIv.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onClickFlyer(itemURL, imgURL, itemId);
                }
            });

            llBase.addView(dlIv, lp);
            alImageViewList.add(dlIv);
            count++;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(FlyerFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();

        if(GcmIntentService.start_from_notification){
            GcmIntentService.start_from_notification = false;
            if(MainBaseActivity.intentClassName != null){
                MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition(MainBaseActivity.intentClassName));
            }else{
                MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition(InfomationFragment.class.getSimpleName()));
            }
        }
        if (!MainBaseActivity.startFromSchemeFlg) {
            Intent intent = getActivity().getIntent();
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                Uri uri = intent.getData();
                if (uri != null) {
                    String order_num = "1";
                    order_num = uri.getQueryParameter("order_num");
                    Intent i = new Intent(context, LoginActivity.class);
                    i.putExtra("order_num", order_num);
                    context.startActivity(i);
//					FragmentManager fm = ((MainBaseActivity)context).getSupportFragmentManager();
//					FragmentTransaction ft = fm.beginTransaction();
//					LoginFragment fragment = new LoginFragment();
//					Bundle bundle = new Bundle();
//					bundle.putString("order_num", order_num);
//					fragment.setArguments(bundle);
//					ft.replace(R.id.fragment, fragment);
//					ft.addToBackStack(null);
//					ft.commit();
                }
            }
        }
        //		if(alImageViewList != null && alImageViewList.size() >= 1){
//			for(DownloadImageView iv : alImageViewList){
//				if(!iv.loadImageView()){
//					getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++,null,iv);
//				}
//			}
//		}else{
//			getHomeFlyerID();
//		}

        if (AppUtil.getPrefString(context, "FLYERID", "").equals("0") || AppUtil.getPrefString(context, "FLYERID", "").equals("")) {
            getFlyerWithID(0);
        } else {
            flyer_ID = Integer.parseInt((AppUtil.getPrefString(context, "FLYERID", "")));
            getFlyerWithID(flyer_ID);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//        inflater.inflate(R.menu.menu_coupon, menu);
        //カートのURLがセットされている場合のみ
        //カートの画像を表示する。
        if(Config.CARTURL !="" &&   Config.CARTURL != null) {
            inflater.inflate(R.menu.menu_cart, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onClickFlyer(String url, String imgurl, String item_id_flyer) {


        if (url != null && !url.equals("") && !url.equals("null"))
        {

            //遷移先のURLがURLではない場合、flyerから商品購入画面に遷移する。
            if (!url.startsWith("paypal"))
            {
                if(Config.WEBVIEW_ACTIVITY_MODE.equals("true"))
                {
                    if(!item_id_flyer.equals(""))// item_id has value
                    {
                        AppUtil.debugLog("item_id_flyer",item_id_flyer);
                        if("1".equals(Config.PAY_SELECT_KBN))//LinePay Native
                        {
                            //get Item by ItemListAPI
                            getItemList(item_id_flyer);

                        }
                        else if ("2".equals(Config.PAY_SELECT_KBN))//Paypal Native
                        {
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.addToBackStack(null);
                            PayPalPieceFragment fragment = new PayPalPieceFragment();

                            Bundle bundle = new Bundle();

                            //bundle.putString("item_id", data.item_id);
                            //bundle.putString("price", data.price);
                            //bundle.putString("stocks", data.stocks);
                            //bundle.putString("img_url", data.img_url);
                            //bundle.putString("item_title", data.item_title);
                            //bundle.putString("text", data.text);
                            fragment.setArguments(bundle);
                            ft.replace(R.id.fragment, fragment);
                            ft.commit();
                            //Paypal決済できる詳細画面に遷移する。

                        }
                        else
                        {
                            Intent intent = new Intent(context, WebViewActivity.class);
                            intent.putExtra("send_url", url);
                            context.startActivity(intent);
                        }
                    }
                    else// item_id = null ＞＞＞＞go WebView
                    {
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("send_url", url);
                        context.startActivity(intent);
                    }


                }
                else //WEBVIEW_ACTIVITY_MODE = flase
                {
                    FragmentManager fm = ((MainBaseActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    WebViewFragment fragment = new WebViewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("send_url", url);
                    fragment.setArguments(bundle);
                    ft.replace(R.id.fragment, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            }
            else //url start with paypal
            {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                PayPalPieceFragment fragment = new PayPalPieceFragment();

                Bundle bundle = new Bundle();
                //渡された値よりパラメータの取得
                url = url.substring(url.indexOf(":") - 1);

                String[] param = url.split(",");
                bundle.putString("img_url", imgurl);
                bundle.putString("item_id", param[0]);
                bundle.putString("item_title", param[1]);
                bundle.putString("price", param[2]);

                bundle.putString("item_url", url);

                fragment.setArguments(bundle);
                ft.replace(R.id.fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
                //Paypal決済できる詳細画面に遷移する。
            }
        }
    }

    private void sendView(View v) {
        if (v == btnSendOtherGoods) {
            MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
//			FragmentManager fm = getFragmentManager();
//			FragmentTransaction ft = fm.beginTransaction();
//			ft.addToBackStack(null);
//			ShoppingFragment fragment = new ShoppingFragment();
//			ft.replace(R.id.fragment, fragment);
//			ft.commit();
        }
    }



    @Override
    public void onPageScrollStateChanged(int state) {
        if (ViewPager.SCROLL_STATE_DRAGGING == state) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        } else {
            autoScroll();
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        headerNowPage = position;
        viewPagerIndicator.setCurrentPosition(position);
    }

    private void autoScroll() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        timerTask = new FlyerTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule(timerTask, Config.scrollDelay);
    }

    class FlyerTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                public void run() {
                    headerNowPage++;
                    if (headerNowPage == pageFlagment.getCount()) {
                        headerNowPage = 0;
                    }
                    viewPagerScroll.setCurrentItem(headerNowPage, true);
                    autoScroll();
                }
            });
        }
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

    @Override
    public void onDestroy() {
        // TODO 自動生成されたメソッド・スタブ
        super.onDestroy();
        AppUtil.debugLog("FlyerActivity", "onDestroy");
    }

    @Override
    public void doInSplash(final Activity activity) {
        super.doInSplash(activity);
        Loader l = activity.getLoaderManager().getLoader(Config.loaderCnt);
        if (l != null) {
            return;
        }
        activity.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<FlyerData>() {
            @Override
            public Loader<FlyerData> onCreateLoader(int id, Bundle args) {
                FlyerListAPI flyerAPI = new FlyerListAPI(activity, 0);
                flyerAPI.forceLoad();
                return flyerAPI;
            }

            @Override
            public void onLoadFinished(Loader<FlyerData> loader, FlyerData data) {
                if (data == null) {
                    Common.serverErrorMessage(activity);
                    return;
                }

                SaveData.Flyerdata = data;

                for (final FlyerHeaderData c : data.header_list) {
                    ((MainBaseActivity) activity).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
                        @Override
                        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
                            BitmapDownloader bmDownloader = new BitmapDownloader(activity, c.img_url);
                            bmDownloader.forceLoad();
                            return bmDownloader;
                        }

                        @Override
                        public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
                            if (data != null) {
                                BitmapCache.newInstance().putBitmap(c.img_url, data);
                            }
                        }

                        @Override
                        public void onLoaderReset(android.support.v4.content.Loader<Bitmap> loader) {
                        }
                    });
                }
                for (final FlyerData.FlyerBodyData c : data.body_list) {
                    ((MainBaseActivity) activity).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
                        @Override
                        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
                            BitmapDownloader bmDownloader = new BitmapDownloader(activity, c.img_url);
                            bmDownloader.forceLoad();
                            return bmDownloader;
                        }

                        @Override
                        public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
                            if (data != null) {
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
            public void onLoaderReset(Loader<FlyerData> loader) {
            }
        });
    }





    //get FlyerListAPI data
    private void getFlyerWithID(final int flyerID) {
        this.flyer_ID = flyerID;
        ((Activity) context).getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<FlyerData>() {
            @Override
            public Loader<FlyerData> onCreateLoader(int id, Bundle args) {
                FlyerListAPI flyerAPI = new FlyerListAPI(context, flyerID);
                flyerAPI.forceLoad();
                return flyerAPI;
            }

            @Override
            public void onLoadFinished(Loader<FlyerData> loader, FlyerData data) {
                if (data == null) {
                    Common.serverErrorMessage(context);
                    return;
                }
                flyerData = data;

                ShowFlyerView();


            }

            @Override
            public void onLoaderReset(Loader<FlyerData> loader) {
            }
        });
    }





    //get good's detail
    private void getItemList(final String item_id_flyer)
    {

        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<ItemListData>()
        {

            @Override
            public Loader<ItemListData> onCreateLoader(int id, Bundle args)
            {
                ItemListAPI itemAPI = new ItemListAPI(context, item_id_flyer);
                itemAPI.forceLoad();
                return itemAPI;
            }

            @Override
            public void onLoadFinished(Loader<ItemListData> loader, ItemListData data)
            {

                Bundle bundle = new Bundle();

                if(data.data_list.size()!=0)
                {
                    for(int position=0; position<data.data_list.size(); position++) {
                        ItemListData.ItemData itemdata = data.data_list.get(position);


                        if (item_id_flyer.equals(itemdata.item_id)) {
                            AppUtil.debugLog("ItemListAPI on Finish IF", "If has been executed");
                            //detabaseの商品資料をLinePayFragmentに送る
                            bundle.putString("item_id", itemdata.item_id);
                            bundle.putString("price", itemdata.price);
                            bundle.putString("stocks", itemdata.stocks);
                            bundle.putString("img_url", itemdata.img_url);
                            bundle.putString("item_title", itemdata.item_title);
                            bundle.putString("text", itemdata.text);
                            bundle.putString("item_url", itemdata.item_url);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.addToBackStack(null);
                            LinePayFragment fragment = new LinePayFragment();


                            fragment.setArguments(bundle);
                            ft.replace(R.id.fragment, fragment);
                            ft.commit();
                            //LinePay決済できる詳細画面に遷移する。
                        }
                    }
                }
                else
                {
                    AppUtil.debugLog("ItemListAPI on Finish IF", "else has been executed");
                    new AlertDialog.Builder(FlyerFragment.this.getActivity())
                            .setTitle("申し訳ありませんが、商品が存在しません。")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }


                /*
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                LinePayFragment fragment = new LinePayFragment();


                fragment.setArguments(bundle);
                ft.replace(R.id.fragment, fragment);
                ft.commit();
                //LinePay決済できる詳細画面に遷移する。
                */

            }

            @Override
            public void onLoaderReset(Loader<ItemListData> loader) {
            }
        });
    }

    //If need
    public int getListNum(int page, int quantity) {
        if (quantity < 0) {
            return 0;
        } else {
            return (quantity > ((page - 1) * 10) ? ((page - 1) * 10) : quantity);
        }
    }


}
