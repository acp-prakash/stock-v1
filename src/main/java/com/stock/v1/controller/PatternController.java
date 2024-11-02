package com.stock.v1.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.cache.PatternsCache;
import com.stock.v1.service.PatternService;
import com.stock.v1.service.WatchListService;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Pattern;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PatternController {

	@Autowired
	PatternService patternService;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	@Autowired
	WatchListService watchListService;

	@GetMapping(Constants.CONTEXT_PATTERN)
	public ModelAndView loadPatternView(HttpServletRequest request, String ticker, String user)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.PATTERN.value);
		if(StringUtils.isBlank(user))
			user = (String) request.getSession().getAttribute("USER");
		mv.addObject("USER", user);
		request.getSession().setAttribute("USER", user);
		return mv;
	}
	
	@GetMapping(Constants.CONTEXT_GET_PATTERN_HISTORY)
	public ModelAndView loadPatternHistoryView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.PATTERN.value);
		mv.addObject("TICKER",ticker);
		return mv;
	}
	
	@CrossOrigin	
	@PostMapping(Constants.CONTEXT_GET_PATTERN_HISTORY)
	public @ResponseBody List<Pattern> getPatternHistory(String ticker)
	{
		return patternService.getPatternHistory(ticker);
	}
	
	@CrossOrigin
	@GetMapping(Constants.CONTEXT_FETCH_PATTERNS)
	public @ResponseBody List<Pattern> fetchPatternDetails(String ticker) {
		List<Pattern> finalList = new ArrayList<>();
		PatternsCache.clearPatternHistory();
		patternService.getPatternHistory(null);
		List<String> tickerList = StringUtils.isNotBlank(ticker) 
			    ? new ArrayList<>(Collections.singletonList(ticker)) 
			    : stockServiceDB.getMasterList().stream()
			        .map(x -> x.getTicker())
			        .collect(Collectors.toList());
		patternService.deletePattern(tickerList);
	    if (StringUtils.isBlank(ticker)) {
	        List<Master> list = stockServiceDB.getMasterList();
	        
	        ExecutorService executorService = Executors.newFixedThreadPool(10); // Customize the thread pool size

	        try {
	            List<CompletableFuture<List<Pattern>>> futures = list.stream()
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
	            finalList = futures.stream()
	                .flatMap(future -> future.join().stream()) // Flatten the lists
	                .collect(Collectors.toList());
	            
	        } finally {
	            executorService.shutdown(); // Always shut down the executor
	        }	        
	        // Return an empty list as no specific ticker was passed	        
	    } else {
	        // Fetch pattern details for the given ticker
	    	finalList = patternService.fetchPatternDetails(ticker);
	    }
	    if(finalList != null && !finalList.isEmpty())
	    	patternService.addToPattern(finalList);
	    	
	    return finalList;
	}

}