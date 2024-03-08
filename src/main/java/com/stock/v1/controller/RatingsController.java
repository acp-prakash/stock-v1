package com.stock.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.stock.v1.vo.Stock;

@Controller
public class RatingsController {

	@Autowired
	MarketEdgeRatingService marketEdgeRatingService;
	
	@Autowired
	StreetRatingService streetRatingService;
	
	@Autowired
	MarketBeatRatingService marketBeatRatingService;
	
	@Autowired
	TipRanksRatingService tipRanksRatingService;
	
	@Autowired
	StockInvestRatingService stockInvestRatingService;
	
	@Autowired
	ZenRatingService zenRatingService;	
	
	@Autowired
	ZacksRatingService zacksRatingService;
	
	@Autowired
	StockAnalysisRatingService stockAnalysisRatingService;
	
	@Autowired
	NasdaqRatingService nasdaqRatingService;
	
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
	
	@GetMapping("/rating/wallstreetRating")
	public @ResponseBody List<Stock> populateWallstreetZenRatings()
	{
		return zenRatingService.populateWallstreetZenRatings();
	}
	
	@GetMapping("/rating/zacksRating")
	public @ResponseBody List<Stock> populateZacksRatings()
	{
		return zacksRatingService.populateZacksRatings();
	}	
	
	@GetMapping("/rating/stockAnalysisRating")
	public @ResponseBody List<Stock> populateStockAnalysisRatings()
	{
		return stockAnalysisRatingService.populateStockAnalysisRatings();
	}
	
	@GetMapping("/rating/nasdaqRating")
	public @ResponseBody List<Stock> populateNasdaqRatings()
	{
		return nasdaqRatingService.populateNasdaqRatings();
	}
	
	@GetMapping("/rating/investorObserverRating")
	public @ResponseBody List<Stock> populateInvestorObserverRatings()
	{
		return investorObserverRatingService.populateInvestorObserverRatings();
	}
	
	@GetMapping("/rating/investingComRating")
	public @ResponseBody List<Stock> populateInvestingComRatings()
	{
		return investingComRatingService.populateInvestingComRatings();
	}
	
	@GetMapping("/rating/seekingAlphaRating")
	public @ResponseBody List<Stock> populateSeekingAlphaRatings()
	{
		return seekingAlphaRatingService.populateSeekingAlphaRatings();
	}
	
	@GetMapping("/rating/tradingViewRating")
	public @ResponseBody List<Stock> populateTradingViewRatings()
	{
		return tradingViewRatingService.populateTradingViewRatings();
	}
	
	@GetMapping("/rating/tickeronRating")
	public @ResponseBody List<Stock> populateTickeronRatings()
	{
		return tickeronRatingService.populateTickeronRatings();
	}
	
	@GetMapping("/rating/finscreenerRating")
	public @ResponseBody List<Stock> populateFinscreenerRatings()
	{
		return finscreenerRatingService.populateFinscreenerRatings();
	}
	
	@GetMapping("/rating/streetRating")
	public @ResponseBody List<Stock> populateStreetRatings()
	{
		return streetRatingService.populateStreetRatings();
	}
	
	@GetMapping("/rating/marketBeatRating")
	public @ResponseBody List<Stock> populateMarketBeatRatings()
	{
		return marketBeatRatingService.populateMarketBeatRatings();
	}
	
	@GetMapping("/rating/tipRankRating")
	public @ResponseBody List<Stock> populateTipRankRatings()
	{
		return tipRanksRatingService.populateTipRankRatings();
	}
	
	@GetMapping("/rating/stockInvestUSRating")
	public @ResponseBody List<Stock> populateStockInvestUSRatings()
	{
		return stockInvestRatingService.populateStockInvestUSRatings();
	}
	
	@GetMapping("/rating/portfolio123Rating")
	public @ResponseBody List<Stock> populatePortfolio123Ratings()
	{
		return portfolio123RatingService.populatePortfolio123Ratings();
	}
	
	@GetMapping("/rating/marketEdgeRating")
	public @ResponseBody List<Stock> populateMarketEdgeRatings()
	{
		return marketEdgeRatingService.populateMarketEdgeRatings();
	}
	
}