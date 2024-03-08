package com.stock.v1.service.rating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class ZenRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
		
	/*public List<Stock> populateZenRatings() {

		List<Stock> zenRatingHistory = new ArrayList<>();		
		try
		{
			List<String> urlList = new ArrayList<String>();
			urlList.add(appProp.getProperty("zen.ratings.url.1"));
			urlList.add(appProp.getProperty("zen.ratings.url.1.a"));
			urlList.add(appProp.getProperty("zen.ratings.url.2"));
			urlList.add(appProp.getProperty("zen.ratings.url.2.a"));
			urlList.add(appProp.getProperty("zen.ratings.url.3"));			
			urlList.add(appProp.getProperty("zen.ratings.url.3.a"));
			
			List<String> cookieList = new ArrayList<String>();
			cookieList.add(appProp.getProperty("zen.cookie.1"));
			cookieList.add(appProp.getProperty("zen.cookie.1"));
			cookieList.add(appProp.getProperty("zen.cookie.2"));
			cookieList.add(appProp.getProperty("zen.cookie.2"));
			cookieList.add(appProp.getProperty("zen.cookie.3"));			
			cookieList.add(appProp.getProperty("zen.cookie.3"));

			int i =0;
			for(String url : urlList)
			{				
				System.out.println(url);
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Cookie", cookieList.get(i));
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.setCacheControl("no-cache");				
				headers.add("Accept-Language", "en-US,en;q=0.9");				
				headers.add("Sec-Ch-Ua", "\"Google Chrome\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"");
				headers.add("Sec-Ch-Ua-Mobile", "?0");				
				headers.add("Sec-Ch-Ua-Platform", "\"Windows\"");
				headers.add("Sec-Fetch-Dest", "document");
				headers.add("Sec-Fetch-Mode", "navigate");
				headers.add("Sec-Fetch-Site", "none");				
				headers.add("Sec-Fetch-User", "?1");
				headers.add("Upgrade-Insecure-Requests", "1");				
				headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
				i++;
				HttpEntity<String> entity = new HttpEntity<>(headers);

				String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
				
				System.out.println(response);

				if(StringUtils.isNotBlank(response))
				{
					JSONObject jsonObject = new JSONObject(response);
					if(jsonObject.has("companies"))
					{
						JSONObject jsonCompanies = (JSONObject)(jsonObject.get("companies"));
						
						for (String key : jsonCompanies.keySet()) {
				            JSONObject companyData = jsonCompanies.getJSONObject(key);

				            Stock stock = new Stock();
				            stock.setRating(new Rating());
				            stock.setTicker(UtilityService.checkForPresence(companyData, "ticker"));
				            stock.getRating().setZenRating(UtilityService.getZenrating(UtilityService.checkForPresence(companyData, "cs")));
				            stock.getRating().setZenTarget(UtilityService.checkForPresence(companyData, "pt"));
				            stock.getRating().setZenTargetUpDown(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(companyData, "ptpd"), true));
				            stock.getRating().setZenScore(UtilityService.checkForPresence(companyData, "z"));

				            boolean found = zenRatingHistory.stream()
				                    .anyMatch(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()));

				            if (found) {
				                zenRatingHistory.stream()
				                        .filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
				                        .findFirst()
				                        .ifPresent(matchingElement -> matchingElement.getRating().setZenScore(stock.getRating().getZenScore()));
				            } else {
				                zenRatingHistory.add(stock);
				            }
				        }
					}
				}
			}			
		}
		catch(Exception ex)
		{
			System.err.println(ex);
		}
		return zenRatingHistory;
	}*/
	
	public List<Stock> populateWallstreetZenRatings() {
	    List<String> urlList = Arrays.asList(
	    		Constants.ZEN_URL_1,
	    		Constants.ZEN_URL_2,
	    		Constants.ZEN_URL_3,
	    		Constants.ZEN_URL_4,
	    		Constants.ZEN_URL_5,
	    		Constants.ZEN_URL_6
	    );

	    List<Stock> zenRatingList = new ArrayList<Stock>();
	    try
	    {
	    	zenRatingList = urlList.stream()
	    			.map(this::fetchDataFromURL)
	    			.filter(StringUtils::isNotBlank)
	    			.map(this::extractStockData)
	    			.flatMap(List::stream)
	    			.collect(Collectors.toList());

	    }
	    catch(Exception ex)
	    {
	    	System.err.println("ERROR ==> populateWallstreetZenRatings ==> "+ ex);
	    }
	    updateLiveStock(zenRatingList);
	    System.out.println("7 - WALLSTREETZEN RATING DONE ==> AUTO");
	    return zenRatingList;
	}

	private String fetchDataFromURL(String url) {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Cookie", StringUtils.isNotBlank(CookieCache.getCookie("ZEN_COOKIE"))? CookieCache.getCookie("ZEN_COOKIE"): Constants.ZEN_COOKIE);
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	    return response.getBody();
	}

	private List<Stock> extractStockData(String response) {
	    if (StringUtils.isBlank(response)) {
	        return Collections.emptyList();
	    }

	    Document doc = Jsoup.parse(response);
	    Element divElement = doc.select("div[data-react-class]").first();

	    if (divElement != null) {
	        String dataReactProps = divElement.attr("data-react-props");

	        if (StringUtils.isNotBlank(dataReactProps)) {
	            JSONObject jsonObject = new JSONObject(dataReactProps);

	            if (jsonObject.has("watchlist") && !jsonObject.isNull("watchlist")) {
	                JSONObject watchlist = jsonObject.getJSONObject("watchlist");

	                if (watchlist.has("companies") && !watchlist.isNull("companies")) {
	                    JSONArray jsonCompanyArray = watchlist.getJSONArray("companies");
	                    return extractStocksFromJSON(jsonCompanyArray);
	                }
	            }
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
	    stock.setTicker(UtilityService.checkForPresence(companyData, "ticker"));
	    Rating rating = stock.getRating();
	    rating.setZenRating(UtilityService.checkForPresence(companyData, "consensus"));
	    rating.setZenTarget(UtilityService.checkForPresence(companyData, "targetPrice"));
	    rating.setZenTargetUpDown(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(companyData, "targetPriceMeanPercentageDifference"), true));
	    rating.setZenScore(UtilityService.checkForPresence(companyData, "zenScore"));
	    return stock;
	}
	
	private void updateLiveStock(List<Stock> zenList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && zenList != null && !zenList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					zenList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setZenRating(matchingElement.getRating().getZenRating());
						stock.getRating().setZenScore(matchingElement.getRating().getZenScore());						            
						stock.getRating().setZenTarget(matchingElement.getRating().getZenTarget());
						stock.getRating().setZenTargetUpDown(matchingElement.getRating().getZenTargetUpDown());
					});
				}
			});
		}
	}
}