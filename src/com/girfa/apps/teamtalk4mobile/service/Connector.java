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

import com.girfa.apps.teamtalk4mobile.service.Stream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class Connector {
	public static final String TAG = Connector.class.getSimpleName();
	
	private Context context;
	private Handler handler;
	private boolean bound;
	private Messenger service = null;
	private Messenger client = null;

	public Connector(Context context, Handler.Callback callback) {
		this.context = context;
		handler = new Handler(callback);
		client = new Messenger(handler);
	}
	
	public void start() {
		context.startService(new Intent(context, Stream.class));
	}
	
	public void bind() {
		context.bindService(new Intent(context, Stream.class),
				connection, Context.BIND_AUTO_CREATE);
		bound = true;
	}
	
	public void unbind() {
		send(Stream.INIT_UNREGISTER_CLIENT);
		try {
			context.unbindService(connection);
			bound = false;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "unbind." + e.getMessage());
		}
	}
	
	public void stop() {
		context.stopService(new Intent(context, Stream.class));
	}
	
	public void send(int what) {
		send(what, null);
	}
	
	public void send(int what, Bundle data) {
		if (!bound || service == null) return;
		try {
			Message msg = Message.obtain(null, what);
			msg.setData(data);
			msg.replyTo = client;
			service.send(msg);
		} catch (RemoteException e) {
			Log.e(TAG, "send." + e.getMessage());
		}
	}
	
	public void sendBack(int what) {
		sendBack(what, null);
	}
	
	public void sendBack(int what, Bundle data) {
		if (client == null) return;
		try {
			Message msg = Message.obtain(null, what);
			msg.setData(data);
			client.send(msg);
		} catch (RemoteException e) {
			Log.e(TAG, "sendBack." + e.getMessage());
		}
	}
	
	private final ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Connector.this.service = new Messenger(service);
			send(Stream.INIT_REGISTER_CLIENT);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
			client = null;
		}
	};
	
	public Handler getHandler() {
		return handler;
	}
}
