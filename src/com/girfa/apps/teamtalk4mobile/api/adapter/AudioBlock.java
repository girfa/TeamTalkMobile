package com.girfa.apps.teamtalk4mobile.api.adapter;

public class AudioBlock {
	private Integer sampleRate;
	private Integer channels;
	private byte[] rawAudio;
	private Integer samples;
	private Long sampleIndex;
	
	public AudioBlock setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
		return this;
	}
	
	public AudioBlock setChannels(Integer channels) {
		this.channels = channels;
		return this;
	}
	
	public AudioBlock setRawAudio(byte[] rawAudio) {
		this.rawAudio = rawAudio;
		return this;
	}
	
	public AudioBlock setSamples(Integer samples) {
		this.samples = samples;
		return this;
	}
	
	public AudioBlock setSampleIndex(Long sampleIndex) {
		this.sampleIndex = sampleIndex;
		return this;
	}

	public Integer getSampleRate() {
		return sampleRate;
	}

	public Integer getChannels() {
		return channels;
	}

	public byte[] getRawAudio() {
		return rawAudio;
	}

	public Integer getSamples() {
		return samples;
	}

	public Long getSampleIndex() {
		return sampleIndex;
	}
}
