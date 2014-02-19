package com.girfa.apps.teamtalk4mobile.api.adapter;

import com.girfa.apps.teamtalk4mobile.api.enumflags.FourCC;

public class CaptureFormat {
	private Integer width;
	private Integer height;
	private Integer fpsNumerator; 
	private Integer fpsDenominator; 
	private FourCC picFourCC;

	public CaptureFormat setWidth(Integer width) {
		this.width = width;
		return this;
	}

	public CaptureFormat setHeight(Integer height) {
		this.height = height;
		return this;
	}

	public CaptureFormat setFpsNumerator(Integer fpsNumerator) {
		this.fpsNumerator = fpsNumerator;
		return this;
	}

	public CaptureFormat setFpsDenominator(Integer fpsDenominator) {
		this.fpsDenominator = fpsDenominator;
		return this;
	}

	public CaptureFormat setPicFourCC(FourCC picFourCC) {
		this.picFourCC = picFourCC;
		return this;
	}	
	
	public Integer getWidth() {
		return width;
	}
	
	public Integer getHeight() {
		return height;
	}
	
	public Integer getFpsNumerator() {
		return fpsNumerator;
	}
	
	public Integer getFpsDenominator() {
		return fpsDenominator;
	}
	
	public FourCC getPicFourCC() {
		return picFourCC;
	}
}
