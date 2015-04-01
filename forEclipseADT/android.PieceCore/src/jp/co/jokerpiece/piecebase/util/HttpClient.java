package jp.co.jokerpiece.piecebase.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import jp.co.jokerpiece.piecebase.config.Config;
import android.util.Log;

public class HttpClient {

	// HTTP Method
	public static final String GET = "GET";
	public static final String POST = "POST";

	// android routing
	public static final String GateWay = "10.0.2.1"; // ルータ/ゲートウェイアドレス
	public static final String EMURATER_PC = "10.0.2.2"; // エミュレータが起動中のPC
	public static final String EMURATER_INTERFACE = "10.0.2.7"; // エミュレータのinterface
	public static final String DNS_PRIMARY = "10.0.2.3"; // プライマリDNS
	public static final String DNS_SECOND = "10.0.2.4"; // セカンドDNS
	public static final String DNS_THIRD = "10.0.2.5"; // サードDNS
	public static final String LOOP_BACK = "127.0.0.1"; // エミュレータのループバック

//	public static final String LOCAL_PC = "192.168.77.69";
	public static final String LOCAL_PC = "192.168.11.10";

	public static final String TAG_SUCCESS = "HTTP ACCESS SUCCESSFUL";
	public static final String TAG_FAILURE = "HTTP ACCESS FAILURE";

	private static HttpClientInterface HCInterFace;
	/**
	 * GETメソッドを発行する
	 * メソッドパラメータとか無視してそのままURLを踏む仕様
	 * */
	public static byte[] getByteArrayFromURL(String strUrl,HttpClientInterface receiver) {
		HCInterFace = receiver;
		byte[] byteArray = new byte[1024];
		byte[] result = null;
		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		int size = 0;
		try {
			URL url = new URL(strUrl);
			final String username = Config.USER_NAME;
			final String password = Config.PASS_WORD;
			if(username != null && password != null){
				Authenticator.setDefault(new Authenticator() {
					@Override
			        protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password.toCharArray());
			        }
				});
			}
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(Config.connectionTimeOut);
			con.setReadTimeout(Config.readTimeOut);
			con.setRequestMethod("GET");
			con.connect();
			in = con.getInputStream();

			out = new ByteArrayOutputStream();
			long total = 0;
			int fileLength = con.getContentLength();
			while ((size = in.read(byteArray)) != -1) {
				total += size;
				out.write(byteArray, 0, size);
				if(HCInterFace != null){
					HCInterFace.HttpClientProgress((float)total/(float)fileLength);
				}
				Log.d("DOWNLOAD", total+"/"+fileLength);
			}
			result = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * HTTPでPOSTパラメータを発行するメソッド
	 * @author kimura_kouki
	 * @since 2011/11/20
	 * */
	public static byte[] getByteArrayFromUrlPost(String strUrl, HashMap<String, String> parameter,HttpClientInterface receiver)
			throws MalformedURLException {
		if(parameter == null){
			return getByteArrayFromURL(strUrl, receiver);
		}
		HCInterFace = receiver;

		byte[] byteArray = new byte[1024];
		byte[] result = null;
		int size = 0;

		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			URL url = new URL(strUrl);
			final String username = Config.USER_NAME;
			final String password = Config.PASS_WORD;
			if(username != null && password != null){
				Authenticator.setDefault(new Authenticator() {
					@Override
			        protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password.toCharArray());
			        }
				});
			}
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(Config.connectionTimeOut);
			con.setReadTimeout(Config.readTimeOut);

			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "ja");
			con.setDoOutput(true);

			// Post値の設定
			String postdata = parseRequestData(parameter, POST);
			String parameterString = new String(postdata);

			PrintWriter printWriter = new PrintWriter(con.getOutputStream());
			printWriter.print(parameterString);
			printWriter.close();

			con.connect();
			in = con.getInputStream();

			out = new ByteArrayOutputStream();
			long total = 0;
			int fileLength = con.getContentLength();
			while ((size = in.read(byteArray)) != -1) {
				total += size;
				out.write(byteArray, 0, size);
				if(HCInterFace != null){
					HCInterFace.HttpClientProgress((float)total/(float)fileLength);
				}
				Log.d("DOWNLOAD", total+"/"+fileLength);
			}

			result = out.toByteArray();
			Log.v(TAG_SUCCESS, strUrl);

		} catch (Exception e) {
			e.printStackTrace();
			Log.v(TAG_FAILURE, strUrl);
		} finally {
			try {
				if (con != null)
					con.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();

			} catch (IOException ignore) {
			}
		}

		return result;
	}

	/**
	 * HashMapからリクエストパラメータの形式にParseする
	 * @author kimura_kouki
	 * @since 2011/11/20
	 * */
	private static String parseRequestData(HashMap<String, String> request,
			String method) {

		ArrayList<String> ele = new ArrayList<String>();
		Set<String> keys = null;

		// HashMapからごっそりKeyValueを回収する。
		if (!request.isEmpty()) {
			keys = request.keySet();
		}

		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = request.get(key);
			ele.add(key + "=" + value);
		}

		// KeyValueを接続する
		StringBuffer data = new StringBuffer();
		for (Iterator<String> iterator = ele.iterator(); iterator.hasNext();) {
			String kv = (String) iterator.next();
			data.append(kv);

			if (iterator.hasNext()) {
				data.append("&");
			}
		}

		// 形式を合わせる
		if (method.equals(GET)) {
			data.insert(0, "?");
		}
		if (method.equals(POST)) {
		}

		return data.toString();
	}
	public interface HttpClientInterface {
		public void HttpClientProgress(float progress);
	}
}