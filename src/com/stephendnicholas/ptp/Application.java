package com.stephendnicholas.ptp;

import java.util.HashMap;

public class Application {

	private String name;

	private String processId;

	private HashMap<String, HashMap<Integer, Integer>> powerUsageMap;

	private HashMap<String, Integer> powerTotalMap;

	public Application(String name, String processId) {
		this.name = name;
		this.processId = processId;

		this.powerUsageMap = new HashMap<String, HashMap<Integer, Integer>>();
		this.powerTotalMap = new HashMap<String, Integer>();
	}

	public void addPowerUsage(String powerTypeName, int period, int value) {

		if (!powerUsageMap.containsKey(powerTypeName)) {
			powerUsageMap.put(powerTypeName, new HashMap<Integer, Integer>());
			powerTotalMap.put(powerTypeName, 0);
		}

		powerUsageMap.get(powerTypeName).put(period, value);
		if(!powerTypeName.equalsIgnoreCase("cpu-"))
		//System.out.println("adding to total power usage var = " + powerTypeName + " Value : " + value);
		powerTotalMap.put(powerTypeName, powerTotalMap.get(powerTypeName)
				.intValue() + value);
	}

	public String getName() {
		return name;
	}

	public String getProcessId() {
		return processId;
	}

	public HashMap<String, Integer> getPowerTotalMap() {
		return powerTotalMap;
	}
	public String getPowerTotalToString(){
		String str = "Power Usage: ";
		//System.out.println("Size of map for this App is " + powerTotalMap.size() + " and var is " + powerTotalMap.toString());
		for(Integer vals : powerTotalMap.values())
			str += vals.toString() + "|";
		return str;
	}

	public HashMap<String, HashMap<Integer, Integer>> getPowerUsageMap() {
		return powerUsageMap;
	}
}