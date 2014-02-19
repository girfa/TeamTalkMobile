package com.girfa.apps.teamtalk4mobile.api.enumflags;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public enum BandMode {
	NARROW(0, 8000), WIDE(1, 16000), ULTRA_WIDE(2, 32000);
	public final int value;
	public final int frequency;
	
	private BandMode(int value, int band) {
		this.value = value;
		this.frequency = band;
	}
	
	@SuppressLint("UseSparseArrays")
	private static final Map<Integer, BandMode> map = new HashMap<Integer, BandMode>();
	
	static {
	    for (BandMode value : BandMode.values()) {
	        map.put(value.value, value);
	    }
	}
	
	public static BandMode valueOf(int value) {
		return map.get(value);
	}
}
