package com.example.securecircle;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScanInfoActivity extends Activity {

	String packageName;
	String appName;
	File apkObj_in;
	File apkObj_out;
	private static final String TAG = "ScanAPKFile";
	String fileName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_info);
		
		Bundle bundle = getIntent().getExtras();
		String url = bundle.getString("url");
		packageName = bundle.getString("packageName");
		appName = bundle.getString("appName");
		//fileName = appName + ".apk";
		fileName = packageName + ".apk";

		ScanAPKFile task = new ScanAPKFile(); 
        task.execute(new String[] { url });
        
        if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}		

	}
	
	void displayResult(String result){
		TextView tvAppName = (TextView) findViewById(R.id.tvAppName);         
        if (tvAppName == null)
        	return;         
        tvAppName.setText(appName);
        TextView tvRisk = (TextView) findViewById(R.id.tvRisk);         
        if (tvRisk == null)
        	return;
        //check for fuzzy risk value
/*        int value = Integer.parseInt("result");
        if(value>5)
        	tvRisk.setTextColor(getResources().getColor(R.color.red));*/
        tvRisk.setText(result);
		
	}
	
    void downloadAPKFile(String urlStr) {
    	int count;
    	try{
    		//check external directory
    		File sdCard = Environment.getExternalStorageDirectory();
    		File root = new File (sdCard.getAbsolutePath() + "/myLocation/");
    		//Log.i("ScanAPKFile", root.getName());
    		if (!root.exists()) {
    			root.mkdirs();
    		}
    		apkObj_in = new File(urlStr);

    		apkObj_out = new File(root, fileName);
    		try {
    			InputStream in = new FileInputStream(apkObj_in);
    			OutputStream out = new FileOutputStream(apkObj_out);
    			byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0){
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.i(TAG,"APK File copied.");
    		} 
    		catch(Exception e){
    			Log.i(TAG,"Cannot download " + fileName);
    			Log.i(TAG, e.getMessage());    			
    		}
    		
    	} catch (Exception e) {
    		Log.i(TAG, e.getMessage());
    	}
    	//checkFileExistence();
    	
    	
    }
    
    void checkFileExistence(){
    	//check directory
		File sdCard = Environment.getExternalStorageDirectory();
		File root = new File (sdCard.getAbsolutePath() + "/myLocation");
		File lsFiles[] = root.listFiles();
		if(lsFiles == null)
			Log.i(TAG, "No files in sd");
		else{
			
			for(int i=0; i<=lsFiles.length; i++ )
				Log.i(TAG, lsFiles[i].getName());
		}
    	
    }
    
	private class ScanAPKFile extends AsyncTask<String, Void, String> {
		
		//private static final String TAG = "ScanAPKFile";
	 
	    private ProgressDialog processDlg = null;
	 
        @Override
        protected void onPreExecute() {
        	Log.i(TAG, "onPreExecute");
            processDlg = createProgressDialog(ScanInfoActivity.this, getString(R.string.scanning));	 
            super.onPreExecute(); 
        }
	 
        @Override
        protected String doInBackground(String... urls) {
 
        	Log.i(TAG, "doInBackground");
        	String fileLocation = urls[0];
        	String responseStr;
        	try{
        		downloadAPKFile(fileLocation);
        	} 
        	catch(Exception e)
        	{
        		Log.i(TAG, e.getMessage());
        		return null;
        	}
    		
            try{
            	String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024; 
                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    			Log.i("PowerTutor","Start time: "+mydate);
            	// open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myLocation/" 
                    	+ fileName);
                URL url1 = new URL("http://54.215.220.42:8080/androlyze");
                
                // Open a HTTP  connection to  the URL
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                
                // Allow Inputs & Outputs
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Content-Disposition", "upload");
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\""
                        + fileName + "\"" + lineEnd);
                
                Log.i(TAG,"Content-Disposition: form-data; name=\"upload\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

                while (bytesRead > 0) {
                	dos.write(buffer, 0, bufferSize);
                	bytesAvailable = fileInputStream.available();
                	bufferSize = Math.min(bytesAvailable, maxBufferSize);
                	bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                
                fileInputStream.close();
                dos.flush();
                dos.close();
                Log.i(TAG, fileName + " uploaded successfully for scanning!");

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                Log.i(TAG, fileName + " - code: " + serverResponseCode);

               // String serverResponseMessage = conn.getResponseMessage();
                
              //Get Response	
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer(); 
                while((line = rd.readLine()) != null) {
                  response.append(line);
                  response.append('\r');
                }
                rd.close();

                JSONObject jObject  = new JSONObject(response.toString()); // json
                responseStr = jObject.getString("Malware");  

                Log.i(TAG, "Response - "+ responseStr);
                
                
            } catch(Exception e)
            {
            	Log.i(TAG, fileName + " cannot be uploaded for scanning!");
            	Log.i(TAG,e.getMessage());
            	return null;
            }           

            return responseStr;

        }
	 
        @Override
        protected void onPostExecute(String response) {
        	Log.i(TAG, "onPostExecute");
        	if(response == null )
        		response = "Could not scan for malware";
        	else{
	        	if( response.equals("null"))
	        		response = "No malware reported!!";
	        	else
	        		response = "Caution - " + response + " malware found!";
	        }
            	 
            processDlg.dismiss();
            displayResult(response);
 
        }
	}
	
    private ProgressDialog createProgressDialog(final Context context, final String message) {

    	final ProgressDialog progressDlg = new ProgressDialog(context); 
        progressDlg.setMessage(message);
        progressDlg.setProgressDrawable(getWallpaper());
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setCancelable(false);
        progressDlg.show();	 
        return progressDlg;
    }
	
	public void onClickDone(View vw){		
		finish();		
	}
	
	public void onClickRiskCheck(View vw){
		scanForRisk();
	}
	
	void scanForRisk(){
		CheckAPKRisk task = new CheckAPKRisk(); 
        task.execute();
	}
	
	private class CheckAPKRisk extends AsyncTask<String, Void, String> {
		
		//private static final String TAG = "ScanAPKFile";
	 
	    private ProgressDialog processDlg = null;
	 
        @Override
        protected void onPreExecute() {
        	Log.i(TAG, "onPreExecute");
            processDlg = createProgressDialog(ScanInfoActivity.this, getString(R.string.scanning));	 
            super.onPreExecute(); 
        }
	 
        @Override
        protected String doInBackground(String... urls) {
 
        	Log.i(TAG, "CheckAPKRisk - doInBackground");
        	String responseStr;
            
            try{
                URL url1 = new URL("http://54.215.220.42:8080/androrisk");
                
                // Open a HTTP  connection to  the URL
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                
                // Allow Inputs & Outputs
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "application/json");
                
                JSONObject jObj = new JSONObject();
                jObj.put("name", fileName);
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
                //dos.writeBytes(URLEncoder.encode(jObj.toString(), "UTF-8"));
                dos.writeBytes("{\"name\":\"" + fileName + "\"}");
                Log.i(TAG, "JSON - " + "{\"name\":\"" + fileName + "\"}");
                dos.flush();
                dos.close();
                //Log.i(TAG, fileName + " uploaded successfully for scanning!");

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                Log.i(TAG, fileName + " - code: " + serverResponseCode);

               // String serverResponseMessage = conn.getResponseMessage();
                
              //Get Response	
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer(); 
                while((line = rd.readLine()) != null) {
                  response.append(line);
                  response.append('\r');
                }
                rd.close();

                JSONObject jObject  = new JSONObject(response.toString()); // json
                responseStr = jObject.getString("Risk Value");  
                Log.i(TAG, "Risk Value - "+ response.toString());
                Log.i(TAG, "Risk Value - "+ responseStr);
                
                
            } catch(Exception e)
            {
            	Log.i(TAG, fileName + " cannot be checked for risk value!");
            	Log.i(TAG,e.getMessage());
            	return null;
            }           

            return responseStr;

        }
	 
        @Override
        protected void onPostExecute(String response) {
        	Log.i(TAG, "onPostExecute");
        	Log.i(TAG, "response - " + response);
        	if(response == null )
        		response = "Could not check for risk value";
        	else{
	        	if( response.equals("null"))
	        		response = "No Risk reported!";
	        	else{	        		
	        		response = "Number of Dangerous permissions: " + response ;
	        	}
        	}
        	processDlg.dismiss();
        	Log.i(TAG, "displaying... ");
            displayRisk(response);	 
            
 
        }
	}
	
	void displayRisk(String result){
		TextView tvRiskValue = (TextView) findViewById(R.id.tvRiskValue);         
        if (tvRiskValue == null)
        {
        	Log.i(TAG, "find Text NULL , result:  "+result );
        	return;
        }
        Log.i(TAG, "result:  "+result );
        //check for fuzzy risk value
        /*Float value = Float.parseFloat(result);
        if(value > 45)
        	tvRiskValue.setTextColor(getResources().getColor(R.color.red));*/
        tvRiskValue.setText(result);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan_info, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_scan_info,
					container, false);
			return rootView;
		}
	}

}
