package jp.co.jokerpiece.piecebase.data;


public class NewsInfoData {
	public int error_code;
	public String error_message;

	public String type;
	public String newsId;
	public String title;
	public String text;

	@Override
	public String toString() {
		String s = "NewsInfoData::\n" +
				   "error_code: " + error_code + "\n" +
				   "error_message: " + error_message + "\n" +
				   "type: " + type + "\n" +
				   "newsId: " + newsId + "\n" +
				   "title: " + title + "\n" +
				   "text: " + text;
		return s.toString();
	}

}
