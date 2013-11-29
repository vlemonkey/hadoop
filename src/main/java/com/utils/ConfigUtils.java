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

import com.boco.global.Constants;

public class ConfigUtils {
	
	public static final Properties GLOBAL_PROPERTIES = getGlobalProperites();
	public static File file = null;

	/**
	 * 获取global配置文件别名路径
	 * 对应的properties
	 * @return
	 */
	public static Properties getGlobalProperites() {
		return getConfig(getConfig(Constants.GLOBAL_PROP).getProperty("alias"));
	}
	
	/**
	 * 递归取得配置文件的properties
	 * @param filePath 文件路径
	 * @return
	 */
	public static Properties getConfig(String fileName) {
		File file = getDistributedCahceProp(fileName);
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
	public static File getDistributedCahceProp(String filePath) {
		if (null == file || !file.exists()) {
			file = new File(Constants.DISTRIBUTEDCAHCHE_CONFIG); // config--HDFS distributedcache默认配置文件名
		}

		return file.exists() ? getDistFile(file, filePath) : null;
		
	}
	
	// 根据配置文件名称获取配置文件file
	private static File getDistFile(File distFile, String filePath) {
		String fileName = StringUtils.substringAfterLast(filePath, File.separator);
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
	}
}
