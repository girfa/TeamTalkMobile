package com.girfa.apps.teamtalk4mobile.api.bitflags;


public class ClientFlags extends BitFlags {
	public static final int 
		CLOSED                    = 0x00000000,
		SNDINPUT_READY            = 0x00000001,
		SNDOUTPUT_READY           = 0x00000002,
		VIDEO_READY               = 0x00000004,
		DESKTOP_ACTIVE            = 0x00000008,
		SNDINPUT_VOICEACTIVATED   = 0x00000010,
		SNDINPUT_DENOISING        = 0x00000020,
		SNDINPUT_AGC              = 0x00000040,
		SNDOUTPUT_MUTE            = 0x00000080,
		SNDOUTPUT_AUTO3DPOSITION  = 0x00000100,
		SNDINPUT_AEC              = 0x00000200,
		SNDINOUTPUT_DUPLEX        = 0x00000400,
		TX_AUDIO                  = 0x00001000,
		TX_VIDEO                  = 0x00002000,
		MUX_AUDIOFILE             = 0x00004000,
		TX_DESKTOP                = 0x00008000,
		CONNECTING                = 0x00010000,
		CONNECTED                 = 0x00020000,
		CONNECTION                = CONNECTING | CONNECTED,
		AUTHORIZED                = 0x00040000,
		P2P_AUDIO                 = 0x00100000,
		P2P_VIDEO                 = 0x00200000,
		P2P                       = P2P_AUDIO | P2P_VIDEO;
	
	ClientFlags(int value) {
		super(value);
	}
}
