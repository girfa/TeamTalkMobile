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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelper extends SQLiteOpenHelper {
	public static final String TAG = OpenHelper.class.getSimpleName();
	static final String DB_NAME = "teamtalk4mobile.db";
	static final int DB_VERSION = 1;
	
	public OpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ServerDB.buildTable(db);
		HistoryDB.buildTable(db);
		UserDB.buildTable(db);
		ChannelDB.buildTable(db);
		MessageDB.buildTable(db);
		FileDB.buildTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG,
			"Upgrading database from version " + oldVersion + " to "
			+ newVersion + ", which will destroy all old data");
		FileDB.dropTable(db);
		MessageDB.dropTable(db);
		ChannelDB.dropTable(db);
		UserDB.dropTable(db);
		HistoryDB.dropTable(db);
		ServerDB.dropTable(db);
		onCreate(db);
	}
}
