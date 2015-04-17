package jp.co.jokerpiece.piecebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import jp.co.jokerpiece.piecebase.data.ShopListData.ShopData;

/**
 * Created by kaku on 2015/04/15.
 */
public class ShopListAdapter extends BaseAdapter {
    static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    private LayoutInflater inflater;
    private int layout;
  //  private List<ShopData> list;

    protected Context context;
    protected ArrayList<ShopData> list;



    public ShopListAdapter(Context context, int layout,ArrayList<ShopData> list) {
//        super(context, layout, list);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        this.layout = layout;
    }

    //@SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(layout, null);
        }

        TextView tv1 = (TextView) convertView.findViewById(R.id.tvshop_name);
        tv1.setText(list.get(position).shop_name);
        TextView tv2 = (TextView) convertView.findViewById(R.id.tvAddress);
        tv2.setText(list.get(position).address);


        boolean exist = false;


        return convertView;
    }

    public void addAll(ArrayList<ShopData> list) {
        list.addAll(list);
        notifyDataSetChanged();
    }

    //    public ShopListViewAdapter(Context context, int layout, ArrayList<ShopData> list) {
//        this.context = context;
//        setList(list);
//    }

    public Context getContext() {
        return this.context;
    }

    public void setList(ArrayList<ShopData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

//abstract class ShopListViewAdapter<ShopData> extends BaseAdapter {
//
//    public abstract View getView(int position, View convertView, ViewGroup parent);
//}