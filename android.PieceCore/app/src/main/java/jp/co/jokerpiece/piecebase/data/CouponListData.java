package jp.co.jokerpiece.piecebase.data;

import java.util.ArrayList;

public class CouponListData {
	public static final int COUPON_DATA_TYPE_NOT_GIVE = 1;
	public static final int COUPON_DATA_TYPE_GIVEN = 2;
	public int error_code;
	public String error_message;
	public ArrayList<CouponData> data_list;

	public class CouponData{
		public String coupon_code;
		public String img_url;
		public String coupon_title;
		public String coupon_text;
		public String coupon_id;
		public String item_url;
		public String category_id;
        public String coupon_url;
	}
}
