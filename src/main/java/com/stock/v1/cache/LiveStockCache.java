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
		s.setAddPattern("440");
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
		s.setAddPattern("303");
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
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("XOM");
		s.setEntry("113.15");
		s.setAddPrice("113.27");
		s.setExit2("109.2");
		s.setExit1("111.6");
		s.setStop("114.04");
		s.setTargetDate("2024-07-24");
		s.setAddedDate("2024-07-12");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("QCOM");
		s.setEntry("204.75");
		s.setAddPrice("202.43");
		s.setExit2("176");
		s.setExit1("181");
		s.setStop("211.1");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-12");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("FUTU");
		s.setEntry("68.86");
		s.setAddPrice("68.57");
		s.setExit2("62");
		s.setExit1("63.25");
		s.setStop("211.1");
		s.setTargetDate("2024-08-20");
		s.setAddedDate("2024-07-12");		
		s.setType(Constants.SHORT);
		s.setAddPattern("202");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MARA");
		s.setEntry("20.68");
		s.setAddPrice("20.77");
		s.setExit1("22.2");
		s.setExit2("24");
		s.setStop("19.19");
		s.setTargetDate("2024-08-20");
		s.setAddedDate("2024-07-12");		
		s.setType(Constants.LONG);
		s.setAddPattern("550");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NVT");
		s.setEntry("78.53");
		s.setAddPrice("78.58");
		s.setExit1("81.3");
		s.setExit2("81.9");
		s.setStop("77.76");
		s.setTargetDate("2024-08-07");
		s.setAddedDate("2024-07-15");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("PARA");
		s.setEntry("11.58");
		s.setAddPrice("11.58");
		s.setExit1("12.35");
		s.setExit2("12.55");
		s.setStop("11.35");
		s.setTargetDate("2024-08-02");
		s.setAddedDate("2024-07-15");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("GME");
		s.setEntry("26.06");
		s.setAddPrice("27");
		s.setExit1("30");
		s.setExit2("31");
		s.setStop("24.97");
		s.setTargetDate("2024-08-27");
		s.setAddedDate("2024-07-15");		
		s.setType(Constants.LONG);
		s.setAddPattern("330");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("W");
		s.setEntry("52.9");
		s.setAddPrice("53.57");
		s.setExit1("56.4");
		s.setExit2("57.1");
		s.setStop("51.94");
		s.setTargetDate("2024-08-12");
		s.setAddedDate("2024-07-15");		
		s.setType(Constants.LONG);
		s.setAddPattern("330");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AMZN");
		s.setEntry("192.5");
		s.setAddPrice("192.72");
		s.setExit2("183");
		s.setExit1("184.75");
		s.setStop("194.68");
		s.setTargetDate("2024-08-16");
		s.setAddedDate("2024-07-15");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AR");
		s.setEntry("32.38");
		s.setAddPrice("31.99");
		s.setExit2("30.5");
		s.setExit1("30.9");
		s.setStop("33");
		s.setTargetDate("2024-08-28");
		s.setAddedDate("2024-07-15");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LYFT");
		s.setEntry("13.43");
		s.setAddPrice("13.38");
		s.setExit2("12.3");
		s.setExit1("12.5");
		s.setStop("13.69");
		s.setTargetDate("2024-08-05");
		s.setAddedDate("2024-07-16");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TSLA");
		s.setEntry("257.86");
		s.setAddPrice("256.56");
		s.setExit2("243");
		s.setExit1("246");
		s.setStop("261.14");
		s.setTargetDate("2024-07-17");
		s.setAddedDate("2024-07-16");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RRC");
		s.setEntry("34.68");
		s.setAddPrice("34.56");
		s.setExit1("36");
		s.setExit2("36.6");
		s.setStop("33.54");
		s.setTargetDate("2024-08-14");
		s.setAddedDate("2024-07-16");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SBUX");
		s.setEntry("75.05");
		s.setAddPrice("75.51");
		s.setExit1("78.2");
		s.setExit2("78.9");
		s.setStop("74.18");
		s.setTargetDate("2024-07-23");
		s.setAddedDate("2024-07-16");		
		s.setType(Constants.LONG);
		s.setAddPattern("330");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("GPK");
		s.setEntry("27.78");
		s.setAddPrice("28.08");
		s.setExit1("29.4");
		s.setExit2("29.7");
		s.setStop("27.34");
		s.setTargetDate("2024-08-06");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("IPG");
		s.setEntry("29.59");
		s.setAddPrice("30.2");
		s.setExit1("30.8");
		s.setExit2("31");
		s.setStop("29.26");
		s.setTargetDate("2024-08-15");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.LONG);
		s.setAddPattern("330");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("KTOS");
		s.setEntry("21.21");
		s.setAddPrice("21.72");
		s.setExit1("23.2");
		s.setExit2("23.6");
		s.setStop("20.67");
		s.setTargetDate("2024-09-04");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.LONG);
		s.setAddPattern("110");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BA");
		s.setEntry("182.84");
		s.setAddPrice("184.84");
		s.setExit1("189.75");
		s.setExit2("191.25");
		s.setStop("180.91");
		s.setTargetDate("2024-07-26");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.LONG);
		s.setAddPattern("110");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AMC");
		s.setEntry("5.07");
		s.setAddPrice("5.42");
		s.setExit1("6.1");
		s.setExit2("6.4");
		s.setStop("4.77");
		s.setTargetDate("2024-08-22");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.LONG);
		s.setAddPattern("110");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NFLX");
		s.setEntry("662.62");
		s.setAddPrice("647.46");
		s.setExit2("624");
		s.setExit1("631");
		s.setStop("671.34");
		s.setTargetDate("2024-08-26");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("EW");
		s.setEntry("89.9");
		s.setAddPrice("87.31");
		s.setExit2("84");
		s.setExit1("85");
		s.setStop("91.24");
		s.setTargetDate("2024-08-15");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AMZN");
		s.setEntry("186.88");
		s.setAddPrice("187.93");
		s.setExit2("172");
		s.setExit1("175");
		s.setStop("190.15");
		s.setTargetDate("2024-08-23");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LYFT");
		s.setEntry("13.01");
		s.setAddPrice("12.73");
		s.setExit2("11.8");
		s.setExit1("12");
		s.setStop("13.29");
		s.setTargetDate("2024-08-07");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HOOD");
		s.setEntry("24.23");
		s.setAddPrice("24.18");
		s.setExit2("22.9");
		s.setExit1("23.1");
		s.setStop("24.54");
		s.setTargetDate("2024-07-19");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AI");
		s.setEntry("29.53");
		s.setAddPrice("29.85");
		s.setExit2("27.4");
		s.setExit1("27.8");
		s.setStop("30.02");
		s.setTargetDate("2024-07-30");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AAL");
		s.setEntry("10.99");
		s.setAddPrice("11.01");
		s.setExit2("10.49");
		s.setExit1("10.58");
		s.setStop("11.21");
		s.setTargetDate("2024-08-02");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("EAT");
		s.setEntry("66.69");
		s.setAddPrice("66.5");
		s.setExit2("59");
		s.setExit1("60.5");
		s.setStop("68.43");
		s.setTargetDate("2024-07-31");
		s.setAddedDate("2024-07-17");		
		s.setType(Constants.SHORT);
		s.setAddPattern("202");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("EXEL");
		s.setEntry("22.63");
		s.setAddPrice("22.56");
		s.setExit1("23.6");
		s.setExit2("23.8");
		s.setStop("22.36");
		s.setTargetDate("2024-09-04");
		s.setAddedDate("2024-07-18");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("JPM");
		s.setEntry("210.06");
		s.setAddPrice("209.98");
		s.setExit1("216.25");
		s.setExit2("217.75");
		s.setStop("208.31");
		s.setTargetDate("2024-08-02");
		s.setAddedDate("2024-07-18");		
		s.setType(Constants.LONG);
		s.setAddPattern("330");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BALL");
		s.setEntry("62.56");
		s.setAddPrice("61.71");
		s.setExit1("66");
		s.setExit2("66.8");
		s.setStop("61.6");
		s.setTargetDate("2024-09-05");
		s.setAddedDate("2024-07-18");		
		s.setType(Constants.LONG);
		s.setAddPattern("330");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MBLY");
		s.setEntry("26.79");
		s.setAddPrice("26.44");
		s.setExit2("24.8");
		s.setExit1("25.6");
		s.setStop("27.55");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-18");		
		s.setType(Constants.SHORT);
		s.setAddPattern("505");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("WYNN");
		s.setEntry("83.5");
		s.setAddPrice("83.48");
		s.setExit2("80.1");
		s.setExit1("80.8");
		s.setStop("84.26");
		s.setTargetDate("2024-08-02");
		s.setAddedDate("2024-07-18");		
		s.setType(Constants.SHORT);
		s.setAddPattern("202");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("PENN");
		s.setEntry("18.1");
		s.setAddPrice("18.52");
		s.setExit2("14.5");
		s.setExit1("15.2");
		s.setStop("18.92");
		s.setTargetDate("2024-09-12");
		s.setAddedDate("2024-07-19");		
		s.setType(Constants.SHORT);
		s.setAddPattern("606");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("PR");
		s.setEntry("15.97");
		s.setAddPrice("16.09");
		s.setExit2("14.9");
		s.setExit1("15.1");
		s.setStop("16.22");
		s.setTargetDate("2024-08-23");
		s.setAddedDate("2024-07-19");		
		s.setType(Constants.SHORT);
		s.setAddPattern("505");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("OKTA");
		s.setEntry("92.98");
		s.setAddPrice("94.03");
		s.setExit2("87.25");
		s.setExit1("88.25");
		s.setStop("94.3");
		s.setTargetDate("2024-07-22");
		s.setAddedDate("2024-07-19");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ESTC");
		s.setEntry("111.4");
		s.setAddPrice("111.84");
		s.setExit2("98");
		s.setExit1("100");
		s.setStop("114.43");
		s.setTargetDate("2024-08-26");
		s.setAddedDate("2024-07-19");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TECH");
		s.setEntry("76.66");
		s.setAddPrice("75.46");
		s.setExit2("72.5");
		s.setExit1("73.3");
		s.setStop("77.59");
		s.setTargetDate("2024-07-31");
		s.setAddedDate("2024-07-19");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LI");
		s.setEntry("19.89");
		s.setAddPrice("19.64");
		s.setExit2("17.6");
		s.setExit1("18");
		s.setStop("20.41");
		s.setTargetDate("2024-08-13");
		s.setAddedDate("2024-07-19");		
		s.setType(Constants.SHORT);
		s.setAddPattern("303");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NVDA");
		s.setEntry("121.59");
		s.setAddPrice("122.59");
		s.setExit1("126");
		s.setExit2("127");
		s.setStop("120.33");
		s.setTargetDate("2024-07-26");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("JMIA");
		s.setEntry("13.67");
		s.setAddPrice("13.8");
		s.setExit1("14.8");
		s.setExit2("15.3");
		s.setStop("12.23");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AMX");
		s.setEntry("17.07");
		s.setAddPrice("17.32");
		s.setExit1("17.7");
		s.setExit2("17.85");
		s.setStop("16.89");
		s.setTargetDate("2024-08-07");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("440");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TAL");
		s.setEntry("10.4");
		s.setAddPrice("10.58");
		s.setExit1("11.45");
		s.setExit2("11.65");
		s.setStop("10.52");
		s.setTargetDate("2024-08-12");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("220");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SOXL");
		s.setEntry("47.23");
		s.setAddPrice("50.53");
		s.setExit1("63");
		s.setExit2("67");
		s.setStop("42.79");
		s.setTargetDate("2024-07-29");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("220");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RILY");
		s.setEntry("17.55");
		s.setAddPrice("17.55");
		s.setExit1("21.3");
		s.setExit2("22.2");
		s.setStop("16.5");
		s.setTargetDate("2024-07-30");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("220");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("OKTA");
		s.setEntry("95.55");
		s.setAddPrice("96.05");
		s.setExit1("104.25");
		s.setExit2("106");
		s.setStop("93.16");
		s.setTargetDate("2024-09-04");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.LONG);
		s.setAddPattern("220");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("OXY");
		s.setEntry("60.41");
		s.setAddPrice("60.41");
		s.setExit2("55.6");
		s.setExit1("56.5");
		s.setStop("61.49");
		s.setTargetDate("2024-09-09");
		s.setAddedDate("2024-07-23");		
		s.setType(Constants.SHORT);
		s.setAddPattern("404");
		picksList.add(s);

		return picksList;
	}
}