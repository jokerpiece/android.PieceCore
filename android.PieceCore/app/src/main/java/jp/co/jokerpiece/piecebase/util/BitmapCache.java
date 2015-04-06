package jp.co.jokerpiece.piecebase.util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
	 private LruCache<String, Bitmap> mMemoryCache;
    BitmapCache(){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;       // 最大メモリに依存した実装

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    //保存したビットマップを取得
    public Bitmap getBitmap(String url) {
        return mMemoryCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        Bitmap old = mMemoryCache.put(url,bitmap);
        // オブジェクトの解放処理が必要なら以下のように実施
        synchronized (mMemoryCache) {
            if (old != null){
//                if(!old.isRecycled()){
//                    old.recycle();
//                }
                old = null;
            }
        }
    }
}
