package jp.co.jokerpiece.piecebase.util;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;

/**
 * Build.VERSION_CODES.LOLLIPOPより小さい場合に使用するカメラビュー。
 * Build.VERSION_CODES.LOLLIPOPで非推奨となったCameraクラスを使っています。
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback, Camera.PreviewCallback {
    private static final String TAG = CameraSurfaceView.class.getSimpleName();

    private static Camera camera;
    private int CAMERA_ID = getCameraId();

    private ImageScanner scanner;

    private CameraSurfaceViewCallback callback;

    static {
        System.loadLibrary("iconv");
    }

    public CameraSurfaceView(Context context, CameraSurfaceViewCallback callback) {
        super(context);
        this.callback = callback;

        if (camera != null) {
            camera.autoFocus(null);
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }

        SurfaceHolder holder = getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        setTag(TAG);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().toString().equals(TAG)) {
                    camera.autoFocus(CameraSurfaceView.this);
                }
            }
        });
    }

    public int getCameraId() {
        int ret = -1;
        // 各カメラの情報を取得
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo caminfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, caminfo);

            // カメラの向きを取得
            int facing = caminfo.facing;

            if (facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 後部についているカメラの場合
                AppUtil.debugLog(TAG, "cameraId: " + Integer.toString(i)
                        + ", this is a back-facing camera");
                ret = i;
                break;
            }
        }
        return ret;
    }

    private int getOrientation() {
        return getContext().getResources().getConfiguration().orientation;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(CAMERA_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // カメラのプレビューサイズをViewに設定
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getSupportedPreviewSizes().get(0); // 0=最大サイズ
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);

        // 画面回転補正。
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
//            layoutParams.width = size.height;
//            layoutParams.height = size.width;
        } else {
            camera.setDisplayOrientation(0);
//            layoutParams.width = size.width;
//            layoutParams.height = size.height;
        }
//        this.setLayoutParams(layoutParams);

        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
//            camera.autoFocus(null);
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onAutoFocus(boolean success, final Camera camera) {
        AppUtil.debugLog(TAG, "focus");

        if (success) {
            camera.autoFocus(null);

            camera.setOneShotPreviewCallback(this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(data);

        try {
            int result = scanner.scanImage(barcode);

            if (result != 0) {
                AppUtil.debugLog(TAG, "バーコードスキャンに成功しました。");
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    String x = sym.getData();
//                    Common.showToast(getContext(), "sym.getData(): " + x);
                    AppUtil.debugLog(TAG, "sym.getData(): " + x);
                    if (x != null && !x.contains("http")) {
                        // Fragmentにバーコード番号を返す
                        callback.getBarcodeNum(x);
                    }
                }
            } else {
                AppUtil.debugLog(TAG, "バーコードスキャンに失敗しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        camera.startPreview();
    }

    /**
     * バーコード番号をフラグメントに返すためのコールバックインターフェース。
     */
    public interface CameraSurfaceViewCallback {
        public void getBarcodeNum(String barcodeNum);
    }

}
