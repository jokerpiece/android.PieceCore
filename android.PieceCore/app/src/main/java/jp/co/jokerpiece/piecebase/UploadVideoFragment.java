package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import java.io.File;

import jp.co.jokerpiece.piecebase.api.YoutubeAPI;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.YoutubeData;

/**
 * Created by wenHsin on 2015/11/13.
 */
public class UploadVideoFragment extends BaseFragment {

    View rootView;
    Context context;
    Button uploadBtn;
    Button chooseVideo;
    ImageView videoImg;
    int RESULT_PICK_FILENAME = 1;

//    private static final String VIDEO_FILE_FORMAT = "video/*";
//    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
//    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
//    private static YouTube youtube;

    private ProgressDialog dialog;

    private String account_id;
    private String token;
    private String upload_token;
    private String order_id;
    String picPath;
    File videoUri;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        dialog = new ProgressDialog(context);
        rootView = inflater.inflate(R.layout.fragment_uploadvideo, container, false);
        uploadBtn = (Button) rootView.findViewById(R.id.upload_uploadBtn);
        chooseVideo = (Button) rootView.findViewById(R.id.upload_chooseBtn);
        videoImg = (ImageView) rootView.findViewById(R.id.upload_imageView);
        Bundle bundle = getArguments();
        account_id = bundle.getString("account_id");
        order_id = bundle.getString("order_id");
        token = bundle.getString("token");
        upload_token = bundle.getString("upload_token");
        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, RESULT_PICK_FILENAME);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try{
//                    FileInputStream fstrm = new FileInputStream(videoUri);
//                    HttpFileUpload hfu = new HttpFileUpload(Config.YOUTUBE_APIV3,"","",token);
//                    hfu.Send_Now(fstrm);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                dialog.setMessage("Upload...");
                dialog.setCancelable(false);
                dialog.show();

                uploadVideo();
//                new Task_finder(context,picPath,"MOV_0093.mp4",token).execute();

            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_PICK_FILENAME && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Log.d("upload", "" + filePathColumn);
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();

            int columnindex = cursor.getColumnIndex(filePathColumn[0]);
            picPath = cursor.getString(columnindex);
            videoUri = new File(picPath);


            cursor.close();
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(picPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            Matrix matrix = new Matrix();
            Bitmap bitmap = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);
            videoImg.setImageBitmap(bitmap);

        }
    }
    public void uploadVideo(){
        ((Activity)context).getLoaderManager().initLoader(Config.loaderCnt, null, new LoaderManager.LoaderCallbacks<YoutubeData>() {
            @Override
            public Loader<YoutubeData> onCreateLoader(int id, Bundle args) {
                Log.d("token",""+token);
                YoutubeAPI youtubeAPI = new YoutubeAPI(context,token,picPath,"MOV_0093.mp4");
                youtubeAPI.forceLoad();
                return youtubeAPI;
            }

            @Override
            public void onLoadFinished(Loader<YoutubeData> loader, YoutubeData data) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onLoaderReset(Loader<YoutubeData> loader) {

            }
        });
    }


//    public File getVideoFromUser(){
//
//    }
//    public File getUserChoice(File videoFiles[]) throws IOException{
//        if(videoFiles.length < 1){
//            throw new IllegalArgumentException("No video file you want to upload");
//        }
//
//        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
//        String inputChoice;
//        do{
//            System.out.print("Choose the number of the video file you want to upload");
//            inputChoice = bReader.readLine();
//        }while(!isValidIntegerSelection(inputChoice,videoFiles.length));
//        return videoFiles[Integer.parseInt(inputChoice)];
//    }
//    public boolean isValidIntegerSelection(String input,int max){
//        if(input.length() > 9 ) return false;
//        boolean validNumber = false;
//        Pattern intsOnly = Pattern.compile("^\\d{1,9}$");
//        Matcher makeMatch = intsOnly.matcher(input);
//        if(makeMatch.find()){
//            int number = Integer.parseInt(makeMatch.group());
//            if((number >= 0) && (number < max)){
//                validNumber = true;
//            }
//        }
//        return  validNumber;
//    }
//    public void submitVideo(){
////        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload");
////        try{
////
////            youtube = new YouTube.Builder(HTTP_TRANSPORT,JSON_FACTORY).build();
////        }
//    }
////    private Credential authorize(List<String> scopes) throws Exception{
////        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
////                JSON_FACTORY,UploadVideoFragment.class.getResourceAsStream("/client_secrets.json"));
////        if(clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter")){
////            System.exit(1);
////        }
////
////    }
}
