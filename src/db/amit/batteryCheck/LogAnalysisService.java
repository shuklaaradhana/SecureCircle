package db.amit.batteryCheck;
import java.util.Calendar;

import com.example.securecircle.R;
import com.stephendnicholas.ptp.Main;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LogAnalysisService extends Service{
	
	private static final String TAG = "LogAnalysisService";
	private final IBinder binder = new LogAnalysisBinder();
	//private Intent currIntent = null;
	private Context context;
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("PowerTutor", "In Log Analysis Service onBind..");
		//this.currIntent = intent;
	    return binder;
	}
		  
	@Override
	public void onCreate() {
		context = getApplicationContext();
	
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
	    super.onStart(intent, startId);
	    Log.d("PowerTutor", "In Log Analysis Service Start..");
	    
	    AnalyzeLog analyzeLog = new AnalyzeLog();
	    analyzeLog.execute(intent.getStringExtra("FILE"));
	}
	

/*	public void analyseLogs(String file) {
		Log.d("PowerTutor", "In Log Analysis analyseLogs");
	    AnalyzeLog analyzeLog = new AnalyzeLog();
	    analyzeLog.execute("test");
	}*/
	
	public class LogAnalysisBinder extends Binder {
		public LogAnalysisService getService() {
            return LogAnalysisService.this;
        }
    }
	
	
	class AnalyzeLog extends AsyncTask<String,String,String>
    {
		protected void onPreExecute()
    	{   super.onPreExecute();
    		//TextView viewQuote = (TextView) findViewById(R.id.view_quote);
    		//viewQuote.setText("Getting the Result.. Please wait!!");
    	} 
		@Override
		protected String doInBackground(String... file) {
			Log.d("PowerTutor", "Log Analyze Service executing in background");
			String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			Log.i("PowerTutor","Start time: "+mydate);
			Main m = new Main(context);
	    	m.analyzeLogs(file[0]);
	    	mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	    	Log.i("PowerTutor","Stop time: "+mydate);
			return "success";
		}
    }
}
