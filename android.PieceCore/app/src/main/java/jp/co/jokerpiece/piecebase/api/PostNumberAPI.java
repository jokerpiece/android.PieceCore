package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;

import jp.co.jokerpiece.piecebase.LinePayProfileFragment;
import jp.co.jokerpiece.piecebase.config.Config;

import jp.co.jokerpiece.piecebase.data.PostNumberData;

import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Sou on 16/4/1.
 */
public class PostNumberAPI extends AsyncTaskLoader<PostNumberData> implements HttpClient.HttpClientInterface
{


    public String address1,address2,address3;


    public PostNumberAPI(Context context) {
        super(context);
    }

    @Override
    public PostNumberData loadInBackground()
    {
        String postNumResult = null;
        String url = Config.GET_ADDRESS_BY_POSTNUMBER;
        HashMap<String, String> parameter = new HashMap<String, String>();

        HttpURLConnection httpURLConnection = null;
        URL postUrl = null;

        PostNumberData postNumberData = new PostNumberData();

        try {
            // URLの作成
            postUrl = new URL(url + LinePayProfileFragment.Post);
            // 接続用HttpURLConnectionオブジェクト作成
            httpURLConnection = (HttpURLConnection)postUrl.openConnection();
            // リクエストメソッドの設定
            httpURLConnection.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            //httpURLConnection.setInstanceFollowRedirects(false);
            // URL接続からデータを読み取る場合はtrue
            httpURLConnection.setDoInput(true);
            // URL接続にデータを書き込む場合はtrue
            httpURLConnection.setDoOutput(true);

            // 接続
            httpURLConnection.connect();

            //Check the data has got
            String getURL = InputStreamToString(httpURLConnection.getInputStream());
            AppUtil.debugLog("connectionCheck",getURL);


            //Get JSON Data
            try
            {

                JSONObject jsonObject = new JSONObject(getURL);
                AppUtil.debugLog("JSON Results",jsonObject.getString("results"));

                if(jsonObject.getString("results")=="null")
                {

                    postNumberData.result = "no_data";

                }
                else
                {
                    JSONArray jsonArray = new JSONObject(getURL).getJSONArray("results");
                    AppUtil.debugLog("JSON ARRAY RESULTS", jsonArray.toString());
                    address1 = jsonArray.getJSONObject(0).getString("address1");
                    address2 = jsonArray.getJSONObject(0).getString("address2");
                    address3 = jsonArray.getJSONObject(0).getString("address3");


                    postNumberData.address1=address1;
                    postNumberData.address2=address2;
                    postNumberData.address3=address3;
                }





            }
            catch (JSONException e)
            {
                AppUtil.debugLog("JSON", "JSON ERROR");
                e.printStackTrace();
            }




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return postNumberData;
    }


    @Override
    public void HttpClientProgress(float progress) {

    }

    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


}
