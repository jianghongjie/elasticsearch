package com.dachen.elasticsearch.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class EsConfig implements ApplicationContextAware {
//	private static final String DEFAULT_ES_HOST="http://127.0.0.1:9200";
	
	@Value("${es_host:http://127.0.0.1:9200}")
	private String host;
	
	@Value("${es_user:}")
	private String user;
	
	@Value("${es_password:}")
	private String password;
	
	@Value("${es_connTimeout:2000}")
	private String connTimeout;
	
	@Value("${es_socketTimeout:5000}")
	private String socketTimeout;
	
	@Value("${es_maxTotalConnection:5000}")
	private String maxTotalConnection;
	
	@Value("${es_defaultMaxTotalConnectionPerRoute:500}")
	private String defaultMaxTotalConnectionPerRoute;
	
	public String getHost() {
		return host == null ? "http://127.0.0.1:9200" : host;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getConnTimeout() {
		return connTimeout == null ? 2000 : Integer.valueOf(connTimeout);
	}
	
	public int getSocketTimeout() {
		return socketTimeout == null ? 5000 : Integer.valueOf(socketTimeout);
	}
	
	public int getMaxTotalConnection() {
		return maxTotalConnection == null ? 5000 : Integer.valueOf(maxTotalConnection);
	}
	
	public int getDefaultMaxTotalConnectionPerRoute() {
		return defaultMaxTotalConnectionPerRoute == null ? 500 : Integer.valueOf(defaultMaxTotalConnectionPerRoute);
	}

	private static ApplicationContext context; 
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static EsConfig getInstance() {
		if (context != null) {
			return context.getBean(EsConfig.class);
		}
		return new EsConfig();
	}
}
