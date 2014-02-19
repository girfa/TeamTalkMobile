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
package com.girfa.apps.teamtalk4mobile.fragment;

import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.db.PrefChanges;
import com.girfa.apps.teamtalk4mobile.service.Connector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

@SuppressLint("NewApi")
public class PreferencesFrg extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	public static final String TAG = PreferencesFrg.class.getSimpleName();
	
	private SharedPreferences shared;
	private PrefChanges pc;
	private Connector con;

	public static PreferencesFrg newInstance() {
		return new PreferencesFrg();
	}
	
	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		setRetainInstance(true);
		Context context = getActivity().getApplicationContext();
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
