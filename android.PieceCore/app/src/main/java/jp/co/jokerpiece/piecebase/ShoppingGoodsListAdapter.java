package jp.co.jokerpiece.piecebase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.ItemListData.ItemData;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync.DownloadImageSyncCallback;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShoppingGoodsListAdapter extends ArrayAdapter<ItemData> implements DownloadImageSyncCallback  {
	static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
//	private static final int ID_IMAGES = 100;
	private LayoutInflater inflater;
	private LoaderManager loaderManager;
	private List<ItemData> list;

	private ArrayList<ImageView> listImageView = new ArrayList<ImageView>();

	public ShoppingGoodsListAdapter(Context context,int layout, List<ItemData> list, LoaderManager loaderManager) {
		super(context, layout, list);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.loaderManager = loaderManager;
		this.list = list;
	}
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(R.layout.adapter_shopping_goods_list, null);
		}
		LinearLayout Bg = (LinearLayout)convertView.findViewById(R.id.stocksBg);
		TextView tv1 = (TextView) convertView.findViewById(R.id.tvTitle);
		tv1.setText(list.get(position).item_title);		TextView tv2 = (TextView) convertView.findViewById(R.id.tvPrice);
		if(!list.get(position).price.equals("") && list.get(position).price != null) {
			int price = Integer.parseInt(list.get(position).price);
			NumberFormat currencyFormat = NumberFormat.getNumberInstance();
			tv2.setText(currencyFormat.format(price) + getContext().getResources().getString(R.string.yen));
		}
		TextView tv3 = (TextView) convertView.findViewById(R.id.tvStocks);
		tv3.setText(list.get(position).stocks);
		if(list.get(position).stocks != null) {
			if (list.get(position).stocks.startsWith("売り切れ")) {
				tv3.setTextColor(Color.WHITE);
				Bg.setVisibility(View.VISIBLE);
				Bg.setBackgroundColor(Color.GRAY);
			}else{
				tv3.setTextColor(Color.GRAY);
				Bg.setVisibility(View.GONE);
			}
		}
		ImageView iv = (ImageView) convertView.findViewById(R.id.ivItemImage);
		iv.setVisibility(View.INVISIBLE);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setTag(list.get(position).img_url);

		boolean exist = false;
		for (ImageView image : listImageView) {
			if(image.getTag().toString().equals(list.get(position).img_url)){
				exist =true;
				break;
			}
		}
		if(!exist){
			listImageView.add(iv);
		}

		DownloadImageSync sync = new DownloadImageSync(getContext(), list.get(position).img_url, iv,this);
		if(!sync.loadImageView()){
//			loaderManager.initLoader(ID_IMAGES + position, null, sync);
			loaderManager.initLoader(Config.loaderCnt++, null, sync);
		}

		return convertView;
	}
	@Override
	public void setImageCallbackWithURL(Bitmap bitmap, String url) {
		for (ImageView iv : listImageView) {
			if(iv.getTag().toString().equals(url)){
				iv.setImageBitmap(bitmap);
				iv.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
}
