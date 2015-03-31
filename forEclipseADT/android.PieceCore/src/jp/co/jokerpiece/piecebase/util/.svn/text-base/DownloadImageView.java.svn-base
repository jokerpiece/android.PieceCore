package jp.co.jokerpiece.piecebase.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.widget.ImageView;



public class DownloadImageView extends ImageView implements LoaderCallbacks<Bitmap>{
	static BitmapCache bmc = new BitmapCache();
	private String imageURL;
    private String tag;

	int position = -1;
	public DownloadImageView(Context context) {
		super(context);
	}
	public DownloadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public DownloadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public boolean setImageURL(String imageURL) {
		this.imageURL = imageURL;
		if(this.getTag() != null){
			this.tag = this.getTag().toString();
		}
		return loadImageView();
	}
	
	public boolean loadImageView(){
		if(this.imageURL == null){
			return false;
		}
		Bitmap data = bmc.getBitmap(this.imageURL);
		if(data != null){
			if(tag == null || this.getTag().toString().equals(tag)){
				super.setImageBitmap(data);
			}
			return true;
		}
		return false;
	}
	@Override
	public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
		BitmapDownloader bmDownloader = new BitmapDownloader(getContext(),imageURL);
		bmDownloader.forceLoad();
		return bmDownloader;
	}
	@Override
	public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
		if(data != null){
			if(tag == null || this.getTag().toString().equals(tag)){
				super.setImageBitmap(data);
				bmc.putBitmap(imageURL, data);
			}
		}
	}
	@Override
	public void onLoaderReset(Loader<Bitmap> loader) {
		
	}	

}
