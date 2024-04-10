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
		s.setTicker("EQH");
		s.setEntry("34");
		s.setExit1("35.5");
		s.setExit2("36.4");
		s.setStop("33.46");
		s.setTargetDate("2024-03-25");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("34.1");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CNP");
		s.setEntry("27.94");
		s.setExit1("29");
		s.setExit2("29.3");
		s.setStop("27.78");
		s.setTargetDate("2024-03-05");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("28.28");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("KMI");
		s.setEntry("17");
		s.setExit1("17.5");
		s.setExit2("18.35");
		s.setStop("16.89");
		s.setTargetDate("2024-04-10");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("17.42");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DAL");
		s.setEntry("40.32");
		s.setExit1("42.2");
		s.setExit2("42.9");
		s.setStop("39.81");
		s.setTargetDate("2024-03-18");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("40.76");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BRX");
		s.setEntry("22.83");
		s.setExit1("23.8");
		s.setExit2("24");
		s.setStop("22.57");
		s.setTargetDate("2024-03-27");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("22.92");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("KSS");
		s.setEntry("27.12");
		s.setExit1("30.8");
		s.setExit2("33.2");
		s.setStop("26.11");
		s.setTargetDate("2024-03-15");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("27.43");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TPX");
		s.setEntry("48.94");
		s.setExit1("52.5");
		s.setExit2("54.6");
		s.setStop("47.96");
		s.setTargetDate("2024-03-28");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("49.99");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CTVA");
		s.setEntry("53.57");
		s.setExit1("56.4");
		s.setExit2("58.4");
		s.setStop("52.78");
		s.setTargetDate("2024-03-13");
		s.setAddedDate("2024-02-21");
		s.setAddPrice("54.22");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("GM");
		s.setEntry("38.5");
		s.setExit1("40.3");
		s.setExit2("40.6");
		s.setStop("37.86");
		s.setTargetDate("2024-03-11");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("39.34");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("COIN");
		s.setEntry("168.07");
		s.setExit1("175");
		s.setExit2("180");
		s.setStop("166.35");
		s.setTargetDate("2024-03-26");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("170.91");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SBUX");
		s.setEntry("94.24");
		s.setExit1("96.8");
		s.setExit2("97.8");
		s.setStop("93.53");
		s.setTargetDate("2024-03-13");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("95.78");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TSN");
		s.setEntry("53.12");
		s.setExit1("54.7");
		s.setExit2("55.4");
		s.setStop("52.68");
		s.setTargetDate("2024-03-06");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("53.09");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SEDG");
		s.setEntry("71.38");
		s.setExit1("81");
		s.setExit2("84");
		s.setStop("68.58");
		s.setTargetDate("2024-02-29");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("69.93");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BXP");
		s.setEntry("65.36");
		s.setExit1("68.7");
		s.setExit2("71.5");
		s.setStop("64.44");
		s.setTargetDate("2024-03-14");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("65.43");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ALLY");
		s.setEntry("36.21");
		s.setExit1("38.3");
		s.setExit2("38.8");
		s.setStop("35.62");
		s.setTargetDate("2024-03-28");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("36");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("WH");
		s.setEntry("80.54");
		s.setExit1("84.6");
		s.setExit2("85.5");
		s.setStop("79.41");
		s.setTargetDate("2024-02-27");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("80.11");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("VALE");
		s.setEntry("13.56");
		s.setExit1("14.05");
		s.setExit2("14.15");
		s.setStop("13.43");
		s.setTargetDate("2024-03-18");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("13.51");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("STX");
		s.setEntry("87.01");
		s.setExit1("90.5");
		s.setExit2("95.75");
		s.setStop("86.55");
		s.setTargetDate("2024-03-18");
		s.setAddedDate("2024-02-22");
		s.setAddPrice("88.02");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RILY");
		s.setEntry("15.73");
		s.setExit1("20.1");
		s.setExit2("21.75");
		s.setStop("14.4");
		s.setTargetDate("2024-03-07");
		s.setAddedDate("2024-02-26");
		s.setAddPrice("15.92");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CVX");
		s.setEntry("154.25");
		s.setExit1("159.75");
		s.setExit2("162.5");
		s.setStop("152.71");
		s.setTargetDate("2024-04-05");
		s.setAddedDate("2024-02-26");
		s.setAddPrice("154.45");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DOCU");
		s.setEntry("51.74");
		s.setExit1("54");
		s.setExit2("56.2");
		s.setStop("51.15");
		s.setTargetDate("2024-04-03");
		s.setAddedDate("2024-02-26");
		s.setAddPrice("51.8");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("PNR");
		s.setEntry("75.53");
		s.setExit1("78.5");
		s.setExit2("79.2");
		s.setStop("74.69");
		s.setTargetDate("2024-03-22");
		s.setAddedDate("2024-02-26");
		s.setAddPrice("75.95");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NKE");
		s.setEntry("104.61");
		s.setExit1("109.75");
		s.setExit2("111");
		s.setStop("103.17");
		s.setTargetDate("2024-04-04");
		s.setAddedDate("2024-02-27");
		s.setAddPrice("105.15");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ARMK");
		s.setEntry("30.59");
		s.setExit1("32.9");
		s.setExit2("33.4");
		s.setStop("29.95");
		s.setTargetDate("2024-03-14");
		s.setAddedDate("2024-02-27");
		s.setAddPrice("30.2");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ROKU");
		s.setEntry("63.14");
		s.setExit1("66");
		s.setExit2("67.9");
		s.setStop("62.05");
		s.setTargetDate("2024-03-07");
		s.setAddedDate("2024-02-27");
		s.setAddPrice("63.83");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TAL");
		s.setEntry("15.07");
		s.setExit1("17.2");
		s.setExit2("17.7");
		s.setStop("14.47");
		s.setTargetDate("2024-03-26");
		s.setAddedDate("2024-02-27");
		s.setAddPrice("15.11");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NRDS");
		s.setEntry("16.28");
		s.setExit1("18.3");
		s.setExit2("18.8");
		s.setStop("15.7");
		s.setTargetDate("2024-03-15");
		s.setAddedDate("2024-02-27");
		s.setAddPrice("16.98");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BGC");
		s.setEntry("7.12");
		s.setExit1("7.95");
		s.setExit2("8.15");
		s.setStop("6.89");
		s.setTargetDate("2024-04-12");
		s.setAddedDate("2024-02-27");
		s.setAddPrice("6.98");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("QCOM");
		s.setEntry("154.56");
		s.setExit1("158.7");
		s.setExit2("159.6");
		s.setStop("153.41");
		s.setTargetDate("2024-03-01");
		s.setAddedDate("2024-02-28");
		s.setAddPrice("155.85");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ABNB");
		s.setEntry("153.62");
		s.setExit1("168");
		s.setExit2("171");
		s.setStop("149.72");
		s.setTargetDate("2024-04-03");
		s.setAddedDate("2024-02-28");
		s.setAddPrice("153.43");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CRBG");
		s.setEntry("24.35");
		s.setExit1("25.3");
		s.setExit2("25.8");
		s.setStop("24.08");
		s.setTargetDate("2024-03-14");
		s.setAddedDate("2024-02-28");
		s.setAddPrice("24.43");
		picksList.add(s);

		s = new Picks();
		s.setTicker("UCO");
		s.setEntry("29.38");
		s.setExit1("30.3");
		s.setExit2("31.1");
		s.setStop("28.98");
		s.setTargetDate("2024-03-14");
		s.setAddedDate("2024-02-28");
		s.setAddPrice("29.54");
		picksList.add(s);

		s = new Picks();
		s.setTicker("LBRT");
		s.setEntry("21.07");
		s.setExit1("22");
		s.setExit2("24");
		s.setStop("20.38");
		s.setTargetDate("2024-04-15");
		s.setAddedDate("2024-02-28");
		s.setAddPrice("21.13");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LAZ");
		s.setEntry("38.5");
		s.setExit1("40.2");
		s.setExit2("41.3");
		s.setStop("38.03");
		s.setTargetDate("2024-04-02");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("38.54");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("XOM");
		s.setEntry("104.46");
		s.setExit1("109");
		s.setExit2("110");
		s.setStop("103.21");
		s.setTargetDate("2024-04-22");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("104.52");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ESTC");
		s.setEntry("131.09");
		s.setExit1("145");
		s.setExit2("148");
		s.setStop("127.18");
		s.setTargetDate("2024-04-01");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("133.81");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SVXY");
		s.setEntry("105.25");
		s.setExit1("114");
		s.setExit2("116");
		s.setStop("102.83");
		s.setTargetDate("2024-04-16");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("110.36");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SUN");
		s.setEntry("61.15");
		s.setExit1("65");
		s.setExit2("65.8");
		s.setStop("60.09");
		s.setTargetDate("2024-04-16");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("61.38");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NFLX");
		s.setEntry("583.79");
		s.setExit1("610");
		s.setExit2("616");
		s.setStop("576.48");
		s.setTargetDate("2024-03-14");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("602.92");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HESM");
		s.setEntry("34.17");
		s.setExit1("36.6");
		s.setExit2("37.1");
		s.setStop("33.49");
		s.setTargetDate("2024-04-23");
		s.setAddedDate("2024-02-29");
		s.setAddPrice("34.09");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("WHD");
		s.setEntry("46.16");
		s.setExit1("51.75");
		s.setExit2("52.75");
		s.setStop("44.64");
		s.setTargetDate("2024-03-05");
		s.setAddedDate("2024-03-01");
		s.setAddPrice("46.02");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NVO");
		s.setEntry("121.78");
		s.setExit1("127.5");
		s.setExit2("129");
		s.setStop("120.16");
		s.setTargetDate("2024-04-04");
		s.setAddedDate("2024-03-01");
		s.setAddPrice("124.23");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MSFT");
		s.setEntry("409.34");
		s.setExit1("431");
		s.setExit2("435");
		s.setStop("403.45");
		s.setTargetDate("2024-04-22");
		s.setAddedDate("2024-03-04");
		s.setAddPrice("414.92");
		picksList.add(s);

		s = new Picks();
		s.setTicker("CROX");
		s.setEntry("124.7");
		s.setExit1("128.4");
		s.setExit2("129.2");
		s.setStop("123.68");
		s.setTargetDate("2024-04-04");
		s.setAddedDate("2024-03-04");
		s.setAddPrice("124.59");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AMZN");
		s.setEntry("175.03");
		s.setExit1("185");
		s.setExit2("187");
		s.setStop("172.37");
		s.setTargetDate("2024-04-23");
		s.setAddedDate("2024-03-04");
		s.setAddPrice("177.58");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DKNG");
		s.setEntry("42.31");
		s.setExit1("44.6");
		s.setExit2("45.1");
		s.setStop("41.69");
		s.setTargetDate("2024-03-13");
		s.setAddedDate("2024-03-05");
		s.setAddPrice("42.62");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DIS");
		s.setEntry("111.81");
		s.setExit1("118.25");
		s.setExit2("119.5");
		s.setStop("110.04");
		s.setTargetDate("2024-04-12");
		s.setAddedDate("2024-03-05");
		s.setAddPrice("112.87");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MTDR");
		s.setEntry("63.97");
		s.setExit1("66.4");
		s.setExit2("67");
		s.setStop("63.28");
		s.setTargetDate("2024-03-19");
		s.setAddedDate("2024-03-06");
		s.setAddPrice("63.6");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CVNA");
		s.setEntry("80.24");
		s.setExit1("89");
		s.setExit2("91");
		s.setStop("77.73");
		s.setTargetDate("2024-03-25");
		s.setAddedDate("2024-03-06");
		s.setAddPrice("77.81");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DELL");
		s.setEntry("119.67");
		s.setExit1("125");
		s.setExit2("126.5");
		s.setStop("119.61");
		s.setTargetDate("2024-03-19");
		s.setAddedDate("2024-03-07");
		s.setAddPrice("120.5");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NFLX");
		s.setEntry("606.51");
		s.setExit1("619");
		s.setExit2("622");
		s.setStop("603.07");
		s.setTargetDate("2024-03-14");
		s.setAddedDate("2024-03-07");
		s.setAddPrice("608.51");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CLF");
		s.setEntry("21.02");
		s.setExit1("22.5");
		s.setExit2("22.8");
		s.setStop("20.61");
		s.setTargetDate("2024-04-02");
		s.setAddedDate("2024-03-08");
		s.setAddPrice("20.99");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HOOD");
		s.setEntry("16.89");
		s.setExit1("18.7");
		s.setExit2("19");
		s.setStop("16.4");
		s.setTargetDate("2024-04-01");
		s.setAddedDate("2024-03-08");
		s.setAddPrice("17");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HAL");
		s.setEntry("35.71");
		s.setExit1("37.2");
		s.setExit2("37.5");
		s.setStop("35.31");
		s.setTargetDate("2024-03-27");
		s.setAddedDate("2024-03-08");
		s.setAddPrice("36.21");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SQ");
		s.setEntry("82.11");
		s.setExit1("89.25");
		s.setExit2("90.75");
		s.setStop("80.13");
		s.setTargetDate("2024-04-11");
		s.setAddedDate("2024-03-11");
		s.setAddPrice("81.53");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TDW");
		s.setEntry("81.46");
		s.setExit1("85");
		s.setExit2("85.8");
		s.setStop("80.47");
		s.setTargetDate("2024-03-18");
		s.setAddedDate("2024-03-12");
		s.setAddPrice("82.67");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RRR");
		s.setEntry("57.12");
		s.setExit1("60.3");
		s.setExit2("61");
		s.setStop("56.23");
		s.setTargetDate("2024-04-22");
		s.setAddedDate("2024-03-12");
		s.setAddPrice("57.47");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SRRK");
		s.setEntry("16.01");
		s.setExit1("17.4");
		s.setExit2("17.7");
		s.setStop("15.62");
		s.setTargetDate("2024-03-18");
		s.setAddedDate("2024-03-13");
		s.setAddPrice("16.38");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("COOP");
		s.setEntry("72.44");
		s.setExit1("77.25");
		s.setExit2("78.5");
		s.setStop("71.08");
		s.setTargetDate("2024-04-02");
		s.setAddedDate("2024-03-13");
		s.setAddPrice("73.16");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("T");
		s.setEntry("17.16");
		s.setExit1("18");
		s.setExit2("18.2");
		s.setStop("16.92");
		s.setTargetDate("2024-04-26");
		s.setAddedDate("2024-03-13");
		s.setAddPrice("17.19");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CXM");
		s.setEntry("13.78");
		s.setExit1("14.5");
		s.setExit2("14.65");
		s.setStop("13.58");
		s.setTargetDate("2024-03-26");
		s.setAddedDate("2024-03-13");
		s.setAddPrice("13.77");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CI");
		s.setEntry("343.81");
		s.setExit1("359");
		s.setExit2("362");
		s.setStop("339.66");
		s.setTargetDate("2024-05-03");
		s.setAddedDate("2024-03-13");
		s.setAddPrice("348.72");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CLSK");
		s.setEntry("15.81");
		s.setExit1("16.55");
		s.setExit2("16.7");
		s.setStop("15.61");
		s.setTargetDate("2024-03-15");
		s.setAddedDate("2024-03-14");
		s.setAddPrice("15.84");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CVNA");
		s.setEntry("78.03");
		s.setExit1("87");
		s.setExit2("89");
		s.setStop("75.53");
		s.setTargetDate("2024-03-28");
		s.setAddedDate("2024-03-14");
		s.setAddPrice("76.68");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("WES");
		s.setEntry("34.09");
		s.setExit1("35.8");
		s.setExit2("36.2");
		s.setStop("33.62");
		s.setTargetDate("2024-04-25");
		s.setAddedDate("2024-03-15");
		s.setAddPrice("34.53");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CRBG");
		s.setEntry("25.38");
		s.setExit1("26.4");
		s.setExit2("26.7");
		s.setStop("25.09");
		s.setTargetDate("2024-04-18");
		s.setAddedDate("2024-03-15");
		s.setAddPrice("25.41");
		picksList.add(s);		
		
		s = new Picks();
		s.setTicker("PFE");
		s.setEntry("28.08");
		s.setExit1("29");
		s.setExit2("29.2");
		s.setStop("27.81");
		s.setTargetDate("2024-03-18");
		s.setAddedDate("2024-03-15");
		s.setAddPrice("27.94");
		picksList.add(s);
		
		
		s = new Picks();
		s.setTicker("MSFT");
		s.setEntry("416.6");
		s.setExit1("431");
		s.setExit2("435");
		s.setStop("412.73");
		s.setTargetDate("2024-04-24");
		s.setAddedDate("2024-03-18");
		s.setAddPrice("417.32");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("D");
		s.setEntry("47.95");
		s.setExit1("52.5");
		s.setExit2("53.5");
		s.setStop("46.7");
		s.setTargetDate("2024-05-01");
		s.setAddedDate("2024-03-18");
		s.setAddPrice("48.51");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TGT");
		s.setEntry("166.88");
		s.setExit1("169.8");
		s.setExit2("170.4");
		s.setStop("166.07");
		s.setTargetDate("2024-03-26");
		s.setAddedDate("2024-03-18");
		s.setAddPrice("167.59");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("NVDA");
		s.setEntry("881.54");
		s.setExit1("924");
		s.setExit2("934");
		s.setStop("869.65");
		s.setTargetDate("2024-03-19");
		s.setAddedDate("2024-03-18");
		s.setAddPrice("884.55");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MU");
		s.setEntry("94.54");
		s.setExit1("98.4");
		s.setExit2("99.2");
		s.setStop("93.48");
		s.setTargetDate("2024-03-22");
		s.setAddedDate("2024-03-18");
		s.setAddPrice("93.78");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DXCM");
		s.setEntry("133.54");
		s.setExit1("137.3");
		s.setExit2("138.1");
		s.setStop("132.51");
		s.setTargetDate("2024-03-22");
		s.setAddedDate("2024-03-18");
		s.setAddPrice("134.72");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("AMZN");
		s.setEntry("175.95");
		s.setExit1("183.75");
		s.setExit2("185.5");
		s.setStop("173.79");
		s.setTargetDate("2024-05-10");
		s.setAddedDate("2024-03-20");
		s.setAddPrice("178.15");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MS");
		s.setEntry("88.94");
		s.setExit1("95");
		s.setExit2("96.5");
		s.setStop("87.25");
		s.setTargetDate("2024-05-07");
		s.setAddedDate("2024-03-21");
		s.setAddPrice("93.4");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CLSK");
		s.setEntry("17.28");
		s.setExit1("23.25");
		s.setExit2("24.5");
		s.setStop("15.65");
		s.setTargetDate("2024-04-30");
		s.setAddedDate("2024-03-21");
		s.setAddPrice("20.77");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RL");
		s.setEntry("183.04");
		s.setExit1("190.5");
		s.setExit2("192");
		s.setStop("181.11");
		s.setTargetDate("2024-04-04");
		s.setAddedDate("2024-03-22");
		s.setAddPrice("187.6");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DOW");
		s.setEntry("57.27");
		s.setExit1("59.4");
		s.setExit2("59.9");
		s.setStop("56.67");
		s.setTargetDate("2024-04-10");
		s.setAddedDate("2024-03-22");
		s.setAddPrice("57.68");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TSM");
		s.setEntry("140.7");
		s.setExit1("146.25");
		s.setExit2("147.5");
		s.setStop("139.15");
		s.setTargetDate("2024-04-01");
		s.setAddedDate("2024-03-22");
		s.setAddPrice("140.54");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CVX");
		s.setEntry("152.27");
		s.setExit1("158.5");
		s.setExit2("160");
		s.setStop("150.52");
		s.setTargetDate("2024-04-23");
		s.setAddedDate("2024-03-25");
		s.setAddPrice("156.47");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HOOD");
		s.setEntry("18.81");
		s.setExit1("20.4");
		s.setExit2("20.7");
		s.setStop("18.38");
		s.setTargetDate("2024-04-05");
		s.setAddedDate("2024-03-25");
		s.setAddPrice("19.08");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("RRC");
		s.setEntry("33.08");
		s.setExit1("34.8");
		s.setExit2("35.1");
		s.setStop("32.62");
		s.setTargetDate("2024-04-29");
		s.setAddedDate("2024-03-25");
		s.setAddPrice("33.44");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("ARM");
		s.setEntry("139");
		s.setExit1("149");
		s.setExit2("151");
		s.setStop("136.25");
		s.setTargetDate("2024-03-26");
		s.setAddedDate("2024-03-25");
		s.setAddPrice("138.31");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("BHVN");
		s.setEntry("57.48");
		s.setExit1("64.75");
		s.setExit2("66.5");
		s.setStop("55.56");
		s.setTargetDate("2024-04-23");
		s.setAddedDate("2024-03-26");
		s.setAddPrice("56.29");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CVNA");
		s.setEntry("86.68");
		s.setExit1("101");
		s.setExit2("104");
		s.setStop("82.73");
		s.setTargetDate("2024-05-06");
		s.setAddedDate("2024-03-26");
		s.setAddPrice("90.81");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TNA");
		s.setEntry("41.17");
		s.setExit1("47");
		s.setExit2("48.25");
		s.setStop("39.56");
		s.setTargetDate("2024-05-15");
		s.setAddedDate("2024-03-28");
		s.setAddPrice("42.92");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TDW");
		s.setEntry("91.91");
		s.setExit1("94.8");
		s.setExit2("95.4");
		s.setStop("91.11");
		s.setTargetDate("2024-04-10");
		s.setAddedDate("2024-03-28");
		s.setAddPrice("92");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("OVV");
		s.setEntry("51.84");
		s.setExit1("53.6");
		s.setExit2("53.9");
		s.setStop("51.36");
		s.setTargetDate("2024-04-23");
		s.setAddedDate("2024-03-28");
		s.setAddPrice("51.9");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("MDT");
		s.setEntry("85.61");
		s.setExit1("91.4");
		s.setExit2("92.3");
		s.setStop("86.16");
		s.setTargetDate("2024-04-17");
		s.setAddedDate("2024-03-28");
		s.setAddPrice("87.15");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DASH");
		s.setEntry("134.35");
		s.setExit1("141.25");
		s.setExit2("142.75");
		s.setStop("132.44");
		s.setTargetDate("2024-04-03");
		s.setAddedDate("2024-03-28");
		s.setAddPrice("137.72");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CMA");
		s.setEntry("52.03");
		s.setExit1("57");
		s.setExit2("58.25");
		s.setStop("50.63");
		s.setTargetDate("2024-05-06");
		s.setAddedDate("2024-03-28");
		s.setAddPrice("54.99");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SRRK");
		s.setEntry("17.16");
		s.setExit1("20");
		s.setExit2("22");
		s.setStop("16.39");
		s.setTargetDate("2024-05-16");
		s.setAddedDate("2024-04-01");
		s.setAddPrice("16.88");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("QCOM");
		s.setEntry("171.13");
		s.setExit1("179.5");
		s.setExit2("181.25");
		s.setStop("168.83");
		s.setTargetDate("2024-04-29");
		s.setAddedDate("2024-04-01");
		s.setAddPrice("171.72");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DOCU");
		s.setEntry("60.28");
		s.setExit1("62.8");
		s.setExit2("63.4");
		s.setStop("59.57");
		s.setTargetDate("2024-04-18");
		s.setAddedDate("2024-04-03");
		s.setAddPrice("60.55");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HPK");
		s.setEntry("16.04");
		s.setExit1("16.8");
		s.setExit2("17");
		s.setStop("15.82");
		s.setTargetDate("2024-04-30");
		s.setAddedDate("2024-04-03");
		s.setAddPrice("15.83");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LBRT");
		s.setEntry("21.04");
		s.setExit1("23.4");
		s.setExit2("24");
		s.setStop("20.38");
		s.setTargetDate("2024-04-15");
		s.setAddedDate("2024-04-04");
		s.setAddPrice("22.62");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("TENB");
		s.setEntry("46.92");
		s.setExit1("50.5");
		s.setExit2("51.1");
		s.setStop("46.32");
		s.setTargetDate("2024-04-19");
		s.setAddedDate("2024-04-04");
		s.setAddPrice("47.51");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("FULT");
		s.setEntry("15.38");
		s.setExit1("16.5");
		s.setExit2("16.8");
		s.setStop("15.06");
		s.setTargetDate("2024-05-03");
		s.setAddedDate("2024-04-04");
		s.setAddPrice("15.33");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LAZ");
		s.setEntry("39.49");
		s.setExit1("41.8");
		s.setExit2("42.4");
		s.setStop("38.85");
		s.setTargetDate("2024-04-16");
		s.setAddedDate("2024-04-04");
		s.setAddPrice("40.15");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CLSK");
		s.setEntry("15.89");
		s.setExit1("23.25");
		s.setExit2("24.5");
		s.setStop("15.65");
		s.setTargetDate("2024-04-30");
		s.setAddedDate("2024-04-04");
		s.setAddPrice("15.89");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("LLY");
		s.setEntry("781.8");
		s.setExit1("805");
		s.setExit2("810");
		s.setStop("775.4");
		s.setTargetDate("2024-04-09");
		s.setAddedDate("2024-04-05");
		s.setAddPrice("784.21");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("DKNG");
		s.setEntry("45.71");
		s.setExit1("47.4");
		s.setExit2("47.7");
		s.setStop("45.25");
		s.setTargetDate("2024-04-15");
		s.setAddedDate("2024-04-08");
		s.setAddPrice("45.51");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("HESM");
		s.setEntry("35.58");
		s.setExit1("36.7");
		s.setExit2("37.1");
		s.setStop("33.49");
		s.setTargetDate("2024-05-09");
		s.setAddedDate("2024-04-08");
		s.setAddPrice("36.04");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("COIN");
		s.setEntry("257.4");
		s.setExit1("274");
		s.setExit2("277");
		s.setStop("252.89");
		s.setTargetDate("2024-04-11");
		s.setAddedDate("2024-04-08");
		s.setAddPrice("256.99");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("SLB");
		s.setEntry("53.99");
		s.setExit1("56.4");
		s.setExit2("56.9");
		s.setStop("53.56");
		s.setTargetDate("2024-05-10");
		s.setAddedDate("2024-04-09");
		s.setAddPrice("54.17");
		picksList.add(s);
		
		s = new Picks();
		s.setTicker("CGNX");
		s.setEntry("41.8");
		s.setExit1("43.9");
		s.setExit2("44.3");
		s.setStop("41.23");
		s.setTargetDate("2024-05-06");
		s.setAddedDate("2024-04-09");
		s.setAddPrice("42.19");
		picksList.add(s);
		
		return picksList;
	}
}