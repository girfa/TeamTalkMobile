package com.girfa.apps.teamtalk4mobile.api.adapter;

public class UserStatistics {
	private Long audioPacketsRecv;
	private Long audioPacketsLost;
	private Long videoPacketsRecv;
	private Long videoFramesRecv;
	private Long videoFramesLost;
	private Long videoFramesDropped;
	
	public UserStatistics setAudioPacketsRecv(Long audioPacketsRecv) {
		this.audioPacketsRecv = audioPacketsRecv;
		return this;
	}
	
	public UserStatistics setAudioPacketsLost(Long audioPacketsLost) {
		this.audioPacketsLost = audioPacketsLost;
		return this;
	}
	
	public UserStatistics setVideoPacketsRecv(Long videoPacketsRecv) {
		this.videoPacketsRecv = videoPacketsRecv;
		return this;
	}
	
	public UserStatistics setVideoFramesRecv(Long videoFramesRecv) {
		this.videoFramesRecv = videoFramesRecv;
		return this;
	}
	
	public UserStatistics setVideoFramesLost(Long videoFramesLost) {
		this.videoFramesLost = videoFramesLost;
		return this;
	}
	
	public UserStatistics setVideoFramesDropped(Long videoFramesDropped) {
		this.videoFramesDropped = videoFramesDropped;
		return this;
	}

	public Long getAudioPacketsRecv() {
		return audioPacketsRecv;
	}

	public Long getAudioPacketsLost() {
		return audioPacketsLost;
	}

	public Long getVideoPacketsRecv() {
		return videoPacketsRecv;
	}

	public Long getVideoFramesRecv() {
		return videoFramesRecv;
	}

	public Long getVideoFramesLost() {
		return videoFramesLost;
	}

	public Long getVideoFramesDropped() {
		return videoFramesDropped;
	}
}
