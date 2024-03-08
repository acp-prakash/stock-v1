package com.stock.v1.service.rating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
public class ZacksRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
		
	public List<Stock> populateZacksRatings() {
	    List<String> urlList = Arrays.asList(
	            Constants.ZACKS_URL_1,Constants.ZACKS_URL_2
	    		);
	    
	    List<Stock> zacksRatingList = new ArrayList<>();

	    try
	    {
	    	zacksRatingList = urlList.stream()
	    			.map(this::fetchDataFromURL)
	    			.filter(StringUtils::isNotBlank)
	    			.map(this::extractStockData)
	    			.flatMap(List::stream)
	    			.collect(Collectors.toList());	    	
	    }
	    catch(Exception ex)
	    {
	    	System.err.println("ERROR ==> populateZacksRatings ==> "+ ex);
	    }
	    updateLiveStock(zacksRatingList);
	    System.out.println("10 - ZACKS RATING DONE ==> AUTO");
	    return zacksRatingList;
	}

	private String fetchDataFromURL(String url) {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Cookie", StringUtils.isNotBlank(CookieCache.getCookie("ZACKS_COOKIE"))? CookieCache.getCookie("ZACKS_COOKIE"): Constants.ZACKS_COOKIE);
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	    return response.getBody();
	}

	private List<Stock> extractStockData(String response) {
	    if (StringUtils.isBlank(response)) {
	        return Collections.emptyList();
	    }
	    String cleanJson = removeHtmlTags(response);
	    
	    if (StringUtils.isNotBlank(cleanJson)) {
	    	JSONObject jsonObject = new JSONObject(cleanJson);

	    	if (jsonObject.has("data") && !jsonObject.isNull("data")) {
	    		JSONArray jsonCompanyArray = jsonObject.getJSONArray("data");
	    		return extractStocksFromJSON(jsonCompanyArray);
	    	}
	    }

	    return Collections.emptyList();
	}
	
	private static String removeHtmlTags(String jsonData) {
        Pattern pattern = Pattern.compile("<[^>]*>");
        Matcher matcher = pattern.matcher(jsonData);
        return matcher.replaceAll("");
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
	    stock.setTicker(UtilityService.checkForPresence(companyData, "Symbol").split(" ")[0]);
	    Rating rating = stock.getRating();
	    rating.setZacksRank(UtilityService.checkForPresence(companyData, "Zacks Rank"));
	    StringBuilder strRating = new StringBuilder();
	    strRating.append(UtilityService.checkForPresence(companyData, "Value Score"));
	    strRating.append("*");
	    strRating.append(UtilityService.checkForPresence(companyData, "Growth Score"));
	    strRating.append("*");
	    strRating.append(UtilityService.checkForPresence(companyData, "Momentum Score"));
	    strRating.append("*");
	    strRating.append(UtilityService.checkForPresence(companyData, "VGM Score"));
	    rating.setZacksRating(strRating.toString());
	    return stock;
	}
	
	private void updateLiveStock(List<Stock> zacksList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && zacksList != null && !zacksList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					zacksList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setZacksRank(matchingElement.getRating().getZacksRank());
						stock.getRating().setZacksRating(matchingElement.getRating().getZacksRating());
					});
				}
			});
		}
	}
}