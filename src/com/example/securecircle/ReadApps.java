package com.example.securecircle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ReadApps extends Fragment {

	private static final String TAG = "ReadApps Activity";
	TableLayout app_table;
	String selectedApp;
	static File fileObj;
	//static List<PackageInfo> installedApps = new ArrayList<PackageInfo>();
	static ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listapps, container, false);

        app_table = (TableLayout)v.findViewById(R.id.app_table);
        if(!fillAppTable())
        	Toast.makeText(this.getActivity(), "Cannot list installed apps!", Toast.LENGTH_LONG).show();
        
        Button scanBtn = (Button)v.findViewById(R.id.scanbutton);
        scanBtn.setOnClickListener(new OnClickListener(){

	         @Override
	         public void onClick(View v) {
	        	 //Send app signatures for checking
	        	 Log.i(TAG, "Scan button is clicked");
	        	 ScanSelectedApkSignature();        	 
	         }
	    });        
        
        return v;
    }
    
    boolean fillAppTable() {
    	 
        TableRow row;
        RadioButton rButton;
        RadioGroup rGroup = new RadioGroup(this.getActivity());
        rGroup.setOrientation(RadioGroup.VERTICAL);
        
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        	public void onCheckedChanged(RadioGroup rg, int checkedId) {
        		for(int i=0; i<rg.getChildCount(); i++) {
        			RadioButton btn = (RadioButton) rg.getChildAt(i);
        			if(btn.getId() == checkedId) {
        				selectedApp = (String) btn.getText();
        				return;
        			}
        		}
        	}
        });

        //Converting to dip unit
        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());
        
        if(!getListOfInstalledApp(this.getActivity()))
        	return false;
        
        for (int index = 0; index < appInfoList.size(); index++) {
        	final AppInfo appInfo = appInfoList.get(index);
            row = new TableRow(this.getActivity());
            rButton = new RadioButton(this.getActivity());
            rButton.setId(index);            
            rButton.setText(appInfo.getName());
            rButton.setTextColor(getResources().getColor(R.color.rgreen));
            rButton.setHighlightColor(getResources().getColor(R.color.rgreen));
            
            rGroup.addView(rButton);
        }
        
        app_table.addView(rGroup, new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        return true;
        
     }

    
    //private static List<PackageInfo> getListOfInstalledApp(Context context) {
    private static boolean getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
        //installedApps.clear();
        appInfoList.clear();
        if (apps != null && !apps.isEmpty()) {
        	
            for (int i = 0; i < apps.size(); i++) {
                PackageInfo appPackage = apps.get(i);
                ApplicationInfo appInfo = new ApplicationInfo();
                try { 
                    AppInfo app = new AppInfo(); 
                    appInfo = packageManager.getApplicationInfo(appPackage.packageName, 0);
                    app.setName(appPackage.applicationInfo.loadLabel(packageManager).toString()); 
                    app.setPackageName(appPackage.packageName); 
                    app.setVersionName(appPackage.versionName); 
                    app.setVersionCode(appPackage.versionCode); 
                    app.setAppSignatures(appPackage.signatures);
                    app.setIcon(appPackage.applicationInfo.loadIcon(packageManager));
                    app.setSrcDir(appInfo.publicSourceDir);
     
                    //check if the application is not a system app 
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appPackage.packageName);
                    if(launchIntent != null && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        //installedApps.add(appPackage);
                    	appInfoList.add(app);
                    }
                } catch (NameNotFoundException e) {
                	Log.i(TAG, "Error in listing the installed apps.");
                	Log.i(TAG, e.getMessage());
                	e.printStackTrace();
                	return false;
                }
            }
            
          //sort the list of applications alphabetically 
/*            if (appInfoList.size() > 0) {
                Collections.sort(appInfoList, new Comparator() {
 
                    @Override
                    public int compare(final AppInfo app1, final AppInfo app2) {
                        return app1.getName().toLowerCase(Locale.getDefault()).compareTo(app2.getName().toLowerCase(Locale.getDefault()));
                    }

                });
            }
 */    
        }
        return true;
    }
    
    void ScanSelectedApkSignature(){
    	String packageName = null;
    	String url = null;
    	for(int index=0; index<appInfoList.size(); index++){
    		if(appInfoList.get(index).getName().equals(selectedApp)){
    			packageName = appInfoList.get(index).getPackageName();
    			url = appInfoList.get(index).getSrcDir();
    		}
    	}
    	
    	
    	
    	if(packageName != null && url != null)
    	{
    		Log.i("URL - ", url);
	    	Bundle basket= new Bundle();
	    	basket.putString("url", url);
	    	basket.putString("packageName", packageName);
	    	basket.putString("appName", selectedApp);
	    	
	    	Intent activityIntent = new Intent(this.getActivity(), ScanInfoActivity.class);
	    	activityIntent.putExtras(basket);
	    	startActivity(activityIntent);   	
    	}
    	else
    	{
    		Toast.makeText(getActivity(),
					"Select Application to scan from list below" ,
					Toast.LENGTH_SHORT).show();
    	}
    	
    }    
    
}
