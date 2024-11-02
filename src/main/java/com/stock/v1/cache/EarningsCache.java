package com.stock.v1.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stock.v1.vo.Earnings;

public class EarningsCache {

	static final Logger logger = LogManager.getLogger(EarningsCache.class.getName());

	private static final ReentrantLock lock = new ReentrantLock(true);

	private static List<Earnings> earningsHistoryAll;

	private EarningsCache() {}
	public static void clearEarningsHistory() {
		try
		{
			if (null != earningsHistoryAll && !earningsHistoryAll.isEmpty() )
			{
				earningsHistoryAll = new ArrayList<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "EarningsCache - clearEarningsHistory exception:-" + ex);
		}
	}

	public static List<Earnings> getEarningsHistory() {
		if (null != earningsHistoryAll && !earningsHistoryAll.isEmpty() )
		{
			return earningsHistoryAll;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static void setEarningsHistory(List<Earnings> list) {
		if (null != list) {
			try {
				lock.lock();
				try {
					earningsHistoryAll = list;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "EarningsCache - setEarningsHistory exception:-" + ex);
			}
		}
	}
}