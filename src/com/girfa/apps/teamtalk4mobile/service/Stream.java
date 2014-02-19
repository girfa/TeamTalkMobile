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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.girfa.apps.teamtalk4mobile.MainActivity;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.TeamTalk4Mobile;
import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileInfo;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileTransfer;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.api.adapter.ServerProperties;
import com.girfa.apps.teamtalk4mobile.api.adapter.TextMessage;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.adapter.UserAccount;
import com.girfa.apps.teamtalk4mobile.api.enumflags.SoundLevel;
import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;
import com.girfa.apps.teamtalk4mobile.db.ChannelDB;
import com.girfa.apps.teamtalk4mobile.db.FileDB;
import com.girfa.apps.teamtalk4mobile.db.HistoryDB;
import com.girfa.apps.teamtalk4mobile.db.MessageDB;
import com.girfa.apps.teamtalk4mobile.db.PrefChanges;
import com.girfa.apps.teamtalk4mobile.db.ServerDB;
import com.girfa.apps.teamtalk4mobile.db.UserDB;
import com.girfa.apps.teamtalk4mobile.utils.Config;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;


public class Stream extends Service implements Handler.Callback, TCPStream.Listener, UDPStream.Listener, FileListener, Recorder.Listener {
	public static final String TAG = Stream.class.getSimpleName();
	
	public static final int 
		INIT_REGISTER_CLIENT 		= 101,
		INIT_UNREGISTER_CLIENT 		= 102,
		INIT_NOT_CONNECTED 			= 103,
		INIT_ALREADY_CONNECTED 		= 104,
		INIT_NOTIFICATION			= 105,
		
		REQ_CONNECT 				= 201,
		REQ_DISCONNECT 				= 202,
		REQ_LOGIN					= 203,
		REQ_PING					= 204,
		REQ_JOIN_CHANNEL			= 205,
		REQ_MESSAGE		 			= 206,
		REQ_READ_MESSAGE			= 207,
		REQ_UPLOAD_FILE				= 208,
		REQ_DOWNLOAD_FILE			= 209,
		REQ_DELETE_FILE				= 210,
		REQ_DOWNLOAD_CANCEL			= 211,
		REQ_UPLOAD_CANCEL			= 212,
		REQ_CHANGE_STATUS			= 213,
		REQ_CHANGE_NICKNAME			= 214,
		REQ_RECORD					= 215,
		
		RES_DISCONNECTED	 		= 301,
		RES_WELCOME 				= 302,
		RES_SERVER_UPDATE			= 303,
		RES_ADD_CHANNEL 			= 304,
		RES_UPDATE_CHANNEL 			= 305,
		RES_REMOVE_CHANNEL	 		= 306,
		RES_ADD_USER 				= 307,
		RES_UPDATE_USER 			= 308,
		RES_REMOVE_USER 			= 309,
		RES_LOADED					= 310,
		RES_JOINED					= 311,
		RES_MESSAGE			 		= 312,
		RES_MESSAGE_READ			= 313,
		RES_HISTORY					= 314,
		RES_ADD_FILE				= 315,
		RES_REMOVE_FILE				= 316,
		RES_DOWNLOAD_START			= 317,
		RES_UPLOAD_START			= 318,
		RES_DOWNLOAD_PROGRESS		= 319,
		RES_UPLOAD_PROGRESS			= 320,
		RES_DOWNLOAD_FINISH			= 321,
		RES_UPLOAD_FINISH			= 322,
		RES_RECORD_STARTED			= 323,
		RES_RECORD_STOPPED			= 324,
		
		TX_INIT						= 401,
		TX_PING						= 402,
		TX_AUDIO					= 403,
		TX_VIDEO					= 404,
		TX_DESKTOP					= 405,
	
		RX_INIT						= 501,
		RX_PONG						= 502,
		RX_AUDIO					= 503,
		RX_VIDEO					= 504,
		RX_DESKTOP					= 505;

