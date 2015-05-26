package jp.co.jokerpiece.piecebase.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.config.Config;

/**
 * Created by kaku on 2015/05/25.
 */
public class CustomImageView extends ImageView {


    public CustomImageView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.check);

//        switch (Config.CurrentDay){
//            case 1:
//                canvas.drawBitmap(bmp,  50, 20, null);
//                break;
//            case 2:
//                canvas.drawBitmap(bmp,  50, 20, null);
//                canvas.drawBitmap(bmp, 230, 20, null);
//                break;
//        }
//            canvas.drawBitmap(bmp, 410, 20, null);
//            canvas.drawBitmap(bmp, 590, 20, null);
//            canvas.drawBitmap(bmp, 770, 20, null);
//            canvas.drawBitmap(bmp,  50, 200, null);
//            canvas.drawBitmap(bmp, 230, 200, null);
//            canvas.drawBitmap(bmp, 410, 200, null);
//            canvas.drawBitmap(bmp, 590, 200, null);
//            canvas.drawBitmap(bmp, 770, 200, null);

    }
}
