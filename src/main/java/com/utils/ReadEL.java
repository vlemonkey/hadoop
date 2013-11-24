package com.utils;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.utils.ConfigUtils;

public class ReadEL {
	public static Pattern EL_PATTERN = Pattern.compile("\\$\\{(.*?)\\}", Pattern.DOTALL);
	public static Properties GLOBAL_PROP = ConfigUtils.getGlobalProperites();
	
	
	/**
	 * 替换prop里面所有的全局变量
	 * @param prop
	 */
	public static void replaceProp(Properties prop) {
		Set<Entry<Object, Object>> propEntry = prop.entrySet();
		for (Entry<Object, Object> entry : propEntry) {
			prop.setProperty(String.valueOf(entry.getKey()), getELValue(entry.getValue()));
		}
	}
	
	/**
	 * 替换value里面EL表达式的对应global.properties的值
	 * @param value
	 * @return
	 */
	private static String getELValue(Object value) {
		if (null == value) {
			return null;
		}
		
		String tempValue = String.valueOf(value);
		Matcher matcher = EL_PATTERN.matcher(tempValue);
		String temp = null;
		while (matcher.find()) {
			temp = GLOBAL_PROP.getProperty(matcher.group(1));
			if (null != temp) {
				tempValue = tempValue.replace(matcher.group(), temp);
			}else {
				System.err.printf("global.properties does't contains this key:%s\n", matcher.group(1));
				throw new NullPointerException();
			}
		}
		
		return tempValue;
	}
	
	public static void main(String[] args) {
		Properties prop = ConfigUtils.getConfig("/config/test.properties");
		System.out.println(prop.getProperty("name"));
		System.out.println(prop.getProperty("age"));
		System.out.println(prop.getProperty("r"));
		System.out.println("---------------------------");
		replaceProp(prop);
		System.out.println(prop.getProperty("name"));
		System.out.println(prop.getProperty("age"));
		System.out.println(prop.getProperty("r"));
	}
}
