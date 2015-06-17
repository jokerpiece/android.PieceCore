package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.api.StampAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.StampListData;
import jp.co.jokerpiece.piecebase.util.AppUtil;


/**
 * Created by kaku on 2015/05/08.
 */
public class StampFragment  extends Fragment {
    Context context;
    StampListData stampData;
    View rootView;
    GridView gridView;
    GridView redBarView;

    ImageView iV;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    Button btn;
    public static int currentPoint = 0;
    int SaveCurrentPoint;
    public static int  get_point;
    public static int total_point;
    SharedPreferences pref;

    Bitmap bmp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

//        if(Config.StampFragmentNum == 0) {
//            if (Config.Savelist.size() == 1) {
//                Config.Savelist.clear();
//                Config.Savelist.add(0);
//            }
//            if (!Config.Backflg) {
//                if (Config.FragmentCurrentNum != 0) {
//                    Config.Savelist.add(Config.StampFragmentNum);
//                    Config.FragmentCurrentNum += 1;
//                }
//            }
//        }else{
//            if(!Config.Backflg) {
//                Config.Savelist.add(Config.StampFragmentNum);
//                Config.FragmentCurrentNum += 1;
//            }
//        }
        Common.setCurrentFragment(Config.StampFragmentNum);
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_stamp, container, false);
        btn = (Button)rootView.findViewById(R.id.getcoupon);
        btn.setVisibility(View.GONE);

        getStampListData();

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();

        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(StampFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();
    }



    public void getStampListData(){

        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<StampListData>() {
            @Override
            public Loader<StampListData> onCreateLoader(int id, Bundle args) {
                // stamp_idの取得
                String stampId = "";
                if (args != null) {
                    stampId = args.getString("stamp_id");
                }

                StampAPI stampAPI = new StampAPI(context, stampId);
                stampAPI.forceLoad();
                return stampAPI;
            }
            @Override
            public void onLoadFinished(Loader<StampListData> loader, StampListData data) {
                if(data == null){
                    Common.serverErrorMessage(context);
                    return;
                }

                stampData = data;
                if(stampData.get_point != null && stampData.total_point != null){
                    get_point = Integer.valueOf(stampData.get_point).intValue();
                    total_point = Integer.valueOf(stampData.total_point).intValue();
                    showTextView();
                    showStampView();
                }


            }
            @Override
            public void onLoaderReset(Loader<StampListData> loader) {

            }

        });
    }
    private void showTextView() {
        //部分文字を取るため
        String startMon = stampData.start_date.substring(5, 6) + "月";
        String startDay = stampData.start_date.substring(7, 8) + "日　～　";
        String endMon = stampData.end_date.substring(5, 6) + "月";
        String endDay = stampData.end_date.substring(7, 8) + "日まで";

        tv1 = (TextView)rootView.findViewById(R.id.Startmonth);
        tv2 = (TextView)rootView.findViewById(R.id.Startday);
        tv3 = (TextView)rootView.findViewById(R.id.Endmonth);
        tv4 = (TextView)rootView.findViewById(R.id.Endday);
        tv5 = (TextView)rootView.findViewById(R.id.message);


        tv1.setText(startMon);
        tv2.setText(startDay);
        tv3.setText(endMon);
        tv4.setText(endDay);
        tv5.setText(stampData.message);
    }
    private void showStampView() {
        pref= context.getSharedPreferences("setting", Activity.MODE_PRIVATE | Activity.MODE_MULTI_PROCESS);
        currentPoint = pref.getInt("setting",currentPoint);

        gridView = (GridView)rootView.findViewById(R.id.gridview);
        gridView.setNumColumns(5);
        gridView.setVerticalSpacing(5);
        //gridView.setHorizontalSpacing(30);

        StampAdapter adapter = new StampAdapter(context, R.layout.stamp_view);
        gridView.setAdapter(adapter);

        if(get_point == total_point){
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Coupon"));
                }
            });
        }

    }


}