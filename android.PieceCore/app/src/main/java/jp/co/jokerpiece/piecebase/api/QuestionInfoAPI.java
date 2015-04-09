package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.NewsInfoData;
import jp.co.jokerpiece.piecebase.data.QuestionInfoData;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import jp.co.jokerpiece.piecebase.util.HttpClient.HttpClientInterface;

public class QuestionInfoAPI extends AsyncTaskLoader<QuestionInfoData> implements HttpClientInterface {
	private String questionId;
    private String answerNum;

	public QuestionInfoAPI(Context context, String questionId, String answerNum) {
		super(context);
		this.questionId = questionId;
        this.answerNum = answerNum;
	}

	@Override
	public QuestionInfoData loadInBackground() {
		QuestionInfoData quesInfoData = new QuestionInfoData();

    	HashMap<String, String> parameter = new HashMap<String, String>();
    	parameter.put("app_id", Config.APP_ID);
    	parameter.put("app_key", Common.getUUID(getContext()));
    	parameter.put("question_id", questionId == null ? "" : questionId);
        parameter.put("answer_num", answerNum == null ? "" : answerNum);
        String result = null;
        try {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(Config.SENDID_GET_QUES, parameter, this);
            if(resData == null){
            	return null;
            }
            result = new String(resData, "UTF-8");
            Log.d("RESULT",result);
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
        try {
			JSONObject rootObject = new JSONObject(result);
			//Log.d("JSON", rootObject.toString());
//			int error_code = rootObject.getInt("error_code");
//			if(error_code != 0){
//				return null;
//			}
            if (!rootObject.isNull("item_url")) {
                quesInfoData.itemUrl = rootObject.getString("item_url");
            }
			quesInfoData.questionId = rootObject.getString("question_id");
            quesInfoData.text = rootObject.getString("text");
            quesInfoData.imgUrl1 = rootObject.getString("img_url1");
            quesInfoData.imgUrl2 = rootObject.getString("img_url2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        Log.d("quesInfoData", quesInfoData.toString());
        return quesInfoData;
	}

	@Override
	public void HttpClientProgress(float progress) {

	}

}
