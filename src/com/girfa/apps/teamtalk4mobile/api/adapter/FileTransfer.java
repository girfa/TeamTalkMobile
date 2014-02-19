package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.Map;

public class FileTransfer {
	private Integer id;
	private Channel channel;
	private String localFilePath;
	private String remoteFileName;
	private Long fileSize;
	private Long transferred;
	private Boolean inbound;
	
	private static final String TRANSFER_ID = "transferid";
	
	public FileTransfer() {}
	
	public FileTransfer(Map<String, Object> map) {
		build(map);
	}
	
	public FileTransfer build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(TRANSFER_ID)) {
			setId(Integer.valueOf(map.get(TRANSFER_ID) + ""));
		}
		return this;
	}

	public FileTransfer setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public FileTransfer setChannel(Channel channel) {
		this.channel = channel;
		return this;
	}
	
	public FileTransfer setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
		return this;
	}
	
	public FileTransfer setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
		return this;
	}
	
	public FileTransfer setFileSize(Long fileSize) {
		this.fileSize = fileSize;
		return this;
	}
	
	public FileTransfer setTransferred(Long transferred) {
		this.transferred = transferred;
		return this;
	}
	
	public FileTransfer setInbound(Boolean inbound) {
		this.inbound = inbound;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public String getRemoteFileName() {
		return remoteFileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public Long getTransferred() {
		return transferred;
	}

	public Boolean isInbound() {
		return inbound;
	}
}
