package com.girfa.apps.teamtalk4mobile.api.enumflags;

public enum BitmapFormat {
	BMP_NONE(0),
	BMP_RGB8_PALETTE(1),
	BMP_RGB16_555(2),
	BMP_RGB24(3),
	BMP_RGB32(4);

	public int value;
	
	private BitmapFormat(int value) {
		this.value = value;
	}
}
