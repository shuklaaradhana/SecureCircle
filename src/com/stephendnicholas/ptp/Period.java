package com.stephendnicholas.ptp;

import java.util.TreeMap;

public class Period {

	private int periodNum = -1;

	private TreeMap<String, Integer> powerTotalMap;

	public Period(int periodNum) {
		this.periodNum = periodNum;

		this.powerTotalMap = new TreeMap<String, Integer>();
	}

	public void setTotalPower(String powerType, int total) {
		this.powerTotalMap.put(powerType, total);
	}

	public int getPeriodNum() {
		return periodNum;
	}

	public TreeMap<String, Integer> getTotalPowerMap() {
		return powerTotalMap;
	}
}