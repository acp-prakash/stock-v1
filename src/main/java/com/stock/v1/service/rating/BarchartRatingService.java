package com.stock.v1.service.rating;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.stock.v1.cache.CookieCache;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class BarchartRatingService{

	@Autowired
	Environment appProp;

	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateBarchartRatings(String url) {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("barchart.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto(url);			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateBarchartRatings ==>" + ex);
		}
		return list;
	}

	private void processAuto(String url) throws JsonMappingException, JsonProcessingException
	{		
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie",  StringUtils.isNotBlank(CookieCache.getCookie("BARCHART_COOKIE"))? CookieCache.getCookie("BARCHART_COOKIE"): Constants.BARCHART_COOKIE);
		headers.add("X-Xsrf-Token",StringUtils.isNotBlank(CookieCache.getCookie("BARCHART_TOKEN"))? CookieCache.getCookie("BARCHART_TOKEN"): Constants.BARCHART_TOKEN);
		headers.add("Referer","https://www.investing.com/pro/watchlist/w-48459690.iwl/v-232ee660");
		headers.add("Accept-Language","en-US,en;q=0.9,ta;q=0.8");
		headers.add("Referer","https://www.barchart.com/my/watchlist?viewName=157005");		
		headers.add("Sec-Fetch-Mode","cors");
		headers.add("Sec-Fetch-Site","same-origin");
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

		HttpEntity<String> entity = new HttpEntity<>(headers);
	    String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		System.out.println("1 - BARCHART RATING DONE ==> AUTO");
	}
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("barchart.daily.history.folder.name");

		// Use the class loader to access resources
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

		// Resolve resources using the folder name and ticker
		Resource[] resources = resolver.getResources(folderName + "/" + "barchart-daily-history.json");
		InputStream inputStream = resources[0].getInputStream();
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, resultStream);
		String response = resultStream.toString(StandardCharsets.UTF_8.name());
		processResponse(response);
		System.out.println("1 - BARCHART RATING DONE ==> MANUAL");
	}

	private void processResponse(String json) throws JsonMappingException, JsonProcessingException
	{
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);

			if(jsonObject.has("data"))
			{	
				JSONArray jsonArray = (JSONArray)jsonObject.get("data");
				for (int i = 0, size = jsonArray.length(); i < size; i++)
				{
					JSONObject jsonDataObj = jsonArray.getJSONObject(i);
					if(jsonDataObj != null)
					{
						Stock stock = prepareBarchartStockInfo(jsonDataObj);
						if(stock != null)
						{
							list.add(stock);
						}
					}
				}
			}
		}
	}

	private Stock prepareBarchartStockInfo(JSONObject obj)
	{
		if(obj.has("raw"))
		{
			Stock stock = new Stock();
			stock.setRating(new Rating());
			JSONObject jsonItemsRawObj = (JSONObject)obj.get("raw");
			stock.setDate(UtilityService.formatLocalDateToString(LocalDate.now()));
			stock.setTicker(UtilityService.checkForPresence(jsonItemsRawObj, "symbol"));
			stock.setName(UtilityService.checkForPresence(jsonItemsRawObj, "symbolName"));		
			stock.getRating().setBtAnalystRating(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(jsonItemsRawObj, "averageRecommendation"), false));
			stock.getRating().setBtAnalysts(UtilityService.checkForPresence(jsonItemsRawObj, "totalRecommendations"));
			stock.setPrice(UtilityService.checkForPresence(jsonItemsRawObj, "dailyLastPrice"));
			if(StringUtils.isBlank(stock.getPrice()))
			{
				stock.setPrice(UtilityService.checkForPresence(jsonItemsRawObj, "lastPrice"));
				stock.setOpen(UtilityService.checkForPresence(jsonItemsRawObj, "openPrice"));
				stock.setHigh(UtilityService.checkForPresence(jsonItemsRawObj, "highPrice"));
				stock.setLow(UtilityService.checkForPresence(jsonItemsRawObj, "lowPrice"));
				stock.setChange(UtilityService.checkForPresence(jsonItemsRawObj, "priceChange"));				
				stock.setPrevPrice(UtilityService.checkForPresence(jsonItemsRawObj, "previousPrice"));
				stock.setVolume(UtilityService.checkForPresence(jsonItemsRawObj, "volume"));
				stock.getRating().setBtRating(UtilityService.checkForPresence(obj, "opinion"));
				stock.getRating().setBtShortRating(UtilityService.checkForPresence(obj, "opinionShortTerm"));
				stock.getRating().setBtLongRating(UtilityService.checkForPresence(obj, "opinionLongTerm"));		
				stock.getRating().setBtTrend(UtilityService.checkForPresence(obj, "trendSpotterSignal"));
				stock.setPriceChg5(UtilityService.checkForPresence(obj, "priceChange5d"));
				stock.setPriceChg10(UtilityService.checkForPresence(obj, "priceChange10d"));
				stock.setLastEarningsDate(UtilityService.checkForPresence(obj, "epsDate"));
				stock.setNextEarningsDate(UtilityService.checkForPresence(obj, "nextEarningsDate"));				
			}
			else
			{
				stock.setPrice(UtilityService.checkForPresence(jsonItemsRawObj, "dailyLastPrice"));
				stock.setOpen(UtilityService.checkForPresence(jsonItemsRawObj, "dailyOpenPrice"));
				stock.setHigh(UtilityService.checkForPresence(jsonItemsRawObj, "dailyHighPrice"));
				stock.setLow(UtilityService.checkForPresence(jsonItemsRawObj, "dailyLowPrice"));
				stock.setChange(UtilityService.checkForPresence(jsonItemsRawObj, "dailyPriceChange"));				
				stock.setPrevPrice(UtilityService.checkForPresence(jsonItemsRawObj, "dailyPreviousPrice"));
				stock.setVolume(UtilityService.checkForPresence(jsonItemsRawObj, "dailyVolume"));
				stock.getRating().setBtRating(UtilityService.checkForPresence(obj, "dailyOpinion"));
				stock.getRating().setBtShortRating(UtilityService.checkForPresence(obj, "dailyOpinionShortTerm"));
				stock.getRating().setBtLongRating(UtilityService.checkForPresence(obj, "dailyOpinionLongTerm"));		
				stock.getRating().setBtTrend(UtilityService.checkForPresence(obj, "dailyTrendSpotterSignal"));
				stock.setPriceChg5(UtilityService.checkForPresence(obj, "dailyPriceChange5d"));
				stock.setPriceChg10(UtilityService.checkForPresence(obj, "dailyPriceChange10d"));
				stock.setLastEarningsDate(UtilityService.checkForPresence(obj, "epsDate"));
				stock.setNextEarningsDate(UtilityService.checkForPresence(obj, "nextEarningsDate"));				
			}

			if("unch".equalsIgnoreCase(stock.getChange()))
				stock.setChange("0");

			return stock;
		}
		return null;
	}
}