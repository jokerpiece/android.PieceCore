package jp.co.jokerpiece.piecebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class MovieAsyncTask  extends AsyncTask<Void, Void, Void> {
    ProgressDialog progressDialog;
    String videoUrl;
    VideoView videoView;
    Context context;
    String youtubeId;
    public MovieAsyncTask(Context context, VideoView videoView,String youtubeId) {
        this.videoView = videoView;
        this.context = context;
        this.youtubeId = youtubeId;
    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        progressDialog = ProgressDialog.show(context, "", "Loading Video wait...", true);
//
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        try {
//            String url = "http://www.youtube.com/watch?v=E43mgXN10xc";
//            videoUrl = getUrlVideoRTSP(url);
//            Log.d("youtubeUrl","0");
//            Log.e("Video url for playing=========>>>>>", videoUrl);
//        } catch (Exception e) {
//           // Log.e("Login Soap Calling in Exception", e.toString());
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void result) {
//        super.onPostExecute(result);
//        progressDialog.dismiss();
///*
//            videoView.setVideoURI(Uri.parse("rtsp://v4.cache1.c.youtube.com/CiILENy73wIaGQk4RDShYkdS1BMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"));
//            videoView.setMediaController(new MediaController(AlertDetail.this));
//            videoView.requestFocus();
//            videoView.start();*/
//        Log.d("youtubeUrl",videoUrl);
//        Log.d("youtubeUrl","5");
//        videoView.setVideoURI(Uri.parse(videoUrl));
//        MediaController mc = new MediaController(context);
//        videoView.setMediaController(mc);
//        videoView.requestFocus();
//        videoView.start();
//        mc.show();
//    }
//
//    public static String getUrlVideoRTSP(String urlYoutube) {
//        try {
//            Log.d("youtubeUrl","1");
//
//            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
//            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            String id = extractYoutubeId(urlYoutube);
//            URL url = new URL(gdy + id);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            Log.d("youtubeUrl",""+connection.getResponseCode());
//            Document doc = documentBuilder.parse(connection.getInputStream());
//            Element el = doc.getDocumentElement();
//            NodeList list = el.getElementsByTagName("media:content");///media:content
//
//            String cursor = urlYoutube;
//            for (int i = 0; i < list.getLength(); i++) {
//                Node node = list.item(i);
//                if (node != null) {
//                    NamedNodeMap nodeMap = node.getAttributes();
//                    HashMap<String, String> maps = new HashMap<String, String>();
//                    for (int j = 0; j < nodeMap.getLength(); j++) {
//                        Attr att = (Attr) nodeMap.item(j);
//                        maps.put(att.getName(), att.getValue());
//                    }
//                    if (maps.containsKey("yt:format")) {
//                        String f = maps.get("yt:format");
//                        if (maps.containsKey("url")) {
//                            cursor = maps.get("url");
//                        }
//                        if (f.equals("1"))
//                            Log.d("youtubeUrl","3");
//                            return cursor;
//                    }
//                }
//            }
//            Log.d("youtubeUrl",cursor);
//
//            return cursor;
//        } catch (Exception ex) {
//            Log.e("Get Url Video RTSP Exception======>>", ex.toString());
//            Log.d("youtubeUrl", "4");
//            return urlYoutube;
//
//        }
//
//    }
//
//    protected static String extractYoutubeId(String url) throws MalformedURLException {
//        String id = null;
//        try {
//            String query = new URL(url).getQuery();
//            if (query != null) {
//                String[] param = query.split("&");
//                for (String row : param) {
//                    String[] param1 = row.split("=");
//                    if (param1[0].equals("v")) {
//                        id = param1[1];
//                    }
//                }
//            } else {
//                if (url.contains("embed")) {
//                    id = url.substring(url.lastIndexOf("/") + 1);
//                }
//            }
//        } catch (Exception ex) {
//            Log.e("Exception", ex.toString());
//        }
//        return id;
//    }
@Override
protected void onPostExecute(Void result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    Log.d("videoUrl",videoUrl);
    videoView.setVideoURI(Uri.parse(videoUrl));
    MediaController mc = new MediaController(context);
    videoView.setMediaController(mc);
    videoView.requestFocus();
    videoView.start();
    mc.show();

    /**
     videoView.setMediaController(new MediaController(this)); //sets MediaController in the video view
     //   MediaController containing controls for a MediaPlayer
     videoView.requestFocus();//give focus to a specific view
     videoView.start();//starts the video
     */
}


    private String getRstpLinks(String code){
        String[] urls = new String[3];
        String link = "http://gdata.youtube.com/feeds/api/videos/" + code + "?alt=json";
        String json = getJsonString(link); // here you request from the server
        try {
            JSONObject obj = new JSONObject(json);
            String entry = obj.getString("entry");
            JSONObject enObj = new JSONObject(entry);
            String group = enObj.getString("media$group");
            JSONObject grObj = new JSONObject(group);
            String content = grObj.getString("media$content");
            JSONObject cntObj = new JSONObject(group);
            JSONArray array = grObj.getJSONArray("media$content");
            for(int j=0; j<array.length(); j++){
                JSONObject thumbs = array.getJSONObject(j);
                String url = thumbs.getString("url");
                urls[j] = url;
            //    Log.d(TAG, url);
                //data.setThumbUrl(thumbUrl);
            }


          //  Log.v(TAG, content);
        } catch (Exception e) {
        //    Log.e(TAG, e.toString());
            urls[0] = urls[1] = urls[2] = null;
        }
        return urls[2];

    }


    public static String getJsonString(String url){
        Log.e("Request URL", url);
        StringBuilder buffer = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet     request = new HttpGet( url );
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(request);

            if( response.getStatusLine().getStatusCode() == 200 ){
                entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while( (line = br.readLine() )!= null ){
                    buffer.append(line);
                }
                br.close();

            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                entity.consumeContent();
            } catch (Exception e) {
               // Log.e(TAG, "Exception = " + e.toString() );
            }
        }

        return buffer.toString();
    }





    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub


//        code = id_current_url(current_url);
        //here type the url...
        String rstp_url = getRstpLinks("E43mgXN10xc");
        videoUrl = rstp_url;


        // the code crech in this line because null exeption
        // i chack this and discover that code variable is =tFXS9krT2VY , ok..
        // but rstp_url variable in null

   //     Log.d(TAG,getRstpLinks(code) + "   idan id yotube1  " );
        return null;
    }

    public String id_current_url (String url) {

        String c_id = null ;

        c_id = url.substring((url.lastIndexOf("=")), url.length());

        return c_id ;
    }






}