package jp.co.jokerpiece.piecebase.data;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsListData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int NEWS_DATE_TYPE_ALL = 0;
	public static final int NEWS_DATA_TYPE_INFOMATION = 1;
	public static final int NEWS_DATA_TYPE_FLYER = 2;
	public static final int NEWS_DATA_TYPE_COUPON = 3;
	public int error_code;
	public String error_message;

	public ArrayList<NewsData> data_list;

	public class NewsData implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int key;
		public String news_id;
		public String title;
		public String text;
		public String type;
		public String id;
	}
}
