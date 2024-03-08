package com.stock.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.service.StockService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Stock;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;

@Controller
public class StocksController {

	@Autowired
	StockService stockService;

	@GetMapping({Constants.CONTEXT_STOCK_MASTER})
	public ModelAndView loadMasterView()
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.MASTER.value);
		return mv;
	}
	
	@GetMapping(Constants.CONTEXT_STOCK_HISTORY)
	public ModelAndView loadStockHistoryView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.STOCK_HISTORY.value);
		mv.addObject("TICKER",ticker);
		return mv;
	}
	
	@GetMapping(Constants.CONTEXT_STOCK_TRACK)
	public ModelAndView loadTrackView()
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.TRACK.value);		
		return mv;
	}
	
	@CrossOrigin
	@GetMapping("/stock/masterList")
	public @ResponseBody List<Master> getMasterStocks()
	{
		return stockService.getMasterList();		
	}
	
	@CrossOrigin
	@GetMapping("/stock/history/{ticker}")
	public @ResponseBody List<Stock> getStockHistory(@PathVariable String ticker)
	{
		return stockService.getStockHistory(ticker);		
	}
	
	@CrossOrigin
	@PostMapping("/stock/add-initial-history/{ticker}")
	public @ResponseBody String populateInitialStockHistory(@PathVariable String ticker)
	{
		return stockService.populateInitialStockHistory(ticker);		
	}
	
	@CrossOrigin
	@PostMapping("/stock/add-initial-history-all")
	public @ResponseBody String populateInitialStockHistoryAll()
	{
		return stockService.populateInitialStockHistoryAll();		
	}	
	
	@CrossOrigin
	@PostMapping("/stock/add-daily-history-all")
	public @ResponseBody String populateDailyStockHistoryAll()
	{
		return stockService.populateDailyStockHistoryAll();		
	}
	
	@CrossOrigin
	@PostMapping("/stock/updateLiveStockDetails")
	public void updateLiveStockDetails()
	{
		stockService.updateLiveStockDetails();		
	}
	
	@CrossOrigin
	@PostMapping("/stock/update-prev-price/{ticker}")
	public @ResponseBody boolean updatePrevPrice(@PathVariable String ticker)
	{
		return stockService.updatePrevPrice(ticker);
	}
	
	@CrossOrigin
	@PostMapping("/stock/update-prev-price-all")
	public @ResponseBody String updatePrevPriceAll()
	{
		return stockService.updatePrevPriceAll();		
	}
	
	@CrossOrigin
	@PostMapping("/stock/update-next-price/{ticker}")
	public @ResponseBody boolean updateNextPrice(@PathVariable String ticker)
	{
		return stockService.updateNextPrice(ticker);
	}
	
	@CrossOrigin	
	@PostMapping("/stock/update-next-price-all")
	public @ResponseBody String updateNextPriceAll()
	{
		return stockService.updateNextPriceAll();
	}
	
	@CrossOrigin
	@PostMapping("/stock/update-next-price-all-old")
	public @ResponseBody String updateNextPriceAllOld()
	{
		return stockService.updateNextPriceAllOld();
	}
	
	@CrossOrigin
	@PostMapping("/stock/markEarningDay/{ticker}")
	public @ResponseBody boolean markEarningDay(@PathVariable String ticker)
	{
		return stockService.markEarningDay(ticker);
	}
	
	@CrossOrigin
	@PostMapping("/stock/markEarningDayAll")
	public @ResponseBody String markEarningDayAll()
	{
		return stockService.markEarningDayAll();
	}
	
	@CrossOrigin
	@PostMapping("/stock/updatePriceUpDown/{ticker}")
	public @ResponseBody boolean updatePriceUpDownAll(@PathVariable String ticker)
	{
		return stockService.updatePriceUpDown(ticker);
	}
	
	@CrossOrigin
	@PostMapping("/stock/updatePriceUpDownAll")
	public @ResponseBody String updatePriceUpDownAll()
	{
		return stockService.updatePriceUpDownAll();
	}
	
	@CrossOrigin
	@GetMapping("/stock/syncLiveStockWithDBHistory")
	public void syncLiveStockWithDBHistory()
	{
		stockService.syncLiveStockWithDBHistory();
	}	
	
	@CrossOrigin
	@GetMapping("/stock/getMyTrackList")
	public @ResponseBody List<Master> getMyTrackList()
	{
		return stockService.getMyTrackList();
	}	
}