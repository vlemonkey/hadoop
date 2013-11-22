package com.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

public class ConfigUtils {
	
	/**
	 * 递归取得配置文件的properties
	 * @param filePath 文件路径
	 * @return
	 */
	public static Properties getConfig(String fileName, String... alias) {
		File file = getDistributedCahceProp(fileName, alias);
		InputStream inputStream = null;
		Properties prop = new Properties();
		try {
			inputStream = file != null ? new BufferedInputStream(new FileInputStream(file))
				: ConfigUtils.class.getResourceAsStream(fileName);
			prop.load(inputStream);
			ReadEL.replaceProp(prop);
			return prop;
		} catch (Exception e) {
			System.err.printf("propeties file not exist:%s\n", fileName);
			e.printStackTrace();
			return null;
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	/**
	 * 递归取得配置文件的properties
	 * 用于HDFS DistributedCache
	 * @param fileName 文件名
	 * @param dirName 文件夹路径
	 * @return
	 */
	public static File getDistributedCahceProp(String filePath, String... alias) {
		String fileName = StringUtils.substringAfterLast(filePath, File.separator);
		File file = null;
		if (alias.length == 0) {
			file = new File("config"); // config--HDFS distributedcache默认配置文件名
		}else {
			file = new File(alias[0]); // HDFS distributedcache配置文件名
		}
		
		if (!file.exists()) { // 如果HDFS上不存在 则返回null
			return null;
		}
		
		Collection<File> fileCollections = FileUtils.listFiles(file, 
				FileFilterUtils.nameFileFilter(fileName), TrueFileFilter.INSTANCE);
		if (fileCollections.size() == 1) { // properties文件不重名，直接返回prop
			return (File)CollectionUtils.get(fileCollections, 0);
		}else {
			for (File f : fileCollections) { // properties文件重名，判断路径
				if (StringUtils.endsWith(f.getAbsolutePath(), filePath)) {
					return f;
				}
			}
			return null;
		}
	}
	
	public static void main(String[] args) {
		Properties prop = ConfigUtils.getConfig("/config/reflect.properties");
		System.out.println(prop.getProperty("B.VERIFYCLASS"));
		prop.setProperty("B.VERIFYCLASS", "abc");
		System.out.println(prop.getProperty("B.VERIFYCLASS"));
		
		prop.put("a", "b");
		System.out.println(prop.get("a"));
		System.out.println(prop.getProperty("a"));
		
		
		
		
		
		
		
		
//		Set<Entry<Object, Object>> set = prop.entrySet();
//		for (Entry<Object, Object> entry : set) {
//			System.out.println(entry.getKey() + "\t" + entry.getValue());
//		}
		
	}
}
