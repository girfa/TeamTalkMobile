package com.girfa.apps.teamtalk4mobile.api.enumflags;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public enum TextMessageType {
	USER(1),
	CHANNEL(2),
	BROADCAST(3),
	CUSTOM(4);

	public int value;
	
	private TextMessageType(int value) {
		this.value = value;
	}
	
	@SuppressLint("UseSparseArrays")
	private static final Map<Integer, TextMessageType> map = new HashMap<Integer, TextMessageType>();
	
	static {
	    for (TextMessageType value : TextMessageType.values()) {
	        map.put(value.value, value);
	    }
	}
	
	public static TextMessageType valueOf(int value) {
		return map.get(value);
	}
}
