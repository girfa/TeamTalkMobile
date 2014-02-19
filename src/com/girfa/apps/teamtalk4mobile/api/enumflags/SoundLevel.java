package com.girfa.apps.teamtalk4mobile.api.enumflags;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public enum SoundLevel {
	VU_MAX(0, 20),
	VU_MIN(1, 0),
	VOLUME_MAX(2, 255),
	VOLUME_MIN(3, 0),
	GAIN_MAX(4, 32000),
	GAIN_DEFAULT(5, 1000),
	GAIN_MIN(6, 0);
	
	public int value;
	public int id;
	
	private SoundLevel(int id, int value) {
		this.id = id;
		this.value = value;
	}
	
	@SuppressLint("UseSparseArrays")
	private static final Map<Integer, SoundLevel> map = new HashMap<Integer, SoundLevel>();
	
	static {
	    for (SoundLevel id : SoundLevel.values()) {
	        map.put(id.id, id);
	    }
	}
	
	public static SoundLevel idOf(int value) {
		return map.get(value);
	}
}
