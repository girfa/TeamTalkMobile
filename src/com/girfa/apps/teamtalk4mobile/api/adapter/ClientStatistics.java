package com.girfa.apps.teamtalk4mobile.api.adapter;

public class ClientStatistics {
	private Long udpBytesSent;
	private Long udpBytesRecv;
	private Long voiceBytesSent;
	private Long voiceBytesRecv;
	private Long videoBytesSent;
	private Long videoBytesRecv;
	private Long desktopBytesSent;
	private Long desktopBytesRecv;
	private Integer udpPingTimeMs;
	private Integer tcpPingTimeMs;
	private Integer tcpServerSilenceSec;
	private Integer udpServerSilenceSec;
	
	public ClientStatistics setUdpBytesSent(Long udpBytesSent) {
		this.udpBytesSent = udpBytesSent;
		return this;
	}
	
	public ClientStatistics setUdpBytesRecv(Long udpBytesRecv) {
		this.udpBytesRecv = udpBytesRecv;
		return this;
	}
	
	public ClientStatistics setVoiceBytesSent(Long voiceBytesSent) {
		this.voiceBytesSent = voiceBytesSent;
		return this;
	}
	
	public ClientStatistics setVoiceBytesRecv(Long voiceBytesRecv) {
		this.voiceBytesRecv = voiceBytesRecv;
		return this;
	}
	
	public ClientStatistics setVideoBytesSent(Long videoBytesSent) {
		this.videoBytesSent = videoBytesSent;
		return this;
	}
	
	public ClientStatistics setVideoBytesRecv(Long videoBytesRecv) {
		this.videoBytesRecv = videoBytesRecv;
		return this;
	}
	
	public ClientStatistics setDesktopBytesSent(Long desktopBytesSent) {
		this.desktopBytesSent = desktopBytesSent;
		return this;
	}
	
	public ClientStatistics setDesktopBytesRecv(Long desktopBytesRecv) {
		this.desktopBytesRecv = desktopBytesRecv;
		return this;
	}
	
	public ClientStatistics setUdpPingTimeMs(Integer udpPingTimeMs) {
		this.udpPingTimeMs = udpPingTimeMs;
		return this;
	}
	
	public ClientStatistics setTcpPingTimeMs(Integer tcpPingTimeMs) {
		this.tcpPingTimeMs = tcpPingTimeMs;
		return this;
	}
	
	public ClientStatistics setTcpServerSilenceSec(Integer tcpServerSilenceSec) {
		this.tcpServerSilenceSec = tcpServerSilenceSec;
		return this;
	}
	
	public ClientStatistics setUdpServerSilenceSec(Integer udpServerSilenceSec) {
		this.udpServerSilenceSec = udpServerSilenceSec;
		return this;
	}

	public Long getUdpBytesSent() {
		return udpBytesSent;
	}

	public Long getUdpBytesRecv() {
		return udpBytesRecv;
	}

	public Long getVoiceBytesSent() {
		return voiceBytesSent;
	}

	public Long getVoiceBytesRecv() {
		return voiceBytesRecv;
	}

	public Long getVideoBytesSent() {
		return videoBytesSent;
	}

	public Long getVideoBytesRecv() {
		return videoBytesRecv;
	}

	public Long getDesktopBytesSent() {
		return desktopBytesSent;
	}

	public Long getDesktopBytesRecv() {
		return desktopBytesRecv;
	}

	public Integer getUdpPingTimeMs() {
		return udpPingTimeMs;
	}

	public Integer getTcpPingTimeMs() {
		return tcpPingTimeMs;
	}

	public Integer getTcpServerSilenceSec() {
		return tcpServerSilenceSec;
	}

	public Integer getUdpServerSilenceSec() {
		return udpServerSilenceSec;
	}
}
