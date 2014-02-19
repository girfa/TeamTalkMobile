package com.girfa.apps.teamtalk4mobile.api.adapter;

public class TheoraCodec {
	private Integer targetBitRate; 
	private Integer quality;
	
	public TheoraCodec setTargetBitRate(Integer targetBitRate) {
		this.targetBitRate = targetBitRate;
		return this;
	}
	
	public TheoraCodec setQuality(Integer quality) {
		this.quality = quality;
		return this;
	}

	public Integer getTargetBitRate() {
		return targetBitRate;
	}

	public Integer getQuality() {
		return quality;
	}
}
