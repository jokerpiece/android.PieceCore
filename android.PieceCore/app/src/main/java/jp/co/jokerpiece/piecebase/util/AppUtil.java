package jp.co.jokerpiece.piecebase.util;

import jp.co.jokerpiece.piecebase.MainBaseActivity;
import jp.co.jokerpiece.piecebase.MainBaseActivity.TabInfo;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AppUtil {

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

}
