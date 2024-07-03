package com.stock.v1.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stock.v1.vo.Picks;
import com.stock.v1.vo.Stock;

public class LiveStockCache  {

	static final Logger logger = LogManager.getLogger(LiveStockCache.class.getName());

	private static final ReentrantLock lock = new ReentrantLock(true);

	private static List<Stock> liveStockList;

	private LiveStockCache() {}
	public static void clearLiveStockList() {
		try
		{
			if (null != liveStockList && !liveStockList.isEmpty() )
			{
				liveStockList = new ArrayList<>();
			}
		}
		catch (Exception ex) {
			logger.error(()-> "LiveStockCache - clearLiveStockList exception:-" + ex);
		}
	}

	public static List<Stock> getLiveStockList() {
		if (null != liveStockList && !liveStockList.isEmpty() )
		{
			return liveStockList;
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static void setLiveStockList(List<Stock> liveList) {
		if (null != liveList) {
			try {
				lock.lock();
				try {
					liveStockList = liveList;
				} finally {
					if (lock.isLocked()) {
						lock.unlock();
					}
				}
			} catch (Exception ex) {
				logger.error(()-> "LiveStockCache - setLiveStockList exception:-" + ex);
			}
		}
	}
	
	public static List<Stock> getStockWatchList() {
		
		List<Stock> stockWatchList = new ArrayList<>();
		Stock s = new Stock();
		s.setTicker("MRO");
		s.setTrackingPrice("22");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("BABA");
		s.setTrackingPrice("72");
		stockWatchList.add(s);

		s = new Stock();
		s.setTicker("SBUX");
		s.setTrackingPrice("91");
		stockWatchList.add(s);

		s = new Stock();
		s.setTicker("TWLO");
		s.setTrackingPrice("56");
		stockWatchList.add(s);

		s = new Stock();
		s.setTicker("TSLA");
		s.setTrackingPrice("170");
		stockWatchList.add(s);

		s = new Stock();
		s.setTicker("NKE");
		s.setTrackingPrice("97");
		stockWatchList.add(s);

		s = new Stock();
		s.setTicker("MU");
		s.setTrackingPrice("80.5");
		stockWatchList.add(s);
			
		s = new Stock();
		s.setTicker("VALE");
		s.setTrackingPrice("12");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("INTC");
		s.setTrackingPrice("42");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("MARA");
		s.setTrackingPrice("16.13");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("AAPL");
		s.setTrackingPrice("168.68");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("DOCU");
		s.setTrackingPrice("49.12");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("PYPL");
		s.setTrackingPrice("56");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("BA");
		s.setTrackingPrice("177");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("F");
		s.setTrackingPrice("10.9");
		stockWatchList.add(s);
		
		s = new Stock();
		s.setTicker("NIO");
		s.setTrackingPrice("4.7");
		stockWatchList.add(s);

		return stockWatchList;
	}
	
	public static List<Picks> getPicks() {
		
		List<Picks> picksList = new ArrayList<>();
		Picks s = new Picks();
		s.setTicker("NVO");
		s.setEntry("142.32");
		s.setExit1("146.5");
		s.setExit2("147.4");
		s.setStop("141.16");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-06-28");
		s.setAddPrice("142.74");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("JBLU");
		s.setEntry("5.93");
		s.setExit1("6.45");
		s.setExit2("6.55");
		s.setStop("5.79");
		s.setTargetDate("2024-08-06");
		s.setAddedDate("2024-06-28");
		s.setAddPrice("6.09");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ORCL");
		s.setEntry("140.73");
		s.setExit1("143.6");
		s.setExit2("144.2");
		s.setStop("139.94");
		s.setTargetDate("2024-07-09");
		s.setAddedDate("2024-06-28");
		s.setAddPrice("141.2");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SOXL");
		s.setEntry("55.05");
		s.setExit1("56.8");
		s.setExit2("58.5");
		s.setStop("54.26");
		s.setTargetDate("2024-07-01");
		s.setAddedDate("2024-06-28");
		s.setAddPrice("55.36");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NU");
		s.setEntry("12.77");
		s.setExit1("14");
		s.setExit2("14.3");
		s.setStop("12.42");
		s.setTargetDate("2024-07-23");
		s.setAddedDate("2024-06-28");
		s.setAddPrice("12.89");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TSLA");
		s.setEntry("228.7");
		s.setExit1("248");
		s.setExit2("252");
		s.setStop("222.96");
		s.setTargetDate("2024-07-05");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("231.26");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("XOM");
		s.setEntry("112.41");
		s.setExit1("115.8");
		s.setExit2("116.5");
		s.setStop("222.96");
		s.setTargetDate("2024-07-10");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("114.18");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ROKU");
		s.setEntry("58.29");
		s.setExit1("62.7");
		s.setExit2("63.7");
		s.setStop("57.05");
		s.setTargetDate("2024-07-25");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("61.36");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MKC");
		s.setEntry("69.65");
		s.setExit1("72.6");
		s.setExit2("73.2");
		s.setStop("68.85");
		s.setTargetDate("2024-07-30");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("70.24");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AA");
		s.setEntry("40.19");
		s.setExit1("42");
		s.setExit2("42.4");
		s.setStop("39.69");
		s.setTargetDate("2024-07-10");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("40.83");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RBLX");
		s.setEntry("37.09");
		s.setExit1("40.1");
		s.setExit2("40.8");
		s.setStop("36.26");
		s.setTargetDate("2024-08-19");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("37.14");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DELL");
		s.setEntry("140.57");
		s.setExit1("146.5");
		s.setExit2("148");
		s.setStop("138.9");
		s.setTargetDate("2024-07-11");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("143.47");
		s.setType("L");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("UPST");
		s.setEntry("23.32");
		s.setExit1("21.9");
		s.setExit2("19");
		s.setStop("24.29");
		s.setTargetDate("2024-07-17");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("22.97");
		s.setType("S");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("UPS");
		s.setEntry("136.01");
		s.setExit1("131");
		s.setExit2("129.75");
		s.setStop("137.4");
		s.setTargetDate("2024-08-20");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("134.91");
		s.setType("S");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TNA");
		s.setEntry("204.59");
		s.setExit1("181");
		s.setExit2("176");
		s.setStop("211.1");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("200.16");
		s.setType("S");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("KBH");
		s.setEntry("68.34");
		s.setExit1("63.75");
		s.setExit2("62.75");
		s.setStop("69.59");
		s.setTargetDate("2024-08-19");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("66.41");
		s.setType("S");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NRG");
		s.setEntry("79.64");
		s.setExit1("74.75");
		s.setExit2("73.5");
		s.setStop("81.55");
		s.setTargetDate("2024-07-16");
		s.setAddedDate("2024-07-01");
		s.setAddPrice("77.89");
		s.setType("S");
		picksList.add(s);
		

		return picksList;
	}
}