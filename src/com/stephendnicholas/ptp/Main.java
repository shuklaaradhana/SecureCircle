package com.stephendnicholas.ptp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import db.amit.batteryCheck.BatteryUsageDB;

public class Main {

	private static final String _3G = "3G-";
	private static final String AUDIO = "Audio-";
	private static final String CPU = "CPU-";
	private static final String GPS = "GPS-";
	private static final String OLED = "LCD-";
	private static final String WIFI = "Wifi-";

	private static final String[] VARS = new String[] { _3G, AUDIO, CPU, GPS,
			OLED, WIFI };

	private static final String ASSOCIATE = "associate";
	private static final String BEGIN = "begin";
	private static final String TIME = "time";
	//private Context context;
	BatteryUsageDB db = null;

	public Main(Context context) {
		//this.context = context;
		db = new BatteryUsageDB(context);
	}

	public void analyzeLogs(String file) {
		Log.d("PowerTutor", "Log analysis service in analyze logs :" + file);
		//String fileIn = "PT.log";
		//String fileOut = null;
		//String processIdOfInterest = null;
		double batteryVoltage = 3.7;
		double batteryCapacity = 1400;
		int avgOverXPeriods = 1;

		Log.d("PowerTutor","Started...");

		//String startTime = "Not found";

		HashMap<String, Application> apps = new HashMap<String, Application>();
		Application appOfInterest = null;

		int currentPeriodNum = 0;

		try {
			File readFile = new File(file);
			/*
			 * Environment.getExternalStorageDirectory(),
			 * "PowerTrace1398460278745.log");
			 */
			//fileOut = readFile + ".csv";
			System.out.println("File: " + readFile);
			FileInputStream fstream = new FileInputStream(readFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String str;
			while ((str = br.readLine()) != null) {
				int i = 0;
				boolean foundMatch = false;

				while (!foundMatch && i < VARS.length) {
					if (str.startsWith(VARS[i])) {
						String[] split = str.split(" ");
						String processId = split[0].substring(VARS[i].length());
						// Begin by Amit
						if (apps.containsKey(processId)) {
							// System.out.println("Sending data for app : "
							// +VARS[i]);
							Application app = apps.remove(processId);
							if (app != null) {
								app.addPowerUsage(VARS[i], currentPeriodNum,
										Integer.parseInt(split[1]));
								apps.put(processId, app);
							}
						}
						// End by Amit
					}
					i++;
				}

				if (!foundMatch) {
					if (str.startsWith(BEGIN)) {
						String[] split = str.split(" ");
						currentPeriodNum = Integer.parseInt(split[1]);

					} else if (str.startsWith(ASSOCIATE)) {
						String[] split = str.split(" ");
						// Begin by Amit
						if (!apps.containsKey(split[1]))
							apps.put(split[1], new Application(split[2],
									split[1]));
						// End by Amit
					} else if (str.startsWith(TIME)) {
						//String[] split = str.split(" ");

						//startTime = split[1];
					}
				}
			}

			in.close();
		} catch (IOException e) {
			Log.d("PowerTutor","Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		/*
		 * Output
		 */

		try {
			// Begin by Amit
			if (!apps.isEmpty())
				db.deleteAllRows();
			for (Application app : apps.values()) {
				appOfInterest = app;
				if (appOfInterest == null) {
				} else {
					String t_pid = null;
					String t_name = null;
					double t_cpu = 0;
					double t_lcd = 0;
					double t_gps = 0;
					double t_wifi = 0;
					double t_audio = 0;
					double t_ma = 0;
					double t_total = 0;
					t_name = appOfInterest.getName();
					t_pid = appOfInterest.getProcessId();

					/*
					 * Output data
					 */

					HashMap<String, HashMap<Integer, Integer>> powerUsageMap = appOfInterest
							.getPowerUsageMap();
					avgOverXPeriods = currentPeriodNum;
					int numOfAveragePeriods = (int) Math.floor(currentPeriodNum
							/ avgOverXPeriods);

					// For every average period row
					// TODO change to size = size of poweUsageMap, take avg
					// accordingly
					for (int i = 0; i < numOfAveragePeriods; i++) {
						double total = 0;
						for (String var : VARS) {
							double varTotal = 0;
							int startRow = i * avgOverXPeriods;
							int endRow = startRow + avgOverXPeriods;
							for (int j = startRow; j < endRow; j++) {
								HashMap<Integer, Integer> varMap = powerUsageMap
										.get(var);

								if (varMap != null && varMap.containsKey(j)) {
									varTotal += varMap.get(j);
								}
							}

							varTotal = varTotal / (double) avgOverXPeriods;
							// varTotal = varTotal / (double)
							// powerUsageMap.size();
							if (var.equalsIgnoreCase("CPU-"))
								t_cpu = varTotal;
							else if (var.equalsIgnoreCase("LCD-"))
								t_lcd = varTotal;
							else if (var.equalsIgnoreCase("GPS-"))
								t_gps = varTotal;
							else if (var.equalsIgnoreCase("Audio-"))
								t_audio = varTotal;
							else if (var.equalsIgnoreCase("Wifi-"))
								t_wifi = varTotal;
							total += varTotal;
						}

						double totalMa = total / batteryVoltage;
						// Total mA
						t_ma = totalMa;
						// Total Battery %age
						t_total = (totalMa / batteryCapacity) * 100;
						// if(t_total > 1)
						db.insertRow(t_pid, t_name, t_cpu, t_lcd, t_gps,
					 			t_wifi, t_audio, t_ma, t_total);
					}
				}
			}
			// End by Amit

		} catch (Exception e) {
			Log.d("PowerTutor","Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		List<String> result = db.getAllRows();
		for (int i = 0; i < result.size(); i++)
			Log.d("PowerTutor",result.get(i));
		Log.d("PowerTutor","...finished.");
	}
}
