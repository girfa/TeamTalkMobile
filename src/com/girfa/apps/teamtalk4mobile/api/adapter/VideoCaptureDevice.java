package com.girfa.apps.teamtalk4mobile.api.adapter;


public class VideoCaptureDevice {
	private String deviceId;
	private String deviceName;
	private String captureApi;
	private CaptureFormat captureFormats;
	private Integer captureFormatsCount;
	
	public VideoCaptureDevice setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}
	
	public VideoCaptureDevice setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		return this;
	}
	
	public VideoCaptureDevice setCaptureApi(String captureApi) {
		this.captureApi = captureApi;
		return this;
	}
	
	public VideoCaptureDevice setCaptureFormats(CaptureFormat captureFormats) {
		this.captureFormats = captureFormats;
		return this;
	}
	
	public VideoCaptureDevice setCaptureFormatsCount(Integer captureFormatsCount) {
		this.captureFormatsCount = captureFormatsCount;
		return this;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getCaptureApi() {
		return captureApi;
	}

	public CaptureFormat getCaptureFormats() {
		return captureFormats;
	}

	public Integer getCaptureFormatsCount() {
		return captureFormatsCount;
	}
}
