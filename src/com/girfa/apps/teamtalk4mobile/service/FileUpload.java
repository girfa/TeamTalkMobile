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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

import com.girfa.apps.teamtalk4mobile.api.adapter.FileInfo;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileTransfer;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class FileUpload extends Thread {
	public static final String TAG = FileDownload.class.getSimpleName();
	public static final int DIGITS = 3;
	
	private volatile FileListener listener;
	private volatile Server server;
	private volatile FileInfo fileInfo;
	private volatile FileTransfer transfer;
	
	public FileUpload(FileListener listener, Server server) {
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
			pw.println(Request.sendfile + " transferid=" + transfer.getId());
			
			if (Response.valueOf(br.readLine().split(" ", 2)[0])
				.equals(Response.filedeliver)) {
				
				BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
				File file = new File(transfer.getLocalFilePath());
				
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				byte[] bytes = new byte[socket.getSendBufferSize()];
				
				long temp = 0;
				long size = 0;
			    long part = fileInfo.getFileSize() / Utils.minimize(fileInfo.getFileSize(), DIGITS);
			    int count;
			    
			    try {
	    			listener.fileStart(fileInfo, transfer);
	    		} catch (Exception e) {
	    			Log.e(TAG, "run." + e.getMessage());
	    		}
			    
			    while (!isInterrupted() && (count = bis.read(bytes)) > 0) {
					temp += count;
					size += count;
					bis.read(bytes, 0, count);
					bos.write(bytes, 0, count);
					transfer.setTransferred(size);
					if (temp > part) {
			    		temp = temp % part;
			    		try {
			    			listener.fileProgress(fileInfo, transfer);
			    		} catch (Exception e) {
			    			Log.e(TAG, "run." + e.getMessage());
			    		}
			    	}
				}
			    Log.w(TAG, br.readLine());
			    bos.flush();
			    bis.close();
			    fis.close();
			    bos.close();
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
