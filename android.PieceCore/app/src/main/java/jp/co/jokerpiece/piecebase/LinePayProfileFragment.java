package jp.co.jokerpiece.piecebase;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.co.jokerpiece.piecebase.api.DeliveryPriceAPI;
import jp.co.jokerpiece.piecebase.api.LinePayRegistAppPaymentAPI;
import jp.co.jokerpiece.piecebase.api.LinePayRegisterAPI;
import jp.co.jokerpiece.piecebase.api.OrderIdAPI;
import jp.co.jokerpiece.piecebase.api.PostNumberAPI;
import jp.co.jokerpiece.piecebase.api.SendProfileAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.DeliveryPriceData;
import jp.co.jokerpiece.piecebase.data.LinePayData;
import jp.co.jokerpiece.piecebase.data.LinePayRegistAppPaymentData;
import jp.co.jokerpiece.piecebase.data.LinePayRegisterData;
import jp.co.jokerpiece.piecebase.data.OrderIdData;
import jp.co.jokerpiece.piecebase.data.PostNumberData;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.HttpClient;
import twitter4j.HttpResponse;

/**
 * Created by Antifuture on 16/3/25.
 * LinePay決済の個人情報の入力画面
 */
public class LinePayProfileFragment extends Fragment
{

    private Bundle bundleAPI = new Bundle();

    //引継ぎ情報
    //商品画像のURL
    private String img_url = null;
    //商品ID
    private String item_id = null;
    //商品金額
    private String item_price = null;
    //商品名
    private String item_title = null;
    //商品説明
    private String text = null;

    private String item_url = null;
    //商品在庫数
    private String item_stocks = null;
    //商品注文数
    private String order_amount = "1";
    //
    private String kikaku_name=null;


    Context context;
    //View
    View rootView;
    TextView anyTimeBtn,before12Btn, from12To14Btn, from14To16Btn, from16To18Btn, from18To20Btn, from20To21Btn;
    ImageView sendMessage;
    ImageView postNumberSearch;

    private EditText seiEdText;             //姓の入力欄
    private EditText meiEdText;             //名の入力欄
    private EditText postEdText;            //郵便番号
    public  EditText addressTdfkEdText;     //住所(都道府県)
    public  EditText addressCityEdText;     //住所(市区町村)
    private EditText addressStreetEdText;   //住所(番地)
    private EditText mailAddressEdText;     //メールアドレス
    private EditText telEdText;             //= 電話番号
    private EditText mailAddressConfirmEdText;//メールアドレス確認

    //Input data by editText
    public String User_id;             //= ユーザID
    public String Password;            //= パスワード
    public String Sei;                 //= 性
    public String Mei;                 //= 名
    public String Birthday;            //= 誕生日yyyyMMdd
    public static String Post;                //= 郵便番号
    public static String Address_tdfk;        //= 住所(都道府県)
    public String Address_city;        //= 住所(市区町村)
    public String Address_street;      //= 住所(番地)
    public String Sex;                 //= 性別 0:男 1:女
    public String Mail_address;        //= メールアドレス
    public String Tel;                 //= 電話番号
    public String Anniversary_name;    //= 記念日名
    public String Anniversary;         //= yyyyMMdd
    public String order_id;


    //post number API response
    public String address1,address2,address3;
    //deliveryPrice API response
    public String deliveryPrice;
    public String fee="0";
    public String payment_price;
    //LinePayRegister API response
    public String paymentUrlWeb;
    public String paymentUrl;
    public String trans_no;
    //send data to LinePayActivity


    //時間帯指定ボタンのコントロール
    private int currentBtn=0, lastBtn=0;
    //select time
    private String currentSelectedTime=null;

    private String mailCheck;

    SharedPreferences systemData;
    SharedPreferences.Editor systemDataEditor;

