package com.stock.v1.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.cache.MasterStocksCache;
import com.stock.v1.service.db.EarningsServiceDB;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Earnings;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Stock;
import com.stock.v1.vo.Streak;

@Service
public class EarningsService{

	@Autowired
	Environment appProp;
	
	@Autowired
	EarningsServiceDB earningsServiceDB;
	
	@Autowired
	StockServiceDB stockServiceDB;
		
	public List<Earnings> getEarningsHistory(String ticker)
	{
		List<Earnings> earningsList = earningsServiceDB.getEarningsHistory();
		if(StringUtils.isNotBlank(ticker))
			earningsList = earningsList.stream().filter(x -> x.getTicker().equalsIgnoreCase(ticker)).toList();
		stockServiceDB.populateLiveStockList();
		List<Stock> liveStockList = LiveStockCache.getLiveStockList();
		earningsList.forEach(earning -> {
			Stock matchingStock = liveStockList.stream()
					.filter(stock -> earning.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst()
					.orElse(null);

			if (matchingStock != null) {
				earning.setCurrPrice(matchingStock.getPrice());
				if(NumberUtils.isParsable(earning.getPriceAfter()) && NumberUtils.isParsable(matchingStock.getPrice()))
				{
					earning.setCurrAndLastPriceEffectDiff(Double.parseDouble(matchingStock.getPrice())- Double.parseDouble(earning.getPriceAfter()));
					BigDecimal bd = new BigDecimal(earning.getCurrAndLastPriceEffectDiff()).setScale(2, RoundingMode.HALF_UP);
					earning.setCurrAndLastPriceEffectDiff(bd.doubleValue());
				}
			}
		});
		
		countContinuousStreaksForStocks(earningsList);
		
		return earningsList;
	}
	
	public void countContinuousStreaksForStocks(List<Earnings> earningsList) {
	    Map<String, Streak> streaksByStock = new HashMap<>();
	    Set<String> stopProcessingSet = new HashSet<>(); // Tracks stocks to stop processing

	    for (Earnings earnings : earningsList) {
	        String ticker = earnings.getTicker();
	        if (stopProcessingSet.contains(ticker)) {
	            continue; // Skip processing this stock if it's marked
	        }

	        Streak streak = streaksByStock.getOrDefault(ticker, new Streak());

	        try {
	            if (earnings.getPriceEffect() != null && !earnings.getPriceEffect().trim().isEmpty()) {
	                double priceEffectValue = Double.parseDouble(earnings.getPriceEffect());

	                if (priceEffectValue > 0) {
	                    if (streak.currentNegativeStreak > 0) {
	                        stopProcessingSet.add(ticker); // Mark this stock to stop processing
	                        continue; // Skip further processing for this stock
	                    }
	                    streak.currentPositiveStreak++;
	                    streak.currentNegativeStreak = 0;
	                } else if (priceEffectValue < 0) {
	                    if (streak.currentPositiveStreak > 0) {
	                        stopProcessingSet.add(ticker); // Mark this stock to stop processing
	                        continue; // Skip further processing for this stock
	                    }
	                    streak.currentNegativeStreak++;
	                    streak.currentPositiveStreak = 0;
	                } else {
	                    streak.currentPositiveStreak = 0;
	                    streak.currentNegativeStreak = 0;
	                }
	            } else {
	                streak.currentPositiveStreak = 0;
	                streak.currentNegativeStreak = 0;
	            }
	        } catch (NumberFormatException e) {
	            streak.currentPositiveStreak = 0;
	            streak.currentNegativeStreak = 0;
	        }

	        streaksByStock.put(ticker, streak);
	    }

	    // Update Earnings objects
	    earningsList.forEach(earnings -> {
	        Streak streak = streaksByStock.get(earnings.getTicker());
	        if (streak != null) {
	            earnings.setPositiveStreak(streak.currentPositiveStreak);
	            earnings.setNegativeStreak(streak.currentNegativeStreak);
	        }
	    });
	}

	
	public String updatePriceEffectAll()
	{
		System.out.println("START -> updatePriceEffectAll");
		try {
	        // Get the list of Master objects
	        List<Master> masterList = MasterStocksCache.getMasterStocks();

	        // Iterate through the Master list
	        for (Master master : masterList) {
	            // Call populateInitialStockHistory for each Ticker
	        	updatePriceEffect(master.getTicker());
	        }
	        System.out.println("END -> updatePriceEffectAll");
	        return "SUCCESS";
	    } catch (Exception ex) {
	    	System.err.println("ERROR ==> updatePriceEffectAll ==> "+ ex);
	    }
		System.out.println("END -> updatePriceEffectAll-FAIL");
	    return "FAILURE";
		
	}
	
	public boolean updatePriceEffect(String ticker) {
		try
		{
			if (StringUtil.isBlank(ticker)) {
				return false;
			}

			List<Earnings> earningsList = earningsServiceDB.getEarningsHistory();
			if (earningsList.isEmpty()) {
				return false;
			}

			List<Stock> stockHistoryList = stockServiceDB.getStockHistory(ticker);
			if (stockHistoryList.isEmpty()) {
				return false;
			}
			
			if(StringUtils.isNotBlank(ticker))
				earningsList = earningsList.stream().filter(x -> x.getTicker().equalsIgnoreCase(ticker)).toList();

			earningsList.forEach(earning -> {
				Stock matchingStock = stockHistoryList.stream()
						.filter(stock -> earning.getTicker().equalsIgnoreCase(stock.getTicker()) && 
								earning.getDate().equalsIgnoreCase(stock.getDate()))
						.findFirst()
						.orElse(null);

				if (matchingStock != null) {
					if ("After Close".equalsIgnoreCase(earning.getTime())) {
						if(StringUtils.isNotBlank(matchingStock.getNextPrice()))
						{
							earning.setPriceEffect(String.valueOf(Double.valueOf(matchingStock.getNextPrice()) -  Double.valueOf(matchingStock.getPrice())));
							earning.setPriceAfter(matchingStock.getNextPrice());
							earning.setPriceBefore(matchingStock.getPrice());
							earningsServiceDB.updatePriceEffect(earning);
						}
					} else {
						if(StringUtils.isNotBlank(matchingStock.getPrevPrice()))
						{
							earning.setPriceEffect(String.valueOf(Double.valueOf(matchingStock.getPrice()) -  Double.valueOf(matchingStock.getPrevPrice())));
							earning.setPriceAfter(matchingStock.getPrice());
							earning.setPriceBefore(matchingStock.getPrevPrice());
							earningsServiceDB.updatePriceEffect(earning);
						}
					}
				}
			});
		}
		catch(Exception ex)
		{
			System.err.println(ticker + " <updatePriceEffect> " + ex);
			return false;
		}

	    return true;
	}


	public List<Earnings> getHistoricEarningsHistory(String ticker) {
		if (StringUtil.isBlank(ticker)) {
			return Collections.emptyList();
		}

		System.out.println("getHistoricEarningsHistory -->"+ticker);
		
		if(UtilityService.excludedTick(ticker))
			return Collections.emptyList();
			
			String url = appProp.getProperty("historic.earnings.history.url")
			.replace("TICKER", ticker);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();

		int index1 = response.indexOf("earnings_announcements_earnings_table");
		int index2 = response.indexOf("earnings_announcements_sales_table");

		if(index1 > 0 && index2 > 0)
		{

			String data = response.substring(index1 + 42, index2 - 8).trim();

			JSONArray jsonArray = new JSONArray(data);

			List<Earnings> earningsHistory = IntStream.range(0, jsonArray.length())
					.mapToObj(i -> {
						JSONArray jsonArrayObj = jsonArray.getJSONArray(i);
						Earnings earning = new Earnings();
						earning.setTicker(ticker);
						earning.setDate(UtilityService.formatDateString((String) jsonArrayObj.get(0), "M/d/yy","MM/dd/yy" ));
						earning.setDate(UtilityService.formatDateString(earning.getDate(), "MM/dd/yy","yyyy-MM-dd" ));
						earning.setEpsEst(((String) jsonArrayObj.get(2)).replace("$", ""));
						earning.setEpsAct(((String) jsonArrayObj.get(3)).replace("$", ""));
						earning.setTime((String) jsonArrayObj.get(6));
						return earning;
					})
					//.takeWhile(earning -> !earning.getEpsEst().contains("--") && !earning.getEpsAct().contains("--"))
					.collect(Collectors.toList());


			return earningsHistory;
		}
		return Collections.emptyList();
	}
}