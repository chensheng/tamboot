package com.tamboot.common.utils;

import java.util.Properties;

public class PropertiesUtils {
	public static Properties fromStringArray(String[] strArr) {
		Properties props = new Properties();
		
		if (strArr == null || strArr.length == 0) {
			return props;
		}
		
		for (String str : strArr) {
			String[] propArr = str.split("=");
			if (propArr.length != 2) {
				continue;
			}
			
			props.setProperty(propArr[0].trim(), propArr[1].trim());
		}
		
		return props;
	}
}
