package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;

/**
 * Created by Sou on 16/3/25.
 * LinePayで購入する画面処理
 */

public class LinePayFragment extends Fragment {

    //various announcement created by Sou on 16/3/28.


    private TextView minusButton, stocksText, plusButton, goodsEmptyText, goodPriceText;
    private String orderAmount;
    private ArrayList<String> goodsAmountArrayList;
    private ArrayAdapter<String> adapterGoodsAmountList;
    private boolean goodExistCheck = true;
    private int currentState = 1;
    public boolean sameAsBeforeButton;
    public boolean newButton;
    public int currentSpinnerPosition = 0;

    //PayPalのSDKと連携するための設定項目
    private static final String CONFIG_ENVIRONMENT = Config.PAYPAL_ENVIRONMENT;

    private static final String CONFIG_CLIENT_ID = Config.PAYPAL_CLIENT_ID;

    private static final int REQUEST_CODE_PAYMENT = 1;

    //PayPalのSDKへのユーザー識別するための項目
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    Context context;

    //View
    View rootView;

    //PayPal決済用ボタン
    ImageView deciedBtn;

    //引継ぎ情報
    //商品画像のURL
    String img_url = null;
    //商品ID
    String item_id = null;
    //商品金額
    String item_price = null;
    //商品名
    String item_title = null;
    //商品説明
    String text = null;
    String item_url = null;
    //商品在庫数
    String item_stocks = null;

    //税
    //BigDecimal tax;
    //配送料

    //dataDetailAPI's parameter
    String data_detail_item_id; //規格id
    String data_detail_quantity; //規格個数
    ArrayList<HashMap<String, String>> data_detail_detail;//規格list
    String kikaku_name;//規格名称
    String data_detail_exist;//規格listの存在判定


    BigDecimal shopping;

    //表示用画像
    DownloadImageView goodsView;

    /*
    * 画面の表示処理
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        context = getActivity();

        Common.setCurrentFragment(Config.PaypalFragmentNum);

        //Paypal購入画面を取得
        rootView = inflater.inflate(R.layout.fragment_linepayitemintro, container, false);

        deciedBtn = (ImageView)rootView.findViewById(R.id.buyItBtn);
        goodPriceText = (TextView)rootView.findViewById(R.id.goodPrice);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String shoppingtax = null;
        String totalprice = null;


        TextView vi = (TextView) rootView.findViewById(R.id.priceTotal);
        //vi.setText(totalprice);
        TextView vi2 = (TextView) rootView.findViewById(R.id.shoppingTax);
        //vi2.setText(shoppingtax);
        TextView vi3 = (TextView) rootView.findViewById(R.id.itemname);
        vi3.setText(item_title);
        TextView vi4 = (TextView) rootView.findViewById(R.id.goodsText);
        minusButton = (TextView) rootView.findViewById(R.id.minusBtn);
        stocksText = (TextView) rootView.findViewById(R.id.stocksText);
        plusButton = (TextView) rootView.findViewById(R.id.plusBtn);
        goodsEmptyText = (TextView) rootView.findViewById(R.id.goodsEmpty);
        goodsView = (DownloadImageView) rootView.findViewById(R.id.goodsView);


        //引継ぎ情報取得
        Bundle bundle = getArguments();

        if (bundle != null)
        {
            img_url = bundle.getString("img_url");
            item_id = bundle.getString("item_id");
            item_url = bundle.getString("item_url");
            item_price = bundle.getString("price");
            item_title = bundle.getString("item_title");
            text = bundle.getString("text");
            item_stocks = bundle.getString("stocks");
            data_detail_exist = bundle.getString("data_detail_exist");
            //表示画像取得
            if (!goodsView.setImageURL(img_url)) {
                ((FragmentActivity) context).getSupportLoaderManager().initLoader(Config.loaderCnt++, null, goodsView);
            }

            //IF there are datas from the response of ItemDetailAPI, set datas to the screen
            if(bundle.getString("data_exist").equals("true"))
            {
                data_detail_item_id = bundle.getString("data_detail_item_id");
                data_detail_quantity = bundle.getString("data_detail_quantity");

                //If data_detail has value enable the setting of good's type
                if(bundle.getString("data_detail_exist").equals("true"))
                {
                    data_detail_detail = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data_detail_detail");

                    //本文Test
                    vi4.setText(text);

                    for(int i=0; i<data_detail_detail.size(); i++)
                    {
                        adapter.add(data_detail_detail.get(i).get("kikaku_name"));

                    }
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {

                            currentSpinnerPosition = position;
                            //If amount is "null", set item_stock = null
                            if(data_detail_detail.get(position).get("amount").equals("null"))
                            {
                                item_stocks = null;
                                kikaku_name = data_detail_detail.get(position).get("kikaku_name");
                            }
                            else//set item_stock = amount
                            {
                                item_stocks = data_detail_detail.get(position).get("amount");
                                kikaku_name = data_detail_detail.get(position).get("kikaku_name");

                            }

                            item_price = data_detail_detail.get(position).get("price");
                            goodPriceText.setText(item_price + "円");
                            stockControl();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0)
                        {

                            if(data_detail_detail.get(currentSpinnerPosition).get("amount").equals("null"))
                            {
                                item_stocks = null;
                                kikaku_name = data_detail_detail.get(currentSpinnerPosition).get("kikaku_name");
                            }
                            else
                            {
                                item_stocks = data_detail_detail.get(currentSpinnerPosition).get("amount");
                                kikaku_name = data_detail_detail.get(currentSpinnerPosition).get("kikaku_name");
                            }
                            item_price = data_detail_detail.get(currentSpinnerPosition).get("price");
                            goodPriceText.setText(item_price + "円");
                            stockControl();
                        }
                    });


                    rootView.findViewById(R.id.layout_good_type).setVisibility(View.VISIBLE);

                }
                else//disable the setting of good's type
                {
                    data_detail_detail = null;
                    //本文Test
                    vi4.setText(text);
                    goodPriceText.setText(item_price+"円");
                    stockControl();

                }

            }
            else//set datas without good types.
            {
                data_detail_item_id = null;
                data_detail_quantity = null;

                data_detail_detail = null;
                //本文Test
                vi4.setText(text);
                goodPriceText.setText(item_price+"円");
                stockControl();
            }



            //消費税
            //tax = new BigDecimal((new BigDecimal(Integer.valueOf(item_price) * 0.08).longValue()));
            //配送手数料
            shopping = new BigDecimal(Config.DELIVERY);
            shoppingtax = "(内訳 本体価格:" + item_price + "円" + /*税:" + tax.toString() + "円"*/ " 配送料：" + shopping + "円)";
            totalprice = "合計金額：" + new BigDecimal(item_price)/*.add(tax)*/.add(shopping).toString() + "円";



        }


        //LinePay購入用ボタンの処理追加
        deciedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Check if sharekeyperference is exist
                File f = new File(
                        "/data/data/"+Config.PACKAGE_NAME+"/shared_prefs/PersonalDataSave.xml");
                if(f.exists()) //go to LinePaySelectDeliveryFragment
                {

                    //get OrderAmount
                    orderAmount = Integer.toString(currentState);

                    //send item Data to LinePaySelectDeliveryFragment
                    Bundle bundle = new Bundle();
                    bundle.putString("img_url", img_url);
                    bundle.putString("item_url", item_url);
                    bundle.putString("item_id", item_id);
                    bundle.putString("item_price", item_price);
                    bundle.putString("item_title", item_title);
                    bundle.putString("text", text);
                    bundle.putString("item_stocks", item_stocks);
                    bundle.putString("order_amount", orderAmount);
                    if(data_detail_exist.equals("true"))
                    {
                        bundle.putString("kikaku_name",kikaku_name);
                    }
                    else
                    {
                        bundle.putString("kikaku_name","");
                    }

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);

                    LinePaySelectDeliveryFragment fragment = new LinePaySelectDeliveryFragment();

                    //send item Data to LinePaySelectDeliveryFragment
                    fragment.setArguments(bundle);

                    ft.replace(R.id.fragment, fragment);
                    ft.commit();
                }
                else//go to LinePayProfileFragment
                {
                    //get OrderAmount
                    orderAmount = Integer.toString(currentState);


                    sameAsBeforeButton=false;
                    newButton=true;
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    LinePayProfileFragment fragment = new LinePayProfileFragment();

                    //send value to LinePayProfileFragment
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("SameAsBeforeButton", sameAsBeforeButton);
                    bundle.putBoolean("NewButton", newButton);

                    //send item Data to LinePayProfileFragment
                    bundle.putString("img_url",img_url);
                    bundle.putString("item_url",item_url);
                    bundle.putString("item_id",item_id);
                    bundle.putString("item_price",item_price);
                    bundle.putString("item_title",item_title);
                    bundle.putString("text",text);
                    bundle.putString("item_stocks",item_stocks);
                    bundle.putString("order_amount", orderAmount);
                    if(data_detail_exist.equals("true"))
                    {
                        bundle.putString("kikaku_name",kikaku_name);
                    }
                    else
                    {
                        bundle.putString("kikaku_name","");
                    }

                    fragment.setArguments(bundle);

                    ft.replace(R.id.fragment, fragment);
                    ft.commit();

                }
            }
        });

        return rootView;
    }

    private void stockControl()
    {
        currentState=1;//reset currentState
        //在庫数の判定
        if(item_stocks!=null) //item_stocks!=null
        {
            if(!item_stocks.equals("売り切れ"))//item_stocks!=売り切れ
            {
                if(item_stocks.equals(""))//if item_stocks is empty
                {
                    item_stocks = "10";
                    stocksText.setText("1");
                    minusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (1 < currentState && currentState <= Integer.parseInt(item_stocks)) {
                                currentState--;
                                stocksText.setText(Integer.toString(currentState));

                            } else if (currentState == Integer.parseInt(item_stocks)) {

                                currentState = Integer.parseInt(item_stocks);

                            } else if (currentState < 1) {
                                currentState = 1;
                            }

                        }
                    });

                    plusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (0 < currentState && currentState < Integer.parseInt(item_stocks)) {
                                currentState++;
                                stocksText.setText(Integer.toString(currentState));
                            } else if (currentState == Integer.parseInt(item_stocks)) {
                                currentState = Integer.parseInt(item_stocks);
                                new AlertDialog.Builder(LinePayFragment.this.getActivity())
                                        .setMessage("注文数が上限です。\nこれ以上注文できません。")
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();

                            } else if (currentState > Integer.parseInt(item_stocks)) {
                                currentState = Integer.parseInt(item_stocks);
                            }

                        }
                    });

                    //set view
                    goodsEmptyText.setVisibility(View.GONE);
                    deciedBtn.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.goodsAmountChooseLayer).setVisibility(View.VISIBLE);
                }
                else// if item_stock exist
                {
                    if(Integer.parseInt(item_stocks)<=0)//売り切れ
                    {
                        rootView.findViewById(R.id.goodsAmountChooseLayer).setVisibility(View.GONE);
                        deciedBtn.setVisibility(View.GONE);
                        goodsEmptyText.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        AppUtil.debugLog("item_stock_if", item_stocks);

                        stocksText.setText("1");
                        minusButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (1 < currentState && currentState <= Integer.parseInt(item_stocks)) {
                                    currentState--;
                                    stocksText.setText(Integer.toString(currentState));

                                } else if (currentState == Integer.parseInt(item_stocks)) {

                                    currentState = Integer.parseInt(item_stocks);

                                } else if (currentState < 1) {
                                    currentState = 1;
                                }

                            }
                        });

                        plusButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (0 < currentState && currentState < Integer.parseInt(item_stocks)) {
                                    currentState++;
                                    stocksText.setText(Integer.toString(currentState));
                                } else if (currentState == Integer.parseInt(item_stocks)) {
                                    currentState = Integer.parseInt(item_stocks);
                                    new AlertDialog.Builder(LinePayFragment.this.getActivity())
                                            .setMessage("注文数が上限です。\nこれ以上注文できません。")
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();

                                } else if (currentState > Integer.parseInt(item_stocks)) {
                                    currentState = Integer.parseInt(item_stocks);
                                }

                            }
                        });

                        //set view
                        goodsEmptyText.setVisibility(View.GONE);
                        deciedBtn.setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.goodsAmountChooseLayer).setVisibility(View.VISIBLE);
                    }

                }


            } else //売り切れ
            {
                rootView.findViewById(R.id.goodsAmountChooseLayer).setVisibility(View.GONE);
                deciedBtn.setVisibility(View.GONE);
                goodsEmptyText.setVisibility(View.VISIBLE);
            }


        }
        else //item_stock = null (goods are set by 10)
        {
            item_stocks = "10";
            stocksText.setText("1");
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (1 < currentState && currentState <= Integer.parseInt(item_stocks)) {
                        currentState--;
                        stocksText.setText(Integer.toString(currentState));

                    } else if (currentState == Integer.parseInt(item_stocks)) {

                        currentState = Integer.parseInt(item_stocks);

                    } else if (currentState < 1) {
                        currentState = 1;
                    }

                }
            });

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (0 < currentState && currentState < Integer.parseInt(item_stocks)) {
                        currentState++;
                        stocksText.setText(Integer.toString(currentState));
                    } else if (currentState == Integer.parseInt(item_stocks)) {
                        currentState = Integer.parseInt(item_stocks);
                        new AlertDialog.Builder(LinePayFragment.this.getActivity())
                                .setMessage("注文数が上限です。\nこれ以上注文できません。")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                    } else if (currentState > Integer.parseInt(item_stocks)) {
                        currentState = Integer.parseInt(item_stocks);
                    }

                }
            });

            //set view
            goodsEmptyText.setVisibility(View.GONE);
            deciedBtn.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.goodsAmountChooseLayer).setVisibility(View.VISIBLE);


        }
    }



