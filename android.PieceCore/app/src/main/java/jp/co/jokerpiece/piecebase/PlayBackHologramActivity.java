package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;


/**
 * Created by wenHsin on 2015/11/20.
 */
public class PlayBackHologramActivity extends Activity{

    String file_data;

    Context context;

    private String pathToVideo = "rtsp://r2---sn-a5m7zu76.c.youtube.com/Ck0LENy73wIaRAnTmlo5oUgpQhMYESARFEgGUg5yZWNvbW1lbmRhdGlvbnIhAWL2kyn64K6aQtkZVJdTxRoO88HsQjpE1a8d1GxQnGDmDA==/0/0/0/video.3gp";
    private TextureView textureView;
    private TextureView textureView2;
    private TextureView textureView3;
    private TextureView textureView4;
    Button button;
    Button closeBtn;

    MediaPlayer mediaPlayer = null;
    MediaPlayer mediaPlayer2 = null;
    MediaPlayer mediaPlayer3 = null;
    MediaPlayer mediaPlayer4 = null;
    boolean mediaPlayer1isPrepared = false;
    boolean mediaPlayer2isPrepared = false;
    boolean mediaPlayer3isPrepared = false;
    boolean mediaPlayer4isPrepared = false;

    boolean video1IsFinished = false;
    boolean video2IsFinished = false;
    boolean video3IsFinished = false;
    boolean video4IsFinished = false;

    boolean mediaPlayerIsStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_playbackhologram);
        Intent intent = getIntent();
        file_data = intent.getStringExtra("file_data");
        Log.d("movie", file_data);
        mediaPlayer = new MediaPlayer();
        mediaPlayer2 = new MediaPlayer();
        mediaPlayer3 = new MediaPlayer();
        mediaPlayer4 = new MediaPlayer();
        button = (Button)findViewById(R.id.replay_btn);
        closeBtn = (Button)findViewById(R.id.holo_closeBtn);
        textureView = (TextureView)findViewById(R.id.playback_video1);
        textureView2 = (TextureView)findViewById(R.id.playback_video2);
        textureView3 = (TextureView)findViewById(R.id.playback_video3);
        textureView4 = (TextureView)findViewById(R.id.playback_video4);
        try {
            mediaPlayer.setDataSource(file_data);
            mediaPlayer.prepare();
            mediaPlayer2.setDataSource(file_data);
            mediaPlayer2.prepare();
            mediaPlayer3.setDataSource(file_data);
            mediaPlayer3.prepare();
            mediaPlayer4.setDataSource(file_data);
            mediaPlayer4.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }


        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mediaPlayer.setSurface(new Surface(surface));

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                ViewGroup.LayoutParams params = textureView.getLayoutParams();
                params.height = 300;
                params.width = 300;
                textureView.setLayoutParams(params);
                mediaPlayer1isPrepared = true;
                startPlay();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        textureView2.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mediaPlayer2.setSurface(new Surface(surface));

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                ViewGroup.LayoutParams params = textureView2.getLayoutParams();
                params.height = 300;
                params.width = 300;
                textureView2.setLayoutParams(params);
                mediaPlayer2isPrepared = true;
                startPlay();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        textureView3.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mediaPlayer3.setSurface(new Surface(surface));

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                ViewGroup.LayoutParams params = textureView3.getLayoutParams();
                params.height = 300;
                params.width = 300;
                textureView3.setLayoutParams(params);
                mediaPlayer3isPrepared = true;
                startPlay();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        textureView4.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mediaPlayer4.setSurface(new Surface(surface));
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                ViewGroup.LayoutParams params = textureView4.getLayoutParams();
                params.height = 300;
                params.width = 300;
                textureView4.setLayoutParams(params);
                mediaPlayer4isPrepared = true;
                startPlay();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer1isPrepared = true;
//                startPlay();
//            }
//        });
//        mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer2isPrepared = true;
//                startPlay();
//            }
//        });
//        mediaPlayer3.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer3isPrepared = true;
//                startPlay();
//            }
//        });
//        mediaPlayer4.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer4isPrepared = true;
//                startPlay();
//            }
//        });


        textureView.setRotation(0.0f);
        textureView2.setRotation(270.0f);
        textureView3.setRotation(180.0f);
        textureView4.setRotation(90.0f);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayBackMessageActivity.backFromMsg = true;
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer2.reset();
                mediaPlayer2.release();
                mediaPlayer2 = null;
                mediaPlayer3.reset();
                mediaPlayer3.release();
                mediaPlayer3 = null;
                mediaPlayer4.reset();
                mediaPlayer4.release();
                mediaPlayer4 = null;
                mediaPlayerIsStart = true;
                button.setVisibility(View.INVISIBLE);
                closeBtn.setVisibility(View.INVISIBLE);
                video1IsFinished = false;
                video2IsFinished = false;
                video3IsFinished = false;
                video4IsFinished = false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void startPlay(){
        if( mediaPlayer1isPrepared && mediaPlayer2isPrepared&&mediaPlayer3isPrepared&&mediaPlayer4isPrepared){
            new CountDownTimer(1000,1000){
                @Override
                public void onTick(long millisUntilFinished) {}
                @Override
                public void onFinish() {
                    mediaPlayer.start();
                    mediaPlayer2.start();
                    mediaPlayer3.start();
                    mediaPlayer4.start();
                    mediaPlayerIsStart = true;
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            video1IsFinished = true;
                            mediaPlayerIsStart = false;
                           // mediaPlayer.stop();
                           // button.setVisibility(View.VISIBLE);
                            closeBtn.setVisibility(View.VISIBLE);
                        }
                    });
                    mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            video2IsFinished = true;
                            mediaPlayerIsStart = false;
                          //  mediaPlayer2.stop();
                        }
                    });
                    mediaPlayer3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            video3IsFinished = true;
                            mediaPlayerIsStart = false;
                           // mediaPlayer3.stop();
                        }
                    });
                    mediaPlayer4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            video4IsFinished = true;
                            mediaPlayerIsStart = false;
                            //mediaPlayer4.stop();
                        }
                    });
                }
            }.start();
        }
    }

}
