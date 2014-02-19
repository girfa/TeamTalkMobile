package com.girfa.apps.teamtalk4mobile.api.adapter;

public class VideoFrame {
	private Integer width;
	private Integer height;
	private Integer streamId;
	private Boolean keyFrame;
	private byte[] frameBuffer;
	private Integer frameBufferSize;
	
	public VideoFrame setWidth(Integer width) {
		this.width = width;
		return this;
	}
	
	public VideoFrame setHeight(Integer height) {
		this.height = height;
		return this;
	}
	
	public VideoFrame setStreamId(Integer streamId) {
		this.streamId = streamId;
		return this;
	}
	
	public VideoFrame setKeyFrame(Boolean keyFrame) {
		this.keyFrame = keyFrame;
		return this;
	}
	
	public VideoFrame setFrameBuffer(byte[] frameBuffer) {
		this.frameBuffer = frameBuffer;
		return this;
	}
	
	public VideoFrame setFrameBufferSize(Integer frameBufferSize) {
		this.frameBufferSize = frameBufferSize;
		return this;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Integer getStreamId() {
		return streamId;
	}

	public Boolean isKeyFrame() {
		return keyFrame;
	}

	public byte[] getFrameBuffer() {
		return frameBuffer;
	}

	public Integer getFrameBufferSize() {
		return frameBufferSize;
	}
}
