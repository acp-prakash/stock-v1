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
import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class StreetRatingService{

	@Autowired
	Environment appProp;

	@Autowired
	StockServiceDB stockServiceDB;

	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateStreetRatings() {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("street.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateStreetRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}

	private void processAuto() throws JsonMappingException, JsonProcessingException
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);		
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<>(headers);
	    String response = restTemplate.exchange(Constants.STREET_STOCK_URL, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);

		response = restTemplate.exchange(Constants.STREET_ETF_URL, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		System.out.println("2 - STREET RATING DONE ==> AUTO");
	}
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("street.rating.folder.name");

		// Use the class loader to access resources
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

		// Resolve resources using the folder name and ticker
		Resource[] resources = resolver.getResources(folderName);
		for (Resource resource : resources) {
			String json = "";				
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					// Process each line from the file
					json = json + line;
				}
			}
			processResponse(json);				
		}
		System.out.println("2 - STREET RATING DONE ==> MANUAL");
	}

	private void processResponse(String json) throws JsonMappingException, JsonProcessingException
	{
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);

			if(jsonObject.has("response"))
			{
				JSONObject jsonObjectRes = (JSONObject)jsonObject.get("response");
				if(jsonObjectRes.has("docs"))
				{
					JSONArray jsonArray = (JSONArray)jsonObjectRes.get("docs");
					for (int i = 0, size = jsonArray.length(); i < size; i++)
					{
						JSONObject jsonDocsObj = jsonArray.getJSONObject(i);
						if(jsonDocsObj != null)
						{
							Stock stock = new Stock();
							stock.setRating(new Rating());
							stock.setTicker(UtilityService.checkForPresence(jsonDocsObj, "ticker"));							
							stock.getRating().setStreetRating(UtilityService.checkForPresence(jsonDocsObj, "CurrentRating"));
							stock.getRating().setStreetScore(UtilityService.checkForPresence(jsonDocsObj, "LetterGradeRating"));							
							if(jsonDocsObj.has("recommendation"))
							{
								stock.getRating().setStreetRating(UtilityService.checkForPresence(jsonDocsObj, "recommendation"));
								stock.getRating().setStreetScore(UtilityService.checkForPresence(jsonDocsObj, "InvestmentRating"));								
							}
							list.add(stock);					
						}
					}
				}
			}
		}
	}

	private void updateLiveStock(List<Stock> streetList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && streetList != null && !streetList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					streetList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setStreetRating(matchingElement.getRating().getStreetRating());
						stock.getRating().setStreetScore(matchingElement.getRating().getStreetScore());						            
					});
				}
			});
		}
	}
}