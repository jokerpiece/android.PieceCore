package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Antifuture on 2016/9/29.
 */

public class CalendarEventListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> data;

    //private ArrayList<GetReachingTaskData.GetReachingTaskList> reachingTaskList;

    public CalendarEventListAdapter(Context context, ArrayList<HashMap<String, String>> data) {

        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.adapter_calendar_event_listview, viewGroup, false);

        CalendarEventListViewHolder holder = new CalendarEventListViewHolder();

        holder.listTitle = (TextView) view.findViewById(R.id.calendar_list_title);

        holder.listTitle.setText(data.get(position).toString());

        view.setTag(holder);

        return view;
    }

    public static class CalendarEventListViewHolder {

        TextView listTitle;
    }
}
