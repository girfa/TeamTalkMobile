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

import java.io.File;
import java.util.List;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.girfa.apps.teamtalk4mobile.Preferences;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.ServerForm;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.db.ServerDB;
import com.girfa.apps.teamtalk4mobile.utils.Config;
import com.girfa.apps.teamtalk4mobile.utils.FileChooserOld;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ServerListFrg extends SherlockListFragment {
	public static final String TAG = ServerListFrg.class.getSimpleName();
	private ServerDB sdb;
	private List<Server> servers;
	private ListAdapter adapter;
	private Config config;
	
	public static ServerListFrg newInstance() {
		return new ServerListFrg();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		Context context = getSherlockActivity();
		config = new Config(context);
		sdb = (ServerDB) new ServerDB(context).openRead();
		adapter = new ListAdapter(context);
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		sdb.openRead();
		adapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onPause() {
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
	    inflater.inflate(R.menu.server_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.s_alpha :
				if (config.getInt(ServerDB.SERVER_SORT, 0) == ServerDB.SORT_ALPHA_ASC) {
					config.setInt(ServerDB.SERVER_SORT, ServerDB.SORT_ALPHA_DESC).commit();
				} else {
					config.setInt(ServerDB.SERVER_SORT, ServerDB.SORT_ALPHA_ASC).commit();
				}
				adapter.notifyDataSetChanged();
				break;
			case R.id.s_most :
				if (config.getInt(ServerDB.SERVER_SORT, 0) == ServerDB.SORT_USED_ASC) {
					config.setInt(ServerDB.SERVER_SORT, ServerDB.SORT_USED_DESC).commit();
				} else {
					config.setInt(ServerDB.SERVER_SORT, ServerDB.SORT_USED_ASC).commit();
				}
				adapter.notifyDataSetChanged();
				break;
			case R.id.s_recent :
				if (config.getInt(ServerDB.SERVER_SORT, 0) == ServerDB.SORT_ADDED_ASC) {
					config.setInt(ServerDB.SERVER_SORT, ServerDB.SORT_ADDED_DESC).commit();
				} else {
					config.setInt(ServerDB.SERVER_SORT, ServerDB.SORT_ADDED_ASC).commit();
				}
				adapter.notifyDataSetChanged();
				break;
			case R.id.s_newc :
				startActivity(new Intent(getSherlockActivity(), ServerForm.class));
				break;
			case R.id.s_open :
				FileChooserOld fc = new FileChooserOld();
				fc.setExtension(".tt");
				fc.setListener(new FileChooserOld.Listener() {
					@Override
					public void result(File file) {
						 try {
							Server server = sdb.read(file);
							try {
								sdb.add(server);
								adapter.notifyDataSetChanged();
							} catch (SQLiteConstraintException e) {
								e.printStackTrace();
								Toast.makeText(getSherlockActivity(), R.string.s_o_duplicate, Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							Toast.makeText(getSherlockActivity(), R.string.s_o_corrupt, Toast.LENGTH_SHORT).show();
							Log.e(TAG, "result." + e.getMessage());
						}
					}
				});
				fc.show(getFragmentManager());
				break;
			case R.id.s_pref :
				Intent pref = new Intent(getSherlockActivity(), Preferences.class);
				startActivity(pref);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent edit = new Intent(getSherlockActivity(), ServerForm.class);
		edit.putExtra(ServerDB.COL_ID, servers.get(position).getId());
		startActivity(edit);
	}
	
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public ListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return servers.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Server server = servers.get(position);
			convertView = inflater.inflate(R.layout.server_list_item, null);
			if (server.getType() == ServerDB.PUBLIC_SERVER) {
				Drawable publicServer = getResources().getDrawable(R.drawable.server_list_public);
				Utils.setBackground(convertView, publicServer);
			}
			TextView name = (TextView) convertView.findViewById(R.id.server_name);
			TextView address = (TextView) convertView.findViewById(R.id.server_address);
			name.setText(server.getName());
			address.setText(server.getAddress());
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			servers = sdb.gets(config.getInt(ServerDB.SERVER_SORT, ServerDB.SORT_ADDED_ASC));
			super.notifyDataSetChanged();
		}
	}
}
