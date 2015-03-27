package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashBaseActivity extends Activity {
	private static final int SPLASH_DURATIN = 2;

	private Context context;		// コンテキスト

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// コンテキストの取得
		context = this;

		// 指定秒間、スプラッシュ画面を表示する
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// ログイン画面に遷移
				Intent intent = new Intent(context, MainBaseActivity.class);
				startActivity(intent);
				SplashBaseActivity.this.finish();
			}
		}, SPLASH_DURATIN * 1000);
	}

}
