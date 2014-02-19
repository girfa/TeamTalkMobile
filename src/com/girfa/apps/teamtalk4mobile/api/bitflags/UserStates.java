package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class UserStates extends BitFlags {
	public static final int
		NONE          = 0x00,
		TALKING       = 0x01,
		MUTE          = 0x02,
		P2P_CONNECTED = 0x04,
		DESKTOP       = 0x08,
		VIDEO         = 0x10;
	
	public UserStates(int value) {
		super(value);
	}
}
