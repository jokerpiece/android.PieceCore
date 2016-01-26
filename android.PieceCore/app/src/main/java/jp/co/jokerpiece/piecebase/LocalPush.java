package jp.co.jokerpiece.piecebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jp.co.jokerpiece.piecebase.R;

/**
 * Created by Tomita
 */
public class LocalPush extends BroadcastReceiver {

    Context localContext;

    public static final String PUSHU_MSG = "もうすぐ0です！\nプレゼントの準備はできていますか？";

    public static Date nowDate;

    /*
    *アラーム処理の受け取り手
     */
    public void onReceive(Context context, Intent intent) {

        showLocalPush(context);
    }

    /**
     * ローカルプッシュ通知処理
     * アプリ起動時にデータを取得して対象
     */
    public static void showLocalPush(Context context) {

        Calendar now = Calendar.getInstance();
        nowDate = getNow(now);

        SharedPreferences pref;
        SharedPreferences.Editor editor;
        //アプリ内で保存しているデータを取得

        pref = PreferenceManager.getDefaultSharedPreferences(context);

        String st_noties_name;
        String st_noties_day;
        String st_fathersday;
        String st_mothersday;
        String st_keirouday;
        String st_childday;
        String st_varentainday;

        st_noties_name = pref.getString(ReminderFragment.NOTIES_NAME, "");
        st_noties_day = pref.getString(ReminderFragment.NOTIES_DAY, "");
        String msg = PUSHU_MSG;
        //通知したい日の取得
        Date nday = null;
        String sndaymm = null;
        String sndaydd = null;
        String snday = null;

        //登録されているデータに対して20日前のデータを取得
        if (st_noties_day != "" && st_noties_day != null) {
            snday = st_noties_day;
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

            nday = getMeorialday2(now, snday);

        }

        //父の日：6月の第三日曜日
        st_fathersday = pref.getString(ReminderFragment.FATHERS_DAY, "false");
        Date fday = null;
        //その年の父の日を求めて、その日から20日前の通知日を取得する。
        if ("true".equals(st_fathersday)) {

            fday = getMorialday(now, "06", 15, 21, 0);
        }

        //母の日：5月の第2日曜日
        st_mothersday = pref.getString(ReminderFragment.MOTHERS_DAY, "false");
        //その年の母の日を求める。
        Date mday = null;
        if ("true".equals(st_mothersday)) {
            mday = getMorialday(now, "05", 8, 14, 0);
        }

        //敬老の日；9月の第3月曜日
        st_keirouday = pref.getString(ReminderFragment.KEIROU_DAY, "false");
        //その年の敬老の日を求める。
        Date kday = null;
        if ("true".equals(st_keirouday)) {

            kday = getMorialday(now, "09", 15, 21, 1);
        }

        //子供の日：5月5日
        st_childday = pref.getString(ReminderFragment.CHILD_DAY, "false");
        Date cday = null;
        if ("true".equals(st_childday)) {

            cday = getMeorialday2(now, "05/05");
        }

        //バレンタインデー：2月14日
        st_varentainday = pref.getString(ReminderFragment.VAREN_DAY, "false");
        Date vday = null;
        if ("true".equals(st_varentainday)) {

            vday = getMeorialday2(now, "02/14");
        }


        //通知判定
        //アプリを起動させた日が父の日から20日以内の場合はプッシュ通知を送る
        if (nday != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(nday);
            Date befor20ndate = c.getTime();

            if (befor20ndate.compareTo(nowDate) == 0) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", st_noties_name);
                map.put("info", msg.replaceAll("0", st_noties_name));
                sendNotification(context, map, 0);
                ReminderFragment rf = new ReminderFragment();
                //通知したら再設定
                rf.setAlarm(context, st_noties_name, snday, 0, 0, 99);
            }

        }

