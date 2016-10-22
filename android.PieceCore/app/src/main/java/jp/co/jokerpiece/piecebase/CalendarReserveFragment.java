package jp.co.jokerpiece.piecebase;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.jokerpiece.piecebase.api.RegistReserveAPI;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.RegistReserveData;
import jp.co.jokerpiece.piecebase.util.BlurDrawable;
import jp.co.jokerpiece.piecebase.util.DateValidator;
import jp.co.jokerpiece.piecebase.util.Time24HoursValidator;


/**
 * Created by Antifuture on 2016/9/27.
 */

public class CalendarReserveFragment extends Fragment implements ObservableScrollViewCallbacks
{

    ObservableScrollView mScrollView;

    private Context context;
    private Bundle bundleAPI;
    private View rootView;
    private TextView txtEventDate;
    private EditText edtxtName,edtxtTotal, edtxtDate, edtxtTime, edtxtPhone, edtxtMail, edtxtMessage;
    private Button btnSummit;
    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_calendar_reservation, container, false);

        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scrollView);
        mScrollView.setScrollViewCallbacks(this);

        setViewComponent();

        return rootView;
    }

    private void setViewComponent()
    {
        txtEventDate = (TextView)rootView.findViewById(R.id.calender_reserve_txt_date);

        edtxtName = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_name);
        edtxtTotal = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_total);
        //edtxtDate = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_date);
        edtxtTime = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_time);
        edtxtPhone = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_phone_number);
        edtxtMessage = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_message);
        edtxtMail = (EditText)rootView.findViewById(R.id.fragment_reserve_edtxt_mail);

        btnSummit = (Button)rootView.findViewById(R.id.fragment_reserve_btn_summit);

        if(bundle!=null)
        {
            txtEventDate.append(bundle.getString("event_date"));
        }

        edtxtName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtxtName.setHintTextColor(getResources().getColor(R.color.grey));
                edtxtName.setHint("名前");
                return false;
            }
        });

        edtxtTotal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtxtTotal.setHintTextColor(getResources().getColor(R.color.grey));
                edtxtTotal.setHint("人数");
                return false;
            }
        });

//        edtxtDate.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                edtxtDate.setHintTextColor(getResources().getColor(R.color.grey));
//                edtxtDate.setHint("日にち");
//                return false;
//            }
//        });

        edtxtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtxtTime.setHintTextColor(getResources().getColor(R.color.grey));
                edtxtTime.setHint("時間");
                return false;
            }
        });

        edtxtPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtxtPhone.setHintTextColor(getResources().getColor(R.color.grey));
                edtxtPhone.setHint("電話番号");
                return false;
            }
        });
        edtxtMail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtxtMail.setHintTextColor(getResources().getColor(R.color.grey));
                edtxtMail.setHint("メールアドレス");
                return false;
            }
        });


        btnSummit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String error_message ="";
                //Name validation
                if(edtxtName.getText().toString().equals(""))
                {
                    edtxtName.setHintTextColor(getResources().getColor(R.color.red));
                    edtxtName.setHint("名前を入力して下さい");
                    error_message+="名前を入力して下さい。\n\n";
                }

                //Total validation
                if(edtxtTotal.getText().toString().equals("")
                        ||Integer.parseInt(edtxtTotal.getText().toString())<=0)
                {
                    edtxtTotal.setHintTextColor(getResources().getColor(R.color.red));
                    edtxtTotal.setHint("人数を入力して下さい");
                    error_message+="人数を入力して下さい。\n\n";
                }

                //Date validation
