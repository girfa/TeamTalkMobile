package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class TransmitTypes extends BitFlags {
	public static final int
		NONE  = 0x0,
	    AUDIO = 0x1,
	    VIDEO = 0x2;
	
	TransmitTypes(int value) {
		super(value);
	}
}
