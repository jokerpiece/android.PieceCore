package jp.co.jokerpiece.piecebase.util;

import jp.co.jokerpiece.piecebase.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class NavigationBaseActivity extends FragmentActivity  implements NavigationDrawerFragment.NavigationDrawerCallbacks{
	protected NavigationDrawerFragment mNavigationDrawerFragment;
	protected FrameLayout container;

	ImageView ivSendHome;
	ImageView ivSendInfo;
	ImageView ivSendShop;
	ImageView ivSendCoupon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_container);
		container = (FrameLayout)findViewById(R.id.container);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		restoreActionBar();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		ivSendHome = (ImageView)findViewById(R.id.sendFlyer);
		ivSendHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendView(v);
			}
		});
		ivSendInfo = (ImageView)findViewById(R.id.sendInfomation);
        ivSendInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendView(v);
			}
		});
        ivSendShop = (ImageView)findViewById(R.id.sendShopping);
        ivSendShop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendView(v);
			}
		});
        ivSendCoupon = (ImageView)findViewById(R.id.sendCoupon);
        ivSendCoupon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendView(v);
			}
		});
	}

	private void sendView(View v){
//		Intent i = null;
		if(v == ivSendHome){
//			if(getClass() != FlyerActivity.class){
//				i = new Intent(getApplication(), FlyerActivity.class);
//			    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			}
//		}else if(v == ivSendInfo){
//			if(getClass() != InfomationActivity.class){
//				i = new Intent(getApplicationContext(), InfomationActivity.class);
//			}
//		}else if(v == ivSendShop){
//			if(getClass() != ShoppingActivity.class){
//				i = new Intent(getApplicationContext(), ShoppingActivity.class);
//			}
//		}else if(v == ivSendCoupon){
//			if(getClass() != CouponActivity.class && getClass() != CouponUseActivity.class){
//				i = new Intent(getApplicationContext(), CouponActivity.class);
//			}
		}
//		if(i != null){
//			startActivity(i);
//		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();

		switch (position) {
		case 0:
				sendView(ivSendHome);
		    break;
		case 1:
				sendView(ivSendInfo);
			break;
		case 2:
				sendView(ivSendShop);
			break;
		case 3:
				sendView(ivSendCoupon);
			break;
		default:
			break;
		}
	}

	public void onSectionAttached(int number) {
		restoreActionBar();
		invalidateOptionsMenu();
	}

	public void restoreActionBar() {
        getActionBar().setTitle(getTitle());
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		if(item != null && item.getItemId() == android.R.id.home){
			DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
		            mDrawerLayout.closeDrawer(GravityCompat.START);
		        } else {
		            mDrawerLayout.openDrawer(GravityCompat.START);
		    }
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((NavigationBaseActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
}
