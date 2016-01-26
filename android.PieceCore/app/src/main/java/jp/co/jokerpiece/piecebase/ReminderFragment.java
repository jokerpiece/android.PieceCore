package jp.co.jokerpiece.piecebase;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import jp.co.jokerpiece.piecebase.data.CategoryListData;
import jp.co.jokerpiece.piecebase.util.DownloadImageView;

/*
* RemainfdのFragment
* 　JP K.Tomita
 */
public class ReminderFragment extends BaseFragment implements ExpandableListView.OnChildClickListener {
    Context context;

    public static final String NOTIES_NAME = "noties_name";
    public static final String NOTIES_DAY = "noties_day";
    public static final String FATHERS_DAY = "fathersday";
    public static final String MOTHERS_DAY = "mothersday";
    public static final String KEIROU_DAY = "keirouday";
    public static final String CHILD_DAY = "childday";
    public static final String VAREN_DAY = "cb_varentainday";

    View rootView;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        rootView = inflater.inflate(R.layout.fragment_reminder, container, false);

        //日付のセット処理
        TextView et = (TextView) rootView.findViewById(R.id.et_notice_day);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotiesDay(v);
            }
        });

        //日付クリアボタン
        Button btn0 = (Button) rootView.findViewById(R.id.remaind_clear_button);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDate();
            }
        });

        //保存ボタン
        Button btn = (Button) rootView.findViewById(R.id.remaind_save_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRemainder();
            }
        });
        pref = PreferenceManager.getDefaultSharedPreferences(context);

        EditText et_noties_name = (EditText) rootView.findViewById(R.id.et_notice_name);
        TextView tv_noties_day = (TextView) rootView.findViewById(R.id.et_notice_day);
        CheckBox cb_fathersday = (CheckBox) rootView.findViewById(R.id.cb_fathersday);
        CheckBox cb_mothersday = (CheckBox) rootView.findViewById(R.id.cb_mothersday);
        CheckBox cb_keirouday = (CheckBox) rootView.findViewById(R.id.cb_keirounohi);
        CheckBox cb_childday = (CheckBox) rootView.findViewById(R.id.cb_childernday);
        CheckBox cb_varentainday = (CheckBox) rootView.findViewById(R.id.cb_valentain);

        et_noties_name.setText(pref.getString(NOTIES_NAME, ""));
        tv_noties_day.setText(pref.getString(NOTIES_DAY, ""));

        if ("true".equals(pref.getString(FATHERS_DAY, "false"))) {
            cb_fathersday.setChecked(true);
        } else {
            cb_fathersday.setChecked(false);
        }

        if ("true".equals(pref.getString(MOTHERS_DAY, "false"))) {
            cb_mothersday.setChecked(true);
        } else {
            cb_mothersday.setChecked(false);
        }
        if ("true".equals(pref.getString(KEIROU_DAY, "false"))) {
            cb_keirouday.setChecked(true);
        } else {
            cb_keirouday.setChecked(false);
        }
        if ("true".equals(pref.getString(CHILD_DAY, "false"))) {
            cb_childday.setChecked(true);
        } else {
            cb_childday.setChecked(false);
        }
        if ("true".equals(pref.getString(VAREN_DAY, "false"))) {
            cb_varentainday.setChecked(true);
        } else {
            cb_varentainday.setChecked(false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // フラグメントごとのオプションメニューを有効化する
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /*
    *　日付入力機能
     */
    private void getNotiesDay(View pressed) {
        // 現在の日付を取得
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 日付選択ダイアログの生成
        DatePickerDialog datePicker = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view,
                                          int year, int monthOfYear, int dayOfMonth) {
                        TextView et = (TextView) rootView.findViewById(R.id.et_notice_day);

                        et.setText(monthOfYear + 1 + "月" + dayOfMonth + "日");
                    }
                },
                year, month, day);

        // 表示
        datePicker.show();
    }


    /*
  *Remainderのデータ保存処理
   */
    private void clearDate() {
        TextView et = (TextView) rootView.findViewById(R.id.et_notice_day);

        et.setText("");
    }

    /*
    *Remainderのデータ保存処理
     */
    private void saveRemainder() {

        editor = pref.edit();

        //Activityの値をすべて取得する。
        EditText et_noties_name = (EditText) rootView.findViewById(R.id.et_notice_name);
        TextView tv_noties_day = (TextView) rootView.findViewById(R.id.et_notice_day);
        CheckBox cb_fathersday = (CheckBox) rootView.findViewById(R.id.cb_fathersday);
        CheckBox cb_mothersday = (CheckBox) rootView.findViewById(R.id.cb_mothersday);
        CheckBox cb_keirouday = (CheckBox) rootView.findViewById(R.id.cb_keirounohi);
        CheckBox cb_childday = (CheckBox) rootView.findViewById(R.id.cb_childernday);
        CheckBox cb_varentainday = (CheckBox) rootView.findViewById(R.id.cb_valentain);

        editor.putString(NOTIES_NAME, et_noties_name.getText().toString());

        editor.putString(NOTIES_DAY, tv_noties_day.getText().toString());

        //保存したalarmの処理を一度すべて破棄して再度alarmの処理を作成する。
        Intent intent = new Intent(context, LocalPush.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        //alarmの初期化
        sender.cancel();

        if (tv_noties_day.getText().toString() != "" && tv_noties_day.getText().toString() != null) {

            //通知したい日に値がセットされていた場合
            //通知する処理を実装
            String sndaymm = null;
            String sndaydd = null;
            String snday = null;
            snday = tv_noties_day.getText().toString();
            sndaymm = snday.substring(0, snday.indexOf("月"));
            sndaydd = snday.substring(snday.indexOf("月") + 1, snday.indexOf("日"));
            sndaymm = "0" + sndaymm;

            if (sndaymm.length() > 2) {
                sndaymm = sndaymm.substring(1);
            }

            sndaydd = "0" + sndaydd;
            if (sndaydd.length() > 2) {
                sndaydd = sndaydd.substring(1);
            }

            snday = sndaymm + "/" + sndaydd;


            setAlarm(context, et_noties_name.getText().toString(), snday, 0, 0, 99);
        }

        if (cb_fathersday.isChecked()) {
            editor.putString(FATHERS_DAY, "true");
            setAlarm(context, "父の日", "06", 15, 21, 0);

        } else {
            editor.putString(FATHERS_DAY, "false");

        }

        if (cb_mothersday.isChecked()) {
            editor.putString(MOTHERS_DAY, "true");
            setAlarm(context, "母の日", "05", 8, 14, 0);
        } else {
            editor.putString(MOTHERS_DAY, "false");

        }

        if (cb_keirouday.isChecked()) {
            editor.putString(KEIROU_DAY, "true");
            setAlarm(context, "敬老の日", "09", 15, 21, 1);
        } else {
            editor.putString(KEIROU_DAY, "false");

        }
        if (cb_childday.isChecked()) {
            editor.putString(CHILD_DAY, "true");
            setAlarm(context, "子供の日", "05/05", 0, 0, 99);
        } else {
            editor.putString(CHILD_DAY, "false");

        }
        if (cb_varentainday.isChecked()) {
            editor.putString(VAREN_DAY, "true");
            setAlarm(context, "バレンタインデー", "02/14", 0, 0, 99);
        } else {
            editor.putString(VAREN_DAY, "false");
        }

        // データの保存
        editor.commit();

        //保存した文言の表示
        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
        Toast.makeText(context, "保存しました。", Toast.LENGTH_LONG).show();

    }

    /*
    * ローカルプッシュ処理のためのアラーム設定処理
     */
    public void setAlarm(Context context2, String dummy, String mm, int start, int end, int youbi) {

        Intent intent = new Intent(context2, LocalPush.class);
        //Alarmを複数登録するためにダミーの値を設定
        intent.setType(dummy);
        PendingIntent sender = PendingIntent.getBroadcast(context2, 0, intent, 0);
        //alarmの初期化
        sender.cancel();

        sender = PendingIntent.getBroadcast(context2, 0, intent, 0);

        Calendar calendar = Calendar.getInstance(); // Calendar取得
        Date d = null;
        if (youbi != 99) {

            d = LocalPush.getMorialday(calendar, mm, start, end, youbi);

        } else {
            d = LocalPush.getMeorialday2(calendar, mm);

        }
        calendar.setTime(d); // 記念日を取得
        calendar.add(Calendar.HOUR, 13);

        //テスト用
        // ----------------
        //        calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
        //        calendar.add(Calendar.SECOND, 10);
        //        Date dd = calendar.getTime();
        // ----------------
        //20日前の13時
        AlarmManager am = (AlarmManager) context2.getSystemService(context.ALARM_SERVICE); // AlarmManager取得
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // Al

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }
}
