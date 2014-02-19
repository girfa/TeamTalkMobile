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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import com.girfa.apps.teamtalk4mobile.TeamTalk4Mobile;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.Xml;

public class ServerDB extends Database {
	public static final String TAG = ServerDB.class.getSimpleName();
	public static final String SERVER_SORT = "server_sort";
	
	public static final String SAVE_DIRECTORY = TeamTalk4Mobile.SAVE_DIRECTORY + "Server/";
	public static final int TCP_DEF = 10333;
	public static final int UDP_DEF = 10333;
	public static final int PUBLIC_SERVER = 1;
	public static final int PRIVATE_SERVER = 2;
	
	public static final int 
		SORT_ALPHA_ASC = 1,
		SORT_ALPHA_DESC = 2,
		SORT_USED_ASC = 4,
		SORT_USED_DESC = 8,
		SORT_ADDED_ASC = 16,
		SORT_ADDED_DESC = 32;
	
	private static final String DB_TABLE = "server";
	
	public static final String 
		COL_ID = "_id",
		COL_NAME = "name",
		COL_ADDRESS = "address",
		COL_TCP_PORT = "tcp_port",
		COL_UDP_PORT = "udp_port",
		COL_PASSWORD = "password",
		COL_AUTH_USERNAME = "auth_username",
		COL_AUTH_PASSWORD = "auth_password",
		COL_JOIN_CHANNEL = "join_channel",
		COL_JOIN_PASSWORD = "join_password";
	
	private static final String 
		COL_TYPE = "type",
		COL_USED = "used";
	
	
	private static String[] allColumns = {
		COL_ID, COL_NAME, COL_PASSWORD, COL_ADDRESS, COL_TCP_PORT,
		COL_UDP_PORT, COL_AUTH_USERNAME, COL_AUTH_PASSWORD,
		COL_JOIN_CHANNEL, COL_JOIN_PASSWORD, COL_TYPE
	};
	private static String[] listColumns = {COL_ID, COL_NAME, COL_ADDRESS, COL_TYPE};
	
	public ServerDB(Context context) {
		super(context);
	}
	
	public synchronized int add(Server server) {
		if (server == null) return 0;
		return (int) database.insertOrThrow(DB_TABLE, null, serverToCv(server));
	}
	
	public synchronized int update(Server server) {
		if (server == null) return 0;
		return database.update(DB_TABLE,
			serverToCv(server),
			COL_ID + " = ?",
			new String[]{server.getId() + ""});
	}
	
	public synchronized void use(Server server) {
		if (server == null) return;
		database.execSQL(
			"UPDATE " + DB_TABLE 
			+ " SET " + COL_USED + " = " + COL_USED + " + 1 WHERE "
			+ COL_ID + " = ?", new Object[]{String.valueOf(server.getId())});
	}
	
	public synchronized int remove(Server server) {
		if (server == null) return 0;
		return database.delete(DB_TABLE, COL_ID + " = ?", new String[] {server.getId() + ""});
	}
	
