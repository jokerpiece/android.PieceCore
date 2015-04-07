package jp.co.jokerpiece.piecebase.data;

public class QuestionInfoData {
	public int error_code;
	public String error_message;

	public String questionId;
	public String text;
	public String imgUrl1;
	public String imgUrl2;
    public String itemUrl;

	@Override
	public String toString() {
		String s = "NewsInfoData::\n" +
				   "error_code: " + error_code + "\n" +
				   "error_message: " + error_message + "\n" +
				   "questionId: " + questionId + "\n" +
				   "text: " + text + "\n" +
				   "imgUrl1: " + imgUrl1 + "\n" +
                   "imgUrl2: " + imgUrl2 + "\n" +
				   "itemUrl: " + itemUrl;
		return s.toString();
	}

}
