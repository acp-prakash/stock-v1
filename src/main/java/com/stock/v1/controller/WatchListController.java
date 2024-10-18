package com.stock.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.service.WatchListService;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Master;

@Controller
public class WatchListController {

	@Autowired
	WatchListService watchListService;
		
	@GetMapping(Constants.CONTEXT_WATCHLIST)
	public ModelAndView loadWatchListView()
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.WATCHLIST.value);
		return mv;
	}
	
	@CrossOrigin	
	@GetMapping(Constants.CONTEXT_GET_WATCHLIST)
	public @ResponseBody List<Master> getWatchList()
	{
		return watchListService.getWatchList();
	}
	
	@CrossOrigin	
	@PostMapping(Constants.CONTEXT_ADD_WATCHLIST)
	public @ResponseBody List<Master> addWatchList(@RequestBody Master master)
	{
		return watchListService.addWatchList(master.getTicker());
	}
	
	@CrossOrigin	
	@PostMapping(Constants.CONTEXT_DELETE_WATCHLIST)
	public @ResponseBody List<Master> deleteWatchList(@RequestBody Master master)
	{
		return watchListService.deleteWatchList(master.getTicker());
	}
	
	@CrossOrigin	
	@GetMapping(Constants.CONTEXT_DATA_FETCH)
	public @ResponseBody String dataFetch()
	{
		watchListService.dataFetch();
		return "SUCCESS";
	}
}