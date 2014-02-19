package com.girfa.apps.teamtalk4mobile.api.enumflags;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

public enum Codec {
	NO_CODEC(0),
	SPEEX_CODEC(1),
	CELT_0_5_2_OBSOLETE_CODEC(2),
	THEORA_CODEC(3),
	SPEEX_VBR_CODEC(4),
	CELT_CODEC(5),
	CELT_VBR_CODEC(6);

	public int value;
	
	private Codec(int value) {
		this.value = value;
	}
	
	@SuppressLint("UseSparseArrays")
	private static final Map<Integer, Codec> map = new HashMap<Integer, Codec>();
	
	static {
	    for (Codec value : Codec.values()) {
	        map.put(value.value, value);
	    }
	}
	
	public static Codec valueOf(int value) {
		return map.get(value);
	}
}
