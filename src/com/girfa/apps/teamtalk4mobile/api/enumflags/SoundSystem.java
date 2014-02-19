package com.girfa.apps.teamtalk4mobile.api.enumflags;

public enum SoundSystem {
	SOUNDSYSTEM_NONE(0),
	SOUNDSYSTEM_WINMM(1),
	SOUNDSYSTEM_DSOUND(2),
	SOUNDSYSTEM_ALSA(3),
	SOUNDSYSTEM_COREAUDIO(4),
	SOUNDSYSTEM_WASAPI(5);
	public int value;
	
	private SoundSystem(int value) {
		this.value = value;
	}
}
