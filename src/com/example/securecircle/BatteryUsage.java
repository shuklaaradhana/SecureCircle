package com.example.securecircle;

import java.io.File;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import batteryCheck.amit.com.PowerTutorWrapper;
import db.amit.batteryCheck.BatteryUsageDB;

public class BatteryUsage extends Fragment {

	TableLayout battery_table;
	PowerTutorWrapper powerT;
	public Button startBtn;
	Button stopBtn;
    Button showBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerT = new PowerTutorWrapper(this.getActivity());
        Log.i("PowerTutor",this.getActivity().toString());
        powerT.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery, container, false);

        battery_table = (TableLayout)v.findViewById(R.id.battery_table);
        
         startBtn = (Button)v.findViewById(R.id.startbutton);
        startBtn.setOnClickListener(new OnClickListener(){
        	
	         @Override
	         public void onClick(View v) {
	        	
	        	 StartAnalyzingBatteryUsage();
	         }
        });
        
        stopBtn = (Button)v.findViewById(R.id.stopbutton);
        stopBtn.setEnabled(false);
        stopBtn.setOnClickListener(new OnClickListener(){       	

	         @Override
	         public void onClick(View v) {
	        	 StopAnalyzingBatteryUsage();
	         }
        });
        
        showBtn = (Button)v.findViewById(R.id.showbutton);
        showBtn.setOnClickListener(new OnClickListener(){       	

	         @Override
	         public void onClick(View v) {
	        	 ShowAnalyzingBatteryUsage();
	         }
        });

        return v;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	powerT.onResume();
    }
    @Override
    public void onPause() {
    	super.onPause();
    	powerT.onPause();
    }
    void StartAnalyzingBatteryUsage(){
    	startBtn.setEnabled(false);
    	startBtn.setVisibility(View.FOCUS_DOWN);
    	showBtn.setEnabled(false);
    	showBtn.setVisibility(View.FOCUS_DOWN);
    	//displayBatteryUsageLog();
    	powerT.startBatteryCheckService();
    	stopBtn.setEnabled(true);
    	stopBtn.setVisibility(View.VISIBLE);

    }
    
    void StopAnalyzingBatteryUsage(){
    	startBtn.setEnabled(true);
    	startBtn.setVisibility(View.VISIBLE);
    	showBtn.setText("Analyze");
    	showBtn.setEnabled(true);
    	showBtn.setVisibility(View.VISIBLE);
    	powerT.stopBatteryCheckService();

    }

    void ShowAnalyzingBatteryUsage()
    
    {   if (showBtn.getText().equals("Analyze")){
    	startBtn.setEnabled(false);
    	startBtn.setVisibility(View.FOCUS_DOWN);
    	stopBtn.setEnabled(false);
    	stopBtn.setVisibility(View.FOCUS_DOWN);
    	showBtn.setEnabled(false);
    	showBtn.setVisibility(View.FOCUS_DOWN);
    	File fileobj = powerT.saveLog();
    	Log.i("PowerTutor",fileobj.toString());
		//this.sleep(5000);
		powerT.analyzeLogs(fileobj);
		
		//ThreadWait tw = new ThreadWait();
		//tw.execute();
		/*new Thread() {
			public void start() {
				try {
					this.sleep(10000);
					startBtn.setEnabled(true);
					startBtn.setVisibility(View.VISIBLE);
					showBtn.setText("Show");
					showBtn.setEnabled(true);
					showBtn.setVisibility(View.VISIBLE);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}.start();
		*/
		//displayBatteryUsageLog();
		makeShowVisible();
    }
    else{
    	displayBatteryUsageLog();
    }
    }
    void displayBatteryUsageLog(){
    	battery_table.removeAllViews();
    	BatteryUsageDB batteryDB = new BatteryUsageDB(this.getActivity());
    	List<String> results = batteryDB.getAllRows();
    	//List<String> results = null;
    	TableRow row;
    	TextView tv1, tv2;
    	for(int index=0; index<results.size(); index++){
    		String result = results.get(index);
    		String[] values = result.split("::");
    		if(Double.parseDouble(values[8]) > 0)
    		{
    			
    		row = new TableRow(this.getActivity());
    		tv1 = new TextView(this.getActivity());
    		tv2 = new TextView(this.getActivity());
    		tv1.setText(values[1].split("@")[0]);
    		tv1.setTextColor(getResources().getColor(R.color.rgreen));
    		tv2.setText(values[8]);
    		tv2.setTextColor(getResources().getColor(R.color.red));
    		Log.i("PowerTutor",values[1].split("@")[0]+"|"+values[8]);
    		/*int res = Integer.parseInt(values[9]);
    		if(res<0.25)
    			tv2.setTextColor(getResources().getColor(R.color.rgreen));
    		else
    			tv2.setTextColor(getResources().getColor(R.color.red));*/
    		row.addView(tv1);
    		row.addView(tv2);
    		battery_table.addView(row, new TableLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	
    		}
    	}
    	
    }
    public void makeShowVisible()
    {
    	startBtn.setEnabled(true);
		startBtn.setVisibility(View.VISIBLE);
		showBtn.setText("Show");
		showBtn.setEnabled(true);
		showBtn.setVisibility(View.VISIBLE);
    }
    class ThreadWait extends AsyncTask<String,String,String>
    {
    	protected void onPreExecute()
    	{   super.onPreExecute();
    		//TextView viewQuote = (TextView) findViewById(R.id.view_quote);
    		//viewQuote.setText("Getting the Result.. Please wait!!");
    	} 
		@Override
		protected String doInBackground(String... uri) {
			try {
				synchronized(this){
					this.wait(10000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			makeShowVisible();
			/*startBtn.setEnabled(true);
			startBtn.setVisibility(View.VISIBLE);
			showBtn.setText("Show");
			showBtn.setEnabled(true);
			showBtn.setVisibility(View.VISIBLE);*/
			
			return "Success";
		}
    }
}
