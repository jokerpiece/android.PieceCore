package jp.co.jokerpiece.piecebase.data;

import java.util.ArrayList;

public class CategoryListData {
	public int error_code;
	public String error_message;
	
	public ArrayList<CategoryData> data_list;

	public class CategoryData{
		//public int category_id;
		public String category_id;
        public String img_url;
		public String category_name;
		public String shop_category_url;
		public String category_text;
		public String category_price;
		public String shop_url;
	}
}
