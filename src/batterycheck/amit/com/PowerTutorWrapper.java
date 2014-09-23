/** Project under Professor Chandrasekhar at San Jose State University
 * 	Project : Malware Detection in SmartPhone
 * 	author: Amit Agrawal
 * 	Date: 23rd Apr 2014
 * 	This class provides the interface to call various services in PowerTutor Code, developed at University of Michigan
 */
package batteryCheck.amit.com;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import com.example.securecircle.StartActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import db.amit.batteryCheck.LogAnalysisService;
import db.amit.batteryCheck.LogAnalysisService.LogAnalysisBinder;
import edu.umich.PowerTutor.service.ICounterService;
//import edu.umich.PowerTutor.service.UMLoggerService;
import edu.umich.PowerTutor.service.UMLoggerService;

public class PowerTutorWrapper {
	// private static final String TAG = "UMLogger";
	public static final String CURRENT_VERSION = "1.2"; // Don't change this...
	public static final String SERVER_IP = "spidermonkey.eecs.umich.edu";
	public static final int SERVER_PORT = 5204;
	private Intent serviceIntent;
	private Intent analyzeServiceIntent;
	private Context context;
	private ICounterService counterService;
	private LogAnalysisService logAnalysisService;
	private CounterServiceConnection conn;
	private LogAnalysisServiceConnection analysisConn;

	public PowerTutorWrapper(Context pkgContext) {
		context = pkgContext;
		onCreate();
	}

	public void onCreate() {
		serviceIntent = new Intent(context, UMLoggerService.class);
		analyzeServiceIntent = new Intent(context, LogAnalysisService.class);
		conn = new CounterServiceConnection();
		analysisConn = new LogAnalysisServiceConnection();
	}

	public void onResume() {
		context.bindService(serviceIntent, conn, 0);
		context.bindService(analyzeServiceIntent, analysisConn, 0);
	}

	public void onPause() {
		context.unbindService(conn);
		context.unbindService(analysisConn);
	}

	public void analyzeLogs(File file) {
		// analyzeServiceIntent.putExtra("Context", context);
		Log.d("PowerTutor", "Log Analyze Service Starting");
		// context.stopService(analyzeServiceIntent);
		//context.bindService(analyzeServiceIntent, analysisConn, 0);
		analyzeServiceIntent.putExtra("FILE", file.toString());
		context.startService(analyzeServiceIntent);
		/*if (logAnalysisService != null)
			logAnalysisService.analyseLogs("");
		else
			Log.d("PowerTutor", "log service is null");*/
	}

	public File saveLog() {
		final File filename = new File(
				Environment.getExternalStorageDirectory(), "PowerTrace"
						+ System.currentTimeMillis() + ".log");
		new Thread() {
			public void start() {
				File writeFile = filename;/*new File(
						Environment.getExternalStorageDirectory(), "PowerTrace"
								+ System.currentTimeMillis() + ".log");*/
				File dirFiles = context.getFilesDir();
				for (String strFile : dirFiles.list())
				{
				 Log.i("PowerTutor", "internal storage: " + strFile);
				}
				Log.d("PowerTutor", writeFile.toString());
				// filename = writeFile;
				InflaterInputStream logIn =null;
				BufferedOutputStream logOut = null;
				try {
					Log.d("PowerTutor", "In save Log");
					logIn = new InflaterInputStream(
							context.openFileInput("PowerTrace.log"));
					logOut = new BufferedOutputStream(
							new FileOutputStream(writeFile));
					Log.d("PowerTutor", "In save Log 1");
					if(logIn != null)
						Log.i("PowerTutor", "Login is not null" + logIn.available());
					byte[] buffer = new byte[20480];
					Log.d("PowerTutor", "In save Log 1.5");
					for (int ln = logIn.read(buffer); ln != -1; ln = logIn
							.read(buffer)) {
						Log.d("PowerTutor", "In save Log 2");
						Log.d("PowerTutor", "Data Length is: " + ln);
						// System.out.println("Data Lengtg is : " + ln);
						logOut.write(buffer, 0, ln);
					}
					
					Toast.makeText(context,
							"Wrote log to " + writeFile.getAbsolutePath(),
							Toast.LENGTH_SHORT).show();
					Log.d("PowerTutor", "Save Logs Succeed");
					return;
				} catch (Exception e) {
					Toast.makeText(context,
							"Wrote log to " + writeFile.getAbsolutePath(),
							Toast.LENGTH_SHORT).show();
					Log.d("PowerTutor","Save log: "+ e.getMessage() + e.getStackTrace().toString());
					e.printStackTrace();
					return;
				} 
				finally{
					try {
						logIn.close();
						logOut.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				/*catch (IOException e) {
					Log.d("PowerTutor","save log: "+ e.getMessage());
				}
				Toast.makeText(context, "Failed to write log to sdcard",
						Toast.LENGTH_SHORT).show();
						*/
				
			}
		}.start();
		// analyzeLogs();
		return filename;
	}

	public boolean startBatteryCheckService() {
		Toast.makeText(context, "counter service starting", Toast.LENGTH_SHORT)
				.show();
		Log.d("PowerTutor", "Counter service in startBatteryCheckService");
		context.startService(serviceIntent);
		// can even return serviceIntent if need to store it for binding
		return true;
	}

	public boolean stopBatteryCheckService() {
		context.stopService(serviceIntent);
		Log.d("PowerTutor", "Counter service stopped");

		return true;
	}

	public boolean getCounterServiceStatus() {
		if (counterService != null)
			return true;
		return false;
	}

	public class CounterServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className,
				IBinder boundService) {
			Toast.makeText(context, "Counter Service Started",
					Toast.LENGTH_SHORT).show();
			Log.d("PowerTutor", "Counter Service Started");
			counterService = ICounterService.Stub
					.asInterface((IBinder) boundService);
		}

		public void onServiceDisconnected(ComponentName className) {
			counterService = null;
			context.unbindService(conn);
			context.bindService(serviceIntent, conn, 0);
			Log.d("PowerTutor", "Counter Service Stopped");
			Toast.makeText(context, "Profiler stopped", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public class LogAnalysisServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			//Toast.makeText(context, "Log Analysis Started", Toast.LENGTH_SHORT)
				//	.show();
			Log.d("PowerTutor", "Log Analysis Service Started");
			LogAnalysisBinder mybinder = (LogAnalysisBinder) arg1;
			logAnalysisService = mybinder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			context.unbindService(analysisConn);
			context.bindService(analyzeServiceIntent, analysisConn, 0);
			Log.d("PowerTutor", "Log Analysis Service Stopped");
			//Toast.makeText(context, "Log Analysis Stopped", Toast.LENGTH_SHORT)
				//	.show();
		}
	}
}
