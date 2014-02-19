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
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.TextMessage;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;

public class MessageDB extends Database {
	public static final String TAG = MessageDB.class.getSimpleName();
	
	static final String DB_TABLE = "message";
	private static final String DB_VIEW = "message_view";
	
	public static final String 
		COL_TYPE = "type",
		COL_DEST_USER_ID = "dest_user_id",
		COL_DEST_CHANNEL_ID = "dest_channel_id",
		COL_CONTENT = "message";
	private static final String
		COL_ID = "_id",
		COL_SRC_USER_ID = "src_user_id",
		COL_SRC_USER_NICKNAME = "src_user_nickname",
		COL_DEST_USER_NICKNAME = "dest_user_nickname";
	
	private static String[] allColumns = {
		COL_ID, COL_TYPE,
		COL_SRC_USER_ID, COL_SRC_USER_NICKNAME,
		COL_DEST_USER_ID, COL_DEST_USER_NICKNAME,
		COL_DEST_CHANNEL_ID,
		COL_CONTENT
	};
	
	private Context context;

	public MessageDB(Context context) {
		super(context);
		this.context = context;
	}
	
	public synchronized int add(TextMessage message) {
		if (message == null) return 0;
		if (message.getDestChannel() != null && message.getDestChannel().getId() == 0) {
			ChannelDB cdb = (ChannelDB) new ChannelDB(context).openRead();	
			message.setDestChannel(cdb.get(message.getDestChannel().getURI()));
			cdb.close();
		}
		return (int) database.insertOrThrow(DB_TABLE, null, messageToCv(message));
	}
	
	public List<TextMessage> gets(User user) {
		if (user == null) return null;
		List<TextMessage> messages = new ArrayList<TextMessage>();
		Cursor cs = null;
		try {
			cs = database.query(DB_VIEW, allColumns,
				COL_TYPE + " = ? AND (" + COL_SRC_USER_ID + " = ? OR " + COL_DEST_USER_ID + " = ?)",
				new String[]{
					TextMessageType.USER.value + "",
					user.getId() + "",
					user.getId() + ""
				}, null, null, null);
		} catch (SQLiteException e) {
			Log.e(TAG, "gets." + e.getMessage());
			return null;
		}
		cs.moveToFirst();
		while (!cs.isAfterLast()) {
			TextMessage message = new TextMessage()
				.setTextMessageType(TextMessageType.valueOf(cs.getInt(cs.getColumnIndex(COL_TYPE))));
			message.setSrcUser(new User()
					.setId(cs.getInt(cs.getColumnIndex(COL_SRC_USER_ID)))
					.setNickname(cs.getString(cs.getColumnIndex(COL_SRC_USER_NICKNAME))))
				.setDestUser(new User()
					.setId(cs.getInt(cs.getColumnIndex(COL_DEST_USER_ID)))
					.setNickname(cs.getString(cs.getColumnIndex(COL_DEST_USER_NICKNAME))))
				.setDestChannel(new Channel(cs.getInt(cs.getColumnIndex(COL_DEST_CHANNEL_ID))))
				.setContent(cs.getString(cs.getColumnIndex(COL_CONTENT)));
			messages.add(message);
			cs.moveToNext();
		}
		cs.close();
		if (messages.size() > 0) return messages;
		return null;
	}
	
	public List<TextMessage> gets(Channel channel) {
		if (channel == null) return null;
		Cursor cs = null;
		try {
			cs = database.query(DB_VIEW, allColumns,
				COL_TYPE + " = ? AND " + COL_DEST_CHANNEL_ID + " = ?",
				new String[]{
					TextMessageType.CHANNEL.value + "",
					channel.getId() + ""
				}, null, null, null);
		} catch (SQLiteException e) {
			Log.e(TAG, "gets." + e.getMessage());
			return null;
		}
		List<TextMessage> messages = new ArrayList<TextMessage>();
		cs.moveToFirst();
		while (!cs.isAfterLast()) {
			TextMessage message = new TextMessage()
				.setTextMessageType(TextMessageType.valueOf(cs.getInt(cs.getColumnIndex(COL_TYPE))));
			message.setSrcUser(new User()
					.setId(cs.getInt(cs.getColumnIndex(COL_SRC_USER_ID)))
					.setNickname(cs.getString(cs.getColumnIndex(COL_SRC_USER_NICKNAME))))
				.setDestUser(new User()
					.setId(cs.getInt(cs.getColumnIndex(COL_DEST_USER_ID)))
					.setNickname(cs.getString(cs.getColumnIndex(COL_DEST_USER_NICKNAME))))
				.setDestChannel(new Channel(cs.getInt(cs.getColumnIndex(COL_DEST_CHANNEL_ID))))
				.setContent(cs.getString(cs.getColumnIndex(COL_CONTENT)));
			messages.add(message);
			cs.moveToNext();
		}
		cs.close();
		if (messages.size() > 0) return messages;
		return null;
	}
	
	private static ContentValues messageToCv(TextMessage message) {
		if (message == null) return null;
		ContentValues cv = new ContentValues();
		if (message.getTextMessageType() != null) cv.put(COL_TYPE, message.getTextMessageType().value);
		if (message.getSrcUser() != null) cv.put(COL_SRC_USER_ID, message.getSrcUser().getId());
		if (message.getDestUser() != null) cv.put(COL_DEST_USER_ID, message.getDestUser().getId());
		if (message.getDestChannel() != null) cv.put(COL_DEST_CHANNEL_ID, message.getDestChannel().getId());
		cv.put(COL_CONTENT, message.getContent());
		return cv;
	}
	
	static void buildTable(SQLiteDatabase db) {
		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE + "(" 
			+ COL_ID + " integer PRIMARY KEY, "
			+ COL_TYPE + " integer not null, "
			+ COL_SRC_USER_ID + " integer, "
			+ COL_DEST_USER_ID + " integer, "
			+ COL_DEST_CHANNEL_ID + " integer, "
			+ COL_CONTENT + " text"
		+ ");";
		
		final String CREATE_VIEW = "CREATE VIEW IF NOT EXISTS " + DB_VIEW + " AS "
			+ "SELECT m.*, "
				+ "s." + UserDB.COL_NICKNAME + " " + COL_SRC_USER_NICKNAME + ", "
				+ "d." + UserDB.COL_NICKNAME + " " + COL_DEST_USER_NICKNAME + " "
			+ "FROM " + DB_TABLE + " m "
			+ "LEFT JOIN " + UserDB.DB_TABLE + " s "
				+ " ON m." + COL_SRC_USER_ID + " = s." + UserDB.COL_ID + " "
			+ "LEFT JOIN " + UserDB.DB_TABLE + " d "
				+ " ON m." + COL_DEST_USER_ID + " = d." + UserDB.COL_ID + " ";

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
