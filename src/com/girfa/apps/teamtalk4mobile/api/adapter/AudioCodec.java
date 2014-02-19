package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.List;

import com.girfa.apps.teamtalk4mobile.api.enumflags.Codec;

public class AudioCodec {
	private Codec codec; 
	private SpeexCodec speex;
	private CELTCodec celt;
	private SpeexVBRCodec speexVbr;
	private CELTVBRCodec celtVbr;
	
	public AudioCodec() {}
	
	public AudioCodec(List<Integer> audio) {
		build(audio);
	}
	
	public AudioCodec build(List<Integer> audio) {
		this.codec = Codec.valueOf(audio.get(0));
		audio.remove(0);
		switch (codec) {
			case SPEEX_CODEC :
				setSpeex(new SpeexCodec(audio));
				break;
			case CELT_CODEC :
				setCelt(new CELTCodec(audio));
				break;
			case SPEEX_VBR_CODEC : 
				setSpeexVbr(new SpeexVBRCodec(audio));
				break;
			case CELT_VBR_CODEC :
				setCeltVbr(new CELTVBRCodec(audio));
				break;
			default:
				break;
		}
		return this;
	}
	
	@Override
	public String toString() {
		String res = null;
		switch (codec) {
			case SPEEX_CODEC :
				res = getSpeex().toString();
				break;
			case CELT_CODEC :
				res = getCelt().toString();
				break;
			case SPEEX_VBR_CODEC : 
				res = getSpeexVbr().toString();
				break;
			case CELT_VBR_CODEC :
				res = getCeltVbr().toString();
				break;
			default:
				break;
		}
		return res == null ? 
			codec.value + "" : 
			codec.value + "|" + res;
	}

	public AudioCodec setCodec(Codec codec) {
		this.codec = codec;
		return this;
	}
	
	public AudioCodec setSpeex(SpeexCodec speex) {
		this.speex = speex;
		return this;
	}
	
	public AudioCodec setCelt(CELTCodec celt) {
		this.celt = celt;
		return this;
	}
	
	public AudioCodec setSpeexVbr(SpeexVBRCodec speexVbr) {
		this.speexVbr = speexVbr;
		return this;
	}
	
	public AudioCodec setCeltVbr(CELTVBRCodec celtVbr) {
		this.celtVbr = celtVbr;
		return this;
	}

	public Codec getCodec() {
		return codec;
	}

	public SpeexCodec getSpeex() {
		return speex;
	}

	public CELTCodec getCelt() {
		return celt;
	}

	public SpeexVBRCodec getSpeexVbr() {
		return speexVbr;
	}

	public CELTVBRCodec getCeltVbr() {
		return celtVbr;
	}
}
