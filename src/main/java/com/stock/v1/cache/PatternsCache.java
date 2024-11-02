package com.stock.v1.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stock.v1.vo.Pattern;

public class PatternsCache {

	static final Logger logger = LogManager.getLogger(PatternsCache.class.getName());

	private static final ReentrantLock lock = new ReentrantLock(true);

	private static List<Pattern> patternHistory;

	private PatternsCache() {}
	public static void clearPatternHistory() {
		try
		{
			if (null != patternHistory && !patternHistory.isEmpty() )
			{
				patternHistory = new ArrayList<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "PatternsCache - clearPatternHistory exception:-" + ex);
		}
	}

	public static List<Pattern> getPatternHistory() {
		if (null != patternHistory && !patternHistory.isEmpty() )
		{
			return patternHistory;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static void setPatternHistory(List<Pattern> list) {
		if (null != list) {
			try {
				lock.lock();
				try {
					patternHistory = list;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "PatternsCache - setPatternHistory exception:-" + ex);
			}
		}
	}
}