//    /*
//    *Paypal Rest APIの実装
//     */
//    private void getPayPalRest() {
//
//        ((Activity) context).getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderCallbacks<PaypalData>() {
//            @Override
//            public Loader<PaypalData> onCreateLoader(int id, Bundle args) {
//                PayPalAPI payPalAPI = new PayPalAPI(context);
//                payPalAPI.forceLoad();
//                return payPalAPI;
//            }
//
//            @Override
//            public void onLoadFinished(Loader<PaypalData> loader, PaypalData data) {
//                cdl.countDown();
//            }
//
//            @Override
//            public void onLoaderReset(Loader<PaypalData> loader) {
//
//            }
//        });
//    }

    /*
    *  PayPalの画面呼び出し
     */
    public void onBuyPaypal(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        thingToBuy.enablePayPalShippingAddressesRetrieval(true);
        //bnCdode設定
        thingToBuy.bnCode(Config.PAYPAL_BNCODE);

        Intent intent = new Intent(context, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /*
    * 消費税、配送料、商品が複数ある場合の実装
    * This method shows use of optional payment details and item list.
    */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem(item_title, 1, new BigDecimal(Integer.valueOf(item_price).intValue()), "JPY",
                                item_id + ":" + item_title)
                };
        //複数商品あった場合の集計処理。
        BigDecimal subtotal = PayPalItem.getItemTotal(items);

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shopping, subtotal, /*tax*/null);

        BigDecimal amount = subtotal.add(shopping)/*.add(tax)*/;
        Long amount2 = amount.longValue();
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount2), "JPY", item_id + ":" + item_title, paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        return payment;
    }

    /*
    *PayPal SDKを使用して、決済完了後に呼ばれる処理
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));
                    Log.i("Payment", confirm.getPayment().toJSONObject().toString(4));
                    Log.i("Environment", confirm.getEnvironment());
                    Log.i("ProofOfPayment", confirm.getProofOfPayment().toJSONObject().toString());

                    //購入画面に遷移する前に取得したPayPalから送られてきた情報を元にPieceのDBに登録をするようにする。
//                    //-------------------------------------------------------------------------------
//                    RegustAppPaymentData d = new RegustAppPaymentData();
//                    d.app_id = Config.APP_ID;
//                    d.app_key = Config.APP_KEY;
//                    d.order_no = confirm.toJSONObject().getJSONObject("response").getString("id");
//                    d.product_id = item_id;
//                    d.payment_price = item_price;
//                    d.item_price =  item_price;
//                    d.amount = 1L;
//                    d.user_id = null;
//                    d.mail_address = null;
//                    d.sei = null;
//                    d.mei = null;
//                    d.post = null;
//                    d.address_tdfk = null;
//                    d.address_city = null;
//                    d.address_street = null;
//                    d.tel = null;
                    //-------------------------------------------------------------------------------
                    //購入完了画面に遷移する
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ResoultFragment fragment = new ResoultFragment();

                    ft.replace(R.id.fragment, fragment);
                    ft.commit();
                } catch (JSONException e) {
                    Log.e("paymentExample", "非常にまれなエラーが発生しました: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "ユーザーがキャンセルしました。");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "無効な支払いまたはPayPalConfigurationが送信されました。ドキュメントを参照してください。");
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        context.stopService(new Intent(context, PayPalService.class));
        super.onDestroy();
    }

}



