package com.stock.v1.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.cache.MasterStocksCache;
import com.stock.v1.service.db.EarningsServiceDB;
import com.stock.v1.service.db.PatternServiceDB;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.service.rating.BarchartRatingService;
import com.stock.v1.service.rating.FinscreenerRatingService;
import com.stock.v1.service.rating.InvestingComRatingService;
import com.stock.v1.service.rating.InvestorObserverRatingService;
import com.stock.v1.service.rating.MarketBeatRatingService;
import com.stock.v1.service.rating.MarketEdgeRatingService;
import com.stock.v1.service.rating.NasdaqRatingService;
import com.stock.v1.service.rating.Portfolio123RatingService;
import com.stock.v1.service.rating.SeekingAlphaRatingService;
import com.stock.v1.service.rating.StockAnalysisRatingService;
import com.stock.v1.service.rating.StockInvestRatingService;
import com.stock.v1.service.rating.StreetRatingService;
import com.stock.v1.service.rating.TickeronRatingService;
import com.stock.v1.service.rating.TipRanksRatingService;
import com.stock.v1.service.rating.TradingViewRatingService;
import com.stock.v1.service.rating.ZacksRatingService;
import com.stock.v1.service.rating.ZenRatingService;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Earnings;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Monitor;
import com.stock.v1.vo.Pattern;
import com.stock.v1.vo.Stock;

@Service
public class StockService{

	@Autowired
	StockServiceDB stockServiceDB;

	@Autowired
	EarningsService earningsService;

	@Autowired
	EarningsServiceDB earningsServiceDB;

	@Autowired
	PatternServiceDB patternServiceDB;

	@Autowired
	BarchartRatingService barchartRatingService;

	@Autowired
	ZacksRatingService zacksRatingService;

	@Autowired
	ZenRatingService zenRatingService;

	@Autowired
	StockAnalysisRatingService stockAnalysisRatingService;

	@Autowired
	NasdaqRatingService nasdaqRatingService;

	@Autowired
	MarketBeatRatingService marketBeatRatingService;

	@Autowired
	StockInvestRatingService stockInvestRatingService;

	@Autowired
	TipRanksRatingService tipRanksRatingService;

	@Autowired
	StreetRatingService streetRatingService;

	@Autowired
	InvestorObserverRatingService investorObserverRatingService;

	@Autowired
	InvestingComRatingService investingComRatingService;

	@Autowired
	SeekingAlphaRatingService seekingAlphaRatingService;

	@Autowired
	TradingViewRatingService tradingViewRatingService;

	@Autowired
	TickeronRatingService tickeronRatingService;

	@Autowired
	Portfolio123RatingService portfolio123RatingService;

	@Autowired
	FinscreenerRatingService finscreenerRatingService;

	@Autowired
	MarketEdgeRatingService marketEdgeRatingService;

	@Autowired
	private Environment appProp;

