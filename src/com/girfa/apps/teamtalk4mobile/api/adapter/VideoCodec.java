package com.girfa.apps.teamtalk4mobile.api.adapter;

import com.girfa.apps.teamtalk4mobile.api.enumflags.Codec;

public class VideoCodec {
	private Codec codec;
	private TheoraCodec theora;
	
	public VideoCodec setCodec(Codec codec) {
		this.codec = codec;
		return this;
	}
	
	public VideoCodec setTheora(TheoraCodec theora) {
		this.theora = theora;
		return this;
	}

	public Codec getCodec() {
		return codec;
	}

	public TheoraCodec getTheora() {
		return theora;
	}
}
