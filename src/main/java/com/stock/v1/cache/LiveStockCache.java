package com.stock.v1.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stock.v1.utils.Constants;
import com.stock.v1.vo.Picks;
import com.stock.v1.vo.Stock;

public class LiveStockCache {

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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.LONG);
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
		s.setType(Constants.SHORT);
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
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("QCOM");
		s.setEntry("204.59");
		s.setExit1("181");
		s.setExit2("176");
		s.setStop("211.1");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("200.16");
		s.setType(Constants.SHORT);
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
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NRG");
		s.setEntry("79.64");
		s.setExit1("74.75");
		s.setExit2("73.5");
		s.setStop("81.55");
		s.setTargetDate("2024-07-16");
		s.setAddedDate("2024-07-02");
		s.setAddPrice("77.89");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MET");
		s.setEntry("70.31");
		s.setExit1("69");
		s.setExit2("68.5");
		s.setStop("71.83");
		s.setTargetDate("2024-07-11");
		s.setAddedDate("2024-07-03");
		s.setAddPrice("70.16");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DBX");
		s.setEntry("22.2");
		s.setExit1("23.5");
		s.setExit2("23.8");
		s.setStop("21.84");
		s.setTargetDate("2024-08-15");
		s.setAddedDate("2024-07-09");
		s.setAddPrice("22.02");
		s.setType(Constants.LONG);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TWST");
		s.setEntry("48.81");
		s.setExit1("54.75");
		s.setExit2("56");
		s.setStop("47.19");
		s.setTargetDate("2024-08-09");
		s.setAddedDate("2024-07-09");
		s.setAddPrice("50.5");
		s.setType(Constants.LONG);
		picksList.add(s);
				
		s = new Picks();
		s.setTicker("TPX");
		s.setEntry("46.17");
		s.setExit1("44");
		s.setExit2("43");
		s.setStop("47.04");
		s.setTargetDate("2024-08-27");
		s.setAddedDate("2024-07-09");
		s.setAddPrice("45.89");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TXT");
		s.setEntry("85.69");
		s.setExit2("81.3");
		s.setExit1("82.1");
		s.setStop("86.68");
		s.setTargetDate("2024-08-27");
		s.setAddedDate("2024-07-09");
		s.setAddPrice("85.13");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("XP");
		s.setEntry("18.15");
		s.setExit1("19.6");
		s.setExit2("19.9");
		s.setStop("17.75");
		s.setTargetDate("2024-08-27");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("18.25");
		s.setType(Constants.LONG);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("FITB");
		s.setEntry("36.76");
		s.setExit1("38.2");
		s.setExit2("38.5");
		s.setStop("36.36");
		s.setTargetDate("2024-07-30");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("36.85");
		s.setType(Constants.LONG);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("FLNC");
		s.setEntry("17.18");
		s.setExit1("18.6");
		s.setExit2("18.9");
		s.setStop("16.8");
		s.setTargetDate("2024-07-12");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("17.24");
		s.setType(Constants.LONG);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ENPH");
		s.setEntry("106.42");
		s.setExit1("116");
		s.setExit2("119");
		s.setStop("103.62");
		s.setTargetDate("2024-08-08");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("106.75");
		s.setType(Constants.LONG);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BEP");
		s.setEntry("26.23");
		s.setExit1("27.5");
		s.setExit2("27.8");
		s.setStop("25.87");
		s.setTargetDate("2024-07-26");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("26.34");
		s.setType(Constants.LONG);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NET");
		s.setEntry("81.25");
		s.setExit2("74.75");
		s.setExit1("77.5");
		s.setStop("82.75");
		s.setTargetDate("2024-07-30");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("82.37");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AR");
		s.setEntry("32.54");
		s.setExit2("30.5");
		s.setExit1("30.9");
		s.setStop("33");
		s.setTargetDate("2024-08-12");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("32.02");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("UBER");
		s.setEntry("69.31");
		s.setExit2("65");
		s.setExit1("65.8");
		s.setStop("70.28");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("69.27");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SNOW");
		s.setEntry("135.63");
		s.setExit2("127.25");
		s.setExit1("128.75");
		s.setStop("137.53");
		s.setTargetDate("2024-07-25");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("137.5");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("OKTA");
		s.setEntry("92.98");
		s.setExit2("87.25");
		s.setExit1("88.25");
		s.setStop("94.3");
		s.setTargetDate("2024-07-22");
		s.setAddedDate("2024-07-10");
		s.setAddPrice("92.79");
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NFLX");
		s.setEntry("681.49");
		s.setAddPrice("677.65");
		s.setExit2("664");
		s.setExit1("667");
		s.setStop("685.49");
		s.setTargetDate("2024-07-18");
		s.setAddedDate("2024-07-10");		
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CRSP");
		s.setEntry("54.8");
		s.setAddPrice("55.56");
		s.setExit2("42");
		s.setExit1("45");
		s.setStop("57.65");
		s.setTargetDate("2024-08-19");
		s.setAddedDate("2024-07-10");		
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BERY");
		s.setEntry("59.12");
		s.setAddPrice("59.15");
		s.setExit2("55");
		s.setExit1("55.8");
		s.setStop("60.05");
		s.setTargetDate("2024-07-26");
		s.setAddedDate("2024-07-10");		
		s.setType(Constants.SHORT);
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ABNB");
		s.setEntry("150.78");
		s.setAddPrice("151.84");
		s.setExit2("145.4");
		s.setExit1("146.4");
		s.setStop("152");
		s.setTargetDate("2024-07-24");
		s.setAddedDate("2024-07-10");		
		s.setType(Constants.SHORT);
		picksList.add(s);

		return picksList;
	}
}