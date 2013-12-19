package com.utils;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.utils.ConfigUtils;

public class ReadEL {
	
	public static Pattern EL_PATTERN = Pattern.compile("\\$\\{([^\\}]*)\\}");
	public static Matcher matcher = EL_PATTERN.matcher("");
	
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
		return null != value ? replaceValue(value) : null;
	}

	
	/**
	 * 替换prop文件value中包含${}的部分
	 * @param value
	 * @return
	 */
	private static String replaceValue(Object value) {
		matcher.reset(String.valueOf(value).toUpperCase());
		String temp = null;
		
		StringBuffer sb = new StringBuffer(200);
		while (matcher.find()) {
			temp = ConfigUtils.GLOBAL_PROPERTIES.getProperty(matcher.group(1));
			if (null != temp) {
				matcher.appendReplacement(sb, temp);
			}else {
				System.err.printf("global.properties does't contains this key:${%s}\n", matcher.group(1));
				throw new NullPointerException();
			}
		}
		
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Properties properties = null;
		
		properties = ConfigUtils.getConfig("/config/test.properties");
		System.out.printf("name:%s\n", properties.get("name"));
		System.out.printf("age:%s\n", properties.get("age"));
		System.out.printf("r:%s\n", properties.get("r"));
	}
}
