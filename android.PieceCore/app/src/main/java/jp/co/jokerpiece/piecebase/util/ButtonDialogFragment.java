package jp.co.jokerpiece.piecebase.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.EventListener;

import jp.co.jokerpiece.piecebase.R;

/**
 * Created by kaku on 2015/06/09.
 */
public class ButtonDialogFragment extends DialogFragment implements View.OnClickListener {
    private ButtonDialogListener listener;
    public static ButtonDialogFragment newInstance(String title) {
        ButtonDialogFragment frag = new ButtonDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.buttondialogfragment);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton FacebookBtn = (ImageButton) dialog.findViewById(R.id.dialog_facebook);
        ImageButton TwitterBtn = (ImageButton)dialog.findViewById(R.id.dialog_twitter);
        ImageButton GmailBtn = (ImageButton)dialog.findViewById(R.id.dialog_gmail);


        FacebookBtn.setOnClickListener(this);
        TwitterBtn.setOnClickListener(this);
        GmailBtn.setOnClickListener(this);
        ((Button)dialog.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }
    @Override
    public void onClick(View v) {
        //switch (v.getId()){
            if(v.getId() == R.id.dialog_facebook) {
                listener.onFacebookButtonClick();
            }
            if(v.getId() == R.id.dialog_twitter) {
                listener.onTwitterButtonClick();
            }
            if(v.getId() ==  R.id.dialog_gmail) {
                listener.onGmailButtonClick();
            }

    }
    /**
     * リスナーを追加する。
     * @param listener リスナー
     */
    public void setDialogListener(ButtonDialogListener listener) {
        this.listener = listener;
    }

    /**
     * リスナーを削除する。
     */
    public void removeDialogListener() {
        this.listener = null;
    }
    public interface ButtonDialogListener extends EventListener {
        public void onFacebookButtonClick();
        public void onTwitterButtonClick();
        public void onGmailButtonClick();
    }
}
