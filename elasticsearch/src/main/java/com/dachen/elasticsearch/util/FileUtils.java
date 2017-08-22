package com.dachen.elasticsearch.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtils {
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public final static String CRLF = "\r\n";
	public static String getJson(String fileName){
		String filePath = "es_mapping"+File.separator+fileName;
		
		InputStream fis = null;
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder(512);
        try {
        	ClassLoader classLoader = FileUtils.class.getClassLoader();
        	if(classLoader==null){
        		fis = ClassLoader.getSystemResourceAsStream(filePath.toString());
        	}else{
        		fis = classLoader.getResourceAsStream(filePath.toString());
        	}
            scanner = new Scanner(fis, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine().trim());
//                buffer.append(CRLF);
            }
        }catch (Throwable ignore) {
        	
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if(fis!=null){
        		try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return buffer.toString();
	}
	
	 public static Properties loadProperties(ClassLoader classLoader,String filePath) {
        Properties properties = new Properties();
        InputStream fis = null;
        try {
        	if(classLoader==null){
        		fis = ClassLoader.getSystemResourceAsStream(filePath);
        	}else{
        		fis = classLoader.getResourceAsStream(filePath);
        	}
//            fis = new FileInputStream(file);
            properties.load(new InputStreamReader(fis,"UTF-8"));
        } catch (Throwable ignore) {
            if (ignore instanceof FileNotFoundException) {
                logger.error(ignore.getMessage());
                properties = null;
            } else {
                logger.error(ignore.getMessage(), ignore);
            }
        } finally {
        	if(fis!=null){
        		try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return properties;
    }
	 
}
