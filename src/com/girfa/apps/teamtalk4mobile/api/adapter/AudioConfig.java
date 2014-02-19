package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.List;

public class AudioConfig {
	private Boolean enableAgc;
	private Integer gainLevel;
	private Integer maxIncDbSec;
	private Integer maxDecDbSec;
	private Integer maxGainDb;
	private Boolean enableDenoise;
	private Integer maxNoiseSuppressDb;

	public AudioConfig() {}
	
	public AudioConfig(List<Integer> config) {
		build(config);
	}
	
	public AudioConfig build(List<Integer> config) {
		setEnableAgc(config.get(0) != 0);
		setGainLevel(config.get(1));
		setMaxIncDbSec(config.get(2));
		setMaxDecDbSec(config.get(3));
		setMaxGainDb(config.get(4));
		setEnableDenoise(config.get(5) != 0);
		setMaxNoiseSuppressDb(config.get(6));
		return this;
	}
	
	@Override
	public String toString() {
		return (isEnableAgc() ? 1 : 0) + "|"
			+ getGainLevel() + "|"
			+ getMaxIncDbSec() + "|"
			+ getMaxDecDbSec() + "|"
			+ getMaxGainDb() + "|"
			+ (isEnableDenoise() ? 1 : 0) + "|"
			+ getMaxNoiseSuppressDb();
	}
	
	public AudioConfig setEnableAgc(Boolean enableAgc) {
		this.enableAgc = enableAgc;
		return this;
	}
	
	public AudioConfig setGainLevel(Integer gainLevel) {
		this.gainLevel = gainLevel;
		return this;
	}
	
	public AudioConfig setMaxIncDbSec(Integer maxIncDbSec) {
		this.maxIncDbSec = maxIncDbSec;
		return this;
	}
	
	public AudioConfig setMaxDecDbSec(Integer maxDecDbSec) {
		this.maxDecDbSec = maxDecDbSec;
		return this;
	}
	
	public AudioConfig setMaxGainDb(Integer maxGainDb) {
		this.maxGainDb = maxGainDb;
		return this;
	}
	
	public AudioConfig setEnableDenoise(Boolean enableDenoise) {
		this.enableDenoise = enableDenoise;
		return this;
	}
	
	public AudioConfig setMaxNoiseSuppressDb(Integer maxNoiseSuppressDb) {
		this.maxNoiseSuppressDb = maxNoiseSuppressDb;
		return this;
	}

	public Boolean isEnableAgc() {
		return enableAgc;
	}

	public Integer getGainLevel() {
		return gainLevel;
	}

	public Integer getMaxIncDbSec() {
		return maxIncDbSec;
	}

	public Integer getMaxDecDbSec() {
		return maxDecDbSec;
	}

	public Integer getMaxGainDb() {
		return maxGainDb;
	}

	public Boolean isEnableDenoise() {
		return enableDenoise;
	}

	public Integer getMaxNoiseSuppressDb() {
		return maxNoiseSuppressDb;
	}
}
