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

import java.io.IOException;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.db.ServerDB;
import com.girfa.apps.teamtalk4mobile.db.ServerTemp;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

@SuppressWarnings("deprecation")
public class ServerFormOld extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	private ServerDB sdb;
	private Server server;
	private ServerTemp temp;
	private SharedPreferences shared;
	
	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		Context context = getApplicationContext();
		getSherlock().getActionBar().setDisplayHomeAsUpEnabled(true);
		sdb = (ServerDB) new ServerDB(context).openWrite();
		temp = new ServerTemp(context, sdb);
		server = new Server();
		if (saved == null) {
			temp.empty();
			server.setId(getIntent().getIntExtra(ServerDB.COL_ID, 0));
		} else {
			server.setId(saved.getInt(ServerDB.COL_ID));
		}
		shared = PreferenceManager.getDefaultSharedPreferences(context);
		temp.setup(server);
		addPreferencesFromResource(R.xml.server_form_pref);
		onSharedPreferenceChanged(null, null);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(ServerDB.COL_ID, server.getId());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume() {
		sdb.openWrite();
		shared.registerOnSharedPreferenceChangeListener(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		shared.unregisterOnSharedPreferenceChangeListener(this);
		sdb.close();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		sdb.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.server_form, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				NavUtils.navigateUpFromSameTask(this);
				break;
			case R.id.s_connect :
				startActivity(new Intent(this, MainActivity.class)
					.putExtra(ServerDB.COL_ID, server.getId()));
				break;
			case R.id.s_save :
				try {
					String msg = getResources().getString(R.string.s_s_done)
							.replace("[d]", sdb.write(server));
					Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case R.id.s_remove :
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.s_r_title);
				builder.setMessage(R.string.s_r_msg);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						sdb.remove(server);
						finish();
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
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
		return super.onPrepareOptionsMenu(menu);
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
		supportInvalidateOptionsMenu();
		String name = server.getName();
		if (!Utils.isEmpty(name)) {
			setTitle(name);
		}
	}
}
