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
package com.girfa.apps.teamtalk4mobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.SparseArray;

import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.bitflags.StatusModes;
import com.girfa.apps.teamtalk4mobile.api.bitflags.Subscriptions;
import com.girfa.apps.teamtalk4mobile.api.bitflags.UserStates;
import com.girfa.apps.teamtalk4mobile.api.bitflags.UserTypes;
import com.girfa.apps.teamtalk4mobile.api.enumflags.AudioFileFormat;

public class UserDB extends Database {
	public static final String TAG = UserDB.class.getSimpleName();
	
	static final String DB_TABLE = "user";
	public static final String SOUND_LEVEL = "sound_level";
	public static final String 
		COL_ID = "_id",
		COL_NICKNAME = "nickname",
		COL_STATUS_MODE = "status_mode",
		COL_STATUS_MESSAGE = "status_message";
	static final String
		COL_CHANNEL_ID = "channel_id",
		COL_JOINED = "joined",
		COL_UNREAD = "unread";
	private static final String
		COL_USERNAME = "username",
		COL_IP_ADDRESS = "ip_address",
		COL_UDP_ADDRESS = "udp_address",
		COL_VERSION = "version",
		COL_PACKET_PROTOCOL = "packet_protocol",
		COL_TYPES = "types",
		COL_STATES = "states",
		COL_LOCAL_SUB = "local_sub",
		COL_PEER_SUB = "peer_sub",
		COL_DATA = "data",
		COL_AUDIO_FOLDER = "audio_folder",
		COL_AFF = "aff",
		COL_AUDIO_FILE_NAME = "audio_file_name";
	
	private static String[] allColumns = {
		COL_ID, COL_NICKNAME, COL_USERNAME,
		COL_STATUS_MODE, COL_STATUS_MESSAGE,
		COL_CHANNEL_ID, COL_IP_ADDRESS, COL_UDP_ADDRESS,
		COL_VERSION, COL_PACKET_PROTOCOL, COL_TYPES, COL_STATES,
		COL_LOCAL_SUB, COL_PEER_SUB, COL_DATA,
		COL_AUDIO_FOLDER, COL_AFF,
		COL_AUDIO_FILE_NAME, COL_JOINED, COL_UNREAD
	};
	private static String[] listColumns = {
		COL_ID, COL_NICKNAME, COL_USERNAME, COL_STATUS_MESSAGE,
		COL_STATUS_MODE, COL_TYPES, COL_STATES, COL_UNREAD
	};
		
	public UserDB(Context context) {
		super(context);
	}
	
	public synchronized int add(User user) {
		if (user == null) return 0;
		return (int) database.insertOrThrow(DB_TABLE, null, userToCv(user));
	}

	public synchronized int update(User user) {
		if (user == null) return 0;
		return database.update(DB_TABLE, userToCv(user),
			COL_ID + " = ?", new String[] {user.getId() + ""});
	}
	
	public synchronized int message(User user) {
		if (user == null) return 0;
		ContentValues cv = new ContentValues();
		cv.put(COL_UNREAD, user.isUnread());
		return database.update(DB_TABLE, cv,
			COL_ID + " = ?", new String[] {user.getId() + ""});
	}
	
	public User get(int id) {
		return get(new User(id));
	}
	
	public User get(User user) {
		if (user == null) return null;
		try {
			Cursor cs = database.query(DB_TABLE, allColumns,
				COL_ID + " = ?",
				new String[] {user.getId() + ""},
				null, null, null);
			cs.moveToFirst();
			User result = new User()
				.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setNickname(cs.getString(cs.getColumnIndex(COL_NICKNAME)))
				.setUsername(cs.getString(cs.getColumnIndex(COL_USERNAME)))
				.setStatusModes(new StatusModes(cs.getInt(cs.getColumnIndex(COL_STATUS_MODE))))
				.setStatusMessage(cs.getString(cs.getColumnIndex(COL_STATUS_MESSAGE)))
				.setChannel(new Channel(cs.getInt(cs.getColumnIndex(COL_CHANNEL_ID))))
				.setIpAddress(cs.getString(cs.getColumnIndex(COL_IP_ADDRESS)))
				.setUdpAddess(cs.getString(cs.getColumnIndex(COL_UDP_ADDRESS)))
				.setVersion(cs.getString(cs.getColumnIndex(COL_VERSION)))
				.setPacketProtocol(cs.getInt(cs.getColumnIndex(COL_PACKET_PROTOCOL)))
				.setUserTypes(new UserTypes(cs.getInt(cs.getColumnIndex(COL_TYPES))))
				.setUserStates(new UserStates(cs.getInt(cs.getColumnIndex(COL_STATES))))
				.setLocalSubscriptions(new Subscriptions(cs.getInt(cs.getColumnIndex(COL_LOCAL_SUB))))
				.setPeerSubscriptions(new Subscriptions(cs.getInt(cs.getColumnIndex(COL_PEER_SUB))))
				.setUserData(cs.getInt(cs.getColumnIndex(COL_DATA)))
				.setAudioFolder(cs.getString(cs.getColumnIndex(COL_AUDIO_FOLDER)))
				.setAff(AudioFileFormat.valueOf(cs.getInt(cs.getColumnIndex(COL_AFF))))
				.setAudioFileName(cs.getString(cs.getColumnIndex(COL_AUDIO_FILE_NAME)))
				.setJoin(cs.getInt(cs.getColumnIndex(COL_JOINED)) != 0)
				.setUnread(cs.getInt(cs.getColumnIndex(COL_UNREAD)) != 0);
			cs.close();
			return result;
		} catch (Exception e) {
			Log.e(TAG, "get." + e.getMessage());
			return null;
		}
	}
	
