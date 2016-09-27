package jp.co.jokerpiece.piecebase;

import android.app.LoaderManager;
import android.content.Context;

import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import jp.co.jokerpiece.piecebase.api.EventListAPI;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.EventListData;



/**
 * Created by Antifuture on 2016/9/26.
 */

public class CalendarFragment extends BaseFragment {

    private Context context;
    private ListView listView;
    private View rootView;
    private String[] members = { "Event1", "Event2", "Event3", "Event4","Event5","Event6","Event7"};
    private Bundle bundleAPI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();

        rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        bundleAPI = new Bundle();
        bundleAPI.putString("cal_date","");

        //call http api post function: get event list
        eventListApi();

        setViewComponent();

        return rootView;

    }

    public void setViewComponent()
    {
        listView = (ListView) rootView.findViewById(R.id.calendar_listview);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_expandable_list_item_1, members);

        listView.setAdapter(adapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Do things if clicked the liste view item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

    }

    private void eventListApi()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<EventListData>()
                {

                    @Override
                    public Loader<EventListData> onCreateLoader(int id, Bundle args) {

                        EventListAPI eventListAPI = new EventListAPI(context, bundleAPI);
                        eventListAPI.forceLoad();

                        return eventListAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<EventListData> loader, EventListData data) {

                    }

                    @Override
                    public void onLoaderReset(Loader<EventListData> loader) {

                    }
                });
    }


}
