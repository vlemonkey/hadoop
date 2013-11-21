package com.fish.etl.tools.process;

import org.apache.commons.lang.StringUtils;

import com.fish.etl.ETLReflect;
import com.fish.etl.tools.Param;

public class SubString {

	public static String process(Param param) {
		String value = param.getValue();
		String[] params = ETLReflect.split.split(param.getParameter());
		return StringUtils.substring(value, Integer.parseInt(params[0]), 
				Integer.parseInt(params[1]));
	}
}
