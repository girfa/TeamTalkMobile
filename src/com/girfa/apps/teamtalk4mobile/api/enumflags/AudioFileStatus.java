package com.girfa.apps.teamtalk4mobile.api.enumflags;

public enum AudioFileStatus {
	AFS_ERROR(0),
	AFS_STARTED(1),
	AFS_FINISHED(2),
	AFS_ABORTED(3);
	
	public int value;
	
	private AudioFileStatus(int value) {
		this.value = value;
	}
}
