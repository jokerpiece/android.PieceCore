package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.File;

import jp.co.jokerpiece.piecebase.config.Config;

public class SplashBaseActivity extends Activity {
	private Context context;        // コンテキスト
    private Class<?> cls;           // 遷移先クラス

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// コンテキストの取得
		context = this;
        // 遷移先クラスの設定
        cls = getTransitionClass();



		// 指定秒間、スプラッシュ画面を表示する
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
                try {
                    // メイン画面に遷移
                    Intent intent = new Intent(context, cls);
                    startActivity(intent);
                    SplashBaseActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
			}
		}, Config.SPLASH_TIME * 1000);
	}

    /**
     * 遷移先クラスを取得する。
     * (注)遷移先クラスはマニフェストに登録したアクティビティでなければなりません。
     * @return 遷移先クラス
     */
    public Class<?> getTransitionClass() {
        return MainBaseActivity.class;
    }

}
