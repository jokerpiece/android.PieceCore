package jp.co.jokerpiece.piecebase.data;

import java.util.ArrayList;

/**
 * Created by kaku on 2015/04/14.
 */
public class ShopListData {
    public int error_code;
    public String error_message;

  //  public String category_img_url;
    public ArrayList<ShopData> data_list;
    public boolean more_flg;

    public class ShopData{
        public String shop_id;
        public String shop_name;
        public String longitude;
        public String latitude;
        public String address;
    }
}
