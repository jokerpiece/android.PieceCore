# [AndroidStudio]

### Pieceプロジェクトのインポート
githubからPieceプロジェクトをコピーして下さい。
Pieceプロジェクトはオトナゴコロアプリの最小構成となっています。
ランチャーアクティビティは、「jp.co.jokerpiece.piece.MainActivity」となります。
AndroidStudioからPieceプロジェクトを起動して下さい。
![Sync Project](./syncProj.png)ボタンを押下でビルドを実行し、![Run Project](./runProj.png)ボタンを押下でアプリを実行して下さい。

## google-play-services_libライブラリのインポート
"Piece/app/build.gradle"内のdependenciesで「compile 'com.google.android.gms:play-services:+'」を実行することでライブラリを読み込んでいます。

## android.PieceCoreライブラリのインポート
"Piece/app/build.gradle"内のmavenでurlを指定し、dependenciesで「compile 'jp.co.jokerpiece.android.piececore:android.piececore:0.0.+@aar'」を実行することでライブラリを読み込んでいます。

### 利用方法

MainActivityは、ライブラリにある「jp.co.jokerpiece.piecebase.MainBaseActivity」をextendsする必要があります。
追加するタブは、setConfig()メソッドをOverrideすることで、実装することができます。

setConfig()メソッドでは、ArrayList<HashMap<String, Object>>を戻り値として返す必要があります。
リストの数がタブの数となり、タブの中に設定する設定内容はHashMapで設定することができます。
以下にHashMapで設定できるキーとバリューを示します。

HashMap<String, Object>

| キー | 説明 | バリュー |
| --- | --- | --- |
| tabTitle | タブに表示するタイトル | getString(R.string.flyer1), getString(R.string.info1), getString(R.string.shopping1), getString(R.string.coupon1) |
| tabIcon | タブに設定する画像 | R.drawable.icon_flyer, R.drawable.icon_infomation, R.drawable.icon_shopping, R.drawable.icon_coupon |
| cls | タブに設定する画面 | FlyerFragment.class, InfomationFragment.class, ShoppingFragment.class, CouponFragment.class |

下記にMainActivityの記述例を記します。

MainActivity.java

    package jp.co.jokerpiece.piece;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.HashMap;

    import jp.co.jokerpiece.piecebase.CouponFragment;
    import jp.co.jokerpiece.piecebase.FlyerFragment;
    import jp.co.jokerpiece.piecebase.InfomationFragment;
    import jp.co.jokerpiece.piecebase.MainBaseActivity;
    import jp.co.jokerpiece.piecebase.R;
    import jp.co.jokerpiece.piecebase.ShoppingFragment;
    import android.os.Bundle;

    public class MainActivity extends MainBaseActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public ArrayList<HashMap<String, Object>> setConfig() {
            return new ArrayList<HashMap<String,Object>>(Arrays.asList(
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.flyer1)); }
                            { put("tabIcon", R.drawable.icon_flyer); }
                            { put("cls", FlyerFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.info1)); }
                            { put("tabIcon", R.drawable.icon_infomation); }
                            { put("cls", InfomationFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.shopping1)); }
                            { put("tabIcon", R.drawable.icon_shopping); }
                            { put("cls", ShoppingFragment.class); }
                    },
                    new HashMap<String, Object>() {
                            { put("tabTitle", getString(R.string.coupon1)); }
                            { put("tabIcon", R.drawable.icon_coupon); }
                            { put("cls", CouponFragment.class); }
                    }
            ));
        }

    }

### 機能クラス

Pieceで提供している機能と紐づくクラス名は下記の通りです。

|名前 | 説明 |
| --- | --- |
|FlyerFragment.class | フライヤー |
|InfomationFragment.class | お知らせ一覧 |
|ShoppingFragment.class | ショッピング |
|CouponFragment.class | クーポン |
