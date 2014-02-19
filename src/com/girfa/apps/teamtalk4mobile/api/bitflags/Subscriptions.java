package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class Subscriptions extends BitFlags {
	public static final int
		NONE                    = 0x0000,
		USER_MSG                = 0x0001,
		CHANNEL_MSG             = 0x0002,
		BROADCAST_MSG           = 0x0004,
		AUDIO                   = 0x0008,
		VIDEO                   = 0x0010,
		DESKTOP                 = 0x0020,
		CUSTOM_MSG              = 0x0040,
		INTERCEPT_USER_MSG      = 0x0100,
		INTERCEPT_CHANNEL_MSG   = 0x0200,
		INTERCEPT_BROADCAST_MSG	= 0x0400,
		INTERCEPT_AUDIO         = 0x0800,
		INTERCEPT_VIDEO         = 0x1000,
		INTERCEPT_DESKTOP       = 0x2000,
		INTERCEPT_CUSTOM_MSG    = 0x4000;
	
	public Subscriptions(int value) {
		super(value);
	}
}
