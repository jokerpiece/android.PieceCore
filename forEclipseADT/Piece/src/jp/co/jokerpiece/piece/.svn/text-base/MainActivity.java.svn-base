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
