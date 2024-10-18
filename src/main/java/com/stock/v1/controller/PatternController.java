package com.stock.v1.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.service.PatternService;
import com.stock.v1.service.WatchListService;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Pattern;

@Controller
public class PatternController {

	@Autowired
	PatternService patternService;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	@Autowired
	WatchListService watchListService;

	@GetMapping(Constants.CONTEXT_PATTERN)
	public ModelAndView loadPatternView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.PATTERN.value);
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
	public @ResponseBody List<Pattern> fetchPatternDetails(String ticker)
	{
		if(StringUtils.isBlank(ticker))
		{
			List<Master> list = stockServiceDB.getMasterList();
			list.stream().forEach(x->patternService.fetchPatternDetails(x.getTicker()));
			return new ArrayList<>();
		}
		else
			return patternService.fetchPatternDetails(ticker);
	}
}