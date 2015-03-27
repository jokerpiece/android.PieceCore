package jp.co.jokerpiece.piecebase.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

public class BitmapDownloader extends AsyncTaskLoader<Bitmap> {
	private String bitmapURL = null;
	public BitmapDownloader(Context context,String url) {
		super(context);
		bitmapURL = url;
	}

	@Override
	public Bitmap loadInBackground() {
		if(bitmapURL == null){
			return null;
		}
		URL url;
		try {
			url = new URL(bitmapURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		InputStream istream;
		try {
			istream = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Bitmap bitmap= BitmapFactory.decodeStream(istream);
		return bitmap;
	}

}
