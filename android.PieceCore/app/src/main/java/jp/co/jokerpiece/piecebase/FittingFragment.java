package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.api.QuestionInfoAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.QuestionInfoData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;

public class FittingFragment extends Fragment implements DownloadImageSync.DownloadImageSyncCallback, View.OnClickListener {
    private Context context;
    private TextView tvQuestion;
    private ImageView ivAnswer1;
    private ImageView ivAnswer2;

    private QuestionInfoData quesInfoData;

    public FittingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
//        if(Config.FittingFragmentNum == 0) {
//            if (Config.Savelist.size() == 1) {
//                Config.Savelist.clear();
//                Config.Savelist.add(0);
//            }
//            if (!Config.Backflg) {
//                if (Config.FragmentCurrentNum != 0) {
//                    Config.Savelist.add(Config.FittingFragmentNum);
//                    Config.FragmentCurrentNum += 1;
//                }
//            }
//        }else{
//            if(!Config.Backflg) {
//                Config.Savelist.add(Config.FittingFragmentNum);
//                Config.FragmentCurrentNum += 1;
//            }
//        }
        Common.setCurrentFragment(Config.FittingFragmentNum);
        View rootView = inflater.inflate(R.layout.fragment_fitting, container, false);

        tvQuestion = (TextView) rootView.findViewById(R.id.tv_question);
        ivAnswer1 = (ImageView) rootView.findViewById(R.id.iv_answer1);
        ivAnswer2 = (ImageView) rootView.findViewById(R.id.iv_answer2);

        ivAnswer1.setOnClickListener(this);
        ivAnswer2.setOnClickListener(this);

        getQuestion(null, null);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(FittingFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//        inflater.inflate(R.menu.menu_coupon, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_answer1) {
            getQuestion(quesInfoData.questionId, "1");

        } else if (i == R.id.iv_answer2) {
            getQuestion(quesInfoData.questionId, "2");

        }
    }

    private void getQuestion(final String questionId, final String answerNum){
        quesInfoData = null;
        tvQuestion.setText("");
        ivAnswer1.setVisibility(View.GONE);
        ivAnswer2.setVisibility(View.GONE);

        getActivity().getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<QuestionInfoData>() {
            @Override
            public Loader<QuestionInfoData> onCreateLoader(int id, Bundle args) {
                QuestionInfoAPI api = new QuestionInfoAPI(context, questionId, answerNum);
                api.forceLoad();
                return api;
            }

            @Override
            public void onLoadFinished(Loader<QuestionInfoData> loader, QuestionInfoData data) {
                if (data == null) {
                    Common.serverErrorMessage(context);
                    return;
                }
                quesInfoData = data;
                // ここにデータ取得時の処理を書く
                displayQuesInfoData();
            }

            @Override
            public void onLoaderReset(Loader<QuestionInfoData> loader) {
            }
        });
    }

    public void displayQuesInfoData() {
        if (quesInfoData == null) { return; }

        // 遷移先urlが存在する場合はWebViewFragmentに遷移する
        transrationIfNeed(quesInfoData.itemUrl);

        // 質問テキストの読み込み
        displayTextView(quesInfoData.text);

        // 回答画像の読み込み
        displayImageView(ivAnswer1, quesInfoData.imgUrl1);
        displayImageView(ivAnswer2, quesInfoData.imgUrl2);
    }

    public void transrationIfNeed(String url) {
        if (url != null && !url.equals("")) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(null);
            WebViewFragment fragment = new WebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("send_url", url);
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        }
    }

    public void displayTextView(String text) {
        if (text != null && !text.equals("") && !text.equals("null")) {
            tvQuestion.setText(text);
            tvQuestion.setVisibility(View.VISIBLE);
        }
    }

    public void displayImageView(ImageView iv, String url) {
        if(url != null && !url.equals("")) {
            iv.setTag(url);
            DownloadImageSync sync = new DownloadImageSync(context, url, iv, this);
            if (!sync.loadImageView()) {
                Loader l = ((Activity)context).getLoaderManager().getLoader(Config.loaderCnt);
                if (l != null){
                    return;
                }
                getLoaderManager().initLoader(Config.loaderCnt++, null, sync);
            }
        }
    }

    @Override
    public void setImageCallbackWithURL(Bitmap bitmap, String url) {
        if(ivAnswer1.getTag() != null && ivAnswer1.getTag().toString().equals(url)){
            ivAnswer1.setImageBitmap(bitmap);
            ivAnswer1.setVisibility(View.VISIBLE);
        } else if(ivAnswer2.getTag() != null && ivAnswer2.getTag().toString().equals(url)){
            ivAnswer2.setImageBitmap(bitmap);
            ivAnswer2.setVisibility(View.VISIBLE);
        }
    }
}
