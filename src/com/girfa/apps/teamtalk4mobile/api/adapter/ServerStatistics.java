package com.girfa.apps.teamtalk4mobile.api.adapter;

public class ServerStatistics {
	private Long totalBytesTX;
	private Long totalBytesRX;
	private Long voiceBytesTX;
	private Long voiceBytesRX;
	private Long videoBytesTX;
	private Long videoBytesRX;
	private Long desktopBytesTX;
	private Long desktopBytesRX;
	private Long uptimeMSec;
	
	public ServerStatistics setTotalBytesTX(Long totalBytesTX) {
		this.totalBytesTX = totalBytesTX;
		return this;
	}
	
	public ServerStatistics setTotalBytesRX(Long totalBytesRX) {
		this.totalBytesRX = totalBytesRX;
		return this;
	}
	
	public ServerStatistics setVoiceBytesTX(Long voiceBytesTX) {
		this.voiceBytesTX = voiceBytesTX;
		return this;
	}
	
	public ServerStatistics setVoiceBytesRX(Long voiceBytesRX) {
		this.voiceBytesRX = voiceBytesRX;
		return this;
	}
	
	public ServerStatistics setVideoBytesTX(Long videoBytesTX) {
		this.videoBytesTX = videoBytesTX;
		return this;
	}
	
	public ServerStatistics setVideoBytesRX(Long videoBytesRX) {
		this.videoBytesRX = videoBytesRX;
		return this;
	}
	
	public ServerStatistics setDesktopBytesTX(Long desktopBytesTX) {
		this.desktopBytesTX = desktopBytesTX;
		return this;
	}
	
	public ServerStatistics setDesktopBytesRX(Long desktopBytesRX) {
		this.desktopBytesRX = desktopBytesRX;
		return this;
	}
	
	public ServerStatistics setUptimeMSec(Long uptimeMSec) {
		this.uptimeMSec = uptimeMSec;
		return this;
	}
	
	public Long getTotalBytesTX() {
		return totalBytesTX;
	}
	
	public Long getTotalBytesRX() {
		return totalBytesRX;
	}
	
	public Long getVoiceBytesTX() {
		return voiceBytesTX;
	}
	
	public Long getVoiceBytesRX() {
		return voiceBytesRX;
	}
	
	public Long getVideoBytesTX() {
		return videoBytesTX;
	}
	
	public Long getVideoBytesRX() {
		return videoBytesRX;
	}
	
	public Long getDesktopBytesTX() {
		return desktopBytesTX;
	}
	
	public Long getDesktopBytesRX() {
		return desktopBytesRX;
	}
	
	public Long getUptimeMSec() {
		return uptimeMSec;
	}
}
