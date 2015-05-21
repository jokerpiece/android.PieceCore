package jp.co.jokerpiece.piecebase.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * カメラビューに被せるマスクビュー。
 * フォーカスを当てやすいように図形やマスクを表示しています。
 */
public class CameraMaskView extends View {
    private Paint paint;

    public CameraMaskView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int CANVAS_WIDTH = getWidth();
        final int CANVAS_HEIGHT = getHeight();
        final int PADDING_WIDTH = AppUtil.getPixelFromDp(getContext(), 10);
        final int MASK_WIDTH = CANVAS_WIDTH - (PADDING_WIDTH * 2);
        final int MASK_HEIGHT = AppUtil.getPixelFromDp(getContext(), 220);
        final int PADDING_HEIGHT = (CANVAS_HEIGHT - MASK_HEIGHT) / 2;

        // マスク
        paint = new Paint();
        paint.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRect(0, 0, CANVAS_WIDTH, PADDING_HEIGHT, paint);
        canvas.drawRect(0, PADDING_HEIGHT, PADDING_WIDTH, PADDING_HEIGHT + MASK_HEIGHT, paint);
        canvas.drawRect(CANVAS_WIDTH - PADDING_WIDTH, PADDING_HEIGHT, CANVAS_WIDTH, PADDING_HEIGHT + MASK_HEIGHT, paint);
        canvas.drawRect(0, PADDING_HEIGHT + MASK_HEIGHT, CANVAS_WIDTH, CANVAS_HEIGHT, paint);

        // 線
        final int LINE_LENGTH = AppUtil.getPixelFromDp(getContext(), 30);
        final float LINE_WIDTH = 10.0f;
        final int LINE_GAP = (int) (LINE_WIDTH / 2);
        paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStrokeWidth(LINE_WIDTH);
        // 左上
        canvas.drawLine(PADDING_WIDTH, PADDING_HEIGHT + LINE_GAP, PADDING_WIDTH + LINE_LENGTH, PADDING_HEIGHT + LINE_GAP, paint);
        canvas.drawLine(PADDING_WIDTH + LINE_GAP, PADDING_HEIGHT, PADDING_WIDTH + LINE_GAP, PADDING_HEIGHT + LINE_LENGTH, paint);
        // 右上
        canvas.drawLine(CANVAS_WIDTH - PADDING_WIDTH, PADDING_HEIGHT + LINE_GAP, CANVAS_WIDTH - PADDING_WIDTH - LINE_LENGTH, PADDING_HEIGHT + LINE_GAP, paint);
        canvas.drawLine(CANVAS_WIDTH - PADDING_WIDTH - LINE_GAP, PADDING_HEIGHT, CANVAS_WIDTH - PADDING_WIDTH - LINE_GAP, PADDING_HEIGHT + LINE_LENGTH, paint);
        // 左下
        canvas.drawLine(PADDING_WIDTH, PADDING_HEIGHT + MASK_HEIGHT - LINE_GAP, PADDING_WIDTH + LINE_LENGTH, PADDING_HEIGHT + MASK_HEIGHT - LINE_GAP, paint);
        canvas.drawLine(PADDING_WIDTH + LINE_GAP, PADDING_HEIGHT + MASK_HEIGHT - LINE_GAP, PADDING_WIDTH + LINE_GAP, PADDING_HEIGHT + MASK_HEIGHT - LINE_LENGTH, paint);
        // 右下
        canvas.drawLine(CANVAS_WIDTH - PADDING_WIDTH, PADDING_HEIGHT + MASK_HEIGHT - LINE_GAP, CANVAS_WIDTH - PADDING_WIDTH - LINE_LENGTH, PADDING_HEIGHT + MASK_HEIGHT - LINE_GAP, paint);
        canvas.drawLine(CANVAS_WIDTH - PADDING_WIDTH - LINE_GAP, PADDING_HEIGHT + MASK_HEIGHT, CANVAS_WIDTH - PADDING_WIDTH - LINE_GAP, PADDING_HEIGHT + MASK_HEIGHT - LINE_LENGTH, paint);

        // フォーカス線
        final int FOCUS_LENGTH = AppUtil.getPixelFromDp(getContext(), 20);
        final float FOCUS_WIDTH = 4.0f;
        paint = new Paint();
        paint.setColor(Color.rgb(255, 0, 0));
        paint.setStrokeWidth(FOCUS_WIDTH);
        // 縦
        canvas.drawLine(PADDING_WIDTH, PADDING_HEIGHT + MASK_HEIGHT / 2, CANVAS_WIDTH - PADDING_WIDTH, PADDING_HEIGHT + MASK_HEIGHT / 2, paint);
        // 横
        canvas.drawLine(PADDING_WIDTH + MASK_WIDTH / 2, PADDING_HEIGHT + MASK_HEIGHT / 2 - FOCUS_LENGTH / 2, PADDING_WIDTH + MASK_WIDTH / 2, PADDING_HEIGHT + MASK_HEIGHT / 2 + FOCUS_LENGTH / 2, paint);
    }
}
