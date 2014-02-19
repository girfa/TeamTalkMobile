package com.girfa.apps.teamtalk4mobile.api.jni;

import com.girfa.apps.teamtalk4mobile.api.enumflags.BandMode;

public class LibSpeex {
	private int id;
	
	private static native int initD(int bandMode, int channels, int quality,
			boolean vbr, int bitRate, int maxBitRate, boolean dtx);
	
	private static native void destroyD(int id);
	
	private static native short[] decode(int id, byte[] frame);
	
	public synchronized LibSpeex initDec(BandMode bandMode, int channels, int quality,
			boolean vbr, int bitRate, int maxBitRate, boolean dtx) {
		id = initD(bandMode.value, channels, quality, vbr, bitRate, maxBitRate, dtx);
		if (id < 0) return null;
		return this;
	}
	
	public synchronized short[] decode(byte[] frame) {
		if (id < 0 || frame == null) return null;
		return decode(id, frame);
	}
	
	public synchronized void destroyDec() {
		if (id < 0) return;
		destroyD(id);
	}
	
	static {
		System.loadLibrary("speex-1.2rc1");
	}
}
