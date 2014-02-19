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

import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileInfo;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileTransfer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class FileDB extends Database {
	public static final String TAG = FileDB.class.getSimpleName();
	public static final String FILE_PATH = "file_path";
	public static final String TRANSFERRED = "transferred";
	
	static final String DB_TABLE = "file";
	
	public static final String
		COL_ID = "_id";
	
	private static final String
		COL_NAME = "name",
		COL_SIZE = "size",
		COL_PATH = "path",
		COL_USERNAME = "username",
		COL_CHANNEL_ID = "channel_id";
		
	private static final String[] allColumns = {
		COL_ID, COL_NAME, COL_SIZE, COL_PATH, COL_USERNAME, COL_CHANNEL_ID
	};
	
	public FileDB(Context context) {
		super(context);
	}
	
	public synchronized int add(FileInfo file) {
		if (file == null) return 0;
		if (file.getId() == null || file.getId() < 0) {
			Cursor cs = database.query(DB_TABLE,
				new String[]{"min(" + COL_ID + ")"}, null, null, null, null, null);
			cs.moveToFirst();
			file.setId(cs.getInt(0) - 1);
		}
		return (int) database.insertOrThrow(DB_TABLE, null, fileToCv(file));
	}
	
	public synchronized int update(FileInfo file) {
		if (file == null) return 0;
		return database.update(DB_TABLE,
			fileToCv(file),
			COL_ID + " = ?",
			new String[]{file.getId() + ""});
	}
	
	public synchronized int remove(FileInfo file) {
		if (file == null) return 0;
		try {
			return database.delete(DB_TABLE,
				COL_NAME + " = ? AND " + COL_CHANNEL_ID + " = ?",
				new String[] {file.getFileName(), file.getChannel().getId() + ""});
		} catch (Exception e) {
			Log.e(TAG, "remove." + e.getMessage());
			return 0;
		}
	}
	
	public FileInfo get(int id) {
		return get(new FileInfo(id));
	}
	
	public FileInfo get(FileInfo file) {
		if (file == null) return null;
		try {
			Cursor cs = database.query(DB_TABLE, allColumns,
				COL_ID + " = ?",
				new String[] {file.getId() + ""},
				null, null, null);
			cs.moveToFirst();
			FileInfo result = new FileInfo();
			result.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setFileName(cs.getString(cs.getColumnIndex(COL_NAME)))
				.setFileSize(cs.getLong(cs.getColumnIndex(COL_SIZE)))
				.setUsername(cs.getString(cs.getColumnIndex(COL_USERNAME)))
				.setChannel(new Channel(cs.getInt(cs.getColumnIndex(COL_CHANNEL_ID))))
				.setFileTransfer(new FileTransfer()
					.setLocalFilePath(cs.getString(cs.getColumnIndex(COL_PATH)))
					.setChannel(result.getChannel()));
			cs.close();
			return result;
		} catch (Exception e) {
			Log.e(TAG, "get." + e.getMessage());
			return null;
		}
	}
	
	public List<FileInfo> gets(Channel channel) {
		if (channel == null) return null;
		Cursor cs = null;
		try {
			cs = database.query(DB_TABLE, allColumns,
				COL_CHANNEL_ID + " = ? AND " + COL_ID + " > 0",
				new String[] {channel.getId() + ""},
				null, null, null);
		} catch (SQLiteException e) {
			Log.e(TAG, "gets." + e.getMessage());
			return null;
		}
		
		List<FileInfo> files = new ArrayList<FileInfo>();
		cs.moveToFirst();
		while (!cs.isAfterLast()) {
			FileInfo file = new FileInfo();
			file.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setFileName(cs.getString(cs.getColumnIndex(COL_NAME)))
				.setFileSize(cs.getLong(cs.getColumnIndex(COL_SIZE)))
				.setUsername(cs.getString(cs.getColumnIndex(COL_USERNAME)))
				.setChannel(new Channel(cs.getInt(cs.getColumnIndex(COL_CHANNEL_ID))))
				.setFileTransfer(new FileTransfer()
					.setLocalFilePath(cs.getString(cs.getColumnIndex(COL_PATH)))
					.setChannel(file.getChannel()));
			files.add(file);
			cs.moveToNext();
		}
		cs.close();
		if (files.size() > 0) return files;
		return null;
	}
	
	private static ContentValues fileToCv(FileInfo file) {
		if (file == null) return null;
		ContentValues cv = new ContentValues();
		if (file.getId() != null) cv.put(COL_ID, file.getId());
		if (file.getFileName() != null) cv.put(COL_NAME, file.getFileName());
		if (file.getFileSize() != null) cv.put(COL_SIZE, file.getFileSize());
		if (file.getUsername() != null) cv.put(COL_USERNAME, file.getUsername());
		if (file.getChannel() != null) cv.put(COL_CHANNEL_ID, file.getChannel().getId());
		if (file.getFileTransfer() != null && 
				file.getFileTransfer().getLocalFilePath() != null) {
			cv.put(COL_PATH, file.getFileTransfer().getLocalFilePath());
		}
		return cv;
	}

	static void buildTable(SQLiteDatabase db) {
		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE + "(" 
			+ COL_ID + " integer PRIMARY KEY, "
			+ COL_NAME + " text not null, "
			+ COL_SIZE + " bigint, "
			+ COL_PATH + " text, "
			+ COL_USERNAME + " text, "
			+ COL_CHANNEL_ID + " integer"
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
