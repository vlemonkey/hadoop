package com.fish.etl.tools.process;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fish.etl.tools.Param;

public class ParseURL {

	public static String process(Param param) {
		String value = param.getValue();
		Matcher matcher = Pattern.compile(param.getParameter()).matcher(value);
		if (matcher.find()) {
			return matcher.group();
		}
		return StringUtils.EMPTY;
	}
}
