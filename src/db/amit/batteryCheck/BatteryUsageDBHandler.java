package db.amit.batteryCheck;


import db.amit.batteryCheck.DBDesign.BatteryUsage;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BatteryUsageDBHandler extends SQLiteOpenHelper {
		
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "myBatteryDB";
		
		private static final String DOUBLE_TYPE = " DOUBLE";
		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		private static final String SQL_CREATE_ENTRIES =
		    "CREATE TABLE " + BatteryUsage.TABLE_NAME + " (" +
		    		BatteryUsage._ID + " INTEGER PRIMARY KEY," +
		    		BatteryUsage.COLUMN_APP_PID + TEXT_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_APP_NAME + TEXT_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_APP_CPU + DOUBLE_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_APP_LCD + DOUBLE_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_APP_WIFI + DOUBLE_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_APP_AUDIO + DOUBLE_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_APP_GPS + DOUBLE_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_TOTAL_MA + DOUBLE_TYPE + COMMA_SEP +
		    		BatteryUsage.COLUMN_TOTAL_PCT + DOUBLE_TYPE +
		    " )";

		private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + BatteryUsage.TABLE_NAME;
		public BatteryUsageDBHandler(Context context)
		{
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ENTRIES);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(SQL_DELETE_ENTRIES);
	        onCreate(db);		
		}
		
		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }
		
	}
