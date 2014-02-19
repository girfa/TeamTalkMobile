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
package com.girfa.apps.teamtalk4mobile.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.util.Log;

import com.girfa.apps.teamtalk4mobile.TeamTalk4Mobile;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileInfo;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileTransfer;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class FileDownload extends Thread {
	public static final String TAG = FileDownload.class.getSimpleName();
	public static final String SAVE_DIRECTORY = TeamTalk4Mobile.SAVE_DIRECTORY + "Download/";
	public static final int DIGITS = 3;
	
	private volatile FileListener listener;
	private volatile Server server;
	private volatile FileInfo fileInfo;
	private volatile FileTransfer transfer;
	
	public FileDownload(FileListener listener, Server server) {
		this.listener = listener;
		this.server = server;
	}
	
	public void start(FileInfo fileInfo, FileTransfer transfer) {
		this.fileInfo = fileInfo;
		this.transfer = transfer;
		start();
	}
	
	@Override
	public void run() {
		if (fileInfo == null) return;
		if (transfer == null) return;
		try {
			Socket socket = new Socket(server.getAddress(), server.getTcpPort());
			InputStream input = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			
			br.readLine();
			pw.println(Request.recvfile + " transferid=" + transfer.getId());
			
			if (Response.valueOf(br.readLine().split(" ", 2)[0])
				.equals(Response.fileready)) {
				
				pw.println(Request.filedeliver);
				
				File dirs = new File(SAVE_DIRECTORY);
				if (!dirs.exists()) dirs.mkdirs();
				File file = new File(dirs, fileInfo.getFileName());
				if (file.exists()) file.delete();
				file.createNewFile();
				transfer.setLocalFilePath(file.getAbsolutePath());
				
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				byte[] bytes = new byte[socket.getReceiveBufferSize()];
				
				long temp = 0;
				long size = 0;
			    long part = fileInfo.getFileSize() / Utils.minimize(fileInfo.getFileSize(), DIGITS);
			    int count;
			    
			    try {
	    			listener.fileStart(fileInfo, transfer);
	    		} catch (Exception e) {
	    			Log.e(TAG, "run." + e.getMessage());
	    		}
			    
			    while (!isInterrupted() && (count = input.read(bytes)) > 0) {
			    	temp += count;
			    	size += count;
			    	transfer.setTransferred(size);
			    	if (temp > part) {
			    		temp = temp % part;
			    		try {
			    			listener.fileProgress(fileInfo, transfer);
			    		} catch (Exception e) {
			    			Log.e(TAG, "run." + e.getMessage());
			    		}
			    	}
			        bos.write(bytes, 0, count);
			    }
			    bos.flush();
			    bos.close();
			    fos.close();
			}
			pw.close();
			br.close();
			input.close();
			socket.close();
		} catch (Exception e) {
			Log.e(TAG, "run." + e.getMessage());
		} finally {
			try {
				if (!isInterrupted()) listener.fileFinish(fileInfo, transfer);
    		} catch (Exception e) {
    			Log.e(TAG, "run." + e.getMessage());
    		}
		}
	}
}
