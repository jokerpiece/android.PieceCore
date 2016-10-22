package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import jp.co.jokerpiece.piecebase.util.BlurDrawable;

/**
 * Created by Antifuture on 2016/9/27.
 */

public class CalendarBookingActivity extends FragmentActivity
{
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar_booking);

        background = (ImageView) findViewById(R.id.reserve_background);
        View beneathView = background;//the view that beneath blur view
        View blurView = (ImageView) findViewById(R.id.reserve_background_mask);//blur View

        BlurDrawable blurDrawable = new BlurDrawable(beneathView, 100);
        blurView.setBackgroundDrawable(blurDrawable);


        //Do things if clicked the liste view item
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);
        CalendarNewFragment fragment = new CalendarNewFragment();

        ft.replace(R.id.calendar_fragment, fragment);
        ft.commit();

    }

}
