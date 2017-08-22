package com.dachen.elasticsearch.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.elasticsearch.util.FileUtils;

@Deprecated
public class EsConfigFactory {
	private static Logger logger = LoggerFactory.getLogger(EsConfigFactory.class);
	private static EsConfigFactory instance;
	
	private String configFilePath;
	private Map<String, Object> config;
	
	private EsConfigFactory(){
		instance = this;
	}

	public EsConfigFactory(Map<String, Object> config) {
		this.config = config;
	}

	public static EsConfigFactory getInstance(){
		if(instance==null){
			instance = new EsConfigFactory();
		}
		return instance;
	}
	
	public EsConfig getEsConfig(){
		Properties properties=null;
		try{
			properties = FileUtils.loadProperties(EsConfigFactory.class.getClassLoader(),configFilePath);
		}
		catch(Exception e){
			logger.error("system.properties for es load is error");
			logger.error(e.getMessage());
		}
		logger.info("config={}", this.config);
		if (null != this.config && this.config.size() > 0) {
			if (null == properties) {
				properties = new Properties();
			}
			Iterator<Map.Entry<String, Object>> iterator = this.config.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				logger.info("config. {}={}", entry.getKey(), entry.getValue());
				properties.put(entry.getKey(), entry.getValue());
			}
		}

		return new EsConfig();
	}
	
	public String getConfigFilePath(){
		if(StringUtils.isEmpty(configFilePath)){
			configFilePath="properties/health.properties";
		}
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	public void putConfig(String key, Object value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		if (null == this.config) {
			this.config = new HashMap<String, Object>();
		}
		this.config.put(key, value);
	}
}
