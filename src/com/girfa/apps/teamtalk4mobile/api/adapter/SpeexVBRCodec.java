package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.List;

import com.girfa.apps.teamtalk4mobile.api.enumflags.BandMode;

public class SpeexVBRCodec {
	private BandMode bandMode;
	private Integer qualityVbr;
	private Integer bitRate;
	private Integer maxBitRate;
	private Boolean dtx;
	private Integer mSecPerPacket;
	private Boolean useJitterBuffer;
	private Boolean stereoPlayback;
	
	public SpeexVBRCodec() {}
	
	public SpeexVBRCodec(List<Integer> speexVbr) {
		build(speexVbr);
	}
	
	public SpeexVBRCodec build(List<Integer> speexVbr) {
		setBandMode(BandMode.valueOf(speexVbr.get(0)));
		setQualityVbr(speexVbr.get(1));
		setBitRate(speexVbr.get(2));
		setMaxBitRate(speexVbr.get(3));
		setDtx(speexVbr.get(4) != 0);
		setMSecPerPacket(speexVbr.get(5));
		setUseJitterBuffer(speexVbr.get(6) != 0);
		setStereoPlayback(speexVbr.get(7) != 0);
		return this;
	}

	@Override
	public String toString() {
		return getBandMode().value + "|"
			+ getQualityVbr() + "|"
			+ getBitRate() + "|"
			+ getMaxBitRate() + "|"
			+ (isDtx() ? 1 : 0) + "|"
			+ getMSecPerPacket() + "|"
			+ (isUseJitterBuffer() ? 1 : 0) + "|"
			+ (isStereoPlayback() ? 1 : 0);
	}
	
	public void setBandMode(BandMode bandMode) {
		this.bandMode = bandMode;
	}
	
	public void setQualityVbr(Integer qualityVBR) {
		this.qualityVbr = qualityVBR;
	}
	
	public void setBitRate(Integer bitRate) {
		this.bitRate = bitRate;
	}
	
	public void setMaxBitRate(Integer maxBitRate) {
		this.maxBitRate = maxBitRate;
	}
	
	public void setDtx(Boolean dtx) {
		this.dtx = dtx;
	}
	
	public void setMSecPerPacket(Integer mSecPerPacket) {
		this.mSecPerPacket = mSecPerPacket;
	}
	
	public void setUseJitterBuffer(Boolean useJitterBuffer) {
		this.useJitterBuffer = useJitterBuffer;
	}
	
	public void setStereoPlayback(Boolean stereoPlayback) {
		this.stereoPlayback = stereoPlayback;
	}

	public BandMode getBandMode() {
		return bandMode;
	}

	public Integer getQualityVbr() {
		return qualityVbr;
	}

	public Integer getBitRate() {
		return bitRate;
	}

	public Integer getMaxBitRate() {
		return maxBitRate;
	}

	public Boolean isDtx() {
		return dtx;
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
