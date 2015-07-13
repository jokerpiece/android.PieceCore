package jp.co.jokerpiece.piecebase.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class BitmapCache {
    private LruCache<String, Bitmap> mMemoryCache;
    public static BitmapCache bitmapCache;

    public static BitmapCache newInstance(){
        if(bitmapCache == null){
            bitmapCache = new BitmapCache();
        }
        return bitmapCache;
    }
    BitmapCache(){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 4;       // 最大メモリに依存した実装

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldBitmap, Bitmap newBitmap) {
                oldBitmap.recycle();
                oldBitmap = null;
            }
        };

    }

    //保存したビットマップを取得
    public Bitmap getBitmap(String url) {
        return mMemoryCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        synchronized (mMemoryCache) {
            if (mMemoryCache.get(url) == null) {
                mMemoryCache.put(url, bitmap);
            }
        }
//        Bitmap old = mMemoryCache.put(url,bitmap);
//        // オブジェクトの解放処理が必要なら以下のように実施
//        synchronized (mMemoryCache) {
//            if (old != null){
////                if(!old.isRecycled()){
////                    old.recycle();
////                }
//                old = null;
//            }
//        }
    }
}