	public Server read(File file) throws IOException, ParserConfigurationException, SAXException {
		if (file == null) return null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		Element teamtalk = (Element) doc.getElementsByTagName("teamtalk").item(0);
		Element host = (Element) teamtalk.getElementsByTagName("host").item(0);
		Element auth = (Element) host.getElementsByTagName("auth").item(0);
		Element join = (Element) host.getElementsByTagName("join").item(0);
		Server server = new Server();
		try {
			server.setAddress(host.getElementsByTagName("address").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException ignore) {}
		try {
			server.setName(host.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException e) {
			server.setName(server.getAddress());
		}
		try {
			server.setTcpPort(Integer.parseInt(host.getElementsByTagName("tcpport").item(0).getFirstChild().getNodeValue()));
		} catch (NullPointerException ignore) {}
		try {
			server.setUdpPort(Integer.parseInt(host.getElementsByTagName("udpport").item(0).getFirstChild().getNodeValue()));
		} catch (NullPointerException ignore) {}
		try {
			server.setPassword(host.getElementsByTagName("password").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException ignore) {}
		try {
			server.setAuthUsername(auth.getElementsByTagName("username").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException ignore) {}
		try {
			server.setAuthPassword(auth.getElementsByTagName("password").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException ignore) {}
		try {
			server.setJoinChannel(join.getElementsByTagName("channel").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException ignore) {}
		try {
			server.setJoinPassword(join.getElementsByTagName("password").item(0).getFirstChild().getNodeValue());
		} catch (NullPointerException ignore) {}
		return server;
	}
	
	public String write(Server server) throws IOException {
		if (server == null) return null;
		File dirs = new File(SAVE_DIRECTORY);
		if (!dirs.exists()) dirs.mkdirs();
		File file = new File(dirs, server.getName().replaceAll("[\\\\/:*?\\\"<>|]", "") + ".tt");
		if (file.exists()) file.delete();
		file.createNewFile();
		OutputStreamWriter output = new OutputStreamWriter(
			new FileOutputStream(file), 
			Charset.forName("UTF-8").newEncoder());
		try {
			XmlSerializer xml = Xml.newSerializer();
			xml.setOutput(output);
			xml.startDocument("UTF-8", null);
			xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			xml.startTag("", "teamtalk");
			xml.attribute("", "version", "4.0"	);
			xml.startTag("", "host");
			xml.startTag("", "name");
			xml.text(server.getName());
			xml.endTag("", "name");
			xml.startTag("", "password");
			if (server.getPassword() != null) xml.text(server.getPassword());
			xml.endTag("", "password");
			xml.startTag("", "address");
			xml.text(server.getAddress());
			xml.endTag("", "address");
			xml.startTag("", "tcpport");
			xml.text(server.getTcpPort() + "");
			xml.endTag("", "tcpport");
			xml.startTag("", "udpport");
			xml.text(server.getUdpPort()+ "");
			xml.endTag("", "udpport");
			xml.startTag("", "auth");
			xml.startTag("", "username");
			if (server.getAuthUsername() != null) xml.text(server.getAuthUsername());
			xml.endTag("", "username");
			xml.startTag("", "password");
			if (server.getAuthPassword() != null) xml.text(server.getAuthPassword());
			xml.endTag("", "password");
			xml.endTag("", "auth");
			xml.startTag("", "join");
			xml.startTag("", "channel");
			if (server.getJoinChannel() != null) xml.text(server.getJoinChannel());
			xml.endTag("", "channel");
			xml.startTag("", "password");
			if (server.getJoinPassword() != null) xml.text(server.getJoinPassword());
			xml.endTag("", "password");
			xml.endTag("", "join");
			xml.endTag("", "host");
			xml.endTag("", "teamtalk");
			xml.endDocument();
		} catch (NullPointerException ignore) {}
		output.close();
		return file.getAbsolutePath();
	}
	
	public Server get(int id) {
		return get(new Server().setId(id));
	}
	
	public Server get(Server server) {
		if (server == null) return null;
		try {
			Cursor cs = null;
			if (server.getId() != null) {
				cs = database.query(DB_TABLE, allColumns,
					COL_ID + " = ?",
					new String[] {server.getId() + ""},
					null, null, null);
			} else if (server.getName() != null && server.getAddress() != null) {
				cs = database.query(DB_TABLE, allColumns,
					COL_NAME + " = ? AND "
					+ COL_ADDRESS + " = ? AND "
					+ COL_TCP_PORT + " = ? AND "
					+ COL_UDP_PORT + " = ?",
					new String[] {
						server.getName(),
						server.getAddress(),
						server.getTcpPort() + "",
						server.getUdpPort() + ""},
					null, null, null);
				Log.e(TAG, "here");
			}
			cs.moveToFirst();
			Server result = new Server()
				.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setName(cs.getString(cs.getColumnIndex(COL_NAME)))
				.setPassword(cs.getString(cs.getColumnIndex(COL_PASSWORD)))
				.setAddress(cs.getString(cs.getColumnIndex(COL_ADDRESS)))
				.setTcpPort(cs.getInt(cs.getColumnIndex(COL_TCP_PORT)))
				.setUdpPort(cs.getInt(cs.getColumnIndex(COL_UDP_PORT)))
				.setAuthUsername(cs.getString(cs.getColumnIndex(COL_AUTH_USERNAME)))
				.setAuthPassword(cs.getString(cs.getColumnIndex(COL_AUTH_PASSWORD)))
				.setJoinChannel(cs.getString(cs.getColumnIndex(COL_JOIN_CHANNEL)))
				.setJoinPassword(cs.getString(cs.getColumnIndex(COL_JOIN_PASSWORD)))
				.setType(cs.getInt(cs.getColumnIndex(COL_TYPE)));
			cs.close();
			return result;
		} catch (Exception e) {
			Log.e(TAG, "get." + e.getMessage());
			return null;
		}
	}
	
	public List<Server> gets(int sort) {
		String order = null;
		switch (sort) {
			case SORT_ALPHA_ASC :
				order = COL_NAME + " COLLATE NOCASE" ;
				break;
			case SORT_ALPHA_DESC :
				order = COL_NAME + " COLLATE NOCASE DESC" ;
				break;
			case SORT_USED_ASC :
				order = COL_USED + " DESC" ;
				break;
			case SORT_USED_DESC :
				order = COL_USED;
				break;
			case SORT_ADDED_ASC :
				order = COL_ID + " DESC" ;
				break;
			case SORT_ADDED_DESC :
				order = COL_ID;
		}
		Cursor cs = null;
		try {
			cs = database.query(DB_TABLE, listColumns,
					null, null, null, null, order);
		} catch (SQLiteException e) {
			Log.e(TAG, "gets." + e.getMessage());
			return null;
		}
		
		List<Server> servers = new ArrayList<Server>();
		cs.moveToFirst();
		while (!cs.isAfterLast()) {
			Server server = new Server()
				.setId(cs.getInt(cs.getColumnIndex(COL_ID)))
				.setName(cs.getString(cs.getColumnIndex(COL_NAME)))
				.setAddress(cs.getString(cs.getColumnIndex(COL_ADDRESS)))
				.setType(cs.getInt(cs.getColumnIndex(COL_TYPE)));
			servers.add(server);
			cs.moveToNext();
		}
		cs.close();
		if (servers.size() > 0) return servers;
		return null;
	}
	
	private static ContentValues serverToCv(Server server) {
		if (server == null) return null;
		ContentValues cv = new ContentValues();
		if (server.getName() != null) cv.put(COL_NAME, server.getName());
		if (server.getAddress() != null) cv.put(COL_ADDRESS, server.getAddress());
		if (server.getTcpPort() != null) cv.put(COL_TCP_PORT, server.getTcpPort());
		if (server.getUdpPort() != null) cv.put(COL_UDP_PORT, server.getUdpPort());
		if (server.getAuthUsername() != null) cv.put(COL_AUTH_USERNAME, server.getAuthUsername());
		if (server.getAuthPassword() != null) cv.put(COL_AUTH_PASSWORD, server.getAuthPassword());
		if (server.getJoinChannel() != null) cv.put(COL_JOIN_CHANNEL, server.getJoinChannel());
		if (server.getJoinPassword() != null) cv.put(COL_JOIN_PASSWORD, server.getJoinPassword());
		if (server.getType() != null) cv.put(COL_TYPE, server.getType());
		return cv;
	}
	
	static void buildTable(SQLiteDatabase db) {
		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE + "(" 
			+ COL_ID + " integer PRIMARY KEY autoincrement, "
			+ COL_NAME + " text not null, "
			+ COL_PASSWORD + " text, "
			+ COL_ADDRESS + " text not null, "
			+ COL_TCP_PORT + " integer not null, "
			+ COL_UDP_PORT + " integer not null, "
			+ COL_AUTH_USERNAME + " text, "
			+ COL_AUTH_PASSWORD + " text, "
			+ COL_JOIN_CHANNEL + " text, "
			+ COL_JOIN_PASSWORD + " text, "
			+ COL_TYPE + " integer default " + PRIVATE_SERVER + ", "
			+ COL_USED + " integer default 0, "
			+ "UNIQUE(" 
				+ COL_NAME + ", "
				+ COL_ADDRESS + ", "
				+ COL_TCP_PORT + ", "
				+ COL_UDP_PORT + ")"
		+ ");";
		
		db.execSQL(CREATE_TABLE);
		
		Server ps1 = new Server()
			.setName("TeamTalk 4 Official Server (EU)")
			.setAddress("tt4eu.bearware.dk")
			.setTcpPort(TCP_DEF).setUdpPort(UDP_DEF)
			.setAuthUsername("guest").setAuthPassword("guest")
			.setJoinChannel("/").setType(PUBLIC_SERVER);
		db.insert(DB_TABLE, null, serverToCv(ps1));
		
		Server ps2 = new Server()
			.setName("TeamTalk 4 Official Server (US)")
			.setAddress("tt4us.bearware.dk")
			.setTcpPort(TCP_DEF).setUdpPort(UDP_DEF)
			.setAuthUsername("guest").setAuthPassword("guest")
			.setJoinChannel("/").setType(PUBLIC_SERVER);
		db.insert(DB_TABLE, null, serverToCv(ps2));
		
		Server ps3 = new Server()
			.setName("Trapeziux.com Public Server (US)")
			.setAddress("trapeziux.com")
			.setTcpPort(TCP_DEF).setUdpPort(UDP_DEF)
			.setJoinChannel("/").setType(PUBLIC_SERVER);
		db.insert(DB_TABLE, null, serverToCv(ps3));
	}
	
	static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
	}
}
