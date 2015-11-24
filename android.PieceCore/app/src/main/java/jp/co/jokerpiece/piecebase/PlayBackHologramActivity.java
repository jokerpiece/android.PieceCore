package jp.co.jokerpiece.piecebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by wenHsin on 2015/11/20.
 */
public class PlayBackHologramActivity extends YouTubeBaseActivity{

    String file_data;
    YouTubePlayerView youTubePlayerView1;
    YouTubePlayerView youTubePlayerView2;
    YouTubePlayerView youTubePlayerView3;
    YouTubePlayerView youTubePlayerView4;

    YouTubePlayer mYouTubePlayer;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_playbackhologram);
        Intent intent = getIntent();
//        videoView = (VideoView)findViewById(R.id.youtube_view);
        file_data = intent.getStringExtra("file_data");
        Log.d("movie", file_data);

        VideoView v = (VideoView)findViewById(R.id.hologram);
        v.setVideoURI(Uri.parse("https://r3---sn-ouu2j-ioqe.googlevideo.com/videoplayback?itag=36&mime=video/3gpp&initcwndbps=2167500&ipbits=0&fexp=9408710,9416126,9417683,9420452,9422596,9422618,9423662&mt=1448022426&key=yt6&upn=yPIjfkLd63A&expire=1448044223&sparams=dur,id,initcwndbps,ip,ipbits,itag,lmt,mime,mm,mn,ms,mv,pcm2cms,pl,requiressl,source,upn,expire&pcm2cms=yes&ms=au&source=youtube&mv=m&dur=400.590&pl=20&ip=61.206.116.50&lmt=1428528641630549&id=o-AJoN0a1deMSqvv-cnJUMm30ukPdAuc4YW1JZ_d_yR_ts&mm=31&mn=sn-ouu2j-ioqe&sver=3&signature=AB42D0F603ED2DCCAA00093685E834A518C3D4EA.DDBB1A52BD1506023CDB2ED16CE012D51660FEE3&requiressl=yes&signature=36"));
        MediaController mc = new MediaController(context);
        v.setMediaController(mc);
        v.requestFocus();
        v.start();
        mc.show();
//        WebView webView = (WebView) findViewById(R.id.webYoutube1);
//        WebView webView2 = (WebView) findViewById(R.id.webYoutube2);
//        WebView webView3 = (WebView) findViewById(R.id.webYoutube3);
//        WebView webView4 = (WebView) findViewById(R.id.webYoutube4);
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//        webView.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.loadUrl("https://www.youtube.com/embed/6p83oTVqrCI?rel=0&autoplay=1");
//
//        webView2.getSettings().setJavaScriptEnabled(true);
//        webView2.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView2.getSettings().setMediaPlaybackRequiresUserGesture(false);
//
//        webView2.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//        webView2.setWebChromeClient(new WebChromeClient());
//        webView2.loadUrl("https://www.youtube.com/embed/6p83oTVqrCI?rel=0&autoplay=1");
//        webView3.getSettings().setJavaScriptEnabled(true);
//        webView3.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView3.getSettings().setMediaPlaybackRequiresUserGesture(false);
//
//        webView3.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//        webView3.setWebChromeClient(new WebChromeClient());
//        webView3.loadUrl("https://www.youtube.com/embed/6p83oTVqrCI?rel=0&autoplay=1");
//
//        webView4.getSettings().setJavaScriptEnabled(true);
//        webView4.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView4.getSettings().setMediaPlaybackRequiresUserGesture(false);
//
//        webView4.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//        webView4.setWebChromeClient(new WebChromeClient());
//        webView4.loadUrl("https://www.youtube.com/embed/6p83oTVqrCI?rel=0&autoplay=1");

//        YoutubePlayerTwo youtubePlayerViewTwo = new YoutubePlayerTwo();
//        youtubePlayerViewTwo.StartPlay();
//        new MovieAsyncTask(this,videoView,file_data).execute();


//         youTubePlayerView1 = (YouTubePlayerView)findViewById(R.id.hologram_view1);
//         youTubePlayerView2 = (YouTubePlayerView)findViewById(R.id.hologram_view2);
//         youTubePlayerView3 = (YouTubePlayerView)findViewById(R.id.hologram_view3);
//         youTubePlayerView4 = (YouTubePlayerView)findViewById(R.id.hologram_view4);
//        youTubePlayerView1.initialize("AIzaSyAS4KpcGjJrCu5du8LBVFj97u8DbGZwaIo", new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                if(!b) {
//                    youTubePlayer.loadVideo("6p83oTVqrCI");
//                    mYouTubePlayer = youTubePlayer;
//                    youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
//                        @Override
//                        public void onLoading() {
//
//                        }
//                        @Override
//                        public void onLoaded(String s) {
//                            Log.d("youtube",s);
//                        }
//                        @Override
//                        public void onAdStarted() {
//
//                        }
//                        @Override
//                        public void onVideoStarted() {
//
//                        }
//                        @Override
//                        public void onVideoEnded() {
//
//                        }
//                        @Override
//                        public void onError(YouTubePlayer.ErrorReason errorReason) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                String errorMessage = youTubeInitializationResult.toString();
//                Log.d("errorMessage",errorMessage);
//
//            }
//        });
//        youTubePlayerView2.initialize("AIzaSyAS4KpcGjJrCu5du8LBVFj97u8DbGZwaIo", new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                if(!b) {
//                    youTubePlayer = mYouTubePlayer;
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//        youTubePlayerView3.initialize("AIzaSyAS4KpcGjJrCu5du8LBVFj97u8DbGZwaIo", new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                if(!b) {
//                    youTubePlayer.loadVideo("6p83oTVqrCI");
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//        youTubePlayerView4.initialize("AIzaSyAS4KpcGjJrCu5du8LBVFj97u8DbGZwaIo", new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                if(!b) {
//                    youTubePlayer.loadVideo("6p83oTVqrCI");
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
    }
//    public class YoutubePlayerViewTwo extends YouTubeBaseActivity{
//        @Override
//        protected void onCreate(Bundle bundle) {
//            super.onCreate(bundle);
//
//        }
//        public void startPlay(){
//            setContentView(R.layout.activity_playbackhologram);
//
//            youTubePlayerView2 = (YouTubePlayerView)findViewById(R.id.hologram_view2);
//
//            youTubePlayerView2.initialize("AIzaSyAS4KpcGjJrCu5du8LBVFj97u8DbGZwaIo", new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                    if (!b) {
//                        youTubePlayer.loadVideo("6p83oTVqrCI");
//                    }
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//                }
//            });
//        }
//    }

}
