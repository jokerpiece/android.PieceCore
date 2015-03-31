package jp.co.jokerpiece.piecebase.util;

import java.util.List;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.data.MenuData;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationMenuListAdapter extends ArrayAdapter<MenuData> {
	private LayoutInflater inflater;
	Context context;
	int resource;
	List<MenuData> dataList;
	public NavigationMenuListAdapter(Context context, int resource,
			List<MenuData> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		dataList = objects;
	}
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
         
        // Viewを再利用している場合は新たにViewを作らない
        if (view == null) {
            inflater =  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_menu, null);     
        }
        MenuData data;
        ImageView iconImage = (ImageView)view.findViewById(R.id.menu_icon);
        TextView menuTitle = (TextView)view.findViewById(R.id.menu_title);

        if(dataList.size() > position){
        	data = dataList.get(position);
            iconImage.setImageResource(data.menu_icon_image);
            menuTitle.setText(data.menu_title);
        }else{
            iconImage.setImageResource(0);
            menuTitle.setText("");
        }
        return view;
	}
}
