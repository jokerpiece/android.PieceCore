package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;

import android.view.KeyEvent;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

import jp.co.jokerpiece.piecebase.api.LinePayRegistAppPaymentAPI;
import jp.co.jokerpiece.piecebase.api.LinePaymentConfirmAPI;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.LinePayRegistAppPaymentData;
import jp.co.jokerpiece.piecebase.data.LinePaymentConfirmData;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by Sou on 16/4/6.
 */
public class LinePayActivity extends Activity
{



    private TextView itemNameText, itemPriceText, deliveryPriceText, amountText, deliveryAddressText,
                        nameText, mailText, kikakuNameTest;

    private ImageView okButton, cancelButton;

    //感謝画面
    private TextView thanksMessageTextView;


    private Bundle bundleAPI;

    SharedPreferences.Editor editor;
    SharedPreferences data;

    //linePayActivityBundle
    private String kikaku_name;         // = 規格名称
    private String order_id;             // = 注文番号
    private String item_id;           // = 商品ID
    private String item_title;         //商品名
    private String payment_price;        // = 決済金額
    private String trans_no;          // = trans_no
    private String item_price;           // = 商品価格
    private String amount;               // = 個数
    private String fee="1000";           // = 手数料
    private String sei;                  // = 性,
    private String mei;                  // = 名,
    private String post;                 // = 郵便番号,
    private String address_tdfk;         // = 住所(都道府県),
    private String address_city;         // = 住所(市区町村),
    private String address_street;       // = 住所(番地),
    private String tel;                  // = 電話番号
    private String mail_address;         // = メールアドレス,
    private String delivery_price;       // =輸送料
    private String delivery_time;        // =配送時間

