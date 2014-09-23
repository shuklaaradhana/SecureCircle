package db.amit.batteryCheck;

import java.util.ArrayList;
import java.util.List;
import db.amit.batteryCheck.DBDesign.BatteryUsage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class BatteryUsageDB {

	private SQLiteDatabase db;
	private BatteryUsageDBHandler mDbHelper;
	
	public BatteryUsageDB(Context context)
	{
		mDbHelper = new BatteryUsageDBHandler(context);
	}
	public void openWritable() throws SQLException {
	    db = mDbHelper.getWritableDatabase();
	}
	public void openReadable() throws SQLException {
	    db = mDbHelper.getReadableDatabase();
	}
	public void deleteAllRows()
	{
		openWritable();
		db.execSQL("delete from " + BatteryUsage.TABLE_NAME);
		close();
	}
	public long insertRow(String Pid, String name, double cpu, double lcd, double gps, double wifi, double audio, double Ma, double totalPct)
	{
		openWritable();
		ContentValues values = new ContentValues();
		values.put(BatteryUsage.COLUMN_APP_PID,Pid);
		values.put(BatteryUsage.COLUMN_APP_NAME, name);
		values.put(BatteryUsage.COLUMN_APP_CPU,cpu);
		values.put(BatteryUsage.COLUMN_APP_LCD, lcd);
		values.put(BatteryUsage.COLUMN_APP_AUDIO, audio);
		values.put(BatteryUsage.COLUMN_APP_GPS, gps);
		values.put(BatteryUsage.COLUMN_APP_WIFI, wifi);
		values.put(BatteryUsage.COLUMN_TOTAL_MA, Ma);
		values.put(BatteryUsage.COLUMN_TOTAL_PCT,totalPct);
		long newRowId;
		newRowId = db.insert(BatteryUsage.TABLE_NAME,null,values);
		close();
		return newRowId;
	}
	
	public List<String> getAllRows()
	{
		openReadable();
		List<String> records = new ArrayList<String>();
		String columns[] = {
				//CardEntry._ID,
				BatteryUsage.COLUMN_APP_PID,
				BatteryUsage.COLUMN_APP_NAME,
				BatteryUsage.COLUMN_APP_CPU,
				BatteryUsage.COLUMN_APP_GPS,
				BatteryUsage.COLUMN_APP_AUDIO,
				BatteryUsage.COLUMN_APP_LCD,
				BatteryUsage.COLUMN_APP_WIFI,
				BatteryUsage.COLUMN_TOTAL_MA,
				BatteryUsage.COLUMN_TOTAL_PCT
		};
		
		Cursor c = db.query(BatteryUsage.TABLE_NAME,columns,null,null,null,null,null);
		c.moveToFirst();
		while(!c.isAfterLast())
		{
			records.add(cursorToString(c));
			c.moveToNext();
		}
		close();
		return records;
	}
	private String cursorToString(Cursor c)
	{
		String record = "";
		record = record + c.getString(0);
		record = record + "::" + c.getString(1);
		record = record + "::" + c.getDouble(2);
		record = record + "::" + c.getDouble(3);
		record = record + "::" + c.getDouble(4);
		record = record + "::" + c.getDouble(5);
		record = record + "::" + c.getDouble(6);
		record = record + "::" + c.getDouble(7);
		record = record + "::" + c.getDouble(8);
		return record;
	}
	
	public void close() {
		mDbHelper.close();
	}
}
