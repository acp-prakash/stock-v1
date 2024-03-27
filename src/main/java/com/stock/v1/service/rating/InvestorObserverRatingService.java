package com.stock.v1.service.rating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class InvestorObserverRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
		
	private List<Stock> list = new ArrayList<>();
	private List<Master> masterList;
	
	public List<Stock> populateInvestorObserverRatings() {

		try
		{
			list = new ArrayList<>();
			masterList = stockServiceDB.getMasterList();
			String auto = appProp.getProperty("investorobserver.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateInvestorObserverRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}
	
	private void processAuto() throws JsonMappingException, JsonProcessingException
	{		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));		
		headers.setCacheControl("no-cache");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String OriginalUrl = Constants.INVESTOR_OBSERVER_URL;
		OriginalUrl = OriginalUrl.replace("%5B", "[").replace("%5D", "]");
		
		/*String queryString = masterList.stream()
				.limit(50)
				.map(Master::getTicker)
				.map(ticker -> "&filter%5Bsymbol%5D%5B0%5D=" + ticker)
				.collect(Collectors.joining());*/
		
		List<Master> filteredList = masterList.stream().filter(x -> !x.isFuture()).collect(Collectors.toList());
		
		String queryString = IntStream.range(0, 50)
                .mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index).getTicker())
                .collect(Collectors.joining());		
		String url = OriginalUrl.replace("REPLACE", queryString);		
		String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);

		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 50).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);		
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);

		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 100).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);		
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 150).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);		
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 200).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);		
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 250).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 300).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 350).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 400).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 50)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 450).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		
		queryString = IntStream.range(0, 48)
				.mapToObj(index -> "&filter[symbol][" + index + "]=" + filteredList.get(index + 500).getTicker())
				.collect(Collectors.joining());
		url = OriginalUrl.replace("REPLACE", queryString);
		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
	
		System.out.println("12 - INVESTOR OBSERVER RATING DONE ==> AUTO");
	}
	private void processManual() throws IOException
	{
		System.out.println("MANUAL ==> populateInvestorObserverRatings");
		String folderName = appProp.getProperty("investorobserver.rating.folder.name");

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
		System.out.println("12 - INVESTOR OBSERVER RATING DONE ==> MANUAL");
	}
	
	private void processResponse(String json) throws JsonMappingException, JsonProcessingException
	{
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);

			if(jsonObject.has("data") && !jsonObject.isNull("data"))
			{
				JSONObject jsonObjectRes = (JSONObject)jsonObject.get("data");
				if(jsonObjectRes.has("companies") && !jsonObjectRes.isNull("companies"))
				{
					JSONArray jsonArray = (JSONArray)jsonObjectRes.get("companies");
					for (int i = 0, size = jsonArray.length(); i < size; i++)
					{
						JSONObject jsonDocsObj = jsonArray.getJSONObject(i);
						if(jsonDocsObj != null)
						{
							Stock stock = new Stock();
							stock.setRating(new Rating());
							stock.setTicker(UtilityService.checkForPresence(jsonDocsObj, "symbol"));
							stock.getRating().setInvestObserveScore(convertScore(UtilityService.checkForPresence(jsonDocsObj, "ssrOverallScore")));
							stock.getRating().setInvestObserveLT(UtilityService.checkForPresence(jsonDocsObj, "priceLowTarget"));
							stock.getRating().setInvestObserveHT(UtilityService.checkForPresence(jsonDocsObj, "priceHighTarget"));
							list.add(stock);					
						}
					}
				}
			}
		}
	}
	
	private String convertScore(String score)
	{
		if(StringUtils.isNotBlank(score))
		{
			double x = Double.valueOf(score) * 100;
			double y = Math.round(x * 100.0) / 100.0; // => 1.23
			int rounded = (int) (y + 0.5);
			return String.valueOf(rounded);
		}
		return score;
	}
	
	private void updateLiveStock(List<Stock> investorObserverList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && investorObserverList != null && !investorObserverList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					investorObserverList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setInvestObserveScore(matchingElement.getRating().getInvestObserveScore());						            						            
						stock.getRating().setInvestObserveLT(matchingElement.getRating().getInvestObserveLT());
						stock.getRating().setInvestObserveHT(matchingElement.getRating().getInvestObserveHT());
					});
				}
			});
		}
	}
}