    private String buying_over = "false";

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_linepay);

        if(Config.ANALYTICS_MODE.equals("true")){
            App app = (App)this.getApplication();
            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
            t.setScreenName("LINE PAYMENT");
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }

        data = getSharedPreferences("SystemDataSave", Context.MODE_PRIVATE);

        editor = data.edit();
        editor.putString("buying_over", buying_over);
        editor.commit();
                item_id = data.getString("item_id","");
                item_title = data.getString("item_title", "" );
                item_price=data.getString("item_price", "");
                amount=data.getString("order_amount", "");
                sei=data.getString("Sei", "");
                mei=data.getString("Mei", "");
                post=data.getString("Post", "");
                address_tdfk=data.getString("Address_tdfk", "");
                address_city=data.getString("Address_city", "");
                address_street=data.getString("Address_street", "");
                tel=data.getString("Tel", "");
                mail_address=data.getString("Mail_address", "");
                delivery_price=data.getString("delivery_price", "");
                delivery_time = data.getString("delivery_time", "");
                trans_no=data.getString("trans_no", "");
                payment_price=data.getString("payment_price", "");
                order_id=data.getString("order_id", "");
                kikaku_name=data.getString("kikaku_name","");

        //create bundleAPI
        context = this;
        bundleAPI = new Bundle();

        //put data to bundleAPI
        bundleAPI.putString("order_id",order_id);
        bundleAPI.putString("item_id",item_id);
        bundleAPI.putString("trans_no",trans_no);
        bundleAPI.putString("item_price",item_price);
        bundleAPI.putString("order_amount",amount);
        bundleAPI.putString("Sei",sei);
        bundleAPI.putString("Mei",mei);
        bundleAPI.putString("Post",post);
        bundleAPI.putString("Address_tdfk",address_tdfk);
        bundleAPI.putString("Address_city",address_city);
        bundleAPI.putString("Address_street",address_street);
        bundleAPI.putString("Tel",tel);
        bundleAPI.putString("Mail_address",mail_address);
        bundleAPI.putString("delivery_price",delivery_price);
        bundleAPI.putString("delivery_time",delivery_time);
        bundleAPI.putString("payment_price", payment_price);
        bundleAPI.putString("kikaku_name",kikaku_name);

        /*
        //Open Line Pay Web
        Uri uri = Uri.parse(paymentUrlWeb);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
        */
        setViewComponent();

    }

    public void setViewComponent()
    {
        thanksMessageTextView = (TextView)findViewById(R.id.thank_message);

        itemNameText = (TextView)findViewById(R.id.item_name);
        kikakuNameTest = (TextView)findViewById(R.id.kikaku_name);
        itemPriceText = (TextView)findViewById(R.id.item_price);
        deliveryPriceText = (TextView)findViewById(R.id.delivery_price);
        amountText = (TextView)findViewById(R.id.total_price);
        deliveryAddressText = (TextView)findViewById(R.id.delivery_address);
        nameText = (TextView)findViewById(R.id.name);
        mailText = (TextView)findViewById(R.id.email);

        okButton = (ImageView)findViewById(R.id.pay_ok);
        cancelButton =(ImageView)findViewById(R.id.pay_cancel);

        //set TextView
        itemNameText.setText(item_title);
        if(kikaku_name!="" && kikaku_name!=null)
        {
            kikakuNameTest.setText(kikaku_name);
            findViewById(R.id.kikaku_layout).setVisibility(View.VISIBLE);
        }
        itemPriceText.setText(item_price+"円"+" * "+amount+"個"+" = "
                                +getItemTotalPrice(item_price, amount)+" 円");
        deliveryPriceText.setText(delivery_price+" 円");
        amountText.setText(payment_price+" 円");
        deliveryAddressText.setText(address_tdfk+address_city+address_street);
        nameText.setText(sei+" 　"+mei+" "+"様");
        mailText.setText(mail_address);

        //決済ボタン
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(LinePayActivity.this);

                builder.setMessage("ご注文内容で商品を購入します。");

                //確定決済ボタンを押した時>>>>決済
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppUtil.debugLog("okButton on click", "okButton has been clicked");

                        //call LinePaymentConfirmAPI
                        linePaymentConfirm();
                        //call linPayRegistAppPaymentAPI
                        linePayRegistAppPayment();

                        Toast.makeText(LinePayActivity.this, "決済完了", Toast.LENGTH_LONG).show();
                        findViewById(R.id.line_scrollview).setVisibility(View.GONE);
                        thanksMessageTextView.setVisibility(View.VISIBLE);
                        thanksMessageTextView.setText("ご購入ありがとうございました。" +
                                "\nタップで画面を閉じます。\n引き続きお買い物をお楽みください。");
                        thanksMessageTextView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                buying_over = "true";
                                editor = data.edit();
                                editor.putString("buying_over",buying_over);
                                editor.commit();
                                finish();


                            }
                        });



                    }
                });

                //確定cancelを押した時＞＞＞＞＞close the alert dialog
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtil.debugLog("cancelButton on click", "cancelButton has been clicked");

                    }
                });

                AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                dialog.show();

            }
        });

        //キャンセルボタン
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AppUtil.debugLog("cancelButton on click", "cancelButton has been clicked");

                AlertDialog.Builder builder = new AlertDialog.Builder(LinePayActivity.this);
                builder.setMessage("注文をキャンセルして商品一覧に戻ります。");

                //cancelを押した時>>>>>close the alert dialog
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtil.debugLog("cancelButton on click", "cancelButton has been clicked");


                    }
                });

                //OKを押した時>>>>cancel and back to itemlist fragment
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtil.debugLog("cancelButton on click", "cancelButton has been clicked");

                        //close activity
                        buying_over = "true";
                        editor = data.edit();
                        editor.putString("buying_over",buying_over);
                        editor.commit();
                        finish();

                    }
                });

                AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                dialog.show();
                

            }
        });

    }



    @Override
    protected void onResume()
    {
        super.onResume();

    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    private void linePaymentConfirm()
    {
        this.getLoaderManager().initLoader
                (Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<LinePaymentConfirmData>() {


                    @Override
                    public Loader<LinePaymentConfirmData> onCreateLoader(int id, Bundle args) {
                        LinePaymentConfirmAPI linePaymentConfirmAPI = new LinePaymentConfirmAPI(context, bundleAPI);
                        linePaymentConfirmAPI.forceLoad();
                        return linePaymentConfirmAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<LinePaymentConfirmData> loader, LinePaymentConfirmData data)
                    {


                    }

                    @Override
                    public void onLoaderReset(Loader<LinePaymentConfirmData> loader) {

                    }
                });
    }


    private void linePayRegistAppPayment()
    {
        this.getLoaderManager().initLoader
                (Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<LinePayRegistAppPaymentData>()
                {


                    @Override
                    public Loader<LinePayRegistAppPaymentData> onCreateLoader(int id, Bundle args) {


                        LinePayRegistAppPaymentAPI linePayRegistAppPaymentAPI = new LinePayRegistAppPaymentAPI(context, bundleAPI );
                        linePayRegistAppPaymentAPI.forceLoad();



                        return linePayRegistAppPaymentAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<LinePayRegistAppPaymentData> loader, LinePayRegistAppPaymentData data) {

                    }

                    @Override
                    public void onLoaderReset(Loader<LinePayRegistAppPaymentData> loader) {

                    }
                });
    }

    public String getItemTotalPrice(String itemPrice,String itemAmount)
    {
        int total;
        total = Integer.parseInt(itemPrice)*Integer.parseInt(itemAmount);

        return Integer.toString(total);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }


}
