package com.stock.v1.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CookieCache  {

	static final Logger logger = LogManager.getLogger(CookieCache.class.getName());

	private static final ReentrantLock lock = new ReentrantLock(true);

	private static Map<String, String> cookieMap = new HashMap<>();

	private CookieCache() {}
	public static void clearAllCookies() {
		try
		{
			if (null != cookieMap && !cookieMap.isEmpty() )
			{
				cookieMap = new HashMap<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "CookieCache - clearAllCookies exception:-" + ex);
		}
	}

	public static Map<String, String> getAllCookies() {
		if (null != cookieMap && !cookieMap.isEmpty() )
		{
			return cookieMap;
		}
		else
		{
			return new HashMap<>();
		}
	}
	
	public static String getCookie(String key) {
		return cookieMap.get(key);
	}

	public static void setCookies(Map<String, String> cookie) {
		if (null != cookie) {
			try {
				lock.lock();
				try {
					cookieMap = cookie;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "CookieCache - setCookies exception:-" + ex);
			}
		}
	}
}