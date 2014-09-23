package com.example.securecircle;

import com.example.securecircle.ScanInfoActivity.PlaceholderFragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class StartActivity extends FragmentActivity {
	
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);	
		Log.i("PowerTutor",StartActivity.this.toString() );
/*		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
*/
		
		mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);
		//mTabHost.getTabWidget().setBackgroundColor(getResources().getColor(R.color.black));
		
		mTabHost.addTab(
				 mTabHost.newTabSpec("Scan").setIndicator("",
						 getResources().getDrawable(R.drawable.signature)),
						 ReadApps.class, null);
		 
		mTabHost.addTab(
				 mTabHost.newTabSpec("Battery").setIndicator("",
						 getResources().getDrawable(R.drawable.battery)),
					 BatteryUsage.class, null);
		
		mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.grey));
		mTabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.grey));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_start,
					container, false);
			
			return rootView;
		}
	}

}
