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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.girfa.apps.teamtalk4mobile.api.adapter.AudioCodec;
import com.girfa.apps.teamtalk4mobile.api.adapter.AudioConfig;
import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.bitflags.ChannelTypes;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class ChannelDB extends Database {
	public static final String TAG = ChannelDB.class.getSimpleName();
	public static final int DEFAULT_CHANNEL = 1;
	private static final String SPARATOR = "|";
	
	private static final String DB_TABLE = "channel";
	private static final String DB_VIEW = "channel_view";
	public static final String
		COL_ID = "_id",
		COL_URI = "uri",
		COL_PROTECTED = "protected",
		COL_PASSWORD = "password";
	private static final String
		COL_PARENT_ID = "parent_id",
		COL_NAME = "name",
		COL_TOPIC = "topic",
		COL_TYPES = "types",
		COL_DISK_QUOTA = "disk_quota",
		COL_OP_PASSWORD = "op_password",
		COL_MAX_USERS = "max_users",
		COL_TOTAL_USERS = "total_users",
		COL_AUDIO_CODEC = "audio_codec",
		COL_AUDIO_CONFIG = "audio_config",
		COL_OPERATORS = "operators",
		COL_VOICE_USERS = "voice_users",
		COL_VIDEO_USERS = "video_users",
		COL_DESKTOP_USERS = "desktop_users",
		COL_REMOVED = "removed";
	
	private static String[] allColumns = {
		COL_ID, COL_PARENT_ID, COL_URI, COL_NAME, COL_TOPIC,
		COL_PASSWORD, COL_PROTECTED, COL_TYPES, COL_DISK_QUOTA,
		COL_OP_PASSWORD, COL_MAX_USERS, COL_TOTAL_USERS,
		COL_AUDIO_CODEC, COL_AUDIO_CONFIG, COL_OPERATORS,
		COL_VOICE_USERS, COL_VIDEO_USERS, COL_DESKTOP_USERS
	};
	private static String[] listColumns = {
		COL_ID, COL_URI, COL_NAME,
		COL_PROTECTED, COL_PASSWORD, COL_TOTAL_USERS
	};
	
	public ChannelDB(Context context) {
		super(context);
	}

	public synchronized int add(Channel channel) {
		if (channel == null) return 0;
		List<String> ch = new LinkedList<String>(Arrays.asList(channel.getURI().split(Pattern.quote("/"))));
		if (ch.size() > 1) channel.setName(ch.get(ch.size() - 1));
		if (ch.size() > 2) {
			ch.remove(ch.size() - 1);
			channel.setParent(get(TextUtils.join("/", ch) + "/"));
		} else {
			channel.setParent(get("/"));
		}
		ContentValues cv = channelToCv(channel);
		cv.remove(COL_TOTAL_USERS);
		return (int) database.insertOrThrow(DB_TABLE, null, cv);
	}
	
	public synchronized int update(Channel channel) {
		if (channel == null) return 0;
		List<String> ch = new LinkedList<String>(Arrays.asList(channel.getURI().split(Pattern.quote("/"))));
		if (ch.size() > 1) channel.setName(ch.get(ch.size() - 1));
		if (ch.size() > 2) {
			ch.remove(ch.size() - 1);
			channel.setParent(get(TextUtils.join("/", ch) + "/"));
		} else {
			channel.setParent(get("/"));
		}
		ContentValues cv = channelToCv(channel);
		cv.remove(COL_TOTAL_USERS);
		return database.update(DB_TABLE, cv,
			COL_ID + " = ?", new String[] {channel.getId() + ""});
	}
	
	public synchronized int remove(Channel channel) {
		if (channel == null) return 0;
		ContentValues cv = new ContentValues();
		cv.put(COL_REMOVED, true);
		return database.delete(DB_TABLE, COL_ID + " = ?", new String[] {channel.getId() + ""});
	}
	
	public Channel get(int id) {
		return get(new Channel(id));
	}
	
	public Channel get(Channel channel) {
		if (channel == null) return null;
		try {
			Cursor cs = database.query(DB_VIEW, allColumns,
				COL_ID + " = ?",
				new String[] {channel.getId() + ""},
				null, null, null);
			cs.moveToFirst();
			Channel result = new Channel()
				.setParent(new Channel(cs.getInt(cs.getColumnIndex(COL_PARENT_ID))))
				.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setURI(cs.getString(cs.getColumnIndex(COL_URI)))
				.setName(cs.getString(cs.getColumnIndex(COL_NAME)))
				.setTopic(cs.getString(cs.getColumnIndex(COL_TOPIC)))
				.setPassword(cs.getString(cs.getColumnIndex(COL_PASSWORD)))
				.setProtected(cs.getInt(cs.getColumnIndex(COL_PROTECTED)) != 0)
				.setChannelTypes(new ChannelTypes(cs.getInt(cs.getColumnIndex(COL_TYPES))))
				.setDiskQuota(cs.getLong(cs.getColumnIndex(COL_DISK_QUOTA)))
				.setOpPassword(cs.getString(cs.getColumnIndex(COL_OP_PASSWORD)))
				.setMaxUsers(cs.getInt(cs.getColumnIndex(COL_MAX_USERS)))
				.setTotalUsers(cs.getInt(cs.getColumnIndex(COL_TOTAL_USERS)))
				.setAudioCodec(new AudioCodec(stringToInts(cs.getString(cs.getColumnIndex(COL_AUDIO_CODEC)))))
				.setAudioConfig(new AudioConfig(stringToInts(cs.getString(cs.getColumnIndex(COL_AUDIO_CONFIG)))))
				.setOperators(stringToUsers(cs.getString(cs.getColumnIndex(COL_OPERATORS))))
				.setVoiceUsers(stringToUsers(cs.getString(cs.getColumnIndex(COL_VOICE_USERS))))
				.setVideoUsers(stringToUsers(cs.getString(cs.getColumnIndex(COL_VIDEO_USERS))))
				.setDesktopUsers(stringToUsers(cs.getString(cs.getColumnIndex(COL_DESKTOP_USERS))));
			cs.close();
			return result;
		} catch (Exception e) {
			Log.e(TAG, "get." + e.getMessage());
			return null;
		}
	}
	
	Channel get(String uri) {
		if (uri == null) return null;
		try {
			Cursor cs = database.query(
				DB_TABLE,
				new String[]{COL_ID},
				COL_URI + " = ?",
				new String[]{uri},
				null, null, null);
				cs.moveToFirst();
			return new Channel(cs.getInt(cs.getColumnIndex(COL_ID)));
		} catch (Exception e) {
			Log.e(TAG, "get." + e.getMessage());
			return null;
		}
	}
	
	public SparseArray<Channel> gets(Channel parent) {
		if (parent == null) return null;
		Cursor cs = null;
		try {
			cs = database.query(DB_VIEW, listColumns,
				COL_PARENT_ID + " = ? AND " + COL_REMOVED + " = 0",
				new String[]{(parent.getId() > DEFAULT_CHANNEL ? parent.getId() : DEFAULT_CHANNEL) + ""},
				null, null,
				COL_NAME + " COLLATE NOCASE");
		} catch (SQLiteException e) {
			Log.e(TAG, "gets." + e.getMessage());
			return null;
		}
		SparseArray<Channel> channels = new SparseArray<Channel>();
		cs.moveToFirst();
		while (!cs.isAfterLast()) {
			Channel channel = new Channel()
				.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setURI(cs.getString(cs.getColumnIndex(COL_URI)))
				.setName(cs.getString(cs.getColumnIndex(COL_NAME)))
				.setProtected(cs.getInt(cs.getColumnIndex(COL_PROTECTED)) != 0)
				.setPassword(cs.getString(cs.getColumnIndex(COL_PASSWORD)))
				.setTotalUsers(cs.getInt(cs.getColumnIndex(COL_TOTAL_USERS)));
			channels.append(channel.getId(), channel);
			cs.moveToNext();
		}
		cs.close();
		if (channels.size() > 0) return channels;
		return null;
	}
	
	private static List<User> stringToUsers(String string) {
		if (!Utils.isEmpty(string)) {
			String[] strA = string.split(Pattern.quote(SPARATOR));
			List<User> users = new ArrayList<User>();
			for (String i : strA) {
				users.add(new User(Integer.valueOf(i)));
			}
			return users;
		}
		return null;
	}
	
	private static List<Integer> stringToInts(String string) {
		if (Utils.isEmpty(string)) return null;
		String[] strA = string.split(Pattern.quote(SPARATOR));
		List<Integer> ints = new ArrayList<Integer>();
		for (String i : strA) {
			ints.add(Integer.valueOf(i));
		}
		return ints;
	}
	
	private static ContentValues channelToCv(Channel channel) {
		if (channel == null) return null;
		ContentValues cv = new ContentValues();
		cv.put(COL_ID, channel.getId());
		if (channel.getParent() != null) cv.put(COL_PARENT_ID, channel.getParent().getId());
		if (channel.getURI() != null) cv.put(COL_URI, channel.getURI());
		if (channel.getName() != null) cv.put(COL_NAME, channel.getName());
		if (channel.getTopic() != null) cv.put(COL_TOPIC, channel.getTopic());
		if (channel.getPassword() != null) cv.put(COL_PASSWORD, channel.getPassword());
		if (channel.isProtected() != null) cv.put(COL_PROTECTED, channel.isProtected());
		if (channel.getChannelTypes() != null) cv.put(COL_TYPES, channel.getChannelTypes().getFlags());
		if (channel.getDiskQuota() != null) cv.put(COL_DISK_QUOTA, channel.getDiskQuota());
		if (channel.getOpPassword() != null) cv.put(COL_OP_PASSWORD, channel.getOpPassword());
		if (channel.getMaxUsers() != null) cv.put(COL_MAX_USERS, channel.getMaxUsers());
		if (channel.getTotalUsers() != null) cv.put(COL_TOTAL_USERS, channel.getTotalUsers());
		if (channel.getAudioCodec() != null) cv.put(COL_AUDIO_CODEC, channel.getAudioCodec().toString());
		if (channel.getAudioConfig() != null) cv.put(COL_AUDIO_CONFIG, channel.getAudioConfig().toString());
		if (channel.getOperators() != null) cv.put(COL_OPERATORS, TextUtils.join(SPARATOR, channel.getOperators()));
		if (channel.getVoiceUsers() != null) cv.put(COL_VOICE_USERS, TextUtils.join(SPARATOR, channel.getVoiceUsers()));
		if (channel.getVideoUsers() != null) cv.put(COL_VIDEO_USERS, TextUtils.join(SPARATOR, channel.getVideoUsers()));
		if (channel.getDesktopUsers() != null) cv.put(COL_DESKTOP_USERS, TextUtils.join(SPARATOR, channel.getDesktopUsers()));
		return cv;
	}
	
	static void buildTable(SQLiteDatabase db) {
		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE + "(" 
			+ COL_ID + " integer PRIMARY KEY, "
			+ COL_PARENT_ID + " integer default 0, "
			+ COL_URI + " text not null, "
			+ COL_NAME + " text, "
			+ COL_TOPIC + " text, "
			+ COL_PASSWORD + " text, "
			+ COL_PROTECTED + " integer, "
			+ COL_TYPES + " integer, "
			+ COL_DISK_QUOTA + " integer, "
			+ COL_OP_PASSWORD + " text, "
			+ COL_MAX_USERS + " integer, "
			+ COL_AUDIO_CODEC + " text, "
			+ COL_AUDIO_CONFIG + " text, "
			+ COL_OPERATORS + " text, "
			+ COL_VOICE_USERS + " text, "
			+ COL_VIDEO_USERS + " text, "
			+ COL_DESKTOP_USERS + " text, "
			+ COL_REMOVED + " integer default 0"
		+ ");";
		
		final String CREATE_VIEW = "CREATE VIEW IF NOT EXISTS " + DB_VIEW + " AS "
			+ "SELECT c.*, COUNT(u." + UserDB.COL_CHANNEL_ID + ") " + COL_TOTAL_USERS + " "
			+ "FROM " + DB_TABLE + " c "
			+ "LEFT JOIN " + UserDB.DB_TABLE + " u "
			+ "ON c." + COL_ID + " = u." + UserDB.COL_CHANNEL_ID + " "
			+ "AND u." + UserDB.COL_JOINED + " = 1 "
			+ "GROUP BY c." + COL_ID;

		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_VIEW);
	}
	
	static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP VIEW IF EXISTS " + DB_VIEW);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
	}

	public synchronized void reset() {
		database.execSQL("DELETE FROM " + DB_TABLE);
	}
}
