package db.amit.batteryCheck;

import android.provider.BaseColumns;
public class DBDesign {
	public DBDesign()
	{
		
	}
	
	public static abstract class BatteryUsage implements BaseColumns {
		public static final String TABLE_NAME = "BatteryUsage";
        public static final String COLUMN_APP_NAME = "AppName";
        public static final String COLUMN_APP_PID = "AppPid";
        public static final String COLUMN_APP_CPU = "AppCPU";
        public static final String COLUMN_APP_LCD = "AppLCD";
        public static final String COLUMN_APP_WIFI = "AppWifi";
        public static final String COLUMN_APP_AUDIO = "AppAudio";
        public static final String COLUMN_APP_GPS = "AppGPS";
        public static final String COLUMN_TOTAL_MA = "AppMa";
        public static final String COLUMN_TOTAL_PCT = "AppTotal";
	}
}