        if (fday != null) {

            Calendar c = Calendar.getInstance();
            c.setTime(fday);
            Date befor20fdate = c.getTime();

            if (befor20fdate.compareTo(nowDate) == 0) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", "父の日");

                map.put("info", msg.replaceAll("0", "父の日"));
                sendNotification(context, map, 1);

                ReminderFragment rf = new ReminderFragment();
                //通知したら再設定
                rf.setAlarm(context, "父の日", "06", 15, 21, 0);
            }

        }
        if (mday != null) {

            Calendar c = Calendar.getInstance();
            c.setTime(mday);
            Date befor20fdate = c.getTime();

            if (befor20fdate.compareTo(nowDate) == 0) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", "母の日");
                map.put("info", msg.replaceAll("0", "母の日"));
                sendNotification(context, map, 2);
                ReminderFragment rf = new ReminderFragment();
                rf.setAlarm(context, "母の日", "05", 8, 14, 0);
            }

        }

        if (kday != null) {

            Calendar c = Calendar.getInstance();
            c.setTime(kday);
            Date befor20fdate = c.getTime();

            if (befor20fdate.compareTo(nowDate) == 0) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", "敬老の日");
                map.put("info", msg.replaceAll("0", "敬老の日"));
                sendNotification(context, map, 3);
                ReminderFragment rf = new ReminderFragment();
                rf.setAlarm(context, "敬老の日", "09", 15, 21, 1);
            }

        }

        if (cday != null) {

            Calendar c = Calendar.getInstance();
            c.setTime(cday);
            Date befor20fdate = c.getTime();

            if (befor20fdate.compareTo(nowDate) == 0) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", "子供の日");
                map.put("info", msg.replaceAll("0", "子供の日"));
                sendNotification(context, map, 4);
                ReminderFragment rf = new ReminderFragment();
                rf.setAlarm(context, "子供の日", "05/05", 0, 0, 99);
            }

        }

        if (vday != null) {

            Calendar c = Calendar.getInstance();
            c.setTime(vday);
            Date befor20fdate = c.getTime();

            if (befor20fdate.compareTo(nowDate) == 0) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", "バレンタインデー");
                map.put("info", msg.replaceAll("0", "バレンタインデー"));
                sendNotification(context, map, 5);
                ReminderFragment rf = new ReminderFragment();
                rf.setAlarm(context, "バレンタインデー", "02/14", 0, 0, 99);
            }
        }
    }


    /*
    * ローカルPush通知処理
     */
    private static void sendNotification(Context context, HashMap<String, String> map, int id) {

        NotificationManager mManager;

        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainBaseActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.status_icon)
                        .setContentTitle(map.get("title"))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(map.get("info")))
                        .setContentText(map.get("info"));

        mBuilder.setContentIntent(contentIntent);
        mManager.notify(id, mBuilder.build());

    }


    /*
    *その年の固定の記念日の日付を取得する。
     */
    public static Date getMeorialday2(Calendar now, String mmdd) {

        Calendar now2 = Calendar.getInstance();
        nowDate = getNow(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Date date = null;

        try {
            date = sdf.parse(now.get(now.YEAR) + "/" + mmdd);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -20);
            //その年の登録した20日前の日付をセット
            date =c.getTime();
            //通知日の日付と今日の日付を確認し、今日が設定日より
            //同じかさきであれば、来年の日付をセットする。
            if (nowDate.compareTo(date) >= 0) {

                c.add(Calendar.YEAR, 1);
                date = c.getTime();
            }
        } catch (ParseException e) {
            date = null;
        } finally {
            sdf = null;
        }
        return date;
    }


    /*
    *その年の変動する記念日の日付を取得する。
     */
    public static Date getMorialday(Calendar now, String mm, int start, int end, int youbi) {

        int yyyy = now.get(now.YEAR);

        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        nowDate = getNow(c2);
        int i;
        Date date = null;

        for (i = start; i < end + 1; i++) {
            SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd");

            try {
                date = s.parse(yyyy + "/" + mm + "/" + i);
            } catch (ParseException e) {
                date = null;
            } finally {
                s = null;
            }

            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -20);
            if (nowDate.compareTo(date) >= 0) {

                c.add(Calendar.YEAR, 1);
            }

            int week_int = now.get(c.DAY_OF_WEEK);

            //日曜の場合は処理を抜ける
            if (week_int == youbi) {
                break;
            }
        }

        return date;
    }

    /*
    * 今日の年月日を取得
     */
    public static Date getNow(Calendar now) {

        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");

        String s = now.get(Calendar.YEAR) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.DAY_OF_MONTH);

        Date d;
        try {

            now.setTime(sd.parse(s));

            d = now.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            d = null;
        } finally {
            sd = null;
        }
        return d;
    }

}
