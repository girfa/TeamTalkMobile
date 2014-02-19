package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.List;

public class CELTCodec {
	private Integer sampleRate;
	private Integer channels;
	private Integer bitRate;
	private Integer mSecPerPacket;
	
	public CELTCodec() {}
	
	public CELTCodec(List<Integer> celt) {
		build(celt);
	}
	
	public CELTCodec build(List<Integer> celt) {
		setSampleRate(celt.get(0));
		setChannels(celt.get(1));
		setBitRate(celt.get(2));
		setMSecPerPacket(celt.get(3));
		return this;
	}
	
	@Override
	public String toString() {
		return getSampleRate() + "|"
			+ getChannels() + "|"
			+ getBitRate() + "|"
			+ getMSecPerPacket();
	}

	public CELTCodec setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
		return this;
	}
	
	public CELTCodec setChannels(Integer channels) {
		this.channels = channels;
		return this;
	}
	
	public CELTCodec setBitRate(Integer bitRate) {
		this.bitRate = bitRate;
		return this;
	}
	
	public CELTCodec setMSecPerPacket(Integer mSecPerPacket) {
		this.mSecPerPacket = mSecPerPacket;
		return this;
	}

	public Integer getSampleRate() {
		return sampleRate;
	}

	public Integer getChannels() {
		return channels;
	}

	public Integer getBitRate() {
		return bitRate;
	}

	public Integer getMSecPerPacket() {
		return mSecPerPacket;
	}
}
