package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.YoutubeData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by wenHsin on 2015/11/17.
 */
public class YoutubeAPI extends AsyncTaskLoader<YoutubeData> implements HttpClient.HttpClientInterface {
    String token;
    String file_path;
    String fileName;
    public YoutubeAPI(Context context,String token,String file_path,String fileName) {
        super(context);
        this.token = token;
        this.file_path = file_path;
        this.fileName = fileName;
    }

    @Override
    public YoutubeData loadInBackground() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("part", "snippet");
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlYoutube(Config.YOUTUBE_APIV3, parameter, this, token, file_path,fileName);
            AppUtil.debugLog("RESULT", ""+resData);

            if(resData == null){
                return null;
            }
            result = new String(resData, "UTF-8");
            AppUtil.debugLog("RESULT", result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }
}
