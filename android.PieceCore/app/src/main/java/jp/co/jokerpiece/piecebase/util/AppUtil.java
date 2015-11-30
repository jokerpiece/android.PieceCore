package jp.co.jokerpiece.piecebase.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import jp.co.jokerpiece.piecebase.BuildConfig;
import jp.co.jokerpiece.piecebase.MainBaseActivity;
import jp.co.jokerpiece.piecebase.MainBaseActivity.TabInfo;
import jp.co.jokerpiece.piecebase.config.Config;

public class AppUtil {
    /**
     * デバッグログ出力
     *
     * @param tag
     * @param message
     */
    public static void debugLog(String tag, String message) {
        if (BuildConfig.DEBUG && tag != null && message != null) {
            AppUtil.debugLog(tag, message);
        }
    }

	public static void setTitleOfActionBar(ActionBar actionBar, int resId) {
		actionBar.setTitle(resId);
	}

	public static int getPosition(String clsName) {
		int ret = -1;
		if (MainBaseActivity.tabInfoList != null) {
			for (int i = 0; i < MainBaseActivity.tabInfoList.size(); i++) {
				TabInfo tabInfo = MainBaseActivity.tabInfoList.get(i);
				if (tabInfo.cls.getSimpleName().contains(clsName)) {
					ret = i;
					break;
				}
			}
		}
		return ret;
	}

    /**
     * ARROWS携帯でImageViewのadjustViewBoundsが効かないため
     * 明示的にImageViewのサイズを指定してから画像をセットする。
     * @param bitmap
     * @param imageView
     */
    public static void setImageBitmap(Bitmap bitmap, ImageView imageView){
        // ウィンドウマネージャのインスタンス取得
        WindowManager wm = (WindowManager)App.getContext().getSystemService(Context.WINDOW_SERVICE);
        // ディスプレイのインスタンス生成
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        int imageWidth = size.x;
        float i = ((float)imageWidth)/((float)bitmap.getWidth());
        float imageHeight = i * (bitmap.getHeight());
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width =imageWidth;
        lp.height = (int) imageHeight;
        imageView.requestLayout();
        imageView.setImageBitmap(bitmap);
    }

    /**
     * dpサイズからピクセルを返す。
     * @param context コンテキスト
     * @param dp dpサイズ
     * @return ピクセル
     */
    public static int getPixelFromDp(Context context, int dp) {
        int pixel;
        float scale = context.getResources().getDisplayMetrics().density;
        pixel = (int) (dp * scale + 0.5f);
        return pixel;
    }


    public static String getPrefString(Context context, String key, String initValue) {
        SharedPreferences pref = context.getSharedPreferences(Config.PREF_KEY, Activity.MODE_PRIVATE | Activity.MODE_MULTI_PROCESS);
        String value = pref.getString(key, initValue);
        return value;
    }

    /**
     * 指定キーとバリューをプリファレンスに設定する。(valueはString)
     * @param context コンテキスト
     * @param key キー
     * @param value バリュー
     */
    public static void setPrefString(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(Config.PREF_KEY, Activity.MODE_PRIVATE | Activity.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = pref.edit();
//        AppUtil.debugLog(TAG, "**********");
//        AppUtil.debugLog(TAG, "プリファレンスに設定します。");
//        AppUtil.debugLog(TAG, "(key, value) = (" + key + ", " + value + ")");
//        AppUtil.debugLog(TAG, "**********");
        editor.putString(key, value);
        editor.commit();
    }
}
