package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class StatusModes extends BitFlags {
	public static final int
		AVAILABLE			= 0x000,
		AWAY				= 0x001,
		QUESTION			= 0x002,
		GENDER_MALE			= 0x000,
		GENDER_FEMALE		= 0x100,
		STREAM_VIDEO		= 0x200,
		STREAM_DESKTOP		= 0x400,
		STREAM_AUDIOFILE	= 0x800;
		
	public StatusModes(int value) {
		super(value);
	}
}
