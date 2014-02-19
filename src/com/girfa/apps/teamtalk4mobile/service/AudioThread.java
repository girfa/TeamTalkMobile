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

import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

public class AudioThread {
	private SparseArray<Runner> runners = new SparseArray<Runner>();
	
	void newTrack(int channel, AudioTrack track) {
		if (track == null) return;
		Runner runner = runners.get(channel);
		if (runner == null) {
			runner = new Runner(track);
			runner.start();
			runners.append(channel, runner);
		}
	}
	
	public void write(final int channel, final short[] audioData) {
		try {
			final Runner runner = runners.get(channel);
			runner.getHandler().post(new Runnable() {
				@Override
				public void run() {
					try {
						AudioTrack track = runner.getAudioTrack();
						track.write(audioData, 0, audioData.length);
						if (track.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
							track.play();
						}
						track.flush();
					} catch (NullPointerException ignore) {}
				}
			});
		} catch (NullPointerException ignore) {}
	}
	
	public void destroy() {
		for (int i = 0; i < runners.size(); i++) {
			Runner moribund = runners.valueAt(i);
			if (moribund != null) {
				moribund.getAudioTrack().stop();
				moribund.interrupt();
			}
		}
		runners.clear();
	}
	
	public boolean exist(int channel) {
		return runners.indexOfKey(channel) >= 0;
	}
	
	class Runner extends Thread implements Runnable {
		private Handler handler;
		private AudioTrack audioTrack;
		
		public Runner(AudioTrack audioTrack) {
			this.audioTrack = audioTrack;
		}
		
		@Override
		public void run() {
			Looper.prepare();
			handler = new Handler();
			Looper.loop();
		}
		
		public Handler getHandler() {
			return handler;
		}
	
		public AudioTrack getAudioTrack() {
			return audioTrack;
		}
	}
}