    SharedPreferences personalData;
    SharedPreferences.Editor personalDataEditor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);


        context = getActivity();

        systemData = getActivity().getSharedPreferences("SystemDataSave", Context.MODE_PRIVATE);

        Common.setCurrentFragment(Config.PaypalFragmentNum);
        rootView = inflater.inflate(R.layout.fragment_linepayprofile, container, false);

        setViewComponent();

        systemDataEditor = systemData.edit();
        systemDataEditor.putString("buying_over", "false");
        systemDataEditor.commit();

        boolean sameAsBeforeButton = false;
        boolean newButton = false;

        //get data from LinePaySelectDeliveryFragment
        Bundle bundle = getArguments();
        if (bundle != null) {

            sameAsBeforeButton = bundle.getBoolean("SameAsBeforeButton");
            newButton = bundle.getBoolean("NewButton");

            img_url = bundle.getString("img_url");
            item_id = bundle.getString("item_id");
            item_price = bundle.getString("item_price");
            item_title = bundle.getString("item_title");
            text = bundle.getString("text");
            item_url=bundle.getString("item_url");
            item_stocks=bundle.getString("item_stocks");
            order_amount = bundle.getString("order_amount");
            kikaku_name = bundle.getString("kikaku_name");

            //put data to bundle API
            bundleAPI.putString("img_url",img_url);
            bundleAPI.putString("item_url",item_url);
            bundleAPI.putString("item_id",item_id);
            bundleAPI.putString("item_price",item_price);
            bundleAPI.putString("item_title",item_title);
            bundleAPI.putString("text",text);
            bundleAPI.putString("item_stocks",item_stocks);
            bundleAPI.putString("order_amount", order_amount);
            bundleAPI.putString("kikaku_name",kikaku_name);


        }

        if(sameAsBeforeButton)
        {
            //Check if sharekeyperference is exist
            /*
            File f = new File(
                    "/data/data/jp.co.jokerpiece.piecebase/shared_prefs/PersonalDataSave.xml");
            */

            File f = new File("/data/data/"+Config.PACKAGE_NAME+"/shared_prefs/PersonalDataSave.xml");
            if(f.exists())
            {

            }
            else
            {
                //提醒使用者輸入新訊息
            }

                //get saving data by SharedPreference

                personalData = getActivity().getSharedPreferences("PersonalDataSave", Context.MODE_PRIVATE);
                String sei = personalData.getString("Sei","");
                String mei = personalData.getString("Mei", "");
                String post = personalData.getString("Post", "");
                String address_tdfk = personalData.getString("Address_tdfk", "");
                String address_city = personalData.getString("Address_city", "");
                String address_street = personalData.getString("Address_street", "");
                String tel = personalData.getString("Tel", "");
                String mail_address = personalData.getString("Mail_address", "");
                String deliveryTime = personalData.getString("delivery_time","");

                seiEdText.setText(sei);
                meiEdText.setText(mei);
                postEdText.setText(post);
                addressTdfkEdText.setText(address_tdfk);
                addressCityEdText.setText(address_city);
                addressStreetEdText.setText(address_street);
                telEdText.setText(tel);
                mailAddressEdText.setText(mail_address);
                mailAddressConfirmEdText.setText(mail_address);
                currentSelectedTime=deliveryTime;

                if(deliveryTime.equals("指定なし"))
                {
                    anyTimeBtn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    anyTimeBtn.setTextColor(Color.WHITE);
                    lastBtn = 1;
                }
                else if (deliveryTime.equals("午前中"))
                {
                    before12Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    before12Btn.setTextColor(Color.WHITE);
                    lastBtn = 2;

                }
                else if (deliveryTime.equals("12:00~14:00"))
                {
                    from12To14Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    from12To14Btn.setTextColor(Color.WHITE);
                    lastBtn = 3;

                }
                else if (deliveryTime.equals("14:00~16:00"))
                {
                    from14To16Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    from14To16Btn.setTextColor(Color.WHITE);
                    lastBtn = 4;

                }
                else if (deliveryTime.equals("16:00~18:00"))
                {
                    from16To18Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    from16To18Btn.setTextColor(Color.WHITE);
                    lastBtn = 5;

                }
                else if (deliveryTime.equals("18:00~20:00"))
                {
                    from18To20Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    from18To20Btn.setTextColor(Color.WHITE);
                    lastBtn = 6;

                }
                else if (deliveryTime.equals("20:00~21:00"))
                {
                    from20To21Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                    from20To21Btn.setTextColor(Color.WHITE);
                    lastBtn = 7;

                }



        }
        else if (newButton)
        {
            //如果是新情報登入執行
        }

        return rootView;
    }


    private void setViewComponent()
    {
        //Get post number button component
        postNumberSearch = (ImageView)rootView.findViewById(R.id.postNumberSearch);

        //Get EditText component
        seiEdText = (EditText)rootView.findViewById(R.id.lastNameInputBar);
        meiEdText = (EditText)rootView.findViewById(R.id.firstNameInputBar);
        postEdText = (EditText)rootView.findViewById(R.id.postNumberInputBar);
        addressTdfkEdText = (EditText)rootView.findViewById(R.id.tdfkInputBar);
        addressCityEdText = (EditText)rootView.findViewById(R.id.cityInputBar);
        addressStreetEdText = (EditText)rootView.findViewById(R.id.streetInputBar);
        mailAddressEdText = (EditText)rootView.findViewById(R.id.mailAddressInputBar);
        telEdText = (EditText)rootView.findViewById(R.id.telInputBar);
        mailAddressConfirmEdText = (EditText)rootView.findViewById(R.id.mailAddressCheckInputBar);

        //Get TextView component
        anyTimeBtn = (TextView)rootView.findViewById(R.id.anytimeBtn);
        before12Btn = (TextView)rootView.findViewById(R.id.befor12Btn);
        from12To14Btn = (TextView)rootView.findViewById(R.id.from12to14Btn);
        from14To16Btn = (TextView)rootView.findViewById(R.id.from14to16Btn);
        from16To18Btn = (TextView)rootView.findViewById(R.id.from16to18Btn);
        from18To20Btn = (TextView)rootView.findViewById(R.id.from18to20Btn);
        from20To21Btn = (TextView)rootView.findViewById(R.id.from20to21Btn);

        //Get sendMessage button component
        sendMessage = (ImageView)rootView.findViewById(R.id.sendMessage);


        //届ける時間帯を選択した処理
        anyTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anyTimeBtn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                anyTimeBtn.setTextColor(Color.WHITE);
                //初めて押した時
                if (lastBtn==0)
                {
                    lastBtn = 1;
                    currentSelectedTime="指定なし";

                }

                currentBtn = 1;
                //他の時間帯を選んだ時に前の時間帯のボランの背景を消す
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime = "指定なし";
                }


            }
        });
        before12Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                before12Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                before12Btn.setTextColor(Color.WHITE);
                if (lastBtn==0)
                {
                    lastBtn = 2;
                    currentSelectedTime="午前中";

                }
                currentBtn = 2;
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime="午前中";
                }

            }
        });
        from12To14Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from12To14Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                from12To14Btn.setTextColor(Color.WHITE);
                if (lastBtn==0)
                {
                    lastBtn = 3;
                    currentSelectedTime="12:00~14:00";

                }
                currentBtn = 3;
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime="12:00~14:00";
                }

            }
        });
        from14To16Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from14To16Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                from14To16Btn.setTextColor(Color.WHITE);

                if (lastBtn==0)
                {
                    lastBtn = 4;
                    currentSelectedTime="14:00~16:00";

                }
                currentBtn = 4;
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime="14:00~16:00";
                }
            }
        });
        from16To18Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from16To18Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                from16To18Btn.setTextColor(Color.WHITE);
                if (lastBtn==0)
                {
                    lastBtn = 5;
                    currentSelectedTime="16:00~18:00";

                }
                currentBtn = 5;
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime="16:00~18:00";
                }
            }
        });
        from18To20Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from18To20Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);
                from18To20Btn.setTextColor(Color.WHITE);
                if (lastBtn==0)
                {
                    lastBtn = 6;
                    currentSelectedTime="18:00~20:00";

                }
                currentBtn = 6;
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime="18:00~20:00";
                }

            }
        });
        from20To21Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from20To21Btn.setBackgroundResource(R.drawable.component_linepay_timeselect_border);

                from20To21Btn.setTextColor(Color.WHITE);
                if (lastBtn==0)
                {
                    lastBtn = 7;
                    currentSelectedTime="20:00~21:00";

                }
                currentBtn = 7;
                if (currentBtn != lastBtn)
                {
                    timeSelectBtnBackgroundGone(lastBtn);
                    lastBtn = currentBtn;
                    currentSelectedTime="20:00~21:00";
                }
            }
        });

        //postNumberSearch button onclick
        postNumberSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SendProfileAPI sendProfileAPI = new SendProfileAPI();
                AppUtil.debugLog("postNumberSearch Button", "postNumberSearch Button onClick");

                String postNumber = postEdText.getText().toString();//入力した郵便番号を取得
                Post = postNumber;


                //Post number exception control
                final View item = LayoutInflater.from(LinePayProfileFragment.this.getActivity()).inflate(R.layout.fragment_linepay_profile_postexception_dialog, null);

                TextView nullInputAllert = (TextView)item.findViewById(R.id.nullPostInputAlertText);

                if(postNumber.equals(""))
                {

                    nullInputAllert.setText("\n");
                    nullInputAllert.setText("郵便番号を入力してください。");

                    new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                            .setTitle("郵便番号エラー")
                            .setView(item)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            }).show();

                }
                else if (postNumber.length()!=7)
                {
                    nullInputAllert.setText("\n");
                    nullInputAllert.setText("7桁の数字を入力してください。");

                    new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                            .setTitle("郵便番号エラー")
                            .setView(item)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            }).show();

                }
                else
                {
                    //call postNumberAPI
                    getAddressFromPostNumberCenter();

                    //close the keyboard
                    //InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(postEdText.getWindowToken(), 0);

                }

            }
        });

        //sendMessage button onclick
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SendProfileAPI sendProfileAPI = new SendProfileAPI();
                AppUtil.debugLog("sendMessage Button", "sendMessage Button onClick");


                ArrayList<String> list = new ArrayList<String>();
                boolean nullInput = false;

                //Check data has already input correctly
                if(currentSelectedTime==null)
                {
                    AppUtil.debugLog("LinePayProfileFragment_currentSelectTime_empty", "null");
                    list.add("時間帯を指定してください。");
                    nullInput = true;
                    //AppUtil.debugLog("LinePayProfileFragment_ArrayList_currentSelectTime", list.get(0));
                }
                if (seiEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_Sei_empty", "null");
                    list.add("姓を入力してください。");
                    nullInput = true;
                }
                if (meiEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_Mei_empty", "null");
                    list.add("名を入力してください。");
                    nullInput = true;
                }
                if (postEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_Post_empty", "null");
                    list.add("郵便番号を入力してください。");
                    nullInput = true;
                }
                if (addressTdfkEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_Tdfk_empty", "null");
                    list.add("都道府県を入力してください。");
                    nullInput = true;
                }
                if (addressCityEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_City", "null");
                    list.add("市区町村を入力してください。");
                    nullInput = true;
                }
                if (addressStreetEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_Street", "null");
                    list.add("番地を入力してください。");
                    nullInput = true;
                }
                if (telEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_Tel_empty", "null");
                    list.add("電話番号を入力してください。");
                    nullInput = true;
                }
                if (mailAddressEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_mailAddress_empty", "null");
                    list.add("メールアドレスを入力してください。");
                    nullInput = true;
                }

                if (mailAddressConfirmEdText.getText().toString().equals(""))
                {
                    AppUtil.debugLog("LinePayProfileFragment_mailAddress_empty", "null");
                    list.add("確認メールアドレスを入力してください。");
                    nullInput = true;
                }
                //check mailAddress
                if(!mailAddressEdText.getText().toString().equals(mailAddressConfirmEdText.getText().toString()))
                {
                    AppUtil.debugLog("LinePayProfileFragment_mailAddress_empty", "null");
                    list.add("メールアドレスが一致していませんので、もう一度確認してください。");
                    nullInput = true;
                }

                //check mailAddress validation
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mailAddressEdText.getText().toString()).matches())
                {
                    AppUtil.debugLog("LinePayProfileFragment_mailAddress_empty", "null");
                    list.add("無効なメールアドレス、もう一度確認してください。");
                    nullInput = true;
                }

                //check comfirm mailAddress validation
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mailAddressConfirmEdText.getText().toString()).matches())
                {
                    AppUtil.debugLog("LinePayProfileFragment_mailAddress_empty", "null");
                    list.add("無効な確認メールアドレス、もう一度確認してください。");
                    nullInput = true;
                }



                //nullInput = false
                if(nullInput)
                {

                    final View item = LayoutInflater.from(LinePayProfileFragment.this.getActivity()).inflate(R.layout.fragment_linepay_profile_fragment_dialog, null);

                    TextView nullInputAllert = (TextView)item.findViewById(R.id.nullInputAlertText);

                    nullInputAllert.setText("\n");
                    for(String text:list)
                    {

                        //AppUtil.debugLog("LinePayProfileFragment_TextView_setText", text+"\n");
                        nullInputAllert.append(text+"\n");
                    }

                    new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                            .setTitle("以下のタイトルに合わせた内容を入力欄で入力してください。")
                            .setView(item)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            }).show();

                }

                else
                {
                    //get input data from EditText
                    Sei = seiEdText.getText().toString();
                    Mei = meiEdText.getText().toString();
                    Post = postEdText.getText().toString();
                    Address_tdfk = addressTdfkEdText.getText().toString();
                    Address_city = addressCityEdText.getText().toString();
                    Address_street = addressStreetEdText.getText().toString();
                    Mail_address = mailAddressEdText.getText().toString();
                    Tel = telEdText.getText().toString();

                    //put data result to debug monitor
                    String result = Sei + "\n" + Mei + "\n" + Post + "\n" + Address_tdfk + "\n" + Address_city + "\n"
                            + Address_street + "\n" + Mail_address + "\n" + Tel + "\n";
                    AppUtil.debugLog("Sending Data", result);

                    //put data to bundle API
                    bundleAPI.putString("Sei",Sei);
                    bundleAPI.putString("Mei",Mei);
                    bundleAPI.putString("Post",Post);
                    bundleAPI.putString("Address_tdfk",Address_tdfk);
                    bundleAPI.putString("Address_city", Address_city);
                    bundleAPI.putString("Address_street", Address_street);
                    bundleAPI.putString("Mail_address", Mail_address);
                    bundleAPI.putString("Tel", Tel);
                    bundleAPI.putString("delivery_time", currentSelectedTime);

                    //call SendProfileAPI
                    SendMessageToProfile();


                    //personaldataをアプリ内で保存
                    personalData = getActivity().getSharedPreferences("PersonalDataSave", Context.MODE_PRIVATE);
                    personalDataEditor = personalData.edit();
                    personalDataEditor.putString("Sei", Sei);
                    personalDataEditor.putString("Mei", Mei);
                    personalDataEditor.putString("Post", Post);
                    personalDataEditor.putString("Address_tdfk", Address_tdfk);
                    personalDataEditor.putString("Address_city", Address_city);
                    personalDataEditor.putString("Address_street", Address_street);
                    personalDataEditor.putString("Mail_address", Mail_address);
                    personalDataEditor.putString("Tel", Tel);
                    personalDataEditor.putString("delivery_time", currentSelectedTime);
                    personalDataEditor.commit();

                    //systemdataをアプリ内で保存
                    systemData = getActivity().getSharedPreferences("SystemDataSave", Context.MODE_PRIVATE);
                    systemDataEditor = systemData.edit();
                    systemDataEditor.putString("Sei", Sei);
                    systemDataEditor.putString("Mei", Mei);
                    systemDataEditor.putString("Post", Post);
                    systemDataEditor.putString("Address_tdfk", Address_tdfk);
                    systemDataEditor.putString("Address_city", Address_city);
                    systemDataEditor.putString("Address_street", Address_street);
                    systemDataEditor.putString("Mail_address", Mail_address);
                    systemDataEditor.putString("Tel", Tel);
                    systemDataEditor.putString("delivery_time", currentSelectedTime);



                    //call GetOrderIdAPI
                    getOrderId();




                }

            }
        });



    }

    private void timeSelectBtnBackgroundGone(int currentBtn)
    {
        switch(currentBtn)
        {
            case 1:
                anyTimeBtn.setBackgroundResource(0);
                anyTimeBtn.setTextColor(Color.parseColor("#598f60"));
                break;
            case 2:
                before12Btn.setBackgroundResource(0);
                before12Btn.setTextColor(Color.parseColor("#598f60"));
                break;
            case 3:
                from12To14Btn.setBackgroundResource(0);
                from12To14Btn.setTextColor(Color.parseColor("#598f60"));
                break;
            case 4:
                from14To16Btn.setBackgroundResource(0);
                from14To16Btn.setTextColor(Color.parseColor("#598f60"));
                break;
            case 5:
                from16To18Btn.setBackgroundResource(0);
                from16To18Btn.setTextColor(Color.parseColor("#598f60"));
                break;
            case 6:
                from18To20Btn.setBackgroundResource(0);
                from18To20Btn.setTextColor(Color.parseColor("#598f60"));
                break;
            case 7:
                from20To21Btn.setBackgroundResource(0);
                from20To21Btn.setTextColor(Color.parseColor("#598f60"));
                break;
            default:
                break;
        }
    }

    private void SendMessageToProfile()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<LinePayData>()
                {


                    @Override
                    public Loader<LinePayData> onCreateLoader(int id, Bundle args)
                    {
                        SendProfileAPI sendProfileAPI = new SendProfileAPI(context, bundleAPI);
                        sendProfileAPI.forceLoad();

                        return sendProfileAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<LinePayData> loader, LinePayData data)
                    {

                    }

                    @Override
                    public void onLoaderReset(Loader<LinePayData> loader)
                    {

                    }
                });
    }

    //Get Address From Post Number Center
    private void getAddressFromPostNumberCenter()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<PostNumberData>() {


                    @Override
                    public Loader<PostNumberData> onCreateLoader(int id, Bundle args) {
                        PostNumberAPI postNumberAPI = new PostNumberAPI(context);
                        postNumberAPI.forceLoad();

                        return postNumberAPI;

                    }


                    @Override
                    public void onLoadFinished(Loader<PostNumberData> loader, PostNumberData data) {



                        //null conmunication exception control
                        if(data.result=="no_data")
                        {
                            final View item = LayoutInflater.from(LinePayProfileFragment.this.getActivity()).inflate(R.layout.fragment_linepay_profile_postexception_dialog, null);

                            TextView nullInputAllert = (TextView)item.findViewById(R.id.nullPostInputAlertText);
                            nullInputAllert.setText("\n");
                            nullInputAllert.setText("住所が確認できませんでした。\n\n郵便番号を確認してください。");

                            new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                                    .setTitle("郵便番号エラー")
                                    .setView(item)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {

                                        }
                                    }).show();

                        }
                        else
                        {
                            address1 = data.address1;
                            address2 = data.address2;
                            address3 = data.address3;
                            AppUtil.debugLog("onFinish address1", address1);
                            AppUtil.debugLog("onFinish address2", address2);
                            AppUtil.debugLog("onFinish address3", address3);
                            //set Post Number response
                            addressTdfkEdText.setText(address1);
                            addressCityEdText.setText(address2+address3);

                            //close the keyboard
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(postEdText.getWindowToken(), 0);
                        }



                    }

                    @Override
                    public void onLoaderReset(Loader<PostNumberData> loader) {

                    }


                });


    }

    private void getDeliveryPrice()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<DeliveryPriceData>()
                {


                    @Override
                    public Loader<DeliveryPriceData> onCreateLoader(int id, Bundle args)
                    {

                        DeliveryPriceAPI deliveryPriceAPI = new DeliveryPriceAPI(context, bundleAPI);
                        deliveryPriceAPI.forceLoad();

                        return deliveryPriceAPI;

                    }

                    @Override
                    public void onLoadFinished(Loader<DeliveryPriceData> loader, DeliveryPriceData data)
                    {
                        AppUtil.debugLog("data.delivery_price",data.delivery_price);
                        if((data.delivery_price.equals(""))||(data.delivery_price.equals(null)))
                        {
                            new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                                    .setTitle("DELIVERY PRICE ERROR")
                                    .setMessage("ERROR CODE: "+data.error_code+"\n"+"ERROR MESSAGE: "+data.error_message)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                        else
                        {
                            deliveryPrice = data.delivery_price;

                            bundleAPI.putString("delivery_price",deliveryPrice);

                            systemDataEditor.putString("delivery_price",deliveryPrice);

                            //Get Payment Price
                            payment_price=getPaymentPrice(item_price, deliveryPrice, fee);

                            bundleAPI.putString("payment_price",payment_price);

                            systemDataEditor.putString("payment_price", payment_price);
                        }

                        //Call LinePayRegisterAPI
                        if(payment_price!=null && payment_price!="0")
                        {
                            getLinePayRegister();
                        }
                        else
                        {
                            new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                                    .setTitle("金額エラー")
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }

                    }

                    @Override
                    public void onLoaderReset(Loader<DeliveryPriceData> loader) {

                    }
                });
    }

    private void getLinePayRegister()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<LinePayRegisterData>()
                {


                    @Override
                    public Loader<LinePayRegisterData> onCreateLoader(int id, Bundle args)
                    {

                        LinePayRegisterAPI linePayRegisterAPI = new LinePayRegisterAPI(context,bundleAPI);
                        linePayRegisterAPI.forceLoad();

                        return linePayRegisterAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<LinePayRegisterData> loader, LinePayRegisterData data) {


                        if(data.returnCode!=0)
                        {
                            new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                                    .setTitle("LINEPAY REGISTER ERROR")
                                    .setMessage("ERROR CODE: "+data.returnCode+"\n"+"ERROR MESSAGE: "+data.returnMessage)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                        else
                        {
                            //get LinePay Url form LinePayRegisterData
                            paymentUrlWeb = data.paymentUrlWeb;
                            paymentUrl = data.paymentUrl;
                            trans_no = data.transaction;
                            AppUtil.debugLog("static paymentUrlWeb", paymentUrlWeb);
                            AppUtil.debugLog("paymentUrl", paymentUrl);
                            AppUtil.debugLog("trans_no", trans_no);

                            bundleAPI.putString("trans_no", trans_no);

                            systemDataEditor.putString("trans_no", trans_no);
                            systemDataEditor.putString("paymentUrlWeb", paymentUrlWeb);
                            systemDataEditor.putString("paymentUrl", paymentUrl);
                            systemDataEditor.putString("order_id",order_id);

                            systemDataEditor.putString("img_url",img_url);
                            systemDataEditor.putString("item_url",item_url);
                            systemDataEditor.putString("item_id",item_id);
                            systemDataEditor.putString("item_price",item_price);
                            systemDataEditor.putString("item_title",item_title);
                            systemDataEditor.putString("text",text);
                            systemDataEditor.putString("item_stocks",item_stocks);
                            systemDataEditor.putString("order_amount", order_amount);
                            systemDataEditor.putString("kikaku_name",kikaku_name);

                            systemDataEditor.commit();

                            //Open Line Pay Web
                            Uri uri = Uri.parse(paymentUrlWeb);
                            Intent intentLinePay = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intentLinePay);
                        }





                    }

                    @Override
                    public void onLoaderReset(Loader<LinePayRegisterData> loader) {

                    }
                });
    }



    private void getOrderId()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<OrderIdData>()
                {


                    @Override
                    public Loader<OrderIdData> onCreateLoader(int id, Bundle args)
                    {

                        OrderIdAPI orderIdAPI = new OrderIdAPI(context);
                        orderIdAPI.forceLoad();


                        return orderIdAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<OrderIdData> loader, OrderIdData data)
                    {
                        if(data.order_id==null||data.order_id.equals("")||data.order_id.equals("null"))
                        {
                            new AlertDialog.Builder(LinePayProfileFragment.this.getActivity())
                                    .setTitle("ORDER_ID ERROR")
                                    .setMessage("ERROR CODE: "+data.error_code+"\n"+"ERROR MESSAGE: "+data.error_message)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                        else
                        {
                            order_id=data.order_id;
                            bundleAPI.putString("order_id",order_id);
                            AppUtil.debugLog("order_id Data", data.order_id);
                            AppUtil.debugLog("static order_id Data", order_id);

                            //call DeliveryPriceAPI
                            getDeliveryPrice();
                        }

                    }

                    @Override
                    public void onLoaderReset(Loader<OrderIdData> loader) {

                    }
                });
    }


    private void linePayRegistAppPayment()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<LinePayRegistAppPaymentData>()
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

    //calculate the payment pirce
    private String getPaymentPrice(String item_price, String delivery_price, String fee )
    {
        int paymentPrice;
        if(delivery_price!=null)
        {
             paymentPrice = Integer.parseInt(item_price)*Integer.parseInt(order_amount)+
                    Integer.parseInt(delivery_price)+
                    Integer.parseInt(fee);

            if(paymentPrice>=0)
            {
                AppUtil.debugLog("getPyamentPrice()>0",Integer.toString(paymentPrice));
                return Integer.toString(paymentPrice);

            }
            else
            {
                AppUtil.debugLog("getPyamentPrice()<0",Integer.toString(paymentPrice));
                return null;
            }
        }
        else
        {
            return null;

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        String buyingOver = systemData.getString("buying_over","");
        String fromWhatFragment = systemData.getString("from_what_fragment","");
        if(buyingOver.equals("true"))
        {
            if(fromWhatFragment.equals("ShoppingGoodsFragment"))
            {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                ShoppingFragment fragment = new ShoppingFragment();

                ft.replace(R.id.fragment, fragment);
                ft.commit();
            }
            else if(fromWhatFragment.equals("FlyerFragment"))
            {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                FlyerFragment fragment = new FlyerFragment();

                ft.replace(R.id.fragment, fragment);
                ft.commit();
            }

        }

    }



}
