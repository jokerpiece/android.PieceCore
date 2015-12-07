package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

import jp.co.jokerpiece.piecebase.util.AppUtil;

public class QRCodeCaptActivity extends Activity {
    private CompoundBarcodeView mBarcodeView;
    BarcodeCallback bCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qrcode_capt);

        mBarcodeView = (CompoundBarcodeView)findViewById(R.id.barcodeView);
        mBarcodeView.getStatusView().setVisibility(View.INVISIBLE);

        bCallback = new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult barcodeResult) {
                String qrText = barcodeResult.getText();
                AppUtil.debugLog("QR", qrText);

                if(qrText.contains("://")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(qrText));
                    try {
                        startActivityForResult(i, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    QRCodeCaptActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainBaseActivity.tabHost.setCurrentTab(AppUtil.getPosition("Shopping"));
                        }
                    });
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> list) {}
        };

    }


    @Override
    public void onResume() {
        super.onResume();
        mBarcodeView.resume();
        mBarcodeView.decodeSingle(bCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBarcodeView.pause();
    }
}
