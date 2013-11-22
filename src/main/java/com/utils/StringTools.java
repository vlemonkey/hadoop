package com.utils;

import org.apache.commons.lang.StringUtils;

public final class StringTools {

	/**
	 * 字符串拼接 先计算出字符串长度 声明此长度的stringbuilder
	 * 
	 * @param strs
	 * @return
	 */
	public static String append(Object... objs) {
		if (0 < objs.length) {
			return StringUtils.join(objs);
		}
		return StringUtils.EMPTY;
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
		return StringUtils.EMPTY;
	}
}
