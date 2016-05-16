package jp.co.jokerpiece.piecebase.data;

import java.io.Serializable;
import java.util.ArrayList;

public class FlyerData {
	public int error_code;
	public String error_message;
	
	public ArrayList<FlyerHeaderData> header_list;
	public ArrayList<FlyerBodyData> body_list;

	public class FlyerHeaderData implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int key;
		public String img_url;
		public String category_id;
		public String item_url;
		public String item_id;
	}
	public class FlyerBodyData{
		public int key;
		public String category_id;
		public String img_url;
		public String item_url;
		public String item_id;
	}
}
