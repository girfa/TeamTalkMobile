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
package com.girfa.apps.teamtalk4mobile.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Config {
	private SharedPreferences pref;
	private Editor editor;
	
	@SuppressLint("CommitPrefEdits")
	public Config(Context context) {
		if (context == null) return;
		pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		editor = pref.edit();
	}
	
	public boolean commit() {
		return editor.commit();
	}
	
	public boolean contains(String key) {
		return pref.contains(key);
	}
	
	public Config remove(String key) {
		editor.remove(key);
		return this;
	}
	
	public Config setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		return this;
	}
	
	public Config setFloat(String key, float value) {
		editor.putFloat(key, value);
		return this;
	}
	
	public Config setInt(String key, int value) {
		editor.putInt(key, value);
		return this;
	}
	
	public Config setLong(String key, long value) {
		editor.putLong(key, value);
		return this;
	}
	
	public Config setString(String key, String value) {
		editor.putString(key, value);
		return this;
	}
	
	public boolean getBoolean(String key, boolean defValue) {
		return pref.getBoolean(key, defValue);
	}
	
	public float getFloat(String key, float defValue) {
		return pref.getFloat(key, defValue);
	}
	
	public int getInt(String key, int defValue) {
		return pref.getInt(key, defValue);
	}
	
	public long getLong(String key, long defValue) {
		return pref.getLong(key, defValue);
	}
	
	public String getString(String key, String defValue) {
		if (Utils.isEmpty(pref.getString(key, null))) return defValue;
		return pref.getString(key, defValue);
	}
}
