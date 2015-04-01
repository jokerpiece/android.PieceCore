package jp.co.jokerpiece.piecebase.util;

import jp.co.jokerpiece.piecebase.MainBaseActivity;
import jp.co.jokerpiece.piecebase.MainBaseActivity.TabInfo;
import android.app.ActionBar;

public class AppUtil {

	public static void setTitleOfActionBar(ActionBar actionBar, String title) {
		actionBar.setTitle(title);
	}

	public static int getPosition(String clsName) {
		int ret = -1;
		if (MainBaseActivity.tabInfoList != null) {
			for (int i = 0; i < MainBaseActivity.tabInfoList.size(); i++) {
				TabInfo tabInfo = MainBaseActivity.tabInfoList.get(i);
				if (tabInfo.cls.getSimpleName().contains(clsName)) {
					ret = i;
					break;
				}
			}
		}
		return ret;
	}

}
