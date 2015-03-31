package jp.co.jokerpiece.piecebase.data;

import java.util.ArrayList;

public class ItemListData {
	public int error_code;
	public String error_message;
	
	public String category_img_url;
	public ArrayList<ItemData> data_list;
	public String quantity;
	public boolean more_flg;

	public class ItemData{
		public String item_id;
		public String img_url;
		public String item_title;
		public String text;
		public String price;
		public String item_url;
	}
}
