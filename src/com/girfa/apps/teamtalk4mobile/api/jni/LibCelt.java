package com.girfa.apps.teamtalk4mobile.api.jni;

public class LibCelt {
	private int decoderId;
	
	
	private static native int initD(int sampleRate, int channels,
			int bitRate, boolean vbr);
	
	private static native void destroyD(int id);
	
	private static native short[] decode(int id, byte[] frame);
	
	public synchronized LibCelt initDec(int sampleRate, int channels,
			int bitRate, boolean vbr) {
		decoderId = initD(sampleRate, channels, bitRate, vbr);
		if (decoderId < 0) return null;
		return this;
	}
	
	public synchronized short[] decode(byte[] frame) {
		if (decoderId < 0 || frame == null) return null;
		return decode(decoderId, frame);
	}
	
	public synchronized void destroyDec() {
		if (decoderId < 0) return;
		destroyD(decoderId);
	}
	
	static {
		System.loadLibrary("celt-0.11.1");
	}
}