	public List<Master> getMasterList() {		
		List<Master> list = stockServiceDB.getMasterList();
		stockServiceDB.populateLiveStockList();
		List<Stock> histList = LiveStockCache.getLiveStockList();
		List<Stock> watchList = LiveStockCache.getStockWatchList();
		List<Earnings> earningsList = earningsService.getEarningsHistory(null);

		//processProfitLossForAllMasterStocks(list);

		list.stream().forEach(x -> {
			if (Constants.MY_OPTIONS.contains(x.getTicker().toUpperCase())) {
				x.setOption(true);
			}
		});

		List<Pattern> patternList = patternServiceDB.getPatternHistory(null);
		if(patternList != null)
		{
			list.forEach(m -> {
				patternList.forEach(pattern -> {
					if(m.getTicker().equalsIgnoreCase(pattern.getTicker()))
					{
						m.setPattern(pattern);
					}
				});			
			});
		}

		// Create a Map of Stock objects indexed by ticker
		Map<String, Stock> stockMap = histList.stream()
				.collect(Collectors.toMap(stock -> stock.getTicker().toLowerCase(), stock -> stock));

		Map<String, Stock> stockWatchListMap = watchList.stream()
				.collect(Collectors.toMap(stock -> stock.getTicker().toLowerCase(), stock -> stock));

		for (Master master : list) {
			Stock stock = stockMap.get(master.getTicker().toLowerCase());
			if (stock != null) {
				master.setContractName(stock.getContractName());
				master.setContractExpiry(stock.getContractExpiry());
				master.setContractPoint(stock.getContractPoint());
				master.setContractMargin(stock.getContractMargin());
				master.setOpenInterest(stock.getOpenInterest());
				master.setGCShortDays(stock.getGCShortDate());
				master.setDCShortDays(stock.getDCShortDate());
				master.setGCLongDays(stock.getGCLongDate());
				master.setDCLongDays(stock.getDCLongDate());

				master.setRating(stock.getRating());
				String btTrend = stock.getRating().getBtTrend();
				String siusTrend = stock.getRating().getSiusRating();
				String tvMATrend = stock.getRating().getTradingViewMARating();
				String tickAITrend = stock.getRating().getTickeronAIRating();

				if(btTrend != null && ("BUY".equalsIgnoreCase(btTrend) || btTrend.toUpperCase().contains("BUY")))						
					master.setBuyStrength(master.getBuyStrength() + 25);
				if(siusTrend != null && ("BUY".equalsIgnoreCase(siusTrend) || siusTrend.toUpperCase().contains("BUY")))						
					master.setBuyStrength(master.getBuyStrength() + 25);
				if(tvMATrend != null && ("BUY".equalsIgnoreCase(tvMATrend) || tvMATrend.toUpperCase().contains("BUY")))						
					master.setBuyStrength(master.getBuyStrength() + 25);
				if(tickAITrend != null && ("B".equalsIgnoreCase(tickAITrend) || tickAITrend.toUpperCase().contains("B")))						
					master.setBuyStrength(master.getBuyStrength() + 25);

				if(btTrend != null && ("SELL".equalsIgnoreCase(btTrend) || btTrend.toUpperCase().contains("SELL")))						
					master.setSellStrength(master.getSellStrength() + 25);
				if(siusTrend != null && ("SELL".equalsIgnoreCase(siusTrend) || siusTrend.toUpperCase().contains("SELL")))						
					master.setSellStrength(master.getSellStrength() + 25);
				if(tvMATrend != null && ("SELL".equalsIgnoreCase(tvMATrend) || tvMATrend.toUpperCase().contains("SELL")))						
					master.setSellStrength(master.getSellStrength() + 25);
				if(tickAITrend != null && ("S".equalsIgnoreCase(tickAITrend) || tickAITrend.toUpperCase().contains("S")))						
					master.setSellStrength(master.getSellStrength() + 25);

				master.setBuyTrend(stock.getBuyTrend());
				master.setSellTrend(stock.getSellTrend());
			}

			Stock stockWatch = stockWatchListMap.get(master.getTicker().toLowerCase());
			if (stockWatch != null && StringUtils.isNotBlank(master.getPrice())) {
				master.setTrackingPrice(stockWatch.getTrackingPrice());
				master.setTrackingDiff(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(master.getPrice()) - Double.valueOf(stockWatch.getTrackingPrice())), false));
			}

			List<Earnings> earningsFilteredList = earningsList.stream()
					.filter(x -> x.getPriceEffect() != null && x.getTicker().equalsIgnoreCase(master.getTicker()))
					.collect(Collectors.toList());

			long positivePriceUpCount = earningsFilteredList.stream()
					.filter(x -> x.getPriceEffect() != null && Double.valueOf(x.getPriceEffect()) > 0)
					.count();

