package com.stock.v1.service.rating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.v1.cache.CookieCache;
import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class StockAnalysisRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
		
	public List<Stock> populateStockAnalysisRatings() {
	    List<String> urlList = Arrays.asList(
	    		Constants.STOCK_ANALYSIS_URL
	    );
	    List<Stock> stockAnalysisRatingList = new ArrayList<>();
	    try
	    {
	    	stockAnalysisRatingList = urlList.stream()
	    			.map(this::fetchDataFromURL)
	    			.filter(StringUtils::isNotBlank)
	    			.map(this::extractStockData)
	    			.flatMap(List::stream)
	    			.collect(Collectors.toList());

	    }
	    catch(Exception ex)
	    {
	    	System.err.println("ERROR ==> populateStockAnalysisRatings ==> "+ ex);
	    }
	    updateLiveStock(stockAnalysisRatingList);
	    System.out.println("8 - STOCKANALYSIS RATING DONE ==> AUTO");
	    return stockAnalysisRatingList;
	}

	private String fetchDataFromURL(String url) {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Cookie", StringUtils.isNotBlank(CookieCache.getCookie("STOCK_ANALYSIS_COOKIE"))? CookieCache.getCookie("STOCK_ANALYSIS_COOKIE"): Constants.STOCK_ANALYSIS_COOKIE);
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	    return response.getBody();
	}

	private List<Stock> extractStockData(String response) {
	    if (StringUtils.isBlank(response)) {
	        return Collections.emptyList();
	    }

	    if (StringUtils.isNotBlank(response)) {
	    	JSONObject jsonObject = new JSONObject(response);

	    	if (jsonObject.has("data") && !jsonObject.isNull("data")) {
	    		JSONArray jsonCompanyArray = jsonObject.getJSONArray("data");
	    		return extractStocksFromJSON(jsonCompanyArray);
	    	}
	    }

	    return Collections.emptyList();
	}

	private List<Stock> extractStocksFromJSON(JSONArray jsonCompanyArray) {
	    List<Stock> stocks = new ArrayList<>();
	    for (int i = 0; i < jsonCompanyArray.length(); i++) {
	        JSONObject companyData = jsonCompanyArray.getJSONObject(i);

	        if (companyData != null) {
	            Stock stock = createStockFromCompanyData(companyData);
	            stocks.add(stock);
	        }
	    }
	    return stocks;
	}

	private Stock createStockFromCompanyData(JSONObject companyData) {
	    Stock stock = new Stock();
	    stock.setRating(new Rating());
	    stock.setTicker(UtilityService.checkForPresence(companyData, "s").replace("$", ""));
	    Rating rating = stock.getRating();
	    rating.setStockAnalysisRating(UtilityService.checkForPresence(companyData, "analystRatings"));
	    rating.setStockAnalysisTarget(UtilityService.checkForPresence(companyData, "priceTarget"));	    
	    rating.setStockAnalysisTargetUpDown(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(companyData, "priceTargetChange"), false));
	    return stock;
	}
	
	private void updateLiveStock(List<Stock> stockAnalysisList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && stockAnalysisList != null && !stockAnalysisList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					stockAnalysisList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setStockAnalysisRating(matchingElement.getRating().getStockAnalysisRating());						            						            
						stock.getRating().setStockAnalysisTarget(matchingElement.getRating().getStockAnalysisTarget());
						stock.getRating().setStockAnalysisTargetUpDown(matchingElement.getRating().getStockAnalysisTargetUpDown());
					});
				}
			});
		}
	}
}