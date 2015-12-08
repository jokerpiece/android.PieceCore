package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.AsyncFileDownload;
import jp.co.jokerpiece.piecebase.util.ProgressHandler;

/**
 * Created by wenHsin on 2015/12/08.
 */
public class MovieDownloadActivity extends Activity {
    private ProgressDialog progressDialog;
    private ProgressHandler progressHandler;
    private AsyncFileDownload asyncFileDownload;
    File dataDir;
    Context context;
    private String videoPath = "";
    private String url;
    private String testUrl = "http://192.168.77.200/piece_dev/test.3gp";
    ImageView tutorialImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.hologram_tutorial);
        Intent intent = getIntent();
        url = intent.getStringExtra("file_data");
        tutorialImg = (ImageView)findViewById(R.id.tutorial);
        dataDir = this.getFilesDir();
        progressHandler = new ProgressHandler();
        tutorialImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialImg.setVisibility(View.GONE);
                initFileLoader();
                showProgress();
                progressHandler.progressDialog = progressDialog;
                progressHandler.asyncFileDownload = asyncFileDownload;

                if(progressDialog != null && asyncFileDownload != null){
                    progressDialog.setProgress(0);
                    progressHandler.sendEmptyMessage(0);
                }

            }
        });

    }

    private void initFileLoader(){
        String sdStatus = Environment.getExternalStorageState();
        if(sdStatus.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/SampleFolder");
            if (directory.exists() == false) {
                directory.mkdir();
            }
            File outputFile = new File(directory, "hologram.3gp");
            asyncFileDownload = new AsyncFileDownload(this, url, outputFile);
            asyncFileDownload.execute();
        }else {
            File dataDir = this.getFilesDir();
            File directory = new File(dataDir.getAbsolutePath()+ "/SampleFolder");
            if(directory.exists() == false){
                directory.mkdir();
            }
            File outputFile = new File(directory, "hologram.3gp");
            asyncFileDownload = new AsyncFileDownload(this,url, outputFile);
            asyncFileDownload.execute();
        }
    }
    private void cancelLoad(){
        if(asyncFileDownload != null){
            asyncFileDownload.cancel(true);
        }
    }
    protected void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle("Downloading files..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Hide",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelLoad();
                    }
                });
        progressDialog.show();
        progressHandler.setListener(new ProgressHandler.onFinishedListener() {
            @Override
            public void onFinished() {
                String sdStatus = Environment.getExternalStorageState();
                if (sdStatus.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    File sdCard = Environment.getExternalStorageDirectory();
                    videoPath = sdCard.getAbsolutePath() + "/SampleFolder/hologram.3gp";
                } else {
                    videoPath = dataDir.getAbsolutePath() + "/SampleFolder/hologram.3gp";
                }
                Intent i = new Intent(context, PlayBackHologramActivity.class);
                i.putExtra("videoPath", videoPath);
                context.startActivity(i);
                finish();
            }
        });

    }
}
