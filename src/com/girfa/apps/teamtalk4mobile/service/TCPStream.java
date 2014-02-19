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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class TCPStream {
	public static final String TAG = TCPStream.class.getSimpleName();
	private volatile Socket socket;
	private BufferedReader input;
	private volatile PrintWriter output;
	private volatile Context context;
	private volatile Listener listener;
	private volatile boolean connected;
	private volatile Thread runner;
	private volatile int id = 1;

	@SuppressLint("UseSparseArrays")
	TCPStream(Context context) {
		this.context = context;
	}

	void connect(final Server server) {
		if (listener == null) return;
		if (server == null) return;
		runner = new Thread(new Runnable() {
			public void run() {
				try {
					if (!Utils.checkConnection(context)) disconnect();
					socket = new Socket(server.getAddress(), server.getTcpPort());
					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					output = new PrintWriter(socket.getOutputStream(), true);
					connected = true;
					listener.onConnected();
		    		while (connected) {
		    			String response = input.readLine();
		    			try {
		    				if (response == null) continue;
		    				// Log.i(TAG, response);
		    				String res[] = response.split(" ", 2);
		    				String message = res.length > 1 ? res[1] : null;
		    				listener.tcpResponse(Response.valueOf(res[0]), message);
		    			} catch (Exception e) {
		    				Log.e(TAG, "connect." + e.getMessage());
		    			}
		    		}
				} catch (Exception e) {
					disconnect();
					Log.e(TAG, "connect." + e.getMessage());
				}
			}
		});
		runner.start();
	}

	void disconnect() {
		connected = false;
		try {
			socket.close();
		} catch (Exception e) {
			socket = null;
			Log.e(TAG, "disconnect." + e.getMessage());
		}
		try {
			if (runner == null) return;
			Thread moribund = runner;
			runner = null;
			moribund.interrupt();
			listener.onDisconnected();
		} catch (Exception e) {
			Log.e(TAG, "disconnect." + e.getMessage());
		}
	}
	
	void tcpRequest(Request command, Map<String, Object> map) {
		if (connected) {
			if (map == null) map = new LinkedHashMap<String, Object>();
			if (!command.equals(Request.ping)) map.put("id", id ++);
			// Log.i(TAG, (command + " " + Stream.encode(map)).trim());
			output.println((command + " " + Stream.encode(map)).trim());
		}
	}
	
	void setListener(Listener listener) {
		this.listener = listener;
	}
	
	void setTimeout(int ms) {
		if (socket == null) return;
		try {
			socket.setSoTimeout(ms);
		} catch (SocketException e) {
			Log.e(TAG, "setTimeout." + e.getMessage());
			disconnect();
		}
	}
	
	boolean isConnected() {
		return connected;
	}
	
	interface Listener {
		void tcpResponse(Response command, String message);
		void onConnected();
		void onDisconnected();
	}
}
