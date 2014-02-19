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
package com.girfa.apps.teamtalk4mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.girfa.apps.teamtalk4mobile.db.PrefChanges;
import com.girfa.apps.teamtalk4mobile.service.Connector;

@SuppressWarnings("deprecation")
public class PreferencesOld extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String TAG = PreferencesOld.class.getSimpleName();
	
	private SharedPreferences shared;
	private PrefChanges pc;
	private Connector con;
	
	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		Context context = getApplicationContext();
		shared = PreferenceManager.getDefaultSharedPreferences(context);
		pc = new PrefChanges(context);
		addPreferencesFromResource(R.xml.preferences_pref);
		con = new Connector(context, null);
		pc.connect(con);
		pc.updateSummary(findPreference(PrefChanges.NICKNAME), PrefChanges.NICKNAME);
		pc.updateSummary(findPreference(PrefChanges.GENDER), PrefChanges.GENDER);
		pc.updateSummary(findPreference(PrefChanges.MESSAGE), PrefChanges.MESSAGE);
		pc.updateSummary(findPreference(PrefChanges.MODE), PrefChanges.MODE);
		pc.updateSummary(findPreference(PrefChanges.SUBSCRIPTION), PrefChanges.SUBSCRIPTION);
	}
	
	@Override
	public void onResume() {
		con.start();
		con.bind();
		shared.registerOnSharedPreferenceChangeListener(this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		shared.unregisterOnSharedPreferenceChangeListener(this);
		con.unbind();
		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		pc.updateSummary(findPreference(key), key);
	}
}
