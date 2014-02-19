/*******************************************************************************
 * Copyright (C) 2013 - 2014, Girfa eSuite
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Author : Afrig Aminuddin <my@girfa.com>
 ******************************************************************************/
package com.girfa.apps.teamtalk4mobile.service;

import com.girfa.apps.teamtalk4mobile.api.adapter.AudioCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.CELTCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.CELTVBRCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.SpeexCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.SpeexVBRCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.enumflags.Codec;
import com.girfa.apps.teamtalk4mobile.api.jni.LibCelt;
import com.girfa.apps.teamtalk4mobile.api.jni.LibSpeex;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

@SuppressLint("InlinedApi")
public class AudioPlayer {
	public static final String TAG = AudioPlayer.class.getSimpleName();
	private static final int MONO = AudioFormat.CHANNEL_OUT_MONO;
	private static final int STEREO = AudioFormat.CHANNEL_OUT_STEREO;

	private AudioCodec audioCodec;
	private AudioThread thread = new AudioThread();
	// private Recorder recorder = new Recorder();

	private CELTCodec celt;
	private CELTVBRCodec celtVbr;
	private SpeexCodec speex;
	private SpeexVBRCodec speexVbr;
	private LibCelt libCelt;
	private LibCelt libCeltVbr;
	private LibSpeex libSpeex;
	private LibSpeex libSpeexVbr;
	
	private int channel;
	private int sampleRate;
	
	public void start(AudioCodec audioCodec) {
		this.audioCodec = audioCodec;
		// recorder.start();
	}
	
	private AudioTrack init(Codec codec) {
		switch (codec) {
			case CELT_CODEC :
				celt = audioCodec.getCelt();
				sampleRate = celt.getSampleRate();
				channel = celt.getChannels() == 1 ? 1 : 2;
				libCelt = new LibCelt();
				libCelt.initDec(celt.getSampleRate(), celt.getChannels(),
						celt.getBitRate(), false);
				break;
			case CELT_VBR_CODEC :
				celtVbr = audioCodec.getCeltVbr();
				sampleRate = celtVbr.getSampleRate();
				channel = celtVbr.getChannels() == 1 ? 1 : 2;
				libCeltVbr = new LibCelt();
				libCeltVbr.initDec(celtVbr.getSampleRate(), celtVbr.getChannels(),
						celtVbr.getBitRate(), true);
				break;
			case SPEEX_CODEC :
				speex = audioCodec.getSpeex();
				sampleRate = speex.getBandMode().frequency;
				channel = speex.isStereoPlayback() ? 2 : 1;
				libSpeex = new LibSpeex();
				libSpeex.initDec(speex.getBandMode(),
						speex.isStereoPlayback() ? 2 : 1,
						speex.getQuality(), false, 0, 0, false);
				break;
			case SPEEX_VBR_CODEC :
				speexVbr = audioCodec.getSpeexVbr();
				sampleRate = speexVbr.getBandMode().frequency;
				channel = speexVbr.isStereoPlayback() ? 2 : 1;
				libSpeexVbr = new LibSpeex();
				libSpeexVbr.initDec(speexVbr.getBandMode(),
						speexVbr.isStereoPlayback() ? 2 : 1,
						speexVbr.getQualityVbr(), true, speexVbr.getBitRate(),
						speexVbr.getMaxBitRate(), speexVbr.isDtx());
				break;
			default:
				break;
		}
		
		// recorder.setChannel(channel);
		// recorder.setSampleRate(sampleRate);
		int channelConfig = channel == 1 ? MONO : STEREO;
		int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRate,
				channelConfig, AudioFormat.ENCODING_PCM_16BIT);
		return new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT,
				bufferSizeInBytes, AudioTrack.MODE_STREAM);
	}
	
	public int write(User user, byte[] audioData) {
		if (!thread.exist(user.getId())) {
			thread.newTrack(user.getId(), init(audioCodec.getCodec()));
		}
		try {
			int id = user.getId();
			int size;
			int slice = 0;
			int length = audioData.length - 35;
			byte[] input;
			short[] output = null;
			switch (audioCodec.getCodec()) {
				case CELT_CODEC :
					size = celt.getMSecPerPacket();
					slice = length / size;
					for (int i = 0; i < slice; i++) {
						input = new byte[size];
						System.arraycopy(audioData, i * size + 13, input, 0, size);
						output = libCelt.decode(input);
						thread.write(id, output);
					}
					break;
				case CELT_VBR_CODEC :
					size = celtVbr.getMSecPerPacket();
					slice = length / size;
					for (int i = 0; i < slice; i++) {
						input = new byte[size];
						System.arraycopy(audioData, i * size + 13, input, 0, size);
						output = libCeltVbr.decode(input);
						thread.write(id, output);
					}
					break;
				case SPEEX_CODEC :
					size = length / speex.getMSecPerPacket();
					slice = length / size;
					for (int i = 0; i < slice; i++) {
						input = new byte[size];
						System.arraycopy(audioData, i * size + 13, input, 0, size);
						output = libSpeex.decode(input);
						thread.write(id, output);
					}
					break;
				case SPEEX_VBR_CODEC :
					size = length / speexVbr.getMSecPerPacket();
					slice = length / size;
					for (int i = 0; i < slice; i++) {
						input = new byte[size];
						System.arraycopy(audioData, i * size + 13, input, 0, size);
						output = libSpeexVbr.decode(input);
						thread.write(id, output);
					}
					break;
				default:
					break;
			}
			if (output == null) return 0;
			return output.length * slice;
		} catch (NullPointerException ignore) {}
		return 0;
	}

	public void stop() {
		// recorder.stop();
		thread.destroy();
		if (libCelt != null) {
			libCelt.destroyDec();
			libCelt = null;
			celt = null;
		}
		
		if (libCeltVbr != null) {
			libCeltVbr.destroyDec();
			libCeltVbr = null;
			celtVbr = null;
		}
		
		if (libSpeex != null) {
			libSpeex.destroyDec();
			libSpeex = null;
			speex = null;
		}
		
		if (libSpeexVbr != null) {
			libSpeexVbr.destroyDec();
			libSpeexVbr = null;
			speexVbr = null;
		}
	}
	
	public int getChannel() {
		return channel;
	}
	
	public int getSampleRate() {
		return sampleRate;
	}
	
	/*
	public Recorder getRecorder() {
		return recorder;
	}
	*/
}
