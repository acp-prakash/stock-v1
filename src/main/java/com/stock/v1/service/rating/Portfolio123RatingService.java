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
public class Portfolio123RatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	private List<Stock> list = new ArrayList<>();

	public List<Stock> populatePortfolio123Ratings() {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("portfolio123.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populatePortfolio123Ratings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}

	private void processAuto() throws JsonMappingException, JsonProcessingException
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);		
		headers.add("Cookie", StringUtils.isNotBlank(CookieCache.getCookie("PORTFOLIO123_COOKIE"))? CookieCache.getCookie("PORTFOLIO123_COOKIE"): Constants.PORTFOLIO123_COOKIE);
		headers.add("X-Xsrf-Token", StringUtils.isNotBlank(CookieCache.getCookie("PORTFOLIO123_TOKEN"))? CookieCache.getCookie("PORTFOLIO123_TOKEN"): Constants.PORTFOLIO123_TOKEN);
		headers.add("Angular_req", "1");
		headers.setConnection("keep-alive");
		headers.add("Host", "www.portfolio123.com");
		headers.add("Referer", "https://www.portfolio123.com/app/watchlist/details?id=34991");		
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
	    String response = restTemplate.exchange(Constants.PORTFOLIO123_URL_1, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_2, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_3, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_4, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_5, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_6, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_7, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_8, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_9, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_10, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_11, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_12, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		response = restTemplate.exchange(Constants.PORTFOLIO123_URL_13, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		System.out.println("13 - PORTFOLIO123 RATING DONE ==> AUTO");
	}
	
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("portfolio123.rating.folder.name");

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
		System.out.println("13 - PORTFOLIO123 RATING DONE ==> MANUAL");
	}

	private void processResponse(String json)
	{
		if(StringUtils.isNotBlank(json))
		{
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0, size = jsonArray.length(); i < size; i++)
			{
				JSONObject jsonArrObj = jsonArray.getJSONObject(i);
				if(jsonArrObj != null)
				{
					Stock stock = new Stock();
					stock.setRating(new Rating());

					if(jsonArrObj.has("ticker") && !jsonArrObj.isNull("ticker"))
					{
						stock.setTicker(UtilityService.checkForPresence(jsonArrObj, "ticker").split(":")[0]);
						if(jsonArrObj.has("serverCols") && !jsonArrObj.isNull("serverCols"))
						{
							JSONObject jsonObjCols= jsonArrObj.getJSONObject("serverCols");
							stock.getRating().setPortfolio123Rating(UtilityService.getPortfolio123Rating(UtilityService.checkForPresence(jsonObjCols, "AvgRec")));
							stock.getRating().setPortfolio123HighPT(UtilityService.checkForPresence(jsonObjCols, "PriceTargetHi"));
							stock.getRating().setPortfolio123LowPT(UtilityService.checkForPresence(jsonObjCols, "PriceTargetLo"));
							stock.getRating().setPortfolio123Analysts(UtilityService.checkForPresence(jsonObjCols, "#AnalystsPriceTarget"));
						}
						list.add(stock);
					}
				}
			}
		}
	}

	private void updateLiveStock(List<Stock> portfolio123List)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && portfolio123List != null && !portfolio123List.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					portfolio123List.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setPortfolio123Rating(matchingElement.getRating().getPortfolio123Rating());						            						            
						stock.getRating().setPortfolio123HighPT(matchingElement.getRating().getPortfolio123HighPT());
						stock.getRating().setPortfolio123LowPT(matchingElement.getRating().getPortfolio123LowPT());
						stock.getRating().setPortfolio123Analysts(matchingElement.getRating().getPortfolio123Analysts());
					});
				}
			});
		}
	}
}