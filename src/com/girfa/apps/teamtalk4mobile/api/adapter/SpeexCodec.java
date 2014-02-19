package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.List;

import com.girfa.apps.teamtalk4mobile.api.enumflags.BandMode;

public class SpeexCodec {
	private BandMode bandMode;
	private Integer quality;
	private Integer mSecPerPacket;
	private Boolean useJitterBuffer;
	private Boolean stereoPlayback;
	
	public SpeexCodec() {}
	
	public SpeexCodec(List<Integer> speex) {
		build(speex);
	}
	
	public SpeexCodec build(List<Integer> speex) {
		setBandMode(BandMode.valueOf(speex.get(0)));
		setQuality(speex.get(1));
		setMSecPerPacket(speex.get(2));
		setUseJitterBuffer(speex.get(3) != 0);
		setStereoPlayback(speex.get(4) != 0);
		return this;
	}
	
	@Override
	public String toString() {
		return getBandMode().value + "|" 
			+ getQuality() + "|"
			+ getMSecPerPacket() + "|"
			+ (isUseJitterBuffer() ? 1 : 0) + "|"
			+ (isStereoPlayback() ? 1 : 0);
	}

	public SpeexCodec setBandMode(BandMode bandMode) {
		this.bandMode = bandMode;
		return this;
	}
	
	public SpeexCodec setQuality(Integer quality) {
		this.quality = quality;
		return this;
	}
	
	public SpeexCodec setMSecPerPacket(Integer mSecPerPacket) {
		this.mSecPerPacket = mSecPerPacket;
		return this;
	}
	
	public SpeexCodec setUseJitterBuffer(Boolean useJitterBuffer) {
		this.useJitterBuffer = useJitterBuffer;
		return this;
	}
	
	public SpeexCodec setStereoPlayback(Boolean stereoPlayback) {
		this.stereoPlayback = stereoPlayback;
		return this;
	}

	public BandMode getBandMode() {
		return bandMode;
	}

	public Integer getQuality() {
		return quality;
	}

	public Integer getMSecPerPacket() {
		return mSecPerPacket;
	}

	public Boolean isUseJitterBuffer() {
		return useJitterBuffer;
	}

	public Boolean isStereoPlayback() {
		return stereoPlayback;
	}
}
