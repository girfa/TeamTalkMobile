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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.ServerProperties;
import com.girfa.apps.teamtalk4mobile.api.adapter.TextMessage;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.service.Response;
import com.girfa.apps.teamtalk4mobile.service.Stream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class HistoryDB extends Database {
	public static final String TAG = HistoryDB.class.getSimpleName();
	
	static final String DB_TABLE = "history";
	private static final String
		COL_ID = "_id",
		COL_COMMAND = "command",
		COL_MESSAGE = "message";
	
	private static final String[] allColumns = {
		COL_ID, COL_COMMAND, COL_MESSAGE
	};
	
	private Context context;
	
	public HistoryDB(Context context) {
		super(context);
		this.context = context;
	}
	
	public synchronized int add(Response command, String message) {
		if (command == null) return 0;
		ContentValues cv = new ContentValues();
		cv.put(COL_COMMAND, command.toString());
		cv.put(COL_MESSAGE, message);
		return (int) database.insertOrThrow(DB_TABLE, null, cv);
	}
	
	public List<Map<Response, Object>> getChats() {
		Cursor cs = null;
		try {
			cs = database.query(DB_TABLE, allColumns,
				null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.e(TAG, "getChats." + e.getMessage());
			return null;
		}
		List<Map<Response, Object>> logs = new ArrayList<Map<Response,Object>>();
		cs.moveToFirst();
		ChannelDB cdb = (ChannelDB) new ChannelDB(context).openRead();
		UserDB udb = (UserDB) new UserDB(context).openRead();
		while (!cs.isAfterLast()) {
			Response res = Response.valueOf(cs.getString(cs.getColumnIndex(COL_COMMAND)));
			Map<String, Object> map = Stream.decode(cs.getString(cs.getColumnIndex(COL_MESSAGE)));
			HashMap<Response, Object> item = new HashMap<Response, Object>();
			User us;
			switch (res) {
				case serverupdate :
					item.put(res, new ServerProperties(map));
					break;
				case adduser :
					us = new User(map);
					User cus = new User();
					cus.dump(context);
					if (us.getId().equals(cus.getId())) {
						us.setChannel(cdb.get(us.getChannel()));
					} else {
						us.setChannel(null);
					}
					item.put(res, us);
					break;
				case removeuser :
					item.put(res, udb.get(new User(map)));
					break;
				case joined :
					item.put(res, cdb.get(new Channel(map)));
					break;
				case left :
					item.put(res, new Channel(map));
					break;
				case messagedeliver :
					TextMessage tm = new TextMessage(map);
					tm.setSrcUser(udb.get(tm.getSrcUser()));
					item.put(res, tm);
					break;
				default:
					break;
			}
			logs.add(item);
			cs.moveToNext();
		}
		udb.close();
		cdb.close();
		cs.close();
		if (logs.size() > 0) return logs;
		return null;
	}
	
	static void buildTable(SQLiteDatabase db) {
		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE + "(" 
			+ COL_ID + " integer PRIMARY KEY autoincrement, "
			+ COL_COMMAND + " text not null, "
			+ COL_MESSAGE + " text"
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
