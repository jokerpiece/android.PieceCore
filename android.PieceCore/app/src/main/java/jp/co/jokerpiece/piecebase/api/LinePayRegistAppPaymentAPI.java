package jp.co.jokerpiece.piecebase.api;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.LinePayRegistAppPaymentData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;

/**
 * Created by Antifuture on 16/4/8.
 */
public class LinePayRegistAppPaymentAPI extends AsyncTaskLoader<LinePayRegistAppPaymentData> implements HttpClient.HttpClientInterface
{

    private String app_id=Config.APP_ID;               // = アプリID
    private String app_key=Config.APP_KEY;              // = アプリキー
    private String uuid=Common.getUUID(getContext());
    private String payment_kbn=Config.PAY_SELECT_KBN;          // = 決済サービス(1:line pay)
    private String user_id="123";              // = ユーザID
    private String order_no;             // = 注文番号
    private String product_id;           // = 商品ID
    private String payment_price;        //= 決済金額
    private String trans_no;             // = 取引番号
    private String item_price;           // = 商品価格
    private String amount;               // = 個数
    private String fee="1000";                  // = 手数料
    private String sei;                  // = 性,
    private String mei;                  // = 名,
    private String post;                 // = 郵便番号,
    private String address_tdfk;         // = 住所(都道府県),
    private String address_city;         // = 住所(市区町村),
    private String address_street;       // = 住所(番地),
    private String tel;                  // = 電話番号
    private String mail_address;         // = メールアドレス,
    private String delivery_price;       //=輸送料
    private String delivery_time;        //=配送時間
    private String kikaku_name;          // =規格名称

    public LinePayRegistAppPaymentAPI(Context context, Bundle bundle)
    {

        super(context);

        order_no = bundle.getString("order_id");
        product_id=bundle.getString("item_id");
        trans_no=bundle.getString("trans_no");
        item_price=bundle.getString("item_price");
        amount=bundle.getString("order_amount");

        sei=bundle.getString("Sei");
        mei=bundle.getString("Mei");
        post=bundle.getString("Post");
        address_tdfk=bundle.getString("Address_tdfk");
        address_city=bundle.getString("Address_city");
        address_street=bundle.getString("Address_street");
        tel=bundle.getString("Tel");
        mail_address=bundle.getString("Mail_address");

        delivery_price=bundle.getString("delivery_price");
        delivery_time=bundle.getString("delivery_time");

        payment_price= bundle.getString("payment_price");
        kikaku_name = bundle.getString("kikaku_name");

    }

    @Override
    public LinePayRegistAppPaymentData loadInBackground()
    {

        String result = null;
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("app_id", app_id);
        //parameter.put("app_key", app_key);
        //parameter.put("uuid", uuid);
        parameter.put("order_no", order_no);
        parameter.put("product_id", product_id);
        parameter.put("payment_price", payment_price);
        parameter.put("trans_no", trans_no);
        parameter.put("item_price", item_price);
        parameter.put("amount", amount);
        parameter.put("fee", fee);
        parameter.put("payment_kbn", payment_kbn);
        parameter.put("user_id", user_id);
        parameter.put("mail_address", mail_address);
        parameter.put("sei", sei);
        parameter.put("mei", mei);
        parameter.put("post", post);
        parameter.put("address_tdfk", address_tdfk);
        parameter.put("address_city", address_city);
        parameter.put("address_street", address_street);
        parameter.put("tel", tel);
        parameter.put("delivery_price", delivery_price);
        parameter.put("delivery_time", delivery_time);
        parameter.put("kikaku_name", kikaku_name);

        String url = Config.REGIST_LINEPAY_APP_PAYMENT;

        try
        {
            byte[] resData = HttpClient.getByteArrayFromUrlPost(url, parameter,this);
            if(resData == null)
            {
                return null;
            }

            result = new String(resData, "UTF-8");

            AppUtil.debugLog("RESULT", result);
            AppUtil.debugLog("app_id", Config.APP_ID);
            AppUtil.debugLog("app_key",Config.APP_KEY);
            AppUtil.debugLog("uuid",Common.getUUID(getContext()));

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }

        //JSONDATA
        try
        {
            JSONObject rootObject = new JSONObject(result);
            AppUtil.debugLog("JSON", rootObject.toString());

            int error_code = rootObject.getInt("error_code");
            if(error_code != 0)
            {
                return null;
            }


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public void HttpClientProgress(float progress) {

    }
}