			if (!earningsFilteredList.isEmpty()) {
				double earningsSuccess = (double) positivePriceUpCount / earningsFilteredList.size() * 100;
				master.setEarningsSuccess(UtilityService.stripStringToTwoDecimals(String.valueOf(earningsSuccess), false));
				master.setEarningsCount(earningsFilteredList.size());
			}

		}

		list.forEach(stock -> {

			List<Earnings> earningsListnew = earningsService.getEarningsHistory(stock.getTicker());
			if(earningsListnew != null && !earningsListnew.isEmpty()) {
				stock.setPositiveStreak(earningsListnew.get(0).getPositiveStreak());
				stock.setNegativeStreak(earningsListnew.get(0).getNegativeStreak());
				stock.setCurrAndLastPriceEffectDiff(earningsListnew.get(0).getCurrAndLastPriceEffectDiff());				
			}			
		});
		MasterStocksCache.setMasterStocks(list);
		return list;
	}

	public List<Stock> getStockHistory(String ticker) {
		return stockServiceDB.getStockHistory(ticker);
	}

	public List<Stock> getStockHistoryByDate(String ticker, String date) {
		return stockServiceDB.getStockHistoryByDate(ticker,date);
	}

	public Stock getStockHistoryForADate(String ticker, String date) {
		return stockServiceDB.getStockHistoryForADate(ticker,date);
	}

	public String updatePriceUpDownAll() {
		try {
			// Get the list of Master objects
			List<Master> masterList = getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				updatePriceUpDown(master.getTicker());
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> updatePriceUpDownAll ==> "+ ex);
		}
		return "FAILURE";
	}

	public boolean updatePriceUpDown(String ticker) {
		try
		{
			List<Stock> list = stockServiceDB.getStockHistory(ticker);
			if (list.isEmpty()) {
				return false;
			}

			Master master = new Master();
			master.setTicker(ticker);

			String upPrice = list.get(list.size() - 1).getPrice();
			String downPrice = list.get(list.size() - 1).getPrice();
			master.setPrice(upPrice);
			double upChange = 0;
			double downChange = 0;
			double upHigh = Double.valueOf(list.get(list.size() - 1).getHigh());
			double downLow = Double.valueOf(list.get(list.size() - 1).getLow());
			master.setChange(list.get(list.size() - 1).getChange());
			list.remove(list.size() - 1);

			int upDays = 0;
			ListIterator<Stock> listIteratorUp = list.listIterator(list.size());
			while (listIteratorUp.hasPrevious()) {
				Stock stock = listIteratorUp.previous();
				if(Double.valueOf(stock.getPrice()) < Double.valueOf(upPrice))
				{
					upChange = upChange + (Double.valueOf(upPrice) - Double.valueOf(stock.getPrice()));
					upDays++;					
					upPrice = stock.getPrice();
					if(upHigh < Double.valueOf(stock.getHigh()))
						upHigh = Double.valueOf(stock.getHigh());

				}
				else
					break;
			}

			int downDays = 0;
			ListIterator<Stock> listIteratorDown = list.listIterator(list.size());
			while (listIteratorDown.hasPrevious()) {
				Stock stock = listIteratorDown.previous();
				if(Double.valueOf(stock.getPrice()) > Double.valueOf(downPrice))
				{
					downChange = downChange + (Double.valueOf(downPrice) - Double.valueOf(stock.getPrice()));						
					downDays++;
					downPrice = stock.getPrice();
					if(downLow > Double.valueOf(stock.getLow()))
						downLow = Double.valueOf(stock.getLow());
				}
				else
					break;
			}

			master.setUpDays(upDays);
			master.setUpBy(String.valueOf(upChange));
			master.setDownDays(downDays);
			master.setDownBy(String.valueOf(downChange));
			master.setUpHigh(String.valueOf(upHigh));
			master.setDownLow(String.valueOf(downLow));

			stockServiceDB.updatePriceUpDown(master);			

			return true;
		}
		catch(Exception ex)
		{
			System.err.println(ticker + " <updatePriceUpDown> " + ex);
			return false;
		}
	}

	public String markEarningDayAll() {
		try {
			// Get the list of Master objects
			List<Master> masterList = getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				markEarningDay(master.getTicker());
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> markEarningDayAll ==> "+ ex);
		}
		return "FAILURE";
	}

	public boolean markEarningDay(String ticker) {
		try
		{
			List<Earnings> list = earningsServiceDB.getEarningsHistory();
			if (list.isEmpty())
				return false;			

			List<Stock> stockHistoryList = stockServiceDB.getStockHistory(ticker);
			if (stockHistoryList.isEmpty())
				return false;			
			
			final List<Earnings> filteredEarnings = StringUtils.isNotBlank(ticker) 
				    ? list.stream()
				            .filter(x -> x.getTicker().equalsIgnoreCase(ticker))
				            .collect(Collectors.toList())
				    : list;			

			List<Stock> updateList = stockHistoryList.stream()
					.filter(stock -> filteredEarnings.stream()
							.anyMatch(earning ->
							stock.getTicker().equalsIgnoreCase(earning.getTicker()) &&
							stock.getDate().equalsIgnoreCase(earning.getDate())))
					.collect(Collectors.toList());

			updateList.forEach(stockServiceDB::markEarningDay);

			return true;
		}
		catch(Exception ex)
		{
			System.err.println(ticker + " <markEarningDay> " + ex);
			return false;
		}
	}


	public String updatePrevPriceAll() {
		try {
			// Get the list of Master objects
			List<Master> masterList = getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				updatePrevPrice(master.getTicker());
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> updatePrevPriceAll ==> "+ ex);
		}
		return "FAILURE";
	}

	public boolean updatePrevPrice(String ticker) {
		try
		{
			List<Stock> list = stockServiceDB.getStockHistory(ticker);

			if (list.isEmpty()) {
				return false;
			}

			String prevPrice = list.get(0).getPrice(); // Initialize prevPrice with the first item's price
			list.remove(0); // Remove the first item since it has no previous price
			list.remove(list.size() - 1); // Remove the first item since it has no previous price

			for (Stock stock : list) {
				stock.setPrevPrice(prevPrice);
				stockServiceDB.updatePrevPrice(stock);
				prevPrice = stock.getPrice();
			}
			return true;
		}
		catch(Exception ex)
		{
			System.err.println(ticker + " <updatePrevPrice> " + ex);
			return false;
		}
	}

	public String updateNextPriceAll() {
		try {
			// Get the list of Master objects
			List<Master> masterList = getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				updateNextPrice(master.getTicker());
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> updateNextPriceAll ==> "+ ex);
		}
		return "FAILURE";
	}

	public boolean updateNextPrice(String ticker) {
		try
		{
			List<Stock> list = stockServiceDB.getTop2StockHistory(ticker);

			if (list.isEmpty()) {
				return false;
			}

			if(list.size() > 1)
			{
				list.get(list.size()-1).setNextPrice(list.get(list.size()-2).getPrice());
				stockServiceDB.updateNextPrice(list.get(list.size()-1));
			}

			/*String nextPrice = list.get(list.size() - 1).getPrice(); // Initialize prevPrice with the first item's price
			list.remove(list.size() - 1); // Remove the first item since it has no previous price

			ListIterator<Stock> listIterator = list.listIterator(list.size());
			while (listIterator.hasPrevious()) {
				Stock stock = listIterator.previous();
				stock.setNextPrice(nextPrice);
				stockServiceDB.updateNextPrice(stock);
				nextPrice = stock.getPrice();
			}*/
			return true;
		}
		catch(Exception ex)
		{
			System.err.println(ticker + " <updateNextPrice> " + ex);
			return false;
		}
	}

	public String updateNextPriceAllOld() {
		try {
			// Get the list of Master objects
			List<Master> masterList = getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				updateNextPriceOld(master.getTicker());
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> updateNextPriceAll ==> "+ ex);
		}
		return "FAILURE";
	}

	public boolean updateNextPriceOld(String ticker) {
		try
		{
			List<Stock> list = stockServiceDB.getStockHistory(ticker);

			if (list.isEmpty()) {
				return false;
			}

			String nextPrice = list.get(list.size() - 1).getPrice(); // Initialize prevPrice with the first item's price
			list.remove(list.size() - 1); // Remove the first item since it has no previous price

			ListIterator<Stock> listIterator = list.listIterator(list.size());
			while (listIterator.hasPrevious()) {
				Stock stock = listIterator.previous();
				stock.setNextPrice(nextPrice);
				stockServiceDB.updateNextPrice(stock);
				nextPrice = stock.getPrice();
			}
			return true;
		}
		catch(Exception ex)
		{
			System.err.println(ticker + " <updateNextPrice> " + ex);
			return false;
		}
	}


	public String populateInitialStockHistory(String ticker) {
		try {
			// Get the folder name from the properties
			String folderName = appProp.getProperty("stock.history.folder.name");

			// Use the class loader to access resources
			ClassLoader cl = this.getClass().getClassLoader();
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

			// Resolve resources using the folder name and ticker
			Resource[] resources = resolver.getResources(folderName + "/" + ticker.toUpperCase() + ".csv");

			// Check if resources were found
			if (resources != null && resources.length > 0) {
				// Select the first resource (assuming there is only one)
				Resource resource = resources[0];

				// Read the CSV file for stock history
				List<Stock> list = UtilityService.readCsvFileForStockHistory(resource.getFile().getPath(), ticker);

				// Check if the list is not empty
				if (list != null && !list.isEmpty()) {
					List<Stock> histList = stockServiceDB.getStockHistory(ticker);
					if(histList != null && !histList.isEmpty())
					{
						List<String> histDates = histList.stream()
								.map(Stock::getDate)
								.collect(Collectors.toList());
						list.removeIf(stock -> histDates.contains(stock.getDate()));
					}
					// Add stock history to the database and return the result
					if (stockServiceDB.addToStockHistory(list)) {
						return "SUCCESS";
					}
				}
			}
		} catch (Exception ex) {
			System.err.println(ticker + " <populateInitialStockHistory> " + ex);
		}
		return "FAILURE";
	}

	public String populateInitialStockHistoryAll() {
		try {
			// Get the list of Master objects
			List<Master> masterList = getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				populateInitialStockHistory(master.getTicker());
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> populateInitialStockHistoryAll ==> "+ ex);
		}
		return "FAILURE";
	}

	public String populateDailyStockHistoryAll()
	{
		return populateDailyStockHistory();
	}

	public String updateLiveStockDetails() {
	    System.out.println("START -> updateLiveStockDetails: " + new Date());
	    String result = "FAILURE";
	    try {
	        List<Stock> list = barchartRatingService.populateBarchartRatings(Constants.BARCHART_URL_INTRA);

	        if (list != null && !list.isEmpty()) {	        	
	            // Set live stock list
	            LiveStockCache.setLiveStockList(list);
	            result = "SUCCESS";
	            // Run these two methods asynchronously in a sequence (synchronously with each other)
	            CompletableFuture.runAsync(() -> {
	                //updateBuySellTrend(true);  // This will run first
	                syncLiveStockWithDBHistory(false);  // This will run after updateBuySellTrend
	                stockServiceDB.updateLivePriceToMaster();	                
	            }).exceptionally(ex -> {
	                System.err.println("Error in updating buy/sell trends and syncing live stock: " + ex.getMessage());
	                return null;
	            });
	        }

	        System.out.println("END -> updateLiveStockDetails: " + new Date());
	    } catch (Exception ex) {
	        System.err.println("ERROR ==> updateLiveStockDetails: " + ex);
	    }
	    return result;
	}

	public String populateDailyStockHistory()
	{
		System.out.println("START -> populateDailyStockHistory");
		try
		{
			List<Stock> list = barchartRatingService.populateBarchartRatings(Constants.BARCHART_URL_DAILY);			

			if (list != null && !list.isEmpty()) {
			    LiveStockCache.setLiveStockList(list);
			    
			    // Create a list of CompletableFutures for each rating service
			    List<CompletableFuture<Void>> ratingTasks = Arrays.asList(
			        CompletableFuture.runAsync(() -> streetRatingService.populateStreetRatings()),
			        //CompletableFuture.runAsync(() -> marketBeatRatingService.populateMarketBeatRatings()), // Uncomment if needed
			        CompletableFuture.runAsync(() -> tipRanksRatingService.populateTipRankRatings()),
			        CompletableFuture.runAsync(() -> seekingAlphaRatingService.populateSeekingAlphaRatings()),
			        CompletableFuture.runAsync(() -> stockInvestRatingService.populateStockInvestUSRatings()),
			        CompletableFuture.runAsync(() -> zenRatingService.populateWallstreetZenRatings()),
			        CompletableFuture.runAsync(() -> stockAnalysisRatingService.populateStockAnalysisRatings()),
			        //CompletableFuture.runAsync(() -> nasdaqRatingService.populateNasdaqRatings()), // Uncomment if needed
			        CompletableFuture.runAsync(() -> zacksRatingService.populateZacksRatings()),
			        CompletableFuture.runAsync(() -> investingComRatingService.populateInvestingComRatings()),
			        CompletableFuture.runAsync(() -> investorObserverRatingService.populateInvestorObserverRatings()),
			        CompletableFuture.runAsync(() -> portfolio123RatingService.populatePortfolio123Ratings()),
			        //CompletableFuture.runAsync(() -> marketEdgeRatingService.populateMarketEdgeRatings()), // Uncomment if needed
			        CompletableFuture.runAsync(() -> tradingViewRatingService.populateTradingViewRatings()),
			        CompletableFuture.runAsync(() -> tickeronRatingService.populateTickeronRatings()),
			        CompletableFuture.runAsync(() -> finscreenerRatingService.populateFinscreenerRatings())
			    );

			    // Wait for all rating tasks to complete
			    CompletableFuture.allOf(ratingTasks.toArray(new CompletableFuture[0]))
			        .thenRun(() -> {
			            // These will run sequentially after all the rating tasks have completed
			            updateBuySellTrend(false);
			            syncLiveStockWithDBHistory(true);
			        }).exceptionally(ex -> {
			            System.err.println("Error during rating population or follow-up tasks: " + ex.getMessage());
			            return null;
			        });
			}

			System.out.println("END -> populateDailyStockHistory");
			return "SUCCESS";
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateDailyStockHistory " + ex);
		}
		System.out.println("END -> populateDailyStockHistory-FAIL");
		return "FAILURE";
	}

	public void syncLiveStockWithDBHistory(boolean executeMore)
	{
		System.out.println("START -> syncLiveStockWithDBHistory=> " + new Date());
		stockServiceDB.deleteFromStockHistory(UtilityService.formatLocalDateToString(LocalDate.now()));
		if(stockServiceDB.addToStockHistory(LiveStockCache.getLiveStockList()))
		{
			stockServiceDB.addToMaster(LiveStockCache.getLiveStockList());
		}
		if(executeMore)
		{
			updateNextPriceAll();
			updatePriceUpDownAll();
		}
		System.out.println("END -> syncLiveStockWithDBHistory=> " + new Date());
	}

	public List<Master> getMyTrackList()
	{
		return MasterStocksCache.getMasterStocks().stream().
				filter(mas -> Constants.MY_TRACK_LIST.stream().
						anyMatch(my -> mas.getTicker().equalsIgnoreCase(my))).collect(Collectors.toList());				
	}

	public void updateBuySellTrend(boolean isLive)
	{
		System.out.println("START -> updateBuySellTrend=> " + new Date());
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		for(Stock stock : liveList)
		{
			List<Stock> histList = stockServiceDB.getStockHistoryByDescOrder(stock.getTicker());
			if(histList == null || histList.isEmpty())
				continue;

			if(histList.get(0).getDate().equalsIgnoreCase(stock.getDate()))
				histList.remove(0);
			if(isLive && histList != null && !histList.isEmpty())
			{
				stock.getRating().setSiusRating(histList.get(0).getRating().getSiusRating());
				stock.getRating().setTradingViewMARating(histList.get(0).getRating().getTradingViewMARating());
				stock.getRating().setTickeronAIRating(histList.get(0).getRating().getTickeronAIRating());
			}
			histList.add(0, stock);				

			int buyCount = 0;
			int sellCount = 0;

			for(Stock hist : histList)
			{
				String bcr = hist.getRating().getBtTrend();
				String sir = hist.getRating().getSiusRating();
				String tvr = hist.getRating().getTradingViewMARating();
				String tir = hist.getRating().getTickeronAIRating();

				if(StringUtils.isBlank(bcr) || StringUtils.isBlank(sir) || StringUtils.isBlank(tvr) || StringUtils.isBlank(tir))
					break;

				if((bcr.toUpperCase().contains("BUY") || "BUY".equalsIgnoreCase(bcr)) && 
						(sir.toUpperCase().contains("BUY") || "BUY".equalsIgnoreCase(sir)) &&
						(tvr.toUpperCase().contains("BUY") || "BUY".equalsIgnoreCase(tvr)) &&
						(tir.toUpperCase().contains("B") || "B".equalsIgnoreCase(tir)))
				{
					buyCount++;						
				}
				else
					break;

			}

			for(Stock hist : histList)
			{
				String bcr = hist.getRating().getBtTrend();
				String sir = hist.getRating().getSiusRating();
				String tvr = hist.getRating().getTradingViewMARating();
				String tir = hist.getRating().getTickeronAIRating();

				if(StringUtils.isBlank(bcr) || StringUtils.isBlank(sir) || StringUtils.isBlank(tvr) || StringUtils.isBlank(tir))
					break;

				if((bcr.toUpperCase().contains("SELL") || "SELL".equalsIgnoreCase(bcr)) && 
						(sir.toUpperCase().contains("SELL") || "SELL".equalsIgnoreCase(sir)) &&
						(tvr.toUpperCase().contains("SELL") || "SELL".equalsIgnoreCase(tvr)) &&
						(tir.toUpperCase().contains("S") || "S".equalsIgnoreCase(tir)))
				{
					sellCount++;						
				}
				else
					break;
			}

			stock.setBuyTrend(buyCount);
			stock.setSellTrend(sellCount);
		}
		System.out.println("END -> updateBuySellTrend=> " + new Date());
	}

	private void processProfitLossForAllMasterStocks(List<Master> list)
	{
		System.out.println("START -> processProfitLossForAllMasterStocks=> " + new Date());
		List<Master> stockEntryList = UtilityService.readExcelFile();
		if(stockEntryList != null && !stockEntryList.isEmpty())
		{
			for(Master m : stockEntryList)
			{
				List<Stock> historyList = getStockHistoryByDate(m.getTicker(), m.getEntryDate());
				if(historyList != null)
				{
					final String entryPrice = getStockHistoryForADate(m.getTicker(), m.getEntryDate()).getPrice();
					final String currPrice = list.stream()
							.filter(m1 -> m1.getTicker().equalsIgnoreCase(m.getTicker()))
							.findFirst()
							.map(Master::getPrice)
							.orElse(null);
					for(Stock hist : historyList)
					{
						if("A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), m.getEntryDate())))
						{
							if(StringUtils.isBlank(m.getH()) || Double.valueOf(m.getH()) <  Double.valueOf(hist.getHigh()))
								m.setH(hist.getHigh());

							if(StringUtils.isBlank(m.getL()) || Double.valueOf(m.getL()) >  Double.valueOf(hist.getLow()))
								m.setL(hist.getLow());
						}
					}

					double currPL = 1000 * (Double.valueOf(currPrice) - Double.valueOf(entryPrice));
					double profit = 1000 * (Double.valueOf(m.getH()) - Double.valueOf(entryPrice));			
					double loss = 1000 * (Double.valueOf(entryPrice) - Double.valueOf(m.getL()));
					if(loss <= 0)
						loss = 0;
					m.setProfitLoss(UtilityService.stripStringToTwoDecimals(String.valueOf(currPL), false));
					m.setMaxProfit(UtilityService.stripStringToTwoDecimals(String.valueOf(profit), false));
					m.setMaxLoss(UtilityService.stripStringToTwoDecimals(String.valueOf(loss), false));
					m.setE(entryPrice);
					
					double currPLpt = ((Double.valueOf(currPrice) - Double.valueOf(entryPrice))/Double.valueOf(entryPrice))*100;
					double profitpt = ((Double.valueOf(m.getH()) - Double.valueOf(entryPrice))/Double.valueOf(entryPrice))*100;			
					double losspt = ((Double.valueOf(entryPrice) - Double.valueOf(m.getL()))/Double.valueOf(entryPrice))*100;
					
					m.setProfitLosspt(UtilityService.stripStringToTwoDecimals(String.valueOf(currPLpt), false));
					m.setMaxProfitpt(UtilityService.stripStringToTwoDecimals(String.valueOf(profitpt), false));
					m.setMaxLosspt(UtilityService.stripStringToTwoDecimals(String.valueOf(losspt), false));					
				}
			}
			list.forEach(m -> {
				stockEntryList.forEach(stockEntry -> {
					if(m.getTicker().equalsIgnoreCase(stockEntry.getTicker()))
					{
						m.setProfitLoss(stockEntry.getProfitLoss());
						m.setMaxProfit(stockEntry.getMaxProfit());
						m.setMaxLoss(stockEntry.getMaxLoss());
						m.setProfitLosspt(stockEntry.getProfitLosspt());
						m.setMaxProfitpt(stockEntry.getMaxProfitpt());
						m.setMaxLosspt(stockEntry.getMaxLosspt());
						m.setE(stockEntry.getE());
						m.setL(stockEntry.getL());
						m.setH(stockEntry.getH());
						m.setEntryDate(stockEntry.getEntryDate());
					}
				});			
			});
		}
		System.out.println("END -> processProfitLossForAllMasterStocks=> " + new Date());
	}
	
	public List<Monitor> getMonitors()
	{
		List<Monitor> monitorList = stockServiceDB.getMonitorList();	
		List<Master> masterList = MasterStocksCache.getMasterStocks();

		monitorList.forEach(x -> masterList.forEach(y -> {
			if (x.getTicker().equalsIgnoreCase(y.getTicker())) {
				x.setMaster(y);				
				x.setMonitorChg(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(y.getPrice()) - Double.valueOf(x.getMonitorPrice())), false));
			}
		})
				);
		return monitorList;		
	}
}