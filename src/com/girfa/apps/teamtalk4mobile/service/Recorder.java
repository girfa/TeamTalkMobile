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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.girfa.apps.teamtalk4mobile.TeamTalk4Mobile;
import com.girfa.apps.teamtalk4mobile.api.jni.AACEncoder;


public class Recorder {
	public static final String LOCATION = "location";
	public static final String SAVE_DIRECTORY = TeamTalk4Mobile.SAVE_DIRECTORY + "Record/";
	
	private AACEncoder encoder;
	private int channel;
	private int sampleRate;
	private String fileName;
	private File file;
	private Listener listener;
	
	public void write(short[] pcm) {
		if (encoder == null || pcm == null) return;
		ByteBuffer buffer = ByteBuffer.allocate(pcm.length * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.asShortBuffer().put(pcm);
		byte[] pcmBytes = buffer.array();
		encoder.encode(pcmBytes);
	}
	
	public void start() {
		encoder = new AACEncoder();
		File dir = new File(SAVE_DIRECTORY);
		if (!dir.exists()) dir.mkdirs();
		int len = dir.listFiles().length;
		while (true) {
			fileName = SAVE_DIRECTORY + "REC" + String.format("%05d", len) + ".m4a";
			file = new File(fileName);
			if (!file.exists()) break;
			len++;
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ignore) {}
		}
		encoder.init(channel, sampleRate, fileName);
		listener.recStarted(file);
	}
	
	public void stop() {
		encoder.uninit();
		encoder = null;
		listener.recStopped(file);
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public int getSampleRate() {
		return sampleRate;
	}
	
	public boolean isRecording() {
		return encoder != null;
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	interface Listener {
		void recStarted(File file);
		void recStopped(File file);
	}
}
