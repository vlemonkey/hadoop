package com.fish.etl.tools.verify;

import com.fish.etl.tools.Param;


public class LengthVerify {

	public static boolean verify(Param param) {
		String value = param.getValue();
		return value.length() == Integer.parseInt(param.getParameter());
	}
}
