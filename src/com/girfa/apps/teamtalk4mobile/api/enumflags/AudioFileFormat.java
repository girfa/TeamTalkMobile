package com.girfa.apps.teamtalk4mobile.api.enumflags;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public enum AudioFileFormat {
	AFF_NONE(0),
	AFF_WAVE_FORMAT(2),
	AFF_MP3_64KBIT_FORMAT(3),
	AFF_MP3_128KBIT_FORMAT(4),
	AFF_MP3_256KBIT_FORMAT(5),
	AFF_MP3_16KBIT_FORMAT(6),
	AFF_MP3_32KBIT_FORMAT(7);
	
	public int value;
	
	private AudioFileFormat(int value) {
		this.value = value;
	}
	
	@SuppressLint("UseSparseArrays")
	private static final Map<Integer, AudioFileFormat> map = new HashMap<Integer, AudioFileFormat>();
	
	static {
	    for (AudioFileFormat value : AudioFileFormat.values()) {
	        map.put(value.value, value);
	    }
	}
	
	public static AudioFileFormat valueOf(int aff) {
		return map.get(aff);
	}
}
