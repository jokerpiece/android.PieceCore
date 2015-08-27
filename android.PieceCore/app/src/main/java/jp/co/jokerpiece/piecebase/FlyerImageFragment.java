package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync.DownloadImageSyncCallback;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FlyerImageFragment extends Fragment implements DownloadImageSyncCallback {
	String imageURL;
	String itemURL;
	LinearLayout view;
	ImageView imageView;

//	private static int loderCount = 2000;

	public FlyerImageFragment() {
		super();
	}
	 public FlyerImageFragment(FlyerHeaderData flyerData) {
		 this.imageURL = flyerData.img_url;
		 this.itemURL = flyerData.item_url;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			    ViewGroup container,
			    Bundle savedInstanceState) {
		view = (LinearLayout)inflater.inflate(R.layout.fragment_flyer_image, null);
		imageView = (ImageView)view.findViewById(R.id.header_image);
		getLoaderManager();

		if(savedInstanceState != null){
			imageURL = savedInstanceState.getString("image_url");
			itemURL = savedInstanceState.getString("item_url");
		}
		if(imageURL != null){
			//view.addView(imageView);
			DownloadImageSync sync = new DownloadImageSync(getActivity(), imageURL, imageView,this);
			if(!sync.loadImageView()){
				getActivity().getSupportLoaderManager().initLoader(Config.loaderCnt++, null, sync);
			}
		}
		if(itemURL != null){
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickFlyer(itemURL);
				}
			});
		}
	    return view;
	}

	public void onClickFlyer(String url) {
        if (!url.equals("") && !url.equals("null")) {
            FragmentManager fm =  ((MainBaseActivity)getActivity()).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            WebViewFragment fragment = new WebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("send_url", url);
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("image_url", imageURL);
        outState.putString("item_url", itemURL);
    }

	@Override
	public void onDestroyView() {
		if(view != null) {
			view.removeAllViews();
		}
		super.onDestroyView();
	}
	@Override
	public void setImageCallbackWithURL(Bitmap bitmap, String url) {
		if(imageView != null && bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
	}
}
