package jp.co.jokerpiece.piecebase.util;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.util.logging.LogRecord;

/**
 * Created by wenHsin on 2015/12/07.
 */
public class ProgressHandler extends Handler {

    public ProgressDialog progressDialog;
    public AsyncFileDownload asyncFileDownload;
    public boolean isProgressEnd = false;
    onFinishedListener listener;

    public void setListener(onFinishedListener listener){
        this.listener = listener;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(asyncFileDownload.isCancelled()){
            progressDialog.dismiss();
        }
        else if(asyncFileDownload.getStatus() == AsyncTask.Status.FINISHED){
            progressDialog.dismiss();
            if(listener != null){
                listener.onFinished();
            }
            isProgressEnd = true;
        }else{
            progressDialog.setProgress(asyncFileDownload.getLoadedBytePercent());
            this.sendEmptyMessageDelayed(0,100);
        }

    }

    public interface onFinishedListener{
        public void onFinished();
    }

}
