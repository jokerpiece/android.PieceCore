package jp.co.jokerpiece.piecebase.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import jp.co.jokerpiece.piecebase.InfomationSyosaiFragment;


public class DownloadImageSync implements LoaderCallbacks<Bitmap>{
    //static BitmapCache bmc = new BitmapCache();
    Context context;
    private String imageURL;
    private String tag;
    private ImageView imageView;
    int position = -1;
    DownloadImageSyncCallback callback;

    public DownloadImageSync(Context context,String imageURL,ImageView imageview,DownloadImageSyncCallback callback) {
        this.context = context;
        this.imageURL = imageURL;
        this.imageView = imageview;
        if(this.imageView != null && this.imageView.getTag() != null){
            tag = this.imageView.getTag().toString();
        }
        this.callback = callback;
    }



    public boolean loadImageView(){
        if(this.imageURL == null){
            return false;
        }
        Bitmap data = BitmapCache.newInstance().getBitmap(this.imageURL);
        if(data != null){
            if(tag == null){
                if(imageView != null){
                    imageView.setImageBitmap(data);
                }
            }else{
                //Log.d("TAG", imageView.getTag().toString()+":"+tag);
                callback.setImageCallbackWithURL(data, tag);
                if(imageView.getTag().toString().equals(tag)){
                    imageView.setImageBitmap(data);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        BitmapDownloader bmDownloader = new BitmapDownloader(context,imageURL);
        bmDownloader.forceLoad();
        return bmDownloader;
    }
    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
        if(data != null){
            if(tag == null){
                if(imageView != null){
                    imageView.setImageBitmap(data);
                }
                BitmapCache.newInstance().putBitmap(imageURL, data);
            }else{
                //Log.d("TAG", imageView.getTag().toString() + ":" + tag);
                callback.setImageCallbackWithURL(data, tag);
                if(imageView.getTag().toString().equals(tag)){
                    imageView.setImageBitmap(data);
                    imageView.setVisibility(View.VISIBLE);
                }
                BitmapCache.newInstance().putBitmap(tag, data);

            }
        }
    }
    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {

    }

    public interface DownloadImageSyncCallback{
        public void setImageCallbackWithURL(Bitmap bitmap,String url);
    }

}
