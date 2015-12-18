package jp.co.jokerpiece.piecebase;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class PlayBackMessageActivity extends FragmentActivity {

    public static boolean backFromMsg;
    TextView tvPlaybackMsg;
    Button close_Btn;
    String file_data;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    ScrollView scrollView;
    Space spaceTop;
    Space spaceBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point displaySize = AppUtil.getDisplaySize(this);
        int displayWidth = displaySize.x;


        setContentView(R.layout.activity_playbackmessage);
        tvPlaybackMsg = (TextView)findViewById(R.id.playbackMessage);
        scrollView = (ScrollView)findViewById(R.id.playbackMessage_scroll);
        spaceTop = (Space)findViewById(R.id.spaceTop);
        spaceBottom = (Space)findViewById(R.id.spaceBottom);

        scrollView.setVerticalScrollBarEnabled(false);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(displayWidth * 2 / 3, WC);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        param.addRule(RelativeLayout.BELOW,R.id.spaceTop);
        param.addRule(RelativeLayout.ABOVE,R.id.spaceBottom);
        scrollView.setLayoutParams(param);


        close_Btn = (Button)findViewById(R.id.closeBtn);
        Intent intent = getIntent();
        file_data = intent.getStringExtra("file_data");
        tvPlaybackMsg.setText(file_data);

        close_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFromMsg = true;
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }else{
            backFromMsg = true;
            finish();
            return true;
        }
    }


}
