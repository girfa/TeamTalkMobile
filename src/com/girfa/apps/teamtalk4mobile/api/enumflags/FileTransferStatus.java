package com.girfa.apps.teamtalk4mobile.api.enumflags;

public enum FileTransferStatus {
	FILETRANSFER_ERROR(0),
	FILETRANSFER_STARTED(1),
	FILETRANSFER_FINISHED(2);
	
	public int value;
	
	private FileTransferStatus(int value) {
		this.value = value;
	}
}
