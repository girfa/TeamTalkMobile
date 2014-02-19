package com.girfa.apps.teamtalk4mobile.api.adapter;

import com.girfa.apps.teamtalk4mobile.api.enumflags.SoundSystem;

public class SoundDevice {
	private Integer deviceId;
	private SoundSystem soundSystem;
	private String deviceName;
	private String deviceCode;
	private Integer waveDeviceId;
	private Boolean supports3d;
	private Integer maxInputChannels;
	private Integer maxOutputChannels;
	private Integer supportedSampleRates;
	private Integer defaultSampleRate;
	
	public SoundDevice setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
		return this;
	}
	
	public SoundDevice setSoundSystem(SoundSystem soundSystem) {
		this.soundSystem = soundSystem;
		return this;
	}
	
	public SoundDevice setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		return this;
	}
	
	public SoundDevice setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
		return this;
	}
	
	public SoundDevice setWaveDeviceId(Integer waveDeviceId) {
		this.waveDeviceId = waveDeviceId;
		return this;
	}
	
	public SoundDevice setSupports3d(Boolean supports3d) {
		this.supports3d = supports3d;
		return this;
	}
	
	public SoundDevice setMaxInputChannels(Integer maxInputChannels) {
		this.maxInputChannels = maxInputChannels;
		return this;
	}
	
	public SoundDevice setMaxOutputChannels(Integer maxOutputChannels) {
		this.maxOutputChannels = maxOutputChannels;
		return this;
	}
	
	public SoundDevice setSupportedSampleRates(Integer supportedSampleRates) {
		this.supportedSampleRates = supportedSampleRates;
		return this;
	}
	
	public SoundDevice setDefaultSampleRate(Integer defaultSampleRate) {
		this.defaultSampleRate = defaultSampleRate;
		return this;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public SoundSystem getSoundSystem() {
		return soundSystem;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public Integer getWaveDeviceId() {
		return waveDeviceId;
	}

	public Boolean isSupports3d() {
		return supports3d;
	}

	public Integer getMaxInputChannels() {
		return maxInputChannels;
	}

	public Integer getMaxOutputChannels() {
		return maxOutputChannels;
	}

	public Integer getSupportedSampleRates() {
		return supportedSampleRates;
	}

	public Integer getDefaultSampleRate() {
		return defaultSampleRate;
	}
}
