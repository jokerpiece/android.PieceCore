package jp.co.jokerpiece.piecebase;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.RelativeLayout;
import android.widget.TextView;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import jp.co.jokerpiece.piecebase.api.EventListAPI;
import jp.co.jokerpiece.piecebase.calendar_decorators.EventDecorator;
import jp.co.jokerpiece.piecebase.calendar_decorators.HighlightWeekendsDecorator;
import jp.co.jokerpiece.piecebase.calendar_decorators.OneDayDecorator;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.EventListData;

import static com.prolificinteractive.materialcalendarview.CalendarDay.from;


/**
 * Created by Antifuture on 2016/9/26.
 */

public class CalendarFragment extends BaseFragment implements OnDateSelectedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private Context context;
    private RelativeLayout btnRserveFrame;
    private TextView eventName, eventDate;
    private Button btnReserve;
    private View rootView;
    private Bundle bundleAPI;
    private MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private ArrayList<HashMap<String, String>> reserveList;
    private ArrayList<HashMap<String, String>> eventList;
    private ArrayList<CalendarDay> eventDays;
    private Calendar instance;
    private CalendarDay selectDate;

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
        setCalendar();

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        if(selectDate!=null)
        {
            eventDateSlectedControl(selectDate);
        }

    }

    public void setCalendar()
    {
        calendarView = (MaterialCalendarView)rootView.findViewById(R.id.calendar_calendarview);
        calendarView.setOnDateChangedListener(this);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

//        instance = Calendar.getInstance();
//        calendarView.setSelectedDate(instance.getTime());
//        CalendarDay day = from(instance.getTime());
//        eventDateSlectedControl(day);


//
//        Calendar instance1 = Calendar.getInstance();
//        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);
//
//        Calendar instance2 = Calendar.getInstance();
//        instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);
//
//        calendarView.state().edit()
//                .setMinimumDate(instance1.getTime())
//                .setMaximumDate(instance2.getTime())
//                .commit();

        calendarView.addDecorators(
                new HighlightWeekendsDecorator()
//                ,oneDayDecorator
        );


    }

    public void setViewComponent() {

        eventName = (TextView) rootView.findViewById(R.id.txt_event_name);
        eventDate = (TextView) rootView.findViewById(R.id.txt_event_date);
        btnReserve = (Button) rootView.findViewById(R.id.btn_reserve);
        btnRserveFrame = (RelativeLayout)rootView.findViewById(R.id.btn_reserve_frame);

        eventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarBookingActivity.class);
                startActivity(intent);
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

                        if(data.reserveList!=null)
                        {
                            reserveList = data.reserveList;
                        }

                        if(data.eventList!=null)
                        {
                            eventList = data.eventList;
                            eventDays = new ArrayList<>();

                            for (HashMap<String,String> event :eventList)
                            {
                                if(event.containsKey("event_date"))
                                {
                                    Log.d("event_date",event.get("event_date"));
                                    Date date = new Date();
                                    date.setTime(date2Millis("yyyy-MM-dd",event.get("event_date")));
                                    CalendarDay day = from(date);
                                    eventDays.add(day);
                                }

                            }

                            calendarView.addDecorator(new EventDecorator(Color.RED, eventDays));

                            instance = Calendar.getInstance();
                            calendarView.setSelectedDate(instance.getTime());
                            CalendarDay day = from(instance.getTime());
                            eventDateSlectedControl(day);
                        }


                    }

                    @Override
                    public void onLoaderReset(Loader<EventListData> loader) {

                    }
                });
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView calendarView, @NonNull CalendarDay date, boolean selected) {
        //If you change a decorate, you need to invalidate decorators
        oneDayDecorator.setDate(date.getDate());
        calendarView.invalidateDecorators();

        selectDate = date;

        eventDateSlectedControl(date);

    }

    public static long date2Millis(String pattern,String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date d = sdf.parse(date);
//            sdf = new SimpleDateFormat("yyyy-MM-dd");
//            System.out.println(sdf.format(d));
//            System.out.println(d.getTime());
            return d.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void eventDateSlectedControl(CalendarDay date)
    {
        Log.d("CalendarSlect_day:",date.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String selectDayFormat = formatter.format(date.getDate());
        Log.d("CalendarSlect_format:",selectDayFormat);

        eventName.setText("NO EVENT");
        eventDate.setVisibility(View.GONE);
        btnRserveFrame.setVisibility(View.GONE);


        if(eventList!=null)
        {
            for (final HashMap<String, String> event:eventList)
            {
                if(event.containsKey("event_date"))
                {
                    Log.d("alendarSlect_event_day",event.get("event_date"));
                    if(event.get("event_date").equals(selectDayFormat))
                    {
                        eventDate.setText(event.get("event_date"));
                        eventName.setText(event.get("event_name"));
                        eventDate.setVisibility(View.VISIBLE);
                        btnRserveFrame.setVisibility(View.VISIBLE);
                        btnReserve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Bundle bundle = new Bundle();
                                bundle.putString("event_date",event.get("event_date"));
                                bundle.putString("event_name", event.get("event_name"));

                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.setCustomAnimations(R.anim.transition_in_from_right,R.anim.transition_out_from_left,R.anim.transition_in_from_left,R.anim.transition_out_from_right);
                                ft.addToBackStack(null);

                                CalendarReserveFragment fragment = new CalendarReserveFragment();
                                fragment.setArguments(bundle);
                                ft.replace(R.id.fragment, fragment);
                                ft.commit();
                            }
                        });
                    }

                }
            }
        }
    }

}
