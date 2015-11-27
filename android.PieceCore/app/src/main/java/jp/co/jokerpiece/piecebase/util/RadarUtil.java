package jp.co.jokerpiece.piecebase.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.co.jokerpiece.piecebase.config.Common;

/**
 * iBeacon受信用メソッド群
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RadarUtil {
    private static final String TAG = RadarUtil.class.getSimpleName();

    private static Context context;

    private static BluetoothLeScanner scanner;
    private static BluetoothAdapter bluetoothAdapter;
    public static boolean isGetBluetoothAdapter;

    private static ProgressDialog pd;                   // プログレスダイアログ
    private static Thread searchingThd;                 // ビーコン検索スレッド
    private static ArrayList<BeaconData> beaconList = null;    // 検知したビーコンのリスト
    private static BeaconData nearestBeacon;            // 検知したビーコンの中で一番近いもの

    private static final int SCAN_SECONDS = 3;                // ビーコンスキャン時間
    private static final boolean IS_DIALOG_SHOWN = false;   // ダイアログを表示するかどうか
    public static final int REQUEST_ENABLE_BT = 0;           // onActivityResultのリクエストコード

    /**
     * BeaconUtilの初期化処理を行う。
     * (注)BeaconUtilを使用するには必ず本メソッドを初めに呼んでおかなければならない。
     * @param context コンテキスト
     */
    public static void init(Activity context) {
        if (!isGraterThanJellyBeanMr1()) {
            Log.d(TAG, "ビーコン検索を行うにはandroid4.3以上が必要です。");
            return;
        }

        RadarUtil.context = context;
        isGetBluetoothAdapter = false;
        BluetoothManager bluetoothManager =
				(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();

		if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
			isGetBluetoothAdapter = true;
            if (isGraterEqualThanLollipop()) {
                scanner = bluetoothAdapter.getBluetoothLeScanner();
            }
		} else {
            // Bluetoothを自動ONにする
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            (context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
    }

    // android5.0以上(ここから)
    private static ScanSettings settings;
    private static List<ScanFilter> filters;
    private static ScanCallback callback;
    static {
        if (isGraterEqualThanLollipop()) {
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
            filters = new ArrayList<>();
            callback =
                    new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            super.onScanResult(callbackType, result);

                            // Get Scan Record byte array (Be warned, this can be null)
                            if (result.getScanRecord() != null) {
                                byte[] scanRecord = result.getScanRecord().getBytes();
                                getBeaconData(scanRecord, result.getRssi());
                            }
                        }
                    };
        }
    }
    // android5.0以上(ここまで)

    /**
     * ビーコン検知スタート時に呼ばれるコールバックメソッドを設定する。
     * (android5.0より小さいバージョン用)
     */
    private static BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    getBeaconData(scanRecord, rssi);
                }
            };

    static boolean BeaconUtilScanActive = false;
    /**
     * ビーコンスキャン処理を行う。
     */
    public static void startScan() {
        if (!isGetBluetoothAdapter) { return; }
        if(BeaconUtilScanActive) { return; }
        BeaconUtilScanActive = true;
        // ビーコン検索中ダイアログの表示
        Log.d(TAG, "チェックインを探しています...");
        pd = new ProgressDialog(context);
        pd.setTitle("チェックインを探しています...");
//        pd.setCancelable(false);
        if (IS_DIALOG_SHOWN) {
            pd.show();
        }

        if(beaconList == null) {
            beaconList = new ArrayList<>();
        }
        nearestBeacon = null;

        searchingThd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // interruptしても終了しない可能性があったので終了処理を追加
                    if (Thread.interrupted()) {
                        BeaconUtilScanActive = false;
                        throw new InterruptedException();
                    }

                    if (isGraterEqualThanLollipop()) {
                        scanner.startScan(filters, settings, callback);
                    } else {
                        bluetoothAdapter.startLeScan(leScanCallback);
                    }
                    if (Thread.interrupted()) { throw new InterruptedException(); }
                    Thread.sleep(SCAN_SECONDS * 1000);
                    if (Thread.interrupted()) { throw new InterruptedException(); }

                    if (isGraterEqualThanLollipop()) {
                        scanner.stopScan(callback);
                    } else {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                    }

                    if (Thread.interrupted()) { throw new InterruptedException(); }

                    // ビーコンが見つかった場合
                    if (beaconList.size() != 0) {
                        pd.dismiss();
                        // 見つかったビーコンの中で一番近いものを取得
                        nearestBeacon = getNearestBeacon();
                    }
                    // ビーコンが見つからなかった場合
                    else {
                        //Common.showToast(context, "ビーコンが見つかりませんでした。");
                        throw new InterruptedException();
                    }

                    if (Thread.interrupted()) { throw new InterruptedException(); }

                } catch(InterruptedException e) {
                    if (pd != null && pd.isShowing()) { pd.dismiss(); }
                    if (isGraterEqualThanLollipop()) {
                        scanner.stopScan(callback);
                    } else {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                    }
                    e.printStackTrace();
                } catch(Exception e) {
                    if (pd != null && pd.isShowing()) { pd.dismiss(); }
                    if (isGraterEqualThanLollipop()) {
                        scanner.stopScan(callback);
                    } else {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                    }
                    e.printStackTrace();
                }
                BeaconUtilScanActive = false;
            }
        });
        searchingThd.start();
    }

    /**
     * ビーコンデータを取得する。
     * @param scanRecord スキャンレコード
     * @param rssi RSSI
     */
    public static void getBeaconData(byte[] scanRecord, int rssi) {
        if (scanRecord.length > 30) {
            // iBeaconの場合、6byte目から9byte目はこの値に固定されている
            if((scanRecord[5] == (byte)0x4c) && (scanRecord[6] == (byte)0x00) &&
                    (scanRecord[7] == (byte)0x02) && (scanRecord[8] == (byte)0x15)) {
                String uuid = convertToUuid(getScanData(9, 24, scanRecord));
                String major = getScanData(25, 26, scanRecord);
                String minor = getScanData(27, 28, scanRecord);
                Log.d("iBeaconSample", "-------------------------------------");
//							Log.d("iBeaconSample", "device found::" + device.getName());
//							Log.d("iBeaconSample", "uuid::" + device.getUuids());
//							Log.d("iBeaconSample", "address::" + device.getAddress());
                Log.d("iBeaconSample", "uuid::" + uuid);
                Log.d("iBeaconSample", "major::" + major);
                Log.d("iBeaconSample", "minor::" + minor);
                Log.d("iBeaconSample", "rssi::" + rssi);

                BeaconData bd = new BeaconData(uuid, major, minor, rssi);
                int x = containInBeaconList(bd);
                if (x == -1) {
                    beaconList.add(bd);
                } else {
                    beaconList.get(x).addRssi(rssi);
                }
            }
        }
    }

    /**
     * ビーコンリストの中から一番近いビーコンデータを返す。
     * @return ビーコンデータ、ビーコンリストが空の場合はnullを返す。
     */
    public static BeaconData getNearestBeacon() {
        BeaconData ret = null;
        int max = -999;
        for (int i = 0; i < beaconList.size(); i++) {
            BeaconData bd = beaconList.get(i);
            // 芳和ビーコンに限定
//			if (!bd.getUuid().equals(Config.UUID_HOUWA)) {
            // Applixビーコンに限定
//			if (!bd.getUuid().equals(Config.UUID_APPLIX)) {
            // 芳和ビーコンとApplixビーコンに限定
//            if (!(bd.getUuid().equals(Config.UUID_HOUWA) ||
//                    bd.getUuid().equals(Config.UUID_APPLIX))) {
//                continue;
//            }
            if (max < bd.getRssi()) {
                max = bd.getRssi();
                ret = bd;
            }
        }
        if (ret != null) {
            // ログの出力
            Log.d("NearestBeacon", "-------------------------------------");
            Log.d("NearestBeacon", "検知できたビーコンの数: " + beaconList.size());
            Log.d("NearestBeacon", "検知できたビーコンの中で一番近いものは以下の通り:");
            Log.d("NearestBeacon", "uuid::" + ret.getUuid());
            Log.d("NearestBeacon", "major::" + ret.getMajor());
            Log.d("NearestBeacon", "minor::" + ret.getMinor());
            Log.d("NearestBeacon", "rssi::" + ret.getRssiAverage());
            Log.d("NearestBeacon", "-------------------------------------");
        }
        return ret;
    }

    /**
     * 検知したビーコンリストの中にbdが含まれるかどうかを返す。
     * @param target ビーコンデータ
     * @return 含まれる場合リストの何番目か、含まれない場合-1
     */
    public static int containInBeaconList(BeaconData target) {
        int result = -1;

        for (int i = 0; i < beaconList.size(); i++) {
            if (beaconList.get(i).equals(target)) {
                result = i;
                break;
            }
        }

        return result;
    }

    /**
     * 検知したビーコンリストの中に一致するUUIDが含まれるかどうかを返す。
     * @param UUID 検索UUID
     * @return ビーコンデータ、ビーコンリストが空の場合はnullを返す。
     */
    public static BeaconData getBeaconWithUUID(String UUID,int major,int  minor){
        BeaconData ret = null;

        if(UUID == null || beaconList == null){
            return null;
        }
        for (BeaconData bd : beaconList) {
            if(bd.getUuid().equals(UUID) && bd.getMajorInt() == major && bd.getMinorInt() == minor){
                ret = bd;
                break;
            }
        }
        if (ret != null) {
            // ログの出力
            Log.d("NearestBeacon", "-------------------------------------");
            Log.d("NearestBeacon", "検知できたビーコンの数: " + beaconList.size());
            Log.d("NearestBeacon", "検知できたビーコンの中で一番近いものは以下の通り:");
            Log.d("NearestBeacon", "uuid::" + ret.getUuid());
            Log.d("NearestBeacon", "major::" + ret.getMajor());
            Log.d("NearestBeacon", "minor::" + ret.getMinor());
            Log.d("NearestBeacon", "rssi::" + ret.getRssiAverage());
            Log.d("NearestBeacon", "-------------------------------------");
        }
        return ret;
    }

    /**
     * 取得したレコード配列のstartからend番目の値を16進数文字列として返す。
     * @param start スタート
     * @param end エンド
     * @param scanRecord 取得したレコード
     * @return 16進数文字列
     */
    public static String getScanData(int start, int end, byte[] scanRecord) {
        StringBuffer sb = new StringBuffer(end - start);
        for (int i = start; i <= end; i++) {
            sb.append(convertHex(scanRecord[i] & 0xff));
        }
        return sb.toString();
    }

    /**
     * 取得した文字列をuuidに成形する。
     * @param s 文字列
     * @return uuid
     */
    public static String convertToUuid(String s) {
        return s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + s.substring(12, 16) + "-" + s.substring(16, 20) + "-" + s.substring(20);
    }

    /**
     * 整数を16進数文字列に変換する。
     * @param i 整数
     * @return 16進数文字列
     */
    public static String convertHex(int i) {
        char hexArray[] = {
                Character.forDigit((i >> 4) & 0x0f, 16), Character.forDigit(i & 0x0f, 16)};
        return new String(hexArray).toUpperCase(new Locale(Locale.getDefault().toString()));
    }

    /**
     * androidバージョンが4.3以上かどうかを返す。
     * (備考)4.3より小さい場合はビーコン処理を行えないため。
     * @return true:4.3以上, false:4.3より小さい
     */
    public static boolean isGraterThanJellyBeanMr1() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * androidバージョンが5.0以上かどうかを返す。
     * (備考)5.0以上ではstartLeScanメソッドが非推奨のため。
     * @return true:5.0以上, false:5.0より小さい
     */
    public static boolean isGraterEqualThanLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ビーコンデータを保持したクラス。
     */
    public static class BeaconData {
        private String uuid;
        private String major;
        private String minor;
        private String serial;
        private ArrayList<Integer> rssiList = new ArrayList<>();

        // コンストラクタ
        public BeaconData() {}
        public BeaconData(String uuid, String major, String minor,int rssi) {
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;

            addRssi(rssi);
        }

        // ゲッター、セッター
        public String getUuid() { return uuid; }
        public String getMajor() { return major; }
        public int getMajorInt() {
            int num = -1;
            if(major != null) {
                try {
                    num = Integer.valueOf(major);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return num;
        }

        public String getMinor() { return minor; }
        public int getMinorInt() {

            int num = -1;
            if(minor != null) {
                try {
                    num = Integer.valueOf(minor);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return num;
        }

        public String getSerial() { return serial; }
        public int getRssi() {
            if(rssiList != null && rssiList.size() >= 1) {
                return rssiList.get(0);
            }else {
                return 0;
            }
        }

        public int getRssiAverage() {
            int rssiAverage = 0;
            if(rssiList != null && rssiList.size() >= 1) {
                int rssiTotal = 0;
                int rssiListSize = rssiList.size();
                for (int i = 0; i < rssiListSize;i++) {
                    if(rssiList.size() > i) {
                        rssiTotal += rssiList.get(i);
                    }
                }
                rssiAverage = rssiTotal / rssiList.size();
            }
            return rssiAverage;
        }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public void setMajor(String major) { this.major = major; }
        public void setMinor(String minor) { this.minor = minor; }
        public void setSerial(String serial) { this.serial = serial; }
        public void addRssi(int rssi) {
            rssiList.add(0, rssi);

            while (rssiList.size() > 5){
                rssiList.remove(rssiList.size() - 1);
            }
        }

        // オーバーライド
        @Override
        public boolean equals(Object o) {
            boolean ret = false;
            if (o instanceof BeaconData) {
                if (((BeaconData)o).getUuid().equals(this.uuid) &&
                        ((BeaconData)o).getMajor().equals(this.major) &&
                        ((BeaconData)o).getMinor().equals(this.minor)) {
                    ret = true;
                }
            }
            return ret;
        }

        // メソッド
        public String getMajorByDecimalDigit() {
            int decimal = hexa2Decimal(major);
            Log.d("BeaconData", "(major:" + major + ") => (decimal:" + decimal + ")");
            return decimal + "";
        }

        public String getMinorByDecimalDigit() {
            int decimal = hexa2Decimal(minor);
            Log.d("BeaconData", "(minor:" + minor + ") => (decimal:" + decimal + ")");
            return decimal + "";
        }

        /**
         * 16進数を10進数に変換する。
         * @param s 16進数
         * @return 10進数
         */
        public int hexa2Decimal(String s) {
            int x = 0;
            if (s != null && !s.equals("")) {
                int len = s.length();
                for (int i = 0; i < len; i++) {
                    if (i != len - 1) {
                        x += (Math.pow(16, (len - i - 1)) * getDecimal(s.substring(i, i+1)));
                    } else {
                        x += (Math.pow(16, (len - i - 1)) * getDecimal(s.substring(i)));
                    }
                }
            }
            return x;
        }

        public int getDecimal(String s) {
            int ret = 0;
            s = s.toLowerCase();
            try {
                switch (s) {
                    case "a":
                        ret = 10;
                        break;
                    case "b":
                        ret = 11;
                        break;
                    case "c":
                        ret = 12;
                        break;
                    case "d":
                        ret = 13;
                        break;
                    case "e":
                        ret = 14;
                        break;
                    case "f":
                        ret = 15;
                        break;
                    default:
                        ret = Integer.valueOf(s);
                }
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }
}
