package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RaitenActivity extends Activity {
    private Context context;
    private RelativeLayout rlGetStamp;
    private TextView tvThankYou;
    private TextView tvGetStamp;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raiten);

        // findViews
        context = this;
        rlGetStamp = (RelativeLayout) findViewById(R.id.rl_getstamp);
        tvThankYou = (TextView) findViewById(R.id.tv_thankyou);
        tvGetStamp = (TextView) findViewById(R.id.tv_getstamp);

        // animation
        Animation animationIn = AnimationUtils.loadAnimation(context, R.anim.frombottom_in);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Animation animationOut = AnimationUtils.loadAnimation(context, R.anim.tobottom_out);
                animationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rlGetStamp.setVisibility(View.GONE);
                        ((Activity) context).finish();
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rlGetStamp.startAnimation(animationOut);
                    }
                }, 1 * 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rlGetStamp.startAnimation(animationIn);
        rlGetStamp.setVisibility(View.VISIBLE);
    }

}