	private Messenger service;
	@SuppressWarnings("unused")
	private NotificationManager nm;
	private volatile List<Messenger> clients = new ArrayList<Messenger>();
	private volatile TCPStream tcp;
	private UDPStream udp;
	private ServerDB sdb;
	private HistoryDB hdb;
	private ChannelDB cdb;
	private UserDB udb;
	private MessageDB mdb;
	private FileDB fdb;
	private Server server;
	private ServerProperties curSp;
	private User curUser;
	private UserAccount curUa;
	private boolean loaded;
	private int top;
	private volatile SparseArray<FileInfo> files = new SparseArray<FileInfo>();
	private volatile SparseArray<FileTransfer> trans = new SparseArray<FileTransfer>();
	private volatile SparseArray<Thread> threads = new SparseArray<Thread>();
	private volatile long lastUdp = System.currentTimeMillis() / 1000;
	private PrefChanges pref;
	
	@Override
	public IBinder onBind(Intent intent) {
		return service.getBinder();
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "created");
		service = new Messenger(new Handler(this));
		tcp = new TCPStream(this);
		tcp.setListener(this);
		udp = new UDPStream(this);
		udp.setListener(this);
		// udp.getPlayer().getRecorder().setListener(this);
		new Config(this);
		curSp = new ServerProperties();
		curSp.dump(this).reset();
		curUser = new User();
		curUser.dump(this).reset();
		curUa = new UserAccount();
		curUa.dump(this).reset();
		sdb = (ServerDB) new ServerDB(this).openRead();
		hdb = (HistoryDB) new HistoryDB(this).openWrite();
		cdb = (ChannelDB) new ChannelDB(this).openWrite();
		udb = (UserDB) new UserDB(this).openWrite();
		mdb = (MessageDB) new MessageDB(this).openWrite();
		fdb = (FileDB) new FileDB(this).openWrite();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "destroyed");
		fdb.close();
		mdb.close();
		udb.close();
		cdb.close();
		hdb.close();
		sdb.close();
		super.onDestroy();
	}
	
	private void log(Response command, String message) {
		hdb.add(command, message);
		send(RES_HISTORY); 
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		Map<String, Object> req = new LinkedHashMap<String, Object>();
		Bundle data = msg.getData();
		int id;
		switch (msg.what) {
		    case INIT_REGISTER_CLIENT :
		    	clients.add(msg.replyTo);
		    	if (tcp.isConnected()) send(INIT_ALREADY_CONNECTED);
		    	else send(INIT_NOT_CONNECTED);
		    	break;
		    case INIT_UNREGISTER_CLIENT :
		    	clients.remove(msg.replyTo);
		    	break;
		    case REQ_CONNECT :
		    	try {
		    		server = sdb.get(data.getInt(ServerDB.COL_ID, 0));
		    		if (server != null && !tcp.isConnected()) {
		    			tcp.connect(server);
		    		}
		    	} catch (NullPointerException ignore) {}
		    	break;
		    case REQ_DISCONNECT :
		    	tcp.tcpRequest(Request.logout, null);
		    	udp.disconnect();
		    	tcp.disconnect();
		    	break;
		    case REQ_LOGIN :
		    	req.clear();
		    	try {
		    		String nickname = curUser.getNickname();
		    		if (Utils.isEmpty(nickname)) {
		    			nickname = server.getAuthUsername();
		    		}
		    		if (Utils.isEmpty(nickname)) {
		    			nickname = getResources().getString(R.string.noname);
		    		}
		    		req.put("nickname", nickname);
					req.put("username", server.getAuthUsername());
					req.put("password", server.getAuthPassword());
					req.put("protocol", getResources().getString(R.string.protocol));
					try {
						req.put("version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
					} catch (NameNotFoundException e) {}
					
		    	} catch (NullPointerException ignore) {}
		    	tcp.tcpRequest(Request.login, req);
		    	break;
		    case REQ_PING :
		    	new Thread(new Runnable() {
					@Override
					public void run() {
						tcp.setTimeout(curSp.getUserTimeout() * 1000);
						while (tcp.isConnected()) {
							Utils.sleep(curSp.getUserTimeout() / 2 * 1000);
							tcp.tcpRequest(Request.ping, null);
						}
					}
				}).start();
		    	break;
		    case REQ_JOIN_CHANNEL :
		    	req.clear();
		    	String name = data.getString(ChannelDB.COL_URI);
				req.put("channel", Utils.isEmpty(name) ? "/" : name);
				if (data.getBoolean(ChannelDB.COL_PROTECTED)) {
					req.put("password", data.getString(ChannelDB.COL_PASSWORD));
				}
				tcp.tcpRequest(Request.join, req);
		    	break;
		    case REQ_MESSAGE :
		    	req.clear();
		    	TextMessageType type = TextMessageType.valueOf((Integer) data.get(MessageDB.COL_TYPE));
		    	req.put("type", data.get(MessageDB.COL_TYPE));
		    	req.put("content", data.get(MessageDB.COL_CONTENT));
		    	switch (type) {
			    	case USER :
			    		req.put("destuserid", data.get(MessageDB.COL_DEST_USER_ID));
			    		break;
			    	case CHANNEL :
			    		if (loaded) req.put("channel", cdb.get(curUser.getChannel()).getURI());
			    		break;
					default:
						break;
		    	}
		    	tcp.tcpRequest(Request.message, req);
		    	break;
		    case REQ_READ_MESSAGE :
		    	udb.message(new User(data.getInt(UserDB.COL_ID)).setUnread(false));
		    	send(RES_MESSAGE_READ, data);
		    	break;
		    case REQ_UPLOAD_FILE :
		    	req.clear();
		    	id = data.getInt(FileDB.COL_ID);
		    	if (files.indexOfKey(id) >= 0) break;
				FileInfo upl = fdb.get(id);
				files.put(id, upl);
				
				FileTransfer uplT = new FileTransfer();
				uplT.setChannel(upl.getChannel());
				uplT.setFileSize(upl.getFileSize());
				uplT.setInbound(false);
				uplT.setRemoteFileName(upl.getFileName());
				uplT.setLocalFilePath(upl.getFileTransfer().getLocalFilePath());
				trans.put(id, uplT);
				
				req.put("filename", upl.getFileName());
		    	req.put("filesize", upl.getFileSize());
		    	req.put("channel", cdb.get(upl.getChannel()).getURI());
		    	top = id;
		    	tcp.tcpRequest(Request.regsendfile, req);
		    	break;
		    case REQ_DOWNLOAD_FILE :
		    	req.clear();
		    	id = data.getInt(FileDB.COL_ID);
		    	if (files.indexOfKey(id) >= 0) break;
		    	FileInfo dwl = fdb.get(id)
		    		.setChannel(cdb.get(curUser.getChannel()));
		    	files.put(id, dwl);
		    	
		    	FileTransfer dwlT = new FileTransfer();
				dwlT.setChannel(dwl.getChannel());
				dwlT.setFileSize(dwl.getFileSize());
				dwlT.setInbound(true);
				dwlT.setRemoteFileName(dwl.getFileName());
				trans.put(id, dwlT);
		    	
		    	req.put("filename", dwl.getFileName());
		    	req.put("channel", dwl.getChannel().getURI());
		    	top = id;
		    	tcp.tcpRequest(Request.regrecvfile, req);
		    	break;
		    case REQ_DELETE_FILE :
		    	req.clear();
		    	FileInfo del = fdb.get(data.getInt(FileDB.COL_ID));
		    	req.put("filename", del.getFileName());
		    	req.put("channel", cdb.get(curUser.getChannel()).getURI());
		    	tcp.tcpRequest(Request.deletefile, req);
		    	break;
		    case REQ_UPLOAD_CANCEL :
		    case REQ_DOWNLOAD_CANCEL :
		    	try {
		    		id = data.getInt(FileDB.COL_ID);
		    		files.remove(id);
		    		trans.remove(id);
			    	threads.get(id).interrupt();
			    	threads.remove(id);
		    	} catch (NullPointerException ignore) {}
		    	break;
		    case REQ_CHANGE_STATUS :
		    	req.clear();
				req.put("statusmode", data.getInt(UserDB.COL_STATUS_MODE));
				req.put("statusmsg", data.getString(UserDB.COL_STATUS_MESSAGE));
				tcp.tcpRequest(Request.changestatus, req);
		    	break;
		    case REQ_CHANGE_NICKNAME :
		    	req.clear();
				req.put("nickname", data.getString(UserDB.COL_NICKNAME));
				tcp.tcpRequest(Request.changenick, req);
		    	break;
		    case REQ_RECORD :
		    	/*
		    	Recorder recorder = udp.getPlayer().getRecorder();
		    	if (recorder.isRecording()) {
		    		recorder.stop();
		    	} else {
		    		recorder.start();
		    	}
		    	*/
		    	break;
		}
		return false;
	}
	
	@Override
	public void tcpResponse(Response command, String message) {
		Map<String, Object> map = decode(message);
		Map<String, Object> req = new LinkedHashMap<String, Object>();
		Channel current;
		current = cdb.get(curUser.getChannel());
		if (current == null) {
			current = cdb.get(ChannelDB.DEFAULT_CHANNEL);
			if (current == null) {
				current = new Channel(ChannelDB.DEFAULT_CHANNEL);
			}
		}
		Channel ch;
		Channel gch;
		User us;
		User gus;
		FileInfo fi;
		Bundle data = null;
		switch (command) {
			case welcome :
				sdb.openWrite();
				sdb.use(server);
				sdb.close();
				curSp.build(map);
				curUser.build(map);
				curUa.build(map);
				udp.setUser(curUser);
				udp.connect(server);
				send(RES_WELCOME);
				break;
			case accepted :
				curSp.build(map);
				curUser.build(map);
				curUa.build(map);
				break;
			case serverupdate :
				curSp.build(map);
				log(command, message);
				send(RES_SERVER_UPDATE);
				break;
			case addchannel :
				ch = new Channel(map);
				cdb.add(ch);
				gch = cdb.get(ch);
				if (gch.getParent().equals(current)) {
					send(RES_ADD_CHANNEL);
				}
				break;
			case updatechannel :
				ch = new Channel(map);
				cdb.update(ch);
				gch = cdb.get(ch);
				if (gch.equals(current) || gch.getParent().equals(current)) {
					if (gch.equals(current)) {
						send(RES_UPDATE_CHANNEL);
					} else {
						data = new Bundle();
						data.putInt(ChannelDB.COL_ID, gch.getId());
						send(RES_UPDATE_CHANNEL, data);
					}
				}
				break;
			case removechannel :
				ch = new Channel(map);
				gch = cdb.get(ch);
				cdb.remove(ch);
				if (gch.getParent().equals(current)) {
					send(RES_REMOVE_CHANNEL);
				}
				break;
			case loggedin :
				us = new User(map);
				udb.add(us);
				if (us.equals(curUser)) curUser.build(map);
				break;
			case adduser :
				us = new User(map);
				udb.update(us.setJoin(true));
				gus = udb.get(us);
				gch = cdb.get(us.getChannel());
				if (gus.getChannel().equals(current) || gch.getParent().equals(current)) {
					if (gus.getChannel().equals(current)) {
						send(RES_ADD_USER);
					} else {
						data = new Bundle();
						data.putInt(ChannelDB.COL_ID, gch.getId());
						send(RES_UPDATE_CHANNEL, data);
					}
					if (loaded) log(command, message);
				}
				int subs = curUser.getLocalSubscriptions().getFlags() ^ PrefChanges.MAX_FLAGS;
				if (gus.equals(curUser)) curUser.build(map);
				if (subs > 0) {
					pref.sync(curUser);
					req.clear();
					req.put("userid", gus.getId());
					req.put("sublocal", subs);
					tcp.tcpRequest(Request.unsubscribe, req);
				}
				break;
			case updateuser :
				us = new User(map);
				udb.update(us.setJoin(true));
				gus = udb.get(us);
				data = new Bundle();
				data.putInt(UserDB.COL_ID, gus.getId());
				if (gus.getChannel().equals(current)) {
					send(RES_UPDATE_USER, data);
				}
				if (gus.equals(curUser)) curUser.build(map);
				break;
			case removeuser :
				us = new User(map);
				udb.update(us.setJoin(false));
				gus = udb.get(us);
				gch = cdb.get(us.getChannel());
				if (gus.getChannel().equals(current) || gch.getParent().equals(current)) {
					if (gus.getChannel().equals(current)) {
						send(RES_REMOVE_USER);
					} else {
						data = new Bundle();
						data.putInt(ChannelDB.COL_ID, gch.getId());
						send(RES_UPDATE_CHANNEL, data);
					}
					if (loaded && !us.equals(curUser)) {
						log(command, message);
					}
				}
				break;
			case loggedout :
				us = new User(map);
				if (us.getId() == null) {
					udp.disconnect();
					tcp.disconnect();
				}
				break;
			case addfile :
				fi = new FileInfo(map);
				fdb.add(fi);
				if (fi.getChannel().equals(current)) {
					send(RES_ADD_FILE);
				}
				break;
			case removefile :
				fi = new FileInfo(map);
				fdb.remove(fi);
				if (fi.getChannel().equals(current)) {
					send(RES_REMOVE_FILE);
				}
				break;
			case fileaccepted :
				FileInfo file = files.get(top);
				FileTransfer ft = trans.get(top);
				ft.build(map);
				if (ft.isInbound()) {
					FileDownload fd = new FileDownload(this , server);
					threads.put(top, fd);
					fd.start(file, ft);
				} else {
					FileUpload fu = new FileUpload(this, server);
					threads.put(top, fu);
					fu.start(file, ft);
				}
				break;
			case ok :
				if (!loaded) {
					loaded = true;
					send(RES_LOADED);
					pref.sync(curUser);
					req.clear();
					Log.d(TAG, curUser.getStatusModes().getFlags() + ":" + curUser.getStatusMessage());
					req.put("statusmode", curUser.getStatusModes().getFlags());
					req.put("statusmsg", curUser.getStatusMessage());
					tcp.tcpRequest(Request.changestatus, req);
				}
				break;
			case joined :
				Channel channel = cdb.get(new Channel(map));
				curUser.setChannel(channel);
				udp.join(channel);
				send(RES_JOINED);
				log(command, message);
				break;
			case left :
				log(command, message);
				udp.left();
				break;
			case messagedeliver :
				TextMessage tm = new TextMessage(map);
				mdb.add(tm);
				data = new Bundle();
				data.putInt(MessageDB.COL_TYPE, tm.getTextMessageType().value);
				switch (tm.getTextMessageType()) {
					case USER :
						us = tm.getDestUser();
						if (us.equals(curUser)) {
							us = tm.getSrcUser();
						}
						if (us != null) {
							data.putInt(UserDB.COL_ID, us.getId());
							data.putInt(MessageDB.COL_DEST_USER_ID, us.getId());
							udb.message(us.setUnread(true));
						}
						break;
					case CHANNEL :
						if (tm.getDestChannel() != null) {
							data.putInt(MessageDB.COL_DEST_CHANNEL_ID, tm.getDestChannel().getId());
							log(command, message);
						}
						break;
					case BROADCAST :
						log(command, message);
						break;
					case CUSTOM :
						break;
				}
				send(RES_MESSAGE, data);
				break;
			case kicked :
				udp.disconnect();
				tcp.disconnect();
				break;
			default:
				break;
		}
	}
	
	@Override
	public void udpReceive(int type, User user, Object param) {
		Bundle data = new Bundle();
		switch (type) {
			case RX_AUDIO :
				long now = System.currentTimeMillis() / 1000;
				if (lastUdp == now) break;
				SoundLevel sl = (SoundLevel) param;
				data.putInt(UserDB.COL_ID, user.getId());
				data.putInt(UserDB.SOUND_LEVEL, sl.id);
				send(RX_AUDIO, data);
				lastUdp = System.currentTimeMillis() / 1000;
				break;
		}
		
	}
	
	@Override
	public void recStarted(File file) {
		/*
		Bundle data = new Bundle();
		data.putString(Recorder.LOCATION, file.getAbsolutePath());
		send(RES_RECORD_STARTED, data);
		*/
	}
	
	@Override
	public void recStopped(File file) {
		/*
		Bundle data = new Bundle();
		data.putString(Recorder.LOCATION, file.getAbsolutePath());
		send(RES_RECORD_STOPPED, data);
		*/
	}
	
	@Override
	synchronized public void onConnected() {
		try {
			Intent intent= new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pin = PendingIntent.getActivity(this, 0, intent, 0);
			Notification notif = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.nf_connected)
				.setContentTitle(TeamTalk4Mobile.NAME)
				.setContentText(getResources().getString(R.string.m_connected))
				.setContentIntent(pin)
				.build();
			notif.flags = Notification.FLAG_ONGOING_EVENT;
			NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(INIT_NOTIFICATION, notif);
			
			hdb.reset();
			udb.reset();
			cdb.reset();
			mdb.reset();
			fdb.reset();
			pref = new PrefChanges(this);
			pref.sync(curUser);
		} catch (Exception e) {
			Log.e(TAG, "onConnected." + e.getMessage());
		}
	}
	
	@Override
	synchronized public void onDisconnected() {
		try {
			NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(INIT_NOTIFICATION);
			fdb.reset();
			mdb.reset();
			cdb.reset();
			udb.reset();
			hdb.reset();
			curUa.reset();
			curUser.reset();
			curSp.reset();
			send(RES_DISCONNECTED);
			stopSelf();
		} catch (Exception e) {
			Log.e(TAG, "onConnected." + e.getMessage());
		}
	}
	
	private void send(int what) {
		send(what, null);
	}
	
	private void send(int what, Bundle data) {
		for (int i = clients.size() - 1; i >= 0; i--) {
    		try {
    			Message msg = Message.obtain(null, what);
				msg.setData(data);
				clients.get(i).send(msg);
    		} catch (RemoteException e) {
    			clients.remove(i);
    		}
    	}
	}

	@Override
	public void fileStart(FileInfo fileInfo, FileTransfer transfer) {
		Bundle data = new Bundle();
		data.putInt(FileDB.COL_ID, fileInfo.getId());
		if (transfer.isInbound()) {
			send(RES_DOWNLOAD_START, data);
		} else {
			send(RES_UPLOAD_START, data);
		}
	}
	
	@Override
	public void fileProgress(FileInfo fileInfo, FileTransfer transfer) {
		Bundle data = new Bundle();
		data.putInt(FileDB.COL_ID, fileInfo.getId());
		data.putLong(FileDB.TRANSFERRED, transfer.getTransferred());
		if (transfer.isInbound()) {
			send(RES_DOWNLOAD_PROGRESS, data);
		} else {
			send(RES_UPLOAD_PROGRESS, data);
		}
		
	}

	@Override
	public void fileFinish(FileInfo fileInfo, FileTransfer transfer) {
		Bundle data = new Bundle();
		data.putInt(FileDB.COL_ID, fileInfo.getId());
		data.putString(FileDB.FILE_PATH, transfer.getLocalFilePath());
		if (transfer.isInbound()) {
			send(RES_DOWNLOAD_FINISH, data);
		} else {
			send(RES_UPLOAD_FINISH, data);
		}
	}
	
	public static String encode(Map<String, Object> map) {
		String result = "";
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (!Utils.isEmpty(value)) {
				result += key + "=";
				if (value instanceof String) {
					result += "\"" + ((String) value)
						.replace("\\", "\\\\")
						.replace("\"", "\\\"")
						.replace("\n", "\\n") + "\"";
				} else if (value instanceof Number) {
					result += value;
				} else if (value instanceof List<?>) {
					result += "[" + TextUtils.join(",", ((List<?>) value)) + "]";
				}
				result += " ";
			}
		}
		return result;
	}
	
	public static Map<String, Object> decode(String message) {
		if (message == null) return null;
		Map<String, Object> map = new HashMap<String, Object>();
		Pattern pat = Pattern.compile("(\\w+)\\s*=\\s*(?:\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|(-?\\d+(?:\\.\\d*)?)|\\[((?:-?\\d+(?:\\.\\d*)?,?)*)])?");
		Matcher mat = pat.matcher(message);
		while (mat.find()) {
			String g;
			if ((g = mat.group(2)) != null) {
				map.put(mat.group(1), g
					.replace("\\\"", "\"")
					.replace("\\n", "\n")
					.replace("\\\\", "\\"));
			}
			else if ((g = mat.group(3)) != null) {
				map.put(mat.group(1), Long.parseLong(g));
			}
			else if (!Utils.isEmpty(g = mat.group(4))) {
				List<Integer> g4 = new ArrayList<Integer>();
				List<String> _g4 = Arrays.asList(g.split(Pattern.quote(",")));
				for (int i = 0; i < _g4.size(); i++) {
					g4.add(Integer.parseInt(_g4.get(i)));
				}
				map.put(mat.group(1), g4);
			}
		}
		return map;
	}
}
