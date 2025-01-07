package com.stock.v1.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.MasterStocksCache;
import com.stock.v1.cache.PatternsCache;
import com.stock.v1.service.db.WatchListServiceDB;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Pattern;

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
	    // Run all tasks in parallel
		/*PatternsCache.clearPatternHistory();
		patternService.getPatternHistory(null);
		List<String> tickerList = getWatchList().stream().map(x -> x.getTicker()).collect(Collectors.toList());
		patternService.deletePattern(tickerList);
	    
		ExecutorService executorService = Executors.newFixedThreadPool(10); // Customize the thread pool size

        try {
            List<CompletableFuture<List<Pattern>>> futures = getWatchList().stream()
                .map(x -> CompletableFuture.supplyAsync(() -> patternService.fetchPatternDetails(x.getTicker()), executorService)
                    .exceptionally(ex -> {
                        // Log or handle the exception for each ticker
                        System.err.println("Error fetching pattern details for " + x.getTicker() + ": " + ex.getMessage());
                        return Collections.emptyList(); // Return an empty list on exception
                    }))
                .collect(Collectors.toList());

            // Wait for all the tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Combine all the results into a single list
            List<Pattern> finalList = futures.stream()
                .flatMap(future -> future.join().stream()) // Flatten the lists
                .collect(Collectors.toList());
            if(finalList != null && !finalList.isEmpty())
    	    	patternService.addToPattern(finalList);
            
        } finally {
            executorService.shutdown(); // Always shut down the executor
        }

	    CompletableFuture<Void> populateOptionsTask = CompletableFuture.runAsync(() -> 
	        optionsService.populateOptions()
	    ).exceptionally(ex -> {
	        // Log or handle the exception
	        System.err.println("Error populating options: " + ex.getMessage());
	        return null;
	    });

	    CompletableFuture<Void> updateStockDetailsTask = CompletableFuture.runAsync(() -> 
	        stockService.updateLiveStockDetails()
	    ).exceptionally(ex -> {
	        // Log or handle the exception
	        System.err.println("Error updating stock details: " + ex.getMessage());
	        return null;
	    });

	    // Wait for all tasks to complete
	    CompletableFuture.allOf(populateOptionsTask, updateStockDetailsTask).join();*/
	}
}