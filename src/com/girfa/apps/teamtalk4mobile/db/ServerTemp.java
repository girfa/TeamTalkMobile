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

import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.preference.Preference;

import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.utils.Config;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class ServerTemp extends Config {
	private ServerDB sdb;
	private Server server;
	private String notset;
	
	public ServerTemp(Context context, ServerDB sdb) {
		super(context);
		this.sdb = sdb;
		server = new Server();
		notset = context.getResources().getString(R.string.notset);
	}
	
	public boolean empty() {
		return remove(ServerDB.COL_NAME)
			.remove(ServerDB.COL_ADDRESS)
			.remove(ServerDB.COL_TCP_PORT)
			.remove(ServerDB.COL_UDP_PORT)
			.remove(ServerDB.COL_PASSWORD)
			.remove(ServerDB.COL_AUTH_USERNAME)
			.remove(ServerDB.COL_AUTH_PASSWORD)
			.remove(ServerDB.COL_JOIN_CHANNEL)
			.remove(ServerDB.COL_JOIN_PASSWORD)
			.commit();
	}
	
	public boolean setup(Server server) {
		Server fromDB = sdb.get(server);
		if (fromDB == null) {
			this.server.setTcpPort(ServerDB.TCP_DEF);
			this.server.setUdpPort(ServerDB.UDP_DEF);
		} else {
			this.server = fromDB;
			return setString(ServerDB.COL_NAME, fromDB.getName())
				.setString(ServerDB.COL_ADDRESS, fromDB.getAddress())
				.setString(ServerDB.COL_TCP_PORT, fromDB.getTcpPort() + "")
				.setString(ServerDB.COL_UDP_PORT, fromDB.getTcpPort() + "")
				.setString(ServerDB.COL_PASSWORD, fromDB.getPassword())
				.setString(ServerDB.COL_AUTH_USERNAME, fromDB.getAuthUsername())
				.setString(ServerDB.COL_AUTH_PASSWORD, fromDB.getAuthPassword())
				.setString(ServerDB.COL_JOIN_CHANNEL, fromDB.getJoinChannel())
				.setString(ServerDB.COL_JOIN_PASSWORD, fromDB.getJoinPassword())
				.commit();
		}
		return false;
	}

	private boolean update() {
		String name = getString(ServerDB.COL_NAME, null);
		String address = getString(ServerDB.COL_ADDRESS, null);
		int tcpPort = Integer.parseInt(getString(ServerDB.COL_TCP_PORT, "0"));
		int udpPort = Integer.parseInt(getString(ServerDB.COL_UDP_PORT, "0"));
		server.setName(name)
			.setAddress(address)
			.setTcpPort(tcpPort)
			.setUdpPort(udpPort);
		
		if (server.getId() == null) {
			server.setPassword(null)
				.setAuthUsername(null)
				.setAuthPassword(null)
				.setJoinChannel(null)
				.setJoinPassword(null);
			if (name != null && address != null && tcpPort > 0 && udpPort > 0) {
				try {
					server.setId((int) sdb.add(server));
					return true;
				} catch (SQLiteConstraintException e) {
					return false;
				}
			}
		} else {
			server.setPassword(getString(ServerDB.COL_PASSWORD, null))
				.setAuthUsername(getString(ServerDB.COL_AUTH_USERNAME, null))
				.setAuthPassword(getString(ServerDB.COL_AUTH_PASSWORD, null))
				.setJoinChannel(getString(ServerDB.COL_JOIN_CHANNEL, null))
				.setJoinPassword(getString(ServerDB.COL_JOIN_PASSWORD, null));
			try {
				sdb.update(server);
			} catch (SQLiteConstraintException ignore) {}
			return true;
		}
		return false;
	}
	
	public Server getServer() {
		return server;
	}
	
	public void updateSummary(Map<String, Preference> prefs, Preference pAuth, Preference pJoin) {
		
		if (update()) {
			pAuth.setEnabled(true);
			pJoin.setEnabled(true);
		} else {
			pAuth.setEnabled(false);
			pJoin.setEnabled(false);
		}
		
		String name = server.getName();
		String address = server.getAddress();
		int tcpPort = server.getTcpPort();
		int udpPort = server.getUdpPort();
		String password = server.getPassword();
		String authUsername = server.getAuthUsername();
		String authPassword = server.getAuthPassword();
		String joinChannel = server.getJoinChannel();
		String joinPassword = server.getJoinPassword();

		prefs.get(ServerDB.COL_NAME).setSummary(Utils.isEmpty(name) ? notset : name);
		prefs.get(ServerDB.COL_ADDRESS).setSummary(Utils.isEmpty(address) ? notset : address);
		prefs.get(ServerDB.COL_TCP_PORT).setSummary(tcpPort < 1 ? notset : tcpPort + "");
		prefs.get(ServerDB.COL_UDP_PORT).setSummary(udpPort < 1 ? notset : udpPort + "");
		prefs.get(ServerDB.COL_PASSWORD).setSummary(Utils.isEmpty(password) ? notset : password.replaceAll(".", "*"));
		prefs.get(ServerDB.COL_AUTH_USERNAME).setSummary(Utils.isEmpty(authUsername) ? notset : authUsername);
		prefs.get(ServerDB.COL_AUTH_PASSWORD).setSummary(Utils.isEmpty(authPassword) ? notset : authPassword.replaceAll(".", "*"));
		prefs.get(ServerDB.COL_JOIN_CHANNEL).setSummary(Utils.isEmpty(joinChannel) ? notset : joinChannel);
		prefs.get(ServerDB.COL_JOIN_PASSWORD).setSummary(Utils.isEmpty(joinPassword) ? notset : joinPassword.replaceAll(".", "*"));
	}
}
