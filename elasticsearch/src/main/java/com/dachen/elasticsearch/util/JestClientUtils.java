package com.dachen.elasticsearch.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.elasticsearch.config.EsConfig;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class JestClientUtils {
	private static Logger logger = LoggerFactory.getLogger(JestClientUtils.class);
	private static final Byte[] LOCKS = new Byte[0];  
	
	private static JestClient jestClient;
	/**
	 * JestClient is designed to be singleton;
	 */
	public static JestClient jestClient() {
		if (jestClient == null) {
			logger.info("JestClient init begin!");
			synchronized (LOCKS) {
				if (jestClient == null) {
					EsConfig config = EsConfig.getInstance();
					JestClientFactory factory = new JestClientFactory();
					
					String[]nodes = config.getHost().split(",");
					HttpClientConfig.Builder clientConfig = null;
					if(nodes.length>1){
					    clientConfig = new HttpClientConfig.Builder(Arrays.asList(nodes));
					    clientConfig.discoveryEnabled(true);
					}else{
					    clientConfig = new HttpClientConfig.Builder(config.getHost());
					}
					clientConfig.multiThreaded(true)
  								.connTimeout(config.getConnTimeout())
  								.readTimeout(config.getSocketTimeout())
  								.maxTotalConnection(config.getMaxTotalConnection())
  								.defaultMaxTotalConnectionPerRoute(config.getDefaultMaxTotalConnectionPerRoute());
					
					String user = config.getUser();
					String password = config.getPassword();
					if(StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)){
						clientConfig.defaultCredentials(user, password);
					}
					factory.setHttpClientConfig(clientConfig.build());
					jestClient = factory.getObject();
				}
			}
			logger.info("JestClient init is ok!");
		}
		return jestClient;
	}
}
