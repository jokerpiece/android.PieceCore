package jp.co.jokerpiece.piecebase.data;

import android.app.Activity;

public class MenuData extends Activity{
	public int menu_id = 0;
	public int menu_icon_image = 0;
	public String menu_title = "";
	public MenuData(int menuID,int resID,String menuTitle){
		menu_id = menuID;
		menu_icon_image = resID;
		menu_title = menuTitle;
	}
}
