package com.example.securecircle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name;
    private String packageName;
    private String versionName;
    private int versionCode = 0;
    private Signature[] sigs;
    private Drawable icon;
    private String srcDir;
 
    public AppInfo() {}
 
    public Intent getLaunchIntent(Context context) {
        Intent intentLaunch = context.getPackageManager().getLaunchIntentForPackage(this.packageName);
        return intentLaunch;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getPackageName() {
        return packageName;
    }
 
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
 
    public String getVersionName() {
        return versionName;
    }
 
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
 
    public int getVersionCode() {
        return versionCode;
    }
 
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
 
    public Drawable getIcon() {
        return icon;
    }
 
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    public Signature[] getAppSignatures(){
    	return sigs;
    }
    
    public void setAppSignatures(Signature[] sigs){
    	this.sigs = sigs;
    }

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}
}