	public SparseArray<User> gets(Channel channel) {
		if (channel == null) return null;
		Cursor cs = null;
		try {
			cs = database.query(DB_TABLE, listColumns,
				COL_CHANNEL_ID + " = ? AND " + COL_JOINED + " = ?",
				new String[]{channel.getId() + "", "1"},
				null, null,
				COL_NICKNAME + " COLLATE NOCASE");
		} catch (SQLiteException e) {
			Log.e(TAG, "gets." + e.getMessage());
			return null;
		}
		SparseArray<User> users = new SparseArray<User>();
		cs.moveToFirst();
		while (!cs.isAfterLast()) {
			User user = new User()
				.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setNickname(cs.getString(cs.getColumnIndex(COL_NICKNAME)))
				.setUsername(cs.getString(cs.getColumnIndex(COL_USERNAME)))
				.setStatusMessage(cs.getString(cs.getColumnIndex(COL_STATUS_MESSAGE)))
				.setStatusModes(new StatusModes(cs.getInt(cs.getColumnIndex(COL_STATUS_MODE))))
				.setUserTypes(new UserTypes(cs.getInt(cs.getColumnIndex(COL_TYPES))))
				.setUserStates(new UserStates(cs.getInt(cs.getColumnIndex(COL_STATES))))
				.setUnread(cs.getInt(cs.getColumnIndex(COL_UNREAD)) != 0);
			users.append(user.getId(), user);
			cs.moveToNext();
		}
		cs.close();
		if (users.size() > 0) return users;
		return null;
	}
	
	private static ContentValues userToCv(User user) {
		if (user == null) return null;
		ContentValues cv = new ContentValues();
		if (user.getId() != null) cv.put(COL_ID, user.getId());
		if (user.getNickname() != null) cv.put(COL_NICKNAME, user.getNickname());
		if (user.getUsername() != null) cv.put(COL_USERNAME, user.getUsername());
		if (user.getStatusModes() != null) cv.put(COL_STATUS_MODE, user.getStatusModes().getFlags());
		if (user.getStatusMessage() != null) cv.put(COL_STATUS_MESSAGE, user.getStatusMessage());
		if (user.getChannel() != null) cv.put(COL_CHANNEL_ID, user.getChannel().getId());
		if (user.getIpAddress() != null) cv.put(COL_IP_ADDRESS, user.getIpAddress());
		if (user.getUdpAddess() != null) cv.put(COL_UDP_ADDRESS, user.getUdpAddess());
		if (user.getVersion() != null) cv.put(COL_VERSION, user.getVersion());
		if (user.getPacketProtocol() != null) cv.put(COL_PACKET_PROTOCOL, user.getPacketProtocol());
		if (user.getUserTypes() != null) cv.put(COL_TYPES, user.getUserTypes().getFlags());
		if (user.getUserStates() != null) cv.put(COL_STATES, user.getUserStates().getFlags());
		if (user.getLocalSubscriptions() != null) cv.put(COL_LOCAL_SUB, user.getLocalSubscriptions().getFlags());
		if (user.getPeerSubscriptions() != null) cv.put(COL_PEER_SUB, user.getPeerSubscriptions().getFlags());
		if (user.getUserData() != null) cv.put(COL_DATA, user.getUserData());
		if (user.getAudioFolder() != null) cv.put(COL_AUDIO_FOLDER, user.getAudioFolder());
		if (user.getAff() != null) cv.put(COL_AFF, user.getAff().value);
		if (user.getAudioFileName() != null) cv.put(COL_AUDIO_FILE_NAME, user.getAudioFileName());
		if (user.isJoin() != null) cv.put(COL_JOINED, user.isJoin());
		if (user.isUnread() != null) cv.put(COL_UNREAD, user.isUnread());
		return cv;
	}
	
	static void buildTable(SQLiteDatabase db) {
		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE + "(" 
			+ COL_ID + " integer PRIMARY KEY, "
			+ COL_NICKNAME + " text, "
			+ COL_USERNAME + " text, "
			+ COL_STATUS_MODE + " integer, "
			+ COL_STATUS_MESSAGE + " text, "
			+ COL_CHANNEL_ID + " integer, "
			+ COL_IP_ADDRESS + " text, "
			+ COL_UDP_ADDRESS + " text, "
			+ COL_VERSION + " text, "
			+ COL_PACKET_PROTOCOL + " integer, "
			+ COL_TYPES + " integer, "
			+ COL_STATES + " integer, "
			+ COL_LOCAL_SUB + " integer, "
			+ COL_PEER_SUB + " integer, "
			+ COL_DATA + " integer, "
			+ COL_AUDIO_FOLDER + " text, "
			+ COL_AFF + " integer, "
			+ COL_AUDIO_FILE_NAME + " text, "
			+ COL_JOINED + " integer, "
			+ COL_UNREAD + " integer"
		+ ");";
		
		db.execSQL(CREATE_TABLE);
	}
	
	static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
	}

	public synchronized void reset() {
		database.execSQL("DELETE FROM " + DB_TABLE);
	}
}
