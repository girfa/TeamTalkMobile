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

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

public class AudioMixer {
	public static final String TAG = AudioMixer.class.getSimpleName();
	public static final long DEFAULT_TIMEOUT = 1000;
	
	private SparseArray<List<short[]>> buffer = new SparseArray<List<short[]>>();
	private List<short[]> output = new ArrayList<short[]>();
	
	private Object lock = new Object();
	private long timeOut = DEFAULT_TIMEOUT;
	private long start = System.currentTimeMillis();
	
	public void write(int channel, short[] audioData) {
		if (System.currentTimeMillis() - start >= timeOut) {
			start = System.currentTimeMillis();
			if (buffer.size() > 0) mix();
			if (output.size() > 0) {
				synchronized (lock) {
					lock.notify();
				}
			}
		}
		List<short[]> list = buffer.get(channel);
		if (list == null) {
			list = new ArrayList<short[]>();
			list.add(audioData);
			buffer.append(channel, list);
		} else {
			list.add(audioData);
		}
	}
	
	private int eliminate() {
		int min = Integer.MAX_VALUE;
		List<Integer> empty = new ArrayList<Integer>();
		for (int x = 0; x < buffer.size(); x++) {
			List<short[]> temp = buffer.valueAt(x);
			if (temp.size() < 1) {
				empty.add(buffer.keyAt(x));
			} else if (temp.size() < min) {
				min = temp.size();
			}
		}
		if (empty.size() == 0) return min;
		for (int x = 0; x < empty.size(); x++) {
			buffer.remove(empty.get(x));
		}
		return min;
	}
	
	private void mix() {
		synchronized (buffer) {
			if (buffer.size() == 1) {
				output.addAll(buffer.valueAt(0));
				buffer.clear();
			} else {
				int min = eliminate();
				if (buffer.size() > 0) {
					Mixer mixer = new Mixer();
					for (int x = 0; x < min; x++) {
						for (int y = 0; y < buffer.size(); y++) {
							mixer.write(buffer.valueAt(y).get(0));
							buffer.valueAt(y).remove(0);
						}
						output.add(mixer.read());
					}
				}
				eliminate();
			}
		}
	}

	public short[] read() {
		short[] res = null;
		while (true) {
			try {
				res = output.get(0);
				output.remove(0);
			} catch (IndexOutOfBoundsException ignore) {}
			if (res != null) break;
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException ignore) {}
			}
		}
		return res;
	}
	
	public void clear() {
		buffer.clear();
		output.clear();
	}
	
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}
	
	public long getTimeOut() {
		return timeOut;
	}
	
	private class Mixer {
		private List<short[]> list = new ArrayList<short[]>();
		
		private void write(short[] audioData) {
			list.add(audioData);
		}
		
		private short[] read() {
			int size = list.size();
			if (size == 0) return null;
			int len = list.get(0).length;
			short[] audioData = new short[len];
			synchronized (list) {
				for (int x = 0; x < len; x++) {
					short y = list.get(0)[x];
					for (int z = 1; z < size; z++) {
						y = mix(y, list.get(z)[x]);
					}
					audioData[x] = y;
				}
				list.clear();
			}
			return audioData;
		}
		
		private short mix(short a, short b) {
			short mix = 0;
			if (a < 0 && b < 0) {
				mix = (short) ((a + b) - ((a * b) / Short.MIN_VALUE));
			} else if (a > 0 && b > 0) {
				mix = (short) ((a + b) - ((a * b) / Short.MAX_VALUE));
			} else {
				mix = (short) (a + b);
			}
			return mix;
		}
	}
}
