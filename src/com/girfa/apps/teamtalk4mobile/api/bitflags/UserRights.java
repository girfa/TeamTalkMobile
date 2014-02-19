package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class UserRights extends BitFlags  {
	public static final int 
		NONE				= 0x0000, 
		GUEST_LOGIN			= 0x0001, 
		VIEW_ALL_USERS		= 0x0002,
		CHANNEL_CREATION	= 0x0004,
		CHANNEL_OPERATORS	= 0x0008,
		CHANNEL_COMMANDS	= 0x0010, 
		CLIENT_BROADCAST	= 0x0020, 
		SUBSCRIPTIONS		= 0x0040, 
		FORWARD_AUDIO		= 0x0080, 
		FORWARD_VIDEO		= 0x0100,
		DOUBLE_LOGIN		= 0x0200,
		FORWARD_DESKTOP		= 0x0400,
		STRICT_UTF8			= 0x0800;
	
	public UserRights(int value) {
		super(value);
	}
}
