package com.tilab.wsig.servlet;

import java.util.HashMap;
import java.util.Map;

public class HTTPInfo {

	private static ThreadLocal<Map<String, String>> inputHeaders = new ThreadLocal<Map<String, String>>();
	private static ThreadLocal<Map<String, String>> outputHeaders = new ThreadLocal<Map<String, String>>();
	
	public static Map<String, String> getInputHeaders() {
		Map<String, String> map = inputHeaders.get();
		if (map == null) {
			map = new HashMap<String, String>();
			inputHeaders.set(map);
		}
		return map;
	}

	public static Map<String, String> getOutputHeaders() {
		Map<String, String> map = outputHeaders.get();
		if (map == null) {
			map = new HashMap<String, String>();
			outputHeaders.set(map);
		}
		return map;
	}
}
