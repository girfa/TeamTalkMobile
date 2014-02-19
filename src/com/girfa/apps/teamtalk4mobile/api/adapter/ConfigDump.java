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
package com.girfa.apps.teamtalk4mobile.api.adapter;

import com.girfa.apps.teamtalk4mobile.utils.Config;

import android.content.Context;

public abstract class ConfigDump {
	private Config config;
	
	public abstract void reset();
	
	protected final void remove(String key) {
		config.remove(key).commit();
	}
	
	protected final boolean isDump() {
		return config != null;
	}
	
	public final ConfigDump dump(Context context) {
		if (context != null) this.config = new Config(context);
		return this;
	}
	
	protected final void setBoolean(String key, boolean value) {
		if (config != null) config.setBoolean(key, value).commit();
	}
	
	protected final void setFloat(String key, float value) {
		if (config != null) config.setFloat(key, value).commit();
	}
	
	protected final void setInt(String key, int value) {
		if (config != null) config.setInt(key, value).commit();
	}
	
	protected final void setLong(String key, long value) {
		if (config != null) config.setLong(key, value).commit();
	}
	
	protected final void setString(String key, String value) {
		if (config != null) config.setString(key, value).commit();
	}
	
	protected final boolean getBoolean(String key, boolean defValue) {
		if (config == null) return defValue;
		return config.getBoolean(key, defValue);
	}
	
	protected final float getFloat(String key, float defValue) {
		if (config == null) return defValue;
		return config.getFloat(key, defValue);
	}
	
	protected final int getInt(String key, int defValue) {
		if (config == null) return defValue;
		return config.getInt(key, defValue);
	}
	
	protected final long getLong(String key, long defValue) {
		if (config == null) return defValue;
		return config.getLong(key, defValue);
	}
	
	protected final String getString(String key, String defValue) {
		if (config == null) return defValue;
		return config.getString(key, defValue);
	}
}
