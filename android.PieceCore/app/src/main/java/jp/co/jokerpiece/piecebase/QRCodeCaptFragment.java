package jp.co.jokerpiece.piecebase;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QRCodeCaptFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QRCodeCaptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRCodeCaptFragment extends Fragment {
    private CompoundBarcodeView mBarcodeView;
    BarcodeCallback bCallback;
    public QRCodeCaptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QRCodeCaptFragment.
     */
    public static QRCodeCaptFragment newInstance() {
        return new QRCodeCaptFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_qrcode_capt, container, false);

        mBarcodeView = (CompoundBarcodeView)rootView.findViewById(R.id.barcodeView);
        mBarcodeView.getStatusView().setVisibility(View.INVISIBLE);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                    getActivity().runOnUiThread(new Runnable() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
