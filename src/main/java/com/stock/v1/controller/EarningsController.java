package com.stock.v1.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.service.EarningsService;
import com.stock.v1.service.db.EarningsServiceDB;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Earnings;
import com.stock.v1.vo.Master;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class EarningsController {

	@Autowired
	EarningsService earningsService;
	
	@Autowired
	EarningsServiceDB earningsServiceDB;
	
	@Autowired
	StockServiceDB stockServiceDB;

	@GetMapping(Constants.CONTEXT_EARNINGS)
	public ModelAndView loadEarningsView(HttpServletRequest request, String ticker, String user)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.EARNINGS.value);
		mv.addObject("TICKER",ticker);
		if(StringUtils.isBlank(user))
			user = (String) request.getSession().getAttribute("USER");
		mv.addObject("USER", user);
		request.getSession().setAttribute("USER", user);
		return mv;
	}
	
	@CrossOrigin	
	@GetMapping("/earnings/history")
	public @ResponseBody List<Earnings> getEarningsHistory(String ticker)
	{
		return earningsService.getEarningsHistory(ticker);
	}
	
	@CrossOrigin
	@GetMapping("/earnings/historicEarningsHistory/{ticker}")
	public @ResponseBody List<Earnings> populateHistoricEarningsHistory(@PathVariable String ticker)
	{
		List<Earnings> earningList = earningsService.getHistoricEarningsHistory(ticker);
		earningsServiceDB.addToEarnings(earningList);
		return earningList;
	}
	
	@CrossOrigin
	@GetMapping("/earnings/historicEarningsHistoryAll")
	public @ResponseBody List<Earnings> populateHistoricEarningsHistoryAll() {
	    List<Master> list = stockServiceDB.getMasterList();
	    
	    System.out.println("START -> historicEarningsHistoryAll");
	    
	    Queue<Earnings> allEarnings = new ConcurrentLinkedQueue<>();
	    ExecutorService executor = Executors.newFixedThreadPool(10);

	    List<CompletableFuture<Void>> futures = list.stream()
	        .map(master -> CompletableFuture.runAsync(() -> {
	            List<Earnings> earningList = earningsService.getHistoricEarningsHistory(master.getTicker());
	            allEarnings.addAll(earningList); // Collect all earnings in a thread-safe queue
	        }, executor).exceptionally(ex -> {
	            System.err.println("Error processing earnings for ticker " + master.getTicker() + ": " + ex.getMessage());
	            return null;
	        }))
	        .collect(Collectors.toList());

	    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	    executor.shutdown();

	    // Batch update in database after all tasks complete
	    earningsServiceDB.addToEarnings(new ArrayList<>(allEarnings)); // Convert to List if needed for the database method
	    
	    System.out.println("END -> historicEarningsHistoryAll");

	    return Collections.emptyList(); // or return null if required
	}

	
	@CrossOrigin
	@PostMapping("/earnings/updatePriceEffect/{ticker}")
	public @ResponseBody boolean updatePriceEffect(@PathVariable String ticker)
	{
		return earningsService.updatePriceEffect(ticker);		
	}
	
	@CrossOrigin
	@PostMapping("/earnings/updatePriceEffectAll")
	public @ResponseBody String updatePriceEffectAll()
	{
		return earningsService.updatePriceEffectAll();		
	}
}