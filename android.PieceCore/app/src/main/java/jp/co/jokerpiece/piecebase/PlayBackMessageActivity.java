package jp.co.jokerpiece.piecebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class PlayBackMessageActivity extends FragmentActivity {

    public static boolean backFromMsg;
    TextView tvPlaybackMsg;
    Button close_Btn;
    String file_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playbackmessage);
        tvPlaybackMsg = (TextView)findViewById(R.id.playbackMessage);
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
