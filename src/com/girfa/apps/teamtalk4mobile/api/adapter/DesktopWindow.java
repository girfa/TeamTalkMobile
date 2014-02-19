package com.girfa.apps.teamtalk4mobile.api.adapter;

import com.girfa.apps.teamtalk4mobile.api.enumflags.BitmapFormat;
import com.girfa.apps.teamtalk4mobile.api.enumflags.DesktopProtocol;

public class DesktopWindow {
	private Integer width;
	private Integer height;
	private BitmapFormat bmpFormat;
	private Integer bytesPerLine;
	private Integer sessionId;
	private DesktopProtocol protocol;
	
	public DesktopWindow setWidth(Integer width) {
		this.width = width;
		return this;
	}
	
	public DesktopWindow setHeight(Integer height) {
		this.height = height;
		return this;
	}
	
	public DesktopWindow setBmpFormat(BitmapFormat bmpFormat) {
		this.bmpFormat = bmpFormat;
		return this;
	}
	
	public DesktopWindow setBytesPerLine(Integer bytesPerLine) {
		this.bytesPerLine = bytesPerLine;
		return this;
	}
	
	public DesktopWindow setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
		return this;
	}
	
	public DesktopWindow setProtocol(DesktopProtocol protocol) {
		this.protocol = protocol;
		return this;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public BitmapFormat getBmpFormat() {
		return bmpFormat;
	}

	public Integer getBytesPerLine() {
		return bytesPerLine;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public DesktopProtocol getProtocol() {
		return protocol;
	}
}
