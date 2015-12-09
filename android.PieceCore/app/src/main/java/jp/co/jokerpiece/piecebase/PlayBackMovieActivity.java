package jp.co.jokerpiece.piecebase;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class PlayBackMovieActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    String file_data;
    VideoView videoView;
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playbackmovie);
        Intent intent = getIntent();
//        videoView = (VideoView)findViewById(R.id.youtube_view);
        file_data = intent.getStringExtra("file_data");
        AppUtil.debugLog("movie", file_data);

//        new MovieAsyncTask(this,videoView,file_data).execute();
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_view);
        youTubePlayerView.initialize("AIzaSyAS4KpcGjJrCu5du8LBVFj97u8DbGZwaIo",this);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            youTubePlayer.loadVideo(file_data);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
