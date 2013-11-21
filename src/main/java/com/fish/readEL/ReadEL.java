package com.fish.readEL;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.utils.ConfigUtils;

public class ReadEL {
	public static Pattern EL_PATTERN = Pattern.compile("\\$\\{(.*?)\\}", Pattern.DOTALL);
	public static Properties GLOBAL_PROP = ConfigUtils.getConfig("/config/global.properties");
	
	/**
	 * 替换value里面EL表达式的对应global.properties的值
	 * @param value
	 * @return
	 */
	public static String getELValue(String value) {
		Matcher matcher = EL_PATTERN.matcher(value);
		String temp = null;
		while (matcher.find()) {
			temp = GLOBAL_PROP.getProperty(matcher.group(1));
			if (null != temp) {
				value = value.replace(matcher.group(), temp);
			}else {
				System.err.printf("global.properties does't contains this key:%s\n", matcher.group(1));
				throw new NullPointerException();
			}
		}
		
		return value;
	}
	
	public static void main(String[] args) {
		String value = "${DATANODE}/nmanma/${REDIS.PORT}/adf";
		value = getELValue(value);
		System.out.println(value);
	}
}
