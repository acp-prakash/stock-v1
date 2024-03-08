package com.stock.v1.controller;

import java.util.Collections;
import java.util.List;

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

@Controller
public class EarningsController {

	@Autowired
	EarningsService earningsService;
	
	@Autowired
	EarningsServiceDB earningsServiceDB;
	
	@Autowired
	StockServiceDB stockServiceDB;

	@GetMapping(Constants.CONTEXT_EARNINGS)
	public ModelAndView loadEarningsView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.EARNINGS.value);
		mv.addObject("TICKER",ticker);
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
	    list.forEach(master -> {
	        List<Earnings> earningList = earningsService.getHistoricEarningsHistory(master.getTicker());
	        earningsServiceDB.addToEarnings(earningList);
	    });
	    System.out.println("END -> historicEarningsHistoryAll");

	    return Collections.emptyList(); // or return null as before
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