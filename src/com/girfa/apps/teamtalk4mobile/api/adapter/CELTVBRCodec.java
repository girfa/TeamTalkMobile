package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.List;

public class CELTVBRCodec {
	private Integer sampleRate;
	private Integer channels;
	private Integer bitRate;
	private Integer mSecPerPacket;
	
	public CELTVBRCodec() {}
	
	public CELTVBRCodec(List<Integer> celtVbr) {
		build(celtVbr);
	}
	
	public CELTVBRCodec build(List<Integer> celtVbr) {
		setSampleRate(celtVbr.get(0));
		setChannels(celtVbr.get(1));
		setBitRate(celtVbr.get(2));
		setMSecPerPacket(celtVbr.get(3));
		return this;
	}
	
	@Override
	public String toString() {
		return getSampleRate() + "|"
			+ getChannels() + "|"
			+ getBitRate() + "|"
			+ getMSecPerPacket();
	}

	public CELTVBRCodec setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
		return this;
	}
	
	public CELTVBRCodec setChannels(Integer channels) {
		this.channels = channels;
		return this;
	}
	
	public CELTVBRCodec setBitRate(Integer bitRate) {
		this.bitRate = bitRate;
		return this;
	}
	
	public CELTVBRCodec setMSecPerPacket(Integer mSecPerPacket) {
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
