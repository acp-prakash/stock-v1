package com.stock.v1.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stock.v1.vo.Master;

public class MasterStocksCache  {

	static final Logger logger = LogManager.getLogger(MasterStocksCache.class.getName());

	private static final ReentrantLock lock = new ReentrantLock(true);

	private static List<Master> masterStocks;

	private MasterStocksCache() {}
	public static void clearMasterStocks() {
		try
		{
			if (null != masterStocks && !masterStocks.isEmpty() )
			{
				masterStocks = new ArrayList<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "MasterStocksCache - clearMasterStocks exception:-" + ex);
		}
	}

	public static List<Master> getMasterStocks() {
		if (null != masterStocks && !masterStocks.isEmpty() )
		{
			return masterStocks;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static void setMasterStocks(List<Master> masStocks) {
		if (null != masStocks) {
			try {
				lock.lock();
				try {
					masterStocks = masStocks;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "MasterStocksCache - setMasterStocks exception:-" + ex);
			}
		}
	}
}