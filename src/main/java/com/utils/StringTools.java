package com.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class StringTools {
	private static String EMPTY = ""; // 默认返回的空字符

	private static Pattern oneHostURL = Pattern.compile("(^http://[^/]+/?).*");

	private static BigDecimal bigDecimal;
	
	public static void main(String[] args) {
		String[] strs = new String[6];
		initArrays(strs, ",");
		System.out.println(StringUtils.join(strs));
	}
	
	/**
	 * 统计词频
	 * 
	 * @param contenList
	 * @return
	 */
	public static Map<String, Integer> wordFrenquency(List<String> contenList) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (contenList.size() > 0) {
			for (String str : contenList) {
				if (null == map.get(str)) {
					map.put(str, 1);
				} else {
					map.put(str, map.get(str) + 1);
				}
			}
		}

		return map;
	}


	/**
	 * 字符串拼接 先计算出字符串长度 声明此长度的stringbuilder
	 * 
	 * @param strs
	 * @return
	 */
	public static String append(Object... objs) {
		System.out.println("StringTools append");
		if (0 < objs.length) {
			return StringUtils.join(objs);
		}
		return EMPTY;
	}

	/**
	 * 用strChar为分隔符拼接
	 * 
	 * @param strChar
	 * @param objs
	 * @return
	 */
	public static String join(String strChar, Object... objs) {
		if (objs.length > 0) {
			return StringUtils.join(objs, strChar);
		}
		return EMPTY;
	}


	/**
	 * 显示float型的数据时不以科学计数法
	 * 
	 * @param obj
	 */
	public static String nonScientificNotation(float f) {
		bigDecimal = new BigDecimal(f);
		bigDecimal = bigDecimal.setScale(6, BigDecimal.ROUND_HALF_UP);
		return bigDecimal.toString();
	}

	public static String nonScientificNotationStr(String s) {
		bigDecimal = new BigDecimal(s);
		bigDecimal = bigDecimal.setScale(6, BigDecimal.ROUND_HALF_UP);
		return bigDecimal.toString();
	}

	/**
	 * 截取url一级目录
	 * @param str
	 * @return
	 */
	public static String getHostURL(String str) {
		Matcher matcher = oneHostURL.matcher(str);
		if (matcher.find()) {
			return matcher.group(1);
		}else {
			return StringUtils.EMPTY;
		}
	}
	
	/**
	 * 初始化double数组
	 * 初始值为defaultValue
	 * @param strs
	 * @param defaultValue
	 */
	public static void initArrays(double[] strs, double defaultValue) {
		int count = strs.length;
		while (count-- > 0) {
			strs[count] = defaultValue;
		}
	}
	
	/**
	 * 初始化字符串数组
	 * 初始值为 defaultValue
	 * @param strs
	 * @param defaultValue
	 */
	public static void initArrays(String[] strs, String defaultValue) {
		int count = strs.length;
		while (count-- > 0) {
			strs[count] = defaultValue;
		}
	}
}
