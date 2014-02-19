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

import java.io.IOException;
import java.util.HashMap;

import com.girfa.apps.teamtalk4mobile.MainActivity;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.db.ServerDB;
import com.girfa.apps.teamtalk4mobile.db.ServerTemp;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ServerFormFrg extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	public static final String TAG = ServerFormFrg.class.getSimpleName();
	private ServerDB sdb;
	private Server server;
	private ServerTemp temp;
	private SharedPreferences shared;

	public static ServerFormFrg newInstance(Bundle data) {
		ServerFormFrg sff = new ServerFormFrg();
		sff.setArguments(data);
		return sff;
	}
	
	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		Context context = getActivity().getApplicationContext();
		sdb = (ServerDB) new ServerDB(context).openWrite();
		temp = new ServerTemp(context, sdb);
		temp.empty();
		server = new Server().setId(getActivity().getIntent().getIntExtra(ServerDB.COL_ID, 0));
		shared = PreferenceManager.getDefaultSharedPreferences(context);
		temp.setup(server);
		addPreferencesFromResource(R.xml.server_form_pref);
	}
	
	@Override
	public void onResume() {
		sdb.openWrite();
		shared.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(null, null);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		shared.unregisterOnSharedPreferenceChangeListener(this);
		sdb.close();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		sdb.close();
		super.onDestroy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.server_form, menu);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem miC = menu.findItem(R.id.s_connect);
		MenuItem miS = menu.findItem(R.id.s_save);
		MenuItem miR = menu.findItem(R.id.s_remove);
		boolean enabled = server.getId() != null;
		miC.getIcon().setAlpha(enabled ? 255 : 63);
		miS.getIcon().setAlpha(enabled ? 255 : 63);
		miR.getIcon().setAlpha(enabled ? 255 : 63);
		miC.setEnabled(enabled);
		miS.setEnabled(enabled);
		miR.setEnabled(enabled);
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				NavUtils.navigateUpFromSameTask(getActivity());
				break;
			case R.id.s_connect :
				startActivity(new Intent(getActivity(), MainActivity.class)
					.putExtra(ServerDB.COL_ID, server.getId()));
				break;
			case R.id.s_save :
				try {
					String msg = getResources().getString(R.string.s_s_done)
						.replace("[d]", sdb.write(server));
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case R.id.s_remove :
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.s_r_title);
				builder.setMessage(R.string.s_r_msg);
				builder.setIcon(R.drawable.ab_warning);
				builder.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						sdb.remove(server);
						getActivity().finish();
					}
				});
				builder.setNegativeButton(R.string.cancel, null);
				builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyIgnore) {
		Preference pAuth = findPreference(getResources().getString(R.string.s_auth));
		Preference pJoin = findPreference(getResources().getString(R.string.s_join));
		
		HashMap<String, Preference> prefs = new HashMap<String, Preference>();
		prefs.put(ServerDB.COL_NAME, findPreference(ServerDB.COL_NAME));
		prefs.put(ServerDB.COL_ADDRESS, findPreference(ServerDB.COL_ADDRESS));
		prefs.put(ServerDB.COL_TCP_PORT, findPreference(ServerDB.COL_TCP_PORT));
		prefs.put(ServerDB.COL_UDP_PORT, findPreference(ServerDB.COL_UDP_PORT));
		prefs.put(ServerDB.COL_PASSWORD, findPreference(ServerDB.COL_PASSWORD));
		prefs.put(ServerDB.COL_AUTH_USERNAME, findPreference(ServerDB.COL_AUTH_USERNAME));
		prefs.put(ServerDB.COL_AUTH_PASSWORD, findPreference(ServerDB.COL_AUTH_PASSWORD));
		prefs.put(ServerDB.COL_JOIN_CHANNEL, findPreference(ServerDB.COL_JOIN_CHANNEL));
		prefs.put(ServerDB.COL_JOIN_PASSWORD, findPreference(ServerDB.COL_JOIN_PASSWORD));
		
		temp.updateSummary(prefs, pAuth, pJoin);
		
		server = temp.getServer();
		getActivity().invalidateOptionsMenu();
		String name = server.getName();
		if (!Utils.isEmpty(name)) {
			getActivity().setTitle(name);
		}
	}
}
