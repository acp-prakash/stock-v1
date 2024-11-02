package com.stock.v1.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stock.v1.vo.Options;

public class OptionsCache {

	static final Logger logger = LogManager.getLogger(OptionsCache.class.getName());

	private static final ReentrantLock lock = new ReentrantLock(true);

	private static List<Options> options;
	private static List<Options> optionsHistory;

	private OptionsCache() {}
	
	public static void clearOptions() {
		try
		{
			if (null != options && !options.isEmpty() )
			{
				options = new ArrayList<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "OptionsCache - clearOptions exception:-" + ex);
		}
	}

	public static List<Options> getOptions() {
		if (null != options && !options.isEmpty() )
		{
			return options;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static void setOptions(List<Options> list) {
		if (null != list) {
			try {
				lock.lock();
				try {
					options = list;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "OptionsCache - setOptions exception:-" + ex);
			}
		}
	}

	public static void clearOptionsHistory() {
		try
		{
			if (null != optionsHistory && !optionsHistory.isEmpty() )
			{
				optionsHistory = new ArrayList<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "OptionsCache - clearOptionsHistory exception:-" + ex);
		}
	}

	public static List<Options> getOptionsHistory() {
		if (null != optionsHistory && !optionsHistory.isEmpty() )
		{
			return optionsHistory;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static void setOptionsHistory(List<Options> list) {
		if (null != list) {
			try {
				lock.lock();
				try {
					optionsHistory = list;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "OptionsCache - setOptionsHistory exception:-" + ex);
			}
		}
	}
}