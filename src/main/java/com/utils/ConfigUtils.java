package com.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class ConfigUtils {
	
	// 返回配置对象
	public static Properties getConfig(File file) {
		InputStream inputStream = null;
		Properties prop = new Properties();
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			prop.load(inputStream);
		} catch (Exception e) {
			System.out.println("init properties error: " + file.getAbsolutePath());
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
		return prop;
	}
	
	// 返回配置对象
	public static Properties getConfig(String filePath) {
		InputStream inputStream = null;
		Properties prop = new Properties();
		try {
			inputStream = ConfigUtils.class.getResourceAsStream(filePath);
			prop.load(inputStream);
		} catch (Exception e) {
			System.out.println("init properties error: " + filePath);
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
		return prop;
	}
	
	public static void main(String[] args) {
		Properties prop = ConfigUtils.getConfig("/config/reflect.properties");
		Set<Entry<Object, Object>> set = prop.entrySet();
		for (Entry<Object, Object> entry : set) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		
	}
}
