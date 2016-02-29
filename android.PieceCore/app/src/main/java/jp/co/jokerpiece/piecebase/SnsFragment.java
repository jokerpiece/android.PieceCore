package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import jp.co.jokerpiece.piecebase.oauth_temp.FacebookActivity;
import jp.co.jokerpiece.piecebase.oauth_temp.TwitterActivity;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.ButtonDialogFragment;
import jp.co.jokerpiece.piecebase.util.TwitterUtils;
import twitter4j.Twitter;

/**
 * Created by kaku on 2015/06/01.
 */
public class SnsFragment extends Fragment {

    Button Snsbtn;
    View rootView;
    Context context;
    ImageButton imageButton;
    public static InputStream IsForFb;
    static int REQUEST_GET_IMAGE = 100;

    public static String filePath;    //選んだ写真のパスを保存する
    Uri bitmapUri;
    String cameraPath;  //カメラで撮った写真のパスを保存する
    File picFile;
    Twitter mTwitter;

    @Override
    public void onCreate(Bundle saveStanceState){
        super.onCreate(saveStanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        AppUtil.debugLog("mAdapter","5");
        Common.setCurrentFragment(Config.SnsFragmentNum);
        context = getActivity();
        rootView = inflater.inflate(R.layout.snsfragment, container, false);

        Snsbtn = (Button)rootView.findViewById(R.id.snsButton);
        imageButton = (ImageButton)rootView.findViewById(R.id.Photo);
        mTwitter = TwitterUtils.getTwitterInstance(context);
        final ButtonDialogFragment cdf =
                ButtonDialogFragment.newInstance(null);
        cdf.setCancelable(false);
        cdf.setDialogListener(new ButtonDialogFragment.ButtonDialogListener() {
            @Override
            public void onFacebookButtonClick() {
                cdf.dismiss();
                if(filePath != null) {
                    Intent intent = new Intent(context, FacebookActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "画像を選んでください", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onTwitterButtonClick(){
                cdf.dismiss();
                if(filePath != null) {
                    Intent intent = new Intent(context, TwitterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "画像を選んでください", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public  void onGmailButtonClick(){
                cdf.dismiss();
                if(filePath != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "添付ファイル");
                    intent.setType("image/*");
                    //ファイルパス指定（nameはIntentで保持したファイル名です）
                    File sendFile = new File(filePath);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sendFile));
                    //Gmailを指定
                    intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "画像を選んでください", Toast.LENGTH_LONG).show();
                }
            }


        });
        Snsbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
               cdf.show(((MainBaseActivity)context).getSupportFragmentManager(), "display share dialog");
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    // インテントを二つ作成
                    //実行フロー
                    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //ギャラリー専門が開きます
                    Intent pickPhotoIntent = new Intent( Intent.ACTION_PICK);
                    pickPhotoIntent.setType("image/*");
                    Intent takePhotoIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                    picFile = new File(Environment.getExternalStorageDirectory(), getPicFileName());
                    bitmapUri = Uri.fromFile(picFile);
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, bitmapUri);
                    // チューザーの作成
                    Intent chooserIntent = Intent.createChooser( pickPhotoIntent, "Picture...");
                    chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                    startActivityForResult( chooserIntent, REQUEST_GET_IMAGE);

            }
        });

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_GET_IMAGE == requestCode &&
            resultCode == Activity.RESULT_OK &&
             data != null) {
            // 画像パスを取るため
            String[] projection = {MediaStore.MediaColumns.DATA};
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;
            Cursor cursor = context.getContentResolver().query(data.getData(), projection, selection, selectionArgs, sortOrder);
            if (cursor.getCount() == 1) {
                cursor.moveToNext();
                filePath = cursor.getString(0);
            }

            try {
                if(data.getExtras() != null&&data.getExtras().get("data")!= null){
                        Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                        AppUtil.debugLog("bit",""+capturedImage);

                        imageButton.setImageBitmap(capturedImage);
                } else{
                        InputStream stream = context.getContentResolver().openInputStream(data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        stream.close();
                        imageButton.setImageBitmap(bitmap);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                FileInputStream in = new FileInputStream(picFile);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                Bitmap capturedImage = BitmapFactory.decodeStream(in, null, options);
                imageButton.setImageBitmap(capturedImage);
            }catch (IOException e){
                e.printStackTrace();
            }
            cameraPath = bitmapUri.getPath();
            filePath = cameraPath;
        }

    }




    @Override
    public void onResume() {
        super.onResume();
            AppUtil.setTitleOfActionBar(
                    getActivity().getActionBar(),
                    MainBaseActivity.titleOfActionBar.get(SnsFragment.class.getSimpleName()));
            getActivity().invalidateOptionsMenu();

    }
    //　撮った写真のファイル名を作る
    protected String getPicFileName(){
        Calendar c = Calendar.getInstance();
        String s = c.get(Calendar.YEAR) + "_" +
                  (c.get(Calendar.MONTH)+1) + "_" +
                   c.get(Calendar.DAY_OF_MONTH) + "_" +
                   c.get(Calendar.HOUR_OF_DAY) + ":" +
                   c.get(Calendar.MINUTE) + ".jpg";
        return s;
    }
}
