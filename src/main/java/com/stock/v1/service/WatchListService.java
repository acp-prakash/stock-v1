package com.stock.v1.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.MasterStocksCache;
import com.stock.v1.service.db.WatchListServiceDB;
import com.stock.v1.vo.Master;

@Service
public class WatchListService{
	
	@Autowired
	WatchListServiceDB watchListServiceDB;
	
	@Autowired
	StockService stockService;
	
	@Autowired
	PatternService patternService;
	
	@Autowired
	OptionsService optionsService;
		
	public List<Master> getWatchList()
	{
		List<Master> watchList = watchListServiceDB.getWatchList();
		List<Master> masterList = MasterStocksCache.getMasterStocks();
		
		if(masterList == null || masterList.isEmpty())
			return watchList;
		// Create a Set of tickers from the smaller watchList for faster lookup
		Set<String> watchListTickers = watchList.stream()
		    .map(Master::getTicker)
		    .collect(Collectors.toSet());

		// Filter the masterList based on matching tickers
		List<Master> commonList = masterList.stream()
		    .filter(master -> watchListTickers.contains(master.getTicker()))
		    .collect(Collectors.toList());

		return commonList;
	}
	
	public List<Master> addWatchList(String ticker)
	{
		watchListServiceDB.addWatchList(ticker);
		return getWatchList();
	}
	
	public List<Master> deleteWatchList(String ticker)
	{
		watchListServiceDB.deleteWatchList(ticker);
		return getWatchList();
	}
	
	public void dataFetch() {
	    // Run all three tasks in parallel
	    CompletableFuture<Void> fetchPatternTask = CompletableFuture.runAsync(() -> 
	        getWatchList().stream().forEach(x -> patternService.fetchPatternDetails(x.getTicker()))
	    );

	    CompletableFuture<Void> populateOptionsTask = CompletableFuture.runAsync(() -> 
	        optionsService.populateOptions()
	    );

	    CompletableFuture<Void> updateStockDetailsTask = CompletableFuture.runAsync(() -> 
	        stockService.updateLiveStockDetails()
	    );

	    // Wait for all tasks to complete
	    CompletableFuture.allOf(fetchPatternTask, populateOptionsTask, updateStockDetailsTask).join();
	}
}