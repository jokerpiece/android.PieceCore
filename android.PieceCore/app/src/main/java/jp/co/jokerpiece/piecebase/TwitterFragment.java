package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jp.co.jokerpiece.piecebase.Oauth.TwitterActivity;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;
import jp.co.jokerpiece.piecebase.util.TwitterUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by wenHsin on 2015/09/09.
 */
public class TwitterFragment extends Fragment {

    private TweetAdapter mAdapter;
    private Twitter mTwitter;
    View rootView;
    Button Btn;
    ListView listView;
    Context context;
    View headerView;
    TextView connectErrorView;
    TextView twitterErrorView;
    String screenName;
    boolean performAuthentication = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        context = getActivity();
        Common.setCurrentFragment(Config.TwitterFragmentNum);
        screenName = getString(R.string.screenName);
        rootView = inflater.inflate(R.layout.fragment_twitter, container, false);
        listView = (ListView)rootView.findViewById(R.id.twitterListView);
        connectErrorView = (TextView)rootView.findViewById(R.id.ConnectErrorTv);
        twitterErrorView = (TextView)rootView.findViewById(R.id.TwitterErrorTv);
        headerView = ((LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.twitter_list_headerview,
                null, false);
        Btn = (Button)headerView.findViewById(R.id.TwitterButton);
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TwitterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (screenName.startsWith("@")){
                    intent.putExtra("screenName", screenName);
                }else{
                    intent.putExtra("screenName", "@"+screenName);
                }
                context.startActivity(intent);
            }
        });
        if(Common.CheckNetWork(getActivity())) {
            connectErrorView.setVisibility(View.GONE);
            twitterErrorView.setVisibility(View.GONE);
            if (!TwitterUtils.hasAccessToken(context)) {
                Intent intent = new Intent(context, TwitterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                performAuthentication = true;
            } else {
                mTwitter = TwitterUtils.getTwitterInstance(context);
                reloadTimeLine();

            }
        }else{
            connectErrorView.setVisibility(View.VISIBLE);
        }
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppUtil.setTitleOfActionBar(
                getActivity().getActionBar(),
                MainBaseActivity.titleOfActionBar.get(TwitterFragment.class.getSimpleName()));
        getActivity().invalidateOptionsMenu();
        if(performAuthentication) {
            if (TwitterUtils.hasAccessToken(context)) {
                performAuthentication = false;
                mTwitter = TwitterUtils.getTwitterInstance(context);
                reloadTimeLine();
            }
        }
    }

    private class TweetAdapter extends ArrayAdapter<Status> {

        private LayoutInflater mInflater;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_tweet, null);
            }

            Status item = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());
            DownloadImageView icon = (DownloadImageView) convertView.findViewById(R.id.UserIcon);
            if (!icon.setImageURL(item.getUser().getProfileImageURL())) {
                ((FragmentActivity) context).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, icon);
            }
            return convertView;
        }
    }

    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
//                    mTwitter.createFriendship(getString(R.string.screenName));
                    return mTwitter.getUserTimeline(getString(R.string.screenName));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    if(mAdapter == null) {
                        mAdapter = new TweetAdapter(context);
                        listView.addHeaderView(headerView);
                        listView.setAdapter(mAdapter);
                    }
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    //listView.setSelection(0);
                } else {
                    if(Common.CheckNetWork(getActivity())) {
                        twitterErrorView.setVisibility(View.VISIBLE);
                    }
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        task.execute();
    }


    private void showToast(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