//                if(edtxtDate.getText().toString().equals(""))
//                {
//                    edtxtDate.setHintTextColor(getResources().getColor(R.color.red));
//                    edtxtDate.setHint("日にちを入力して下さい");
//                    error_message+="日にちを入力して下さい。\n\n";
//                }
//                else
//                {
//                    DateValidator dateValidator = new DateValidator();
//                    Boolean validate = dateValidator.validate(edtxtDate.getText().toString());
//
//                    if(!validate)
//                    {
//                        edtxtDate.setHintTextColor(getResources().getColor(R.color.red));
//                        edtxtDate.setHint("日にちの形式が不正です");
//                        error_message+="日にちの形式が不正です。\n\n";
//                    }
//                }

                //Time validation
                if(edtxtTime.getText().toString().equals(""))
                {
                    edtxtTime.setHintTextColor(getResources().getColor(R.color.red));
                    edtxtTime.setHint("時間を入力して下さい");
                    error_message+="時間を入力して下さい。\n\n";
                }
                else
                {
                    Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
                    Boolean validate = time24HoursValidator.validate(edtxtTime.getText().toString());

                    if(!validate)
                    {
                        edtxtTime.setHintTextColor(getResources().getColor(R.color.red));
                        edtxtTime.setHint("時間の形式が不正です");
                        error_message+="時間の形式が不正です。\n\n";
                    }
                }

                //Phone number validation
                if(edtxtPhone.getText().toString().equals(""))
                {
                    edtxtPhone.setHintTextColor(getResources().getColor(R.color.red));
                    edtxtPhone.setHint("電話番号を入力して下さい");
                    error_message+="電話番号を入力して下さい。\n\n";
                }

                //Email validation
                if(edtxtMail.getText().toString().equals(""))
                {
                    edtxtMail.setHintTextColor(getResources().getColor(R.color.red));
                    edtxtMail.setHint("メールアドレスを入力して下さい");
                    error_message+="メールアドレスを入力して下さい。\n\n";
                }
                else
                {
                    Boolean validate = isValidEmail(edtxtMail.getText().toString());
                    if(!validate)
                    {
                        edtxtMail.setHintTextColor(getResources().getColor(R.color.red));
                        edtxtMail.setHint("メールアドレスの形式が不正です");
                        error_message+="メールアドレスの形式が不正です。\n\n";
                    }
                }

                if(error_message.equals(""))
                {
                    //Dialog: all value valid
                    new AlertDialog.Builder(context)
                            .setMessage("予約を確定しますか？")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    bundleAPI = new Bundle();
                                    bundleAPI.putString("user_name", edtxtName.getText().toString());
                                    bundleAPI.putString("people", edtxtTotal.getText().toString());
                                    bundleAPI.putString("reserve_date", edtxtDate.getText().toString());
                                    bundleAPI.putString("reserve_time", edtxtTime.getText().toString());
                                    bundleAPI.putString("phone", edtxtPhone.getText().toString());
                                    bundleAPI.putString("mail_address",edtxtMail.getText().toString());
                                    bundleAPI.putString("remark", edtxtMessage.getText().toString());

                                    registReserveApi();
                                }
                            }).show();
                }
                else
                {
                    //Dialog: has invalid input
                    new AlertDialog.Builder(context)
                            .setMessage(error_message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            }).show();
                }

            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void registReserveApi()
    {
        getActivity().getLoaderManager().initLoader
                (Config.loaderCnt++, getArguments(), new LoaderManager.LoaderCallbacks<RegistReserveData>()
                {

                    @Override
                    public Loader<RegistReserveData> onCreateLoader(int id, Bundle args) {
                        RegistReserveAPI registReserveAPI = new RegistReserveAPI(context, bundleAPI);
                        registReserveAPI.forceLoad();
                        return registReserveAPI;
                    }

                    @Override
                    public void onLoadFinished(Loader<RegistReserveData> loader, RegistReserveData data) {

                        if (data.error_code.equals("0"))
                        {
                            //Dialog: all value valid
                            new AlertDialog.Builder(context)
                                    .setMessage("予約しました。")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {

                                        }
                                    }).show();
                        }
                        else
                        {
                            //Dialog: all value valid
                            new AlertDialog.Builder(context)
                                    .setMessage(data.error_message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {

                                        }
                                    }).show();
                        }

                    }

                    @Override
                    public void onLoaderReset(Loader<RegistReserveData> loader) {

                    }
                });
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //activityHeaderImage.setTranslationY(scrollY / 3);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        //onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

        }
    }


}
