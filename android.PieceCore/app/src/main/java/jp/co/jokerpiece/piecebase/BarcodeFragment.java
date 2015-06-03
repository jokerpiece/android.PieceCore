package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.CameraMaskView;
import jp.co.jokerpiece.piecebase.util.CameraSurfaceView;
import jp.co.jokerpiece.piecebase.util.CameraTextureView;

public class BarcodeFragment extends Fragment {
    private static final String TAG = BarcodeFragment.class.getSimpleName();
	private Context context;

    private static View rootView;
    private CameraTextureView textureView;
	private CameraSurfaceView surfaceView;
	private CameraMaskView maskView;

    private CameraSurfaceView.CameraSurfaceViewCallback callbackOfSurfaceView =
            new CameraSurfaceView.CameraSurfaceViewCallback() {
                @Override
                public void getBarcodeNum(String barcodeNum) {
                    // バーコード番号を取得できた場合の処理
                    Log.d(TAG, "barcodeNum: " + barcodeNum);
                    MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
                    // TODO:バーコード番号を商品一覧画面に設定する処理を追加する必要があります

                }
            };

    private CameraTextureView.CameraTextureViewCallback callbackOfTextureView =
            new CameraTextureView.CameraTextureViewCallback() {
                @Override
                public void getBarcodeNum(String barcodeNum) {
                    // バーコード番号を取得できた場合の処理
                    Log.d(TAG, "barcodeNum: " + barcodeNum);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
                            // TODO:バーコード番号を商品一覧画面に設定する処理を追加する必要があります

                        }
                    });
                }
            };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        if(Config.BarcodeFragmentNum == 0) {
            if (Config.Savelist.size() == 1) {
                Config.Savelist.clear();
                Config.Savelist.add(0);
            }
            if (!Config.Backflg) {
                if (Config.FragmentCurrentNum != 0) {
                    Config.Savelist.add(Config.BarcodeFragmentNum);
                    Config.FragmentCurrentNum += 1;
                }
            }
        }else{
            if(!Config.Backflg) {
                Config.Savelist.add(Config.BarcodeFragmentNum);
                Config.FragmentCurrentNum += 1;
            }
        }
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_barcode, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        context = getActivity();

        // ルート
        RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.rl_root);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);

        // カメラView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textureView = new CameraTextureView(context, callbackOfTextureView);
            textureView.setLayoutParams(layoutParams);
            rl.addView(textureView);
        } else {
            surfaceView = new CameraSurfaceView(context, callbackOfSurfaceView);
            surfaceView.setLayoutParams(layoutParams);
            rl.addView(surfaceView);
        }

        // マスクView
        maskView = new CameraMaskView(context);
        maskView.setLayoutParams(layoutParams);
        rl.addView(maskView);

        // テキストView
        TextView tv = new TextView(context);
        tv.setText("バーコードを\n読み取ってください");
        tv.setTextColor(Color.rgb(255, 255, 255));
        tv.setTextSize(18.0f);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        tv.setPadding(0, AppUtil.getPixelFromDp(context, 30), 0, 0);
        rl.addView(tv);

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
				MainBaseActivity.titleOfActionBar.get(BarcodeFragment.class.getSimpleName()));
		getActivity().invalidateOptionsMenu();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void getInfoSyosai(){
//        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, getArguments(), new LoaderCallbacks<NewsInfoData>() {
//			@Override
//			public Loader<NewsInfoData> onCreateLoader(int id, Bundle args) {
//				// news_idの取得
//				String newsId = "";
//				if (args != null) {
//					newsId = args.getString("newsId");
//				}
//
//				 NewsInfoAPI infoAPI = new NewsInfoAPI(context, newsId);
//				 infoAPI.forceLoad();
//				 return infoAPI;
//			}
//			@Override
//			public void onLoadFinished(Loader<NewsInfoData> loader, NewsInfoData data) {
//				if(data == null){
//					Common.serverErrorMessage(context);
//					return;
//				}
//				// ここにデータ取得時の処理を書く
//				if (data.title != null) {
//					tvTitle.setText(data.title);
//				}
//				if (data.text != null) {
//					tvText.setText(data.text);
//				}
//			}
//			@Override
//			public void onLoaderReset(Loader<NewsInfoData> loader) {
//
//			}
//		});
	}

}
