package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class ChannelTypes extends BitFlags {
	public static final int
		DEFAULT             = 0x0000,
		STATIC              = 0x0001,
		SOLO_TRANSMIT       = 0x0002,
		ECHO                = 0x0004,
		ECHO_AUDIO          = 0x0004,
		CLASSROOM           = 0x0008,
		ECHO_VIDEO          = 0x0010,
		ECHO_DESKTOP        = 0x0020,
		OPERATOR_RECVONLY   = 0x0040;
	
	public ChannelTypes(int value) {
		super(value);
	}
}
