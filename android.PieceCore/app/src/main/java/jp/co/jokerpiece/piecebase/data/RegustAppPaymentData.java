package jp.co.jokerpiece.piecebase.data;

import java.math.BigDecimal;

public class RegustAppPaymentData {
    //アプリID
    public String app_id = null;
    // アプリキー
    public String app_key = null;
    // 注文番号
    public String order_no = null;
    // 商品ID
    public String product_id = null;
    //決済金額
    public String payment_price = null;
    // 取引番号
    public String trans_no = null;
    // 商品価格
    public String item_price = null;
    // 個数
    public Long amount = 0L;
    // 手数料
    public BigDecimal fee = BigDecimal.ZERO;
    // 決済サービス(1:line pay,2:PayPal)
    public String payment_kbn = "2";
    //ユーザID
    public String user_id = null;
    // メールアドレス,
    public String mail_address = null;
    //性,
    public String sei = null;
    // 名,
    public String mei = null;
    // 郵便番号
    public String post = null;
    // 住所(都道府県),
    public  String address_tdfk = null;
    //住所(市区町村),
    public String address_city = null;
    // 住所(番地),
    public String address_street = null;
    //     = 電話番号
    public String tel = null;

    //errorCode
    public int error_code ;

    //errorCode
    public String error_messsege ;

    @Override
    public String toString() {
        String s = "NewsInfoData::\n" +
                "app_id: " + app_id + "\n" +
                "app_key: " + app_key + "\n" +
                "order_no: " + order_no + "\n" +
                "product_id: " + product_id + "\n" +
                "payment_price: " + payment_price + "\n" +
                "trans_no: " + trans_no + "\n" +
                "item_price: " + item_price + "\n" +
                "amount: " + amount + "\n" +
                "fee: " + fee + "\n" +
                "payment_kbn: " + payment_kbn + "\n" +
                "user_id: " + user_id + "\n" +
                "mail_address: " + mail_address + "\n" +
                "sei: " + sei + "\n" +
                "mei: " + mei + "\n" +
                "post: " + post + "\n" +
                "post: " + address_tdfk + "\n" +
                "post: " + address_city + "\n" +
                "post: " + address_street + "\n" +
                "post: " + tel;
        return s.toString();
    }

}
