package com.fish.etl.tools.verify;

import org.apache.commons.lang.StringUtils;

import com.fish.etl.tools.Param;

public class NumberVerify {

	public static boolean verify(Param param) {
		String value = param.getValue();
		return StringUtils.isNotBlank(value) && StringUtils.isNumeric(value);
	}
}
