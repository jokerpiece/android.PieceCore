package jp.co.jokerpiece.piecebase;

import jp.co.jokerpiece.piecebase.api.ItemListAPI;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.FlyerData.FlyerHeaderData;
import jp.co.jokerpiece.piecebase.data.ItemListData;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync.DownloadImageSyncCallback;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
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
	String item_id_flyer;
	LinearLayout view;
	ImageView imageView;

	SharedPreferences systemData;
	SharedPreferences.Editor systemDataEditor;

//	private static int loderCount = 2000;

	public FlyerImageFragment() {
		super();
	}

	//get flyerData
	public FlyerImageFragment(FlyerHeaderData flyerData) {
		this.imageURL = flyerData.img_url;
		this.itemURL = flyerData.item_url;
		this.item_id_flyer = flyerData.item_id;

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

	public void onClickFlyer(String url) //Click the flyer picture
	{

		if (!url.equals("") && !url.equals("null"))
		{

			//遷移先のURLがURLではない場合、flyerから商品購入画面に遷移する。
			if (!url.startsWith("paypal"))
			{
				if(Config.WEBVIEW_ACTIVITY_MODE.equals("true"))
				{
					if(!item_id_flyer.equals(""))// item_id has value
					{


						if("1".equals(Config.PAY_SELECT_KBN))//LinePay Native
						{
							//call itemlistAPI
							getItemList(item_id_flyer);

						}
						else if ("2".equals(Config.PAY_SELECT_KBN))//Paypal Native
						{
							FragmentManager fm = getFragmentManager();
							FragmentTransaction ft = fm.beginTransaction();
							ft.addToBackStack(null);
							PayPalPieceFragment fragment = new PayPalPieceFragment();

							Bundle bundle = new Bundle();

							//bundle.putString("item_id", data.item_id);
							//bundle.putString("price", data.price);
							//bundle.putString("stocks", data.stocks);
							//bundle.putString("img_url", data.img_url);
							//bundle.putString("item_title", data.item_title);
							//bundle.putString("text", data.text);
							fragment.setArguments(bundle);
							ft.replace(R.id.fragment, fragment);
							ft.commit();
							//Paypal決済できる詳細画面に遷移する。

						}
						else
						{
							Intent intent = new Intent(getActivity(), WebViewActivity.class);
							intent.putExtra("send_url", url);
							getActivity().startActivity(intent);
						}
					}
					else// item_id = null ＞＞＞＞go WebView
					{
						Intent intent = new Intent(getActivity(), WebViewActivity.class);
						intent.putExtra("send_url", url);
						getActivity().startActivity(intent);
					}


				}
				else //WEBVIEW_ACTIVITY_MODE = flase
				{
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
			else //url start with paypal
			{
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.addToBackStack(null);
				PayPalPieceFragment fragment = new PayPalPieceFragment();

				Bundle bundle = new Bundle();
				//渡された値よりパラメータの取得
				url = url.substring(url.indexOf(":") - 1);

				String[] param = url.split(",");
				//bundle.putString("img_url", imgurl);
				bundle.putString("item_id", param[0]);
				bundle.putString("item_title", param[1]);
				bundle.putString("price", param[2]);

				bundle.putString("item_url", url);

				fragment.setArguments(bundle);
				ft.replace(R.id.fragment, fragment);
				ft.addToBackStack(null);
				ft.commit();
				//Paypal決済できる詳細画面に遷移する。
			}
			/*

			//If WEBVIEW_ACTIVITY_MODE = true, open by webView (setting by setting file)
			if(Config.WEBVIEW_ACTIVITY_MODE.equals("true"))
			{
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra("send_url", url);
				getActivity().startActivity(intent);
			}
			else //WEBVIEW_ACTIVITY_
			{
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
			*/

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
	public void setImageCallbackWithURL(Bitmap bitmap, String url)
	{
		if(imageView != null && bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
	}

	//get good's detail
	private void getItemList(final String item_id_flyer)
	{

		getActivity().getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<ItemListData>()
		{

			@Override
			public Loader<ItemListData> onCreateLoader(int id, Bundle args)
			{
				ItemListAPI itemAPI = new ItemListAPI(getActivity(), item_id_flyer);
				itemAPI.forceLoad();
				return itemAPI;
			}

			@Override
			public void onLoadFinished(Loader<ItemListData> loader, ItemListData data)
			{

				Bundle bundle = new Bundle();

				//find item_id's good's detail

				if(data.data_list.size()!=0)
				{
					for(int position=0; position<data.data_list.size(); position++) {
						ItemListData.ItemData itemdata = data.data_list.get(position);


						if (item_id_flyer.equals(itemdata.item_id))
						{

							//memory back fragment if the buying is done
							String fromWhatFragment = "FlyerFragment";
							systemData = getActivity().getSharedPreferences("SystemDataSave", getActivity().MODE_PRIVATE);
							systemDataEditor = systemData.edit();
							systemDataEditor.putString("from_what_fragment", fromWhatFragment);
							systemDataEditor.commit();

							//detabaseの商品資料をLinePayFragmentに送る
							bundle.putString("item_id", itemdata.item_id);
							bundle.putString("price", itemdata.price);
							bundle.putString("stocks", itemdata.stocks);
							bundle.putString("img_url", itemdata.img_url);
							bundle.putString("item_title", itemdata.item_title);
							bundle.putString("text", itemdata.text);
							bundle.putString("item_url", itemdata.item_url);

							FragmentManager fm = getActivity().getSupportFragmentManager();
							FragmentTransaction ft = fm.beginTransaction();
							ft.addToBackStack(null);
							LinePayFragment fragment = new LinePayFragment();


							fragment.setArguments(bundle);
							ft.replace(R.id.fragment, fragment);
							ft.commit();
						}
					}
				}
				else
				{
					new AlertDialog.Builder(FlyerImageFragment.this.getActivity())
							.setTitle("商品が存在しません。")
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{

								}
							}).show();
				}


			}

			@Override
			public void onLoaderReset(Loader<ItemListData> loader) {
			}
		});
	}

}


