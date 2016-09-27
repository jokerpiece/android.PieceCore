package jp.co.jokerpiece.piecebase;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.util.App;
import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;

/**
 * Created by Sou on 16/3/25.
 * LinePayで購入する画面処理
 */

public class LinePayFragment extends Fragment {

    //various announcement created by Sou on 16/3/28.

    private TextView vi3;
    private TextView vi4;

    private TextView minusButton, stocksText, plusButton, goodsEmptyText, goodPriceText;
    private String orderAmount;

    private int currentState = 1;
    public boolean sameAsBeforeButton;
    public boolean newButton;
    public int currentSpinnerPosition = 0;

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
    String item_stocks_empty="";
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

        if(Config.ANALYTICS_MODE.equals("true")){
            App app = (App)getActivity().getApplication();
            Tracker t = app.getTracker(App.TrackerName.APP_TRACKER);
            t.setScreenName("LINE PAY");
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }

        context = getActivity();

        Common.setCurrentFragment(Config.PaypalFragmentNum);

        //linePay購入画面を取得
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
        vi3 = (TextView) rootView.findViewById(R.id.itemname);
        vi4 = (TextView) rootView.findViewById(R.id.goodsText);
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

            if(item_stocks!=null)
            {
                if(item_stocks.equals("売り切れ"))
                {
                    item_stocks_empty="売り切れ";
                }
            }

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


                    //商品名
                    vi3.setText(item_title);
                    //本文Test
                    vi4.setText(text);
                    //kikaku name's null check
                    int spinnerCount = 0;

                    for(int i=0; i<data_detail_detail.size(); i++)
                    {
                        if(!data_detail_detail.get(i).get("kikaku_name").equals(""))
                        {
                            adapter.add(data_detail_detail.get(i).get("kikaku_name"));
                            spinnerCount++;
                        }
                    }
                    //If there is any good including kikaku name
                    if(spinnerCount!=0)
                    {
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
                    else
                    {
                        data_detail_detail = null;
                        //商品名
                        vi3.setText(item_title);
                        //本文Test
                        vi4.setText(text);
                        goodPriceText.setText(item_price+"円");
                        stockControl();
                    }


                }
                else//disable the setting of good's type
                {
                    data_detail_detail = null;
                    //商品名
                    vi3.setText(item_title);
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

            //配送手数料
            shopping = new BigDecimal(Config.DELIVERY);



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

        if(item_stocks_empty.equals("売り切れ"))
        {
            rootView.findViewById(R.id.goodsAmountChooseLayer).setVisibility(View.GONE);
            deciedBtn.setVisibility(View.GONE);
            goodsEmptyText.setVisibility(View.VISIBLE);
        }
        else
        {
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

    }

}



