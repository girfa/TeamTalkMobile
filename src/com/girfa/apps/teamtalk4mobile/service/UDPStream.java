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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import com.girfa.apps.teamtalk4mobile.api.adapter.AudioCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.enumflags.SoundLevel;

import android.content.Context;
import android.util.Log;

public class UDPStream {
	static final String TAG = UDPStream.class.getSimpleName();
	private static final long START = System.currentTimeMillis();
	private static final int PACKET_SIZE = 0x10000;
	
	private static final byte AUDIO = 0x05;
	private static final byte VIDEO = 0x06;
	private static final byte DESKTOP = 0x0d;
	
	enum Action {INIT, PING, TRANSMIT}
	
	private volatile Listener listener;
	private boolean connected;
	private volatile Thread thread;
	private DatagramSocket socket;
	private byte byteId[];
	private volatile AudioPlayer player = new AudioPlayer();
	
	UDPStream(Context context) {}

	private byte[] header(Action action) {
		byte[] data = null;
		byte[] pre;
		switch (action) {
			case INIT :
			case PING :
				data = new byte[10];
				long now = System.currentTimeMillis();
				pre = new byte[] {
					(byte) (action == Action.INIT ? 0x01 : 0x03),
					0x03
				};
				byte[] post = new byte[] {
					(byte) ((now - START) % 0x100),
					(byte) ((now - START) % 0x10000 / 0x100),
					(byte) ((now - START) % 0x1000000 / 0x10000),
					(byte) ((now - START) % 0x100000000L / 0x1000000)
				};
				System.arraycopy(pre, 0, data, 0, 2);
				System.arraycopy(byteId, 0, data, 2, 4);
				System.arraycopy(post, 0, data, 6, 4);
				break;
			case TRANSMIT :
				pre = new byte[] {0x05, 0x03};
				System.arraycopy(pre, 0, data, 0, 2);
				System.arraycopy(byteId, 0, data, 2, 4);
				break;
		}
		return data;
	}
	
	void connect(final Server server) {
		if (server == null) return;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InetAddress host = InetAddress.getByName(server.getAddress());
					socket = new DatagramSocket();
					byte[] init = header(Action.INIT);
					DatagramPacket initPack = new DatagramPacket(init, init.length, host, server.getUdpPort());
					socket.send(initPack);
					socket.receive(initPack);
					listener.udpReceive(Stream.RX_INIT, null, null);
					connected = true;
					byte[] recvData = new byte[PACKET_SIZE];
					while (connected) {
						try {
							DatagramPacket recvPack = new DatagramPacket(recvData, recvData.length, host, server.getUdpPort());
							socket.setSoTimeout(10000);
							socket.receive(recvPack);
							byte[] data = new byte[recvPack.getLength()];
							System.arraycopy(recvData, recvPack.getOffset(), data, 0, recvPack.getLength());
							User user =  new User(data[3] * 256 + data[2]);
							switch (data[0]) {
								case AUDIO :
									if (player != null) {
										player.write(user, data);
									}
									listener.udpReceive(Stream.RX_AUDIO, user, SoundLevel.GAIN_DEFAULT);
									break;
								case VIDEO :
									listener.udpReceive(Stream.RX_VIDEO, user, null);
									break;
								case DESKTOP :
									listener.udpReceive(Stream.RX_DESKTOP, user, null);
									break;
							}
						} catch (SocketTimeoutException e) {
							try {
								byte[] ping = header(Action.PING);
								DatagramPacket pingPack = new DatagramPacket(ping, ping.length, host, server.getUdpPort());
								socket.send(pingPack);
								socket.receive(pingPack);
								listener.udpReceive(Stream.RX_PONG, null, null);
							} catch (SocketTimeoutException ignore) {
								Log.e(TAG, "connect.Ping timeout");
							} catch (Exception in) {
								Log.e(TAG, "connect." + in.getMessage());
							}
						} catch (Exception e) {
							e.printStackTrace();
							Log.e(TAG, "connect." + e.getMessage());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					disconnect();
					Log.e(TAG, "connect." + e.getMessage());
				}
			}
		});
		thread.start();
	}
	
	void disconnect() {
		left();
		connected = false;
		try {
			socket.close();
		} catch (Exception e) {
			socket = null;
			Log.e(TAG, "disconnect." + e.getMessage());
		}
		try {
			if (thread == null) return;
			Thread moribund = thread;
			thread = null;
			moribund.interrupt();
		} catch (Exception e) {
			Log.e(TAG, "disconnect." + e.getMessage());
		}
	}
	
	void join(final Channel channel) {
		try {
			AudioCodec audioCodec = channel.getAudioCodec();
			player.start(audioCodec);
		} catch (Exception e) {
			Log.e(TAG, "join." + e.getMessage());
		}
	}
	
	void left() {
		try {
			player.stop();
		} catch (Exception e) {
			Log.e(TAG, "left." + e.getMessage());
		}
		
	}
	
	void setListener(Listener listener) {
		this.listener = listener;
	}
	
	void setUser(User user) {
		if (user == null) return;
		if (user.getId() == null) return;
		this.byteId = new byte[] {
			(byte) (user.getId() % 0x100),
			(byte) (user.getId() / 0x100),
			0x00, 0x00
		};
	}
	
	public AudioPlayer getPlayer() {
		return player;
	}
	
	interface Listener {
		void udpReceive(int type, User user, Object param);
	}
	
	static final String HEXES = "0123456789ABCDEF";
	static String getHex( byte [] raw ) {
	    if ( raw == null ) {
	        return null;
	    }
	    final StringBuilder hex = new StringBuilder( 3 * raw.length );
	    for ( final byte b : raw ) {
	        hex.append(HEXES.charAt((b & 0xF0) >> 4))
	            .append(HEXES.charAt((b & 0x0F)))
	            .append(' ');
	    }
	    return hex.toString();
	}
}
