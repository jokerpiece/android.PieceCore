package jp.co.jokerpiece.piecebase;

import java.util.ArrayList;
import java.util.List;

import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.CategoryListData.CategoryData;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync;
import jp.co.jokerpiece.piecebase.util.DownloadImageSync.DownloadImageSyncCallback;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class ShoppingListAdapter extends ArrayAdapter<CategoryData> implements DownloadImageSyncCallback  {
	static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
//	private static final int ID_IMAGES = 100;
	private LayoutInflater inflater;
	private LoaderManager loaderManager;
	private List<CategoryData> list;

	private ArrayList<ImageView> listImageView = new ArrayList<ImageView>();

	public ShoppingListAdapter(Context context,int layout, List<CategoryData> list, LoaderManager loaderManager) {
		super(context, layout, list);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.loaderManager = loaderManager;
		this.list = list;
	}
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(R.layout.adapter_shopping_category_list, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.categoryTitle);
		tv.setText(list.get(position).category_name);
		ImageView iv = (ImageView) convertView.findViewById(R.id.categoryImage);
		iv.setVisibility(View.GONE);
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
			if(iv.getTag().toString().equals(url) && !url.equals("")){
				iv.setImageBitmap(bitmap);
				iv.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
}
