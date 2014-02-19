package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class UserTypes extends BitFlags {
	public static final int
		NONE    = 0x00, 
		DEFAULT = 0x01,
		ADMIN   = 0x02;
	
	public UserTypes(int value) {
		super(value);
	}
}
