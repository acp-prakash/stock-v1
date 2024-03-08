package com.stock.v1.service.rating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class SeekingAlphaRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateSeekingAlphaRatings() {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("seekingalpha.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateSeekingAlphaRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}

	private void processAuto() throws JsonMappingException, JsonProcessingException
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie",StringUtils.isNotBlank(CookieCache.getCookie("SEEKINGALPHA_COOKIE"))? CookieCache.getCookie("SEEKINGALPHA_COOKIE"): Constants.SEEKINGALPHA_COOKIE);		
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<>(headers);

	    String json1 = restTemplate.exchange(Constants.SEEKINGALPHA_PORTFOLIO_URL_1, HttpMethod.GET, entity, String.class).getBody();				
		String json2 = restTemplate.exchange(Constants.SEEKINGALPHA_RATINGS_URL_1, HttpMethod.GET, entity, String.class).getBody();
		processResponse(json1, json2);

		/*
		 * json1 = restTemplate.exchange(Constants.SEEKINGALPHA_PORTFOLIO_URL_2,
		 * HttpMethod.GET, entity, String.class).getBody(); json2 =
		 * restTemplate.exchange(Constants.SEEKINGALPHA_RATINGS_URL_2, HttpMethod.GET,
		 * entity, String.class).getBody(); processResponse(json1, json2);
		 */

		System.out.println("5 - SEEKING ALPHA RATING DONE ==> AUTO");
	}
	
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("seeking.alpha.rating.folder.name");

		// Use the class loader to access resources
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

		// Resolve resources using the folder name and ticker
		Resource[] resources = resolver.getResources(folderName);
		String json1 = "";
		String json2 = "";
		for (Resource resource : resources) {
			String json = "";				
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					// Process each line from the file
					json = json + line;
				}
				if(StringUtils.contains(json, "quant_tickers_count"))
					json2 = json;
				else
					json1 = json;
			}
			catch(Exception ex)
			{					
			}
			finally
			{				
			}
		}
		processResponse(json1, json2);		
		System.out.println("5 - SEEKING ALPHA RATING DONE ==> MANUAL");
	}

	private void processResponse(String json1, String json2)
	{
		if(StringUtils.isNotBlank(json1) && StringUtils.isNotBlank(json2))
		{
			JSONObject jsonObject = new JSONObject(json1);
			if(jsonObject.has("included") && !jsonObject.isNull("included"))
			{
				JSONArray jsonArray = (JSONArray)jsonObject.get("included");
				for (int i = 0, size = jsonArray.length(); i < size; i++)
				{
					JSONObject jsonArrObj = jsonArray.getJSONObject(i);
					if(jsonArrObj != null && jsonArrObj.has("attributes") && !jsonArrObj.isNull("attributes"))
					{
						String id = UtilityService.checkForPresence(jsonArrObj, "id");
						JSONObject jsonObjAttr = (JSONObject)jsonArrObj.get("attributes");							
						String ticker = (UtilityService.checkForPresence(jsonObjAttr, "name"));
						Stock stock = prepareRating(id, json2);
						stock.setTicker(ticker);							
						list.add(stock);
					}
				}
			}
		}
	}
	
	private Stock prepareRating(String id, String json)
	{
		Stock stock = new Stock();
		stock.setRating(new Rating());		
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);
			if(jsonObject.has("data") && !jsonObject.isNull("data"))
			{
				JSONArray jsonArray = (JSONArray)jsonObject.get("data");
				for (int i = 0, size = jsonArray.length(); i < size; i++)
				{
					JSONObject jsonArrObj = jsonArray.getJSONObject(i);
					if(jsonArrObj != null && jsonArrObj.has("attributes") && !jsonArrObj.isNull("attributes"))
					{
						String currID = UtilityService.checkForPresence(jsonArrObj, "id");
						currID = currID.split(",")[0];
						if(currID.equalsIgnoreCase(id))
						{
							JSONObject jsonObjAttr = (JSONObject)jsonArrObj.get("attributes");						
							stock.getRating().setSeekAlphaQuantRating(UtilityService.getSeekingAlphaRating(UtilityService.checkForPresence(jsonObjAttr, "quant_rating")));
							stock.getRating().setSeekAlphaWallstreetRating(UtilityService.getSeekingAlphaRating(UtilityService.checkForPresence(jsonObjAttr, "sell_side_rating")));
							stock.getRating().setSeekAlphaAnalystsRating(UtilityService.getSeekingAlphaRating(UtilityService.checkForPresence(jsonObjAttr, "authors_rating")));
							return stock;
						}
					}
				}
			}
		}
		return stock;
	}
	
	private void updateLiveStock(List<Stock> seekAlphaList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && seekAlphaList != null && !seekAlphaList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					seekAlphaList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setSeekAlphaQuantRating(matchingElement.getRating().getSeekAlphaQuantRating());						            						            
						stock.getRating().setSeekAlphaWallstreetRating(matchingElement.getRating().getSeekAlphaWallstreetRating());
						stock.getRating().setSeekAlphaAnalystsRating(matchingElement.getRating().getSeekAlphaAnalystsRating());
					});
				}
			});
		}
	}
}