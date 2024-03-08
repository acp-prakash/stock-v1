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
public class StockInvestRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateStockInvestUSRatings() {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("stockinvestus.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateStockInvestUSRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}

	private void processAuto() throws JsonMappingException, JsonProcessingException
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Accept-Language", "en-US,en;q=0.9,ta;q=0.8");
		headers.add("Cookie", Constants.STOCKINVEST_US_COOKIE);
		headers.add("Referer", "https://stockinvest.us/watchlist/335437");
		headers.add("Sec-Ch-Ua", "\"Google Chrome\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"");
		headers.add("Sec-Ch-Ua-Mobile", "?0");
		headers.add("Sec-Ch-Ua-Platform", "\"Windows\"");
		headers.add("Sec-Fetch-Dest", "empty");
		headers.add("Sec-Fetch-Mode", "cors");
		headers.add("Sec-Fetch-Site", "same-origin");
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
		headers.add("X-Xsrf-Token", Constants.STOCKINVEST_US_TOKEN);
		HttpEntity<String> entity = new HttpEntity<>(headers);
	    String response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_1, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_2, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_3, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_4, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_5, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_5, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_6, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_7, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_8, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_9, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_10, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_11, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_12, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		System.out.println("6 - STOCKINVESTUS RATING DONE ==> AUTO");
	}
	
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("stockinvest.rating.folder.name");

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
		System.out.println("6 - STOCKINVESTUS RATING DONE ==> MANUAL");
	}

	private void processResponse(String json)
	{
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);

			if(jsonObject.has("items"))
			{
				JSONArray jsonArray = (JSONArray)jsonObject.get("items");
				for (int i = 0, size = jsonArray.length(); i < size; i++)
				{
					JSONObject jsonDataObj = jsonArray.getJSONObject(i);
					if(jsonDataObj != null)
					{
						Stock stock = new Stock();
						stock.setRating(new Rating());
						stock.setTicker(UtilityService.checkForPresence(jsonDataObj, "symbol"));							
						stock.getRating().setSiusScore(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(jsonDataObj, "score"), false));
						stock.getRating().setSiusRating(UtilityService.checkForPresence(jsonDataObj, "current_signal"));
						stock.getRating().setSiusDays(UtilityService.checkForPresence(jsonDataObj, "duration"));
						list.add(stock);					
					}
				}
			}
		}
	}

	private void updateLiveStock(List<Stock> siustList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && siustList != null && !siustList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					siustList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setSiusScore(matchingElement.getRating().getSiusScore());
						stock.getRating().setSiusRating(matchingElement.getRating().getSiusRating());
						stock.getRating().setSiusDays(matchingElement.getRating().getSiusDays());
					});
				}
			});
		}
	}
}