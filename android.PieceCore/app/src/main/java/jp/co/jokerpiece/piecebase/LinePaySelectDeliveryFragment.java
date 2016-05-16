package jp.co.jokerpiece.piecebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;

/**
 * Created by Sou on 16/3/30.
 */
public class LinePaySelectDeliveryFragment extends Fragment
{
    View rootView;
    Context context;
    ImageView selectAdressBtn;
    ImageView changeAdressBtn;
    TextView title;
    TextView deliveryAddress;
    public boolean sameAsBeforeButton;
    public boolean newButton;

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
    private String order_amount = null;
    //
    private String kikaku_name = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        //get data from LinePayFragment
        Bundle bundle = getArguments();
        img_url = bundle.getString("img_url");
        item_id = bundle.getString("item_id");
        item_price = bundle.getString("item_price");
        item_title = bundle.getString("item_title");
        text = bundle.getString("text");
        item_url=bundle.getString("item_url");
        item_stocks=bundle.getString("item_stocks");
        order_amount = bundle.getString("order_amount");
        kikaku_name = bundle.getString("kikaku_name");

        context = getActivity();

        Common.setCurrentFragment(Config.PaypalFragmentNum);

        //LinePay購入画面を取得
        rootView = inflater.inflate(R.layout.fragment_linepay_selectdeliveryaddress, container, false);

        sameAsBeforeButton = false;
        newButton = false;

        //Layoutの設定
        setComponent();

        return rootView;
    }

    //Layoutの設定
    public void setComponent()
    {
        title = (TextView)rootView.findViewById(R.id.deliveryTitle);
        deliveryAddress = (TextView)rootView.findViewById(R.id.DeliveryAddress);
        selectAdressBtn = (ImageView)rootView.findViewById(R.id.sameDeliveryAddress);
        changeAdressBtn = (ImageView)rootView.findViewById(R.id.changeDeliveryAddress);

        title.setText("お届け情報を選択して下さい。");


        //get saving data by SharedPreference
        SharedPreferences data = getActivity().getSharedPreferences("PersonalDataSave", Context.MODE_PRIVATE);
        String sei = data.getString("Sei", "");
        String mei = data.getString("Mei", "");
        String post = data.getString("Post", "");
        String address_tdfk = data.getString("Address_tdfk", "");
        String address_city = data.getString("Address_city", "");
        String address_street = data.getString("Address_street", "");
        String tel = data.getString("Tel", "");
        String mail_address = data.getString("Mail_address", "");


        deliveryAddress.setText(sei+" "+mei+"\n"+post+"\n"+address_tdfk+address_city+address_street+"\n"
                +tel+"\n"+mail_address);

        //”以前と同じ配送先に送る”ボタンの処理
        selectAdressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newButton = false;
                sameAsBeforeButton = true;

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
                bundle.putString("order_amount", order_amount);
                bundle.putString("kikaku_name",kikaku_name);

                fragment.setArguments(bundle);

                ft.replace(R.id.fragment, fragment);
                ft.commit();

            }
        });

        //”配送先を変更する”ボタンの処理
        changeAdressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newButton = true;
                sameAsBeforeButton = false;

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                LinePayProfileFragment fragment = new LinePayProfileFragment();

                //send value to LinePayProfileFragment
                Bundle bundle = new Bundle();
                bundle.putBoolean("SameAsBeforeButton", sameAsBeforeButton);
                bundle.putBoolean("NewButton", newButton);

                bundle.putString("img_url",img_url);
                bundle.putString("item_url",item_url);
                bundle.putString("item_id",item_id);
                bundle.putString("item_price",item_price);
                bundle.putString("item_title",item_title);
                bundle.putString("text",text);
                bundle.putString("item_stocks",item_stocks);
                bundle.putString("order_amount", order_amount);
                bundle.putString("kikaku_name",kikaku_name);

                fragment.setArguments(bundle);

                ft.replace(R.id.fragment, fragment);
                ft.commit();
            }
        });

    }
}


