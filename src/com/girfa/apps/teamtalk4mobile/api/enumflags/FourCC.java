package com.girfa.apps.teamtalk4mobile.api.enumflags;

public enum FourCC {
	NONE(0),
	I420(100),
	YUY2(101),
	RGB32(102);
	
	public int value;
	
	private FourCC(int value) {
		this.value = value;
	}
}
