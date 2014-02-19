package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.Map;

public class FileInfo {
	private Integer id;
	private String fileName;
	private Long fileSize;
	private String username;
	private Channel channel;
	private FileTransfer fileTransfer;
	
	private static final String
		FILE_ID = "fileid",
		FILE_NAME = "filename",
		FILE_SIZE = "filesize",
		OWNER = "owner",
		CHANNEL_ID = "channelid";
	
	public FileInfo() {}
	
	public FileInfo(Map<String, Object> map) {
		build(map);
	}
	
	public FileInfo(Integer id) {
		setId(id);
	}

	public FileInfo build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(FILE_ID)) {
			setId(Integer.valueOf(map.get(FILE_ID) + ""));
		}
		if (map.containsKey(FILE_NAME)) {
			setFileName(map.get(FILE_NAME) + "");
		}
		if (map.containsKey(FILE_SIZE)) {
			setFileSize(Long.valueOf(map.get(FILE_SIZE) + ""));
		}
		if (map.containsKey(OWNER)) {
			setUsername(map.get(OWNER) + "");
		}
		if (map.containsKey(CHANNEL_ID)) {
			setChannel(new Channel(Integer.valueOf(map.get(CHANNEL_ID) + "")));
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FileInfo) return this.getId().equals(((FileInfo) o).getId());
		return super.equals(o);
	}
	
	public FileInfo setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public FileInfo setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	public FileInfo setFileSize(Long fileSize) {
		this.fileSize = fileSize;
		return this;
	}
	
	public FileInfo setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public FileInfo setChannel(Channel channel) {
		this.channel = channel;
		return this;
	}
	
	public FileInfo setFileTransfer(FileTransfer fileTransfer) {
		this.fileTransfer = fileTransfer;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public String getUsername() {
		return username;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public FileTransfer getFileTransfer() {
		return fileTransfer;
	}
}
