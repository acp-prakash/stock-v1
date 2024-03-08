package com.stock.v1.service.rating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class FinscreenerRatingService{

	@Autowired
	StockServiceDB stockServiceDB;
	
	@Autowired
	Environment appProp;
	
	private List<Master> masterList;
	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateFinscreenerRatings() {
		
		try
		{
			list = new ArrayList<>();
			masterList = stockServiceDB.getMasterList();
			String auto = appProp.getProperty("finscreener.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateFinscreenerRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}
	
	private void processAuto() throws JsonMappingException, JsonProcessingException
	{
		String url = Constants.FINSCREENER_URL;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		JSONObject requestJSON = new JSONObject("{\"idZobrazenia\":\"1\",\"strankovanie\":{\"cisloStranky\":1,\"pocetZaznamovNaStranku\":300},\"zoradenie\":{\"idStlpca\":\"1\",\"vzostupne\":true},\"zoznamIdCennehoPapiera\":[4771,4775,573,4790,4797,585,4813,697,725,4894,62276,4916,749,777,48937,4959,5018,5020,40857,873,877,878,879,889,41042,1044,55396,28695,62879,1113,1133,1139,1166,1170,63540,1228,5340,1283,1287,1310,5383,1323,59656,1344,55325,5426,40884,62941,53992,1404,1406,62055,1430,55388,55153,63341,1492,58814,1580,5577,5580,1614,1669,1682,5630,5631,1692,1698,1712,62079,4461,1774,5695,41067,1809,1894,59746,1932,19913,1944,1958,5874,2003,2007,5910,5913,62455,2097,2118,2131,41076,2146,2158,2164,2174,61996,62473,2259,2292,2303,40542,6119,6180,6185,2415,2428,63302,2516,2526,41086,2537,55422,6240,48810,61997,2600,63440,2613,2629,2632,55266,2647,2662,2664,2678,63536,2764,2784,2792,2794,2806,55083,2811,6487,6489,2812,6494,6498,2841,6509,2862,6539,2930,2939,6559,48831,2968,3005,3011,6603,3071,3074,6618,3095,3132,40953,29353,29354,59948,3164,3167,3171,63272,3218,3223,3234,3242,58813,3282,55295,40445,3379,28027,6878,59490,3426,29397,62921,3482,3491,49015,49549,3517,29410,57043,7030,7045,3594,29420,63214,7095,28907,3664,3673,60032,20477,55404,20488,20491,28913,40994,3785,3819,3863,40590,3885,3887,3899,3910,20540,62219,19464,3949,7371,3972,7387,55271,4012,40593,4014,4016,55304,4052,4057,4081,4084,7493,7508,54057,7516,4152,4156,4183,4186,62768,48900,48708,4218,4223,64652,4276,20504,20594,20557,19461],\"idu\":16850}");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestJSON.toString(),headers);	
		String response = restTemplate.postForEntity(url, request, String.class).getBody();
		processResponse(response);
		
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		requestJSON = new JSONObject("{\"idZobrazenia\":\"9\",\"strankovanie\":{\"cisloStranky\":1,\"pocetZaznamovNaStranku\":300},\"zoradenie\":{\"idStlpca\":\"1\",\"vzostupne\":true},\"zoznamIdCennehoPapiera\":[4771,4775,573,4790,4797,585,4813,697,725,4894,62276,4916,749,777,48937,4959,5018,5020,40857,873,877,878,879,889,41042,1044,55396,28695,62879,1113,1133,1139,1166,1170,63540,1228,5340,1283,1287,1310,5383,1323,59656,1344,55325,5426,40884,62941,53992,1404,1406,62055,1430,55388,55153,63341,1492,58814,1580,5577,5580,1614,1669,1682,5630,5631,1692,1698,1712,62079,4461,1774,5695,41067,1809,1894,59746,1932,19913,1944,1958,5874,2003,2007,5910,5913,62455,2097,2118,2131,41076,2146,2158,2164,2174,61996,62473,2259,2292,2303,40542,6119,6180,6185,2415,2428,63302,2516,2526,41086,2537,55422,6240,48810,61997,2600,63440,2613,2629,2632,55266,2647,2662,2664,2678,63536,2764,2784,2792,2794,2806,55083,2811,6487,6489,2812,6494,6498,2841,6509,2862,6539,2930,2939,6559,48831,2968,3005,3011,6603,3071,3074,6618,3095,3132,40953,29353,29354,59948,3164,3167,3171,63272,3218,3223,3234,3242,58813,3282,55295,40445,3379,28027,6878,59490,3426,29397,62921,3482,3491,49015,49549,3517,29410,57043,7030,7045,3594,29420,63214,7095,28907,3664,3673,60032,20477,55404,20488,20491,28913,40994,3785,3819,3863,40590,3885,3887,3899,3910,20540,62219,19464,3949,7371,3972,7387,55271,4012,40593,4014,4016,55304,4052,4057,4081,4084,7493,7508,54057,7516,4152,4156,4183,4186,62768,48900,48708,4218,4223,64652,4276,20504,20594,20557,19461],\"idu\":16850}");
		headers.setContentType(MediaType.APPLICATION_JSON);
		request = new HttpEntity<>(requestJSON.toString(),headers);	
		response = restTemplate.postForEntity(url, request, String.class).getBody();
		processResponse(response);
		System.out.println("17 - FINSCREENER RATING DONE ==> AUTO");
		
	}
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("finscreener.rating.folder.name");

		// Use the class loader to access resources
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

		// Resolve resources using the folder name and ticker
		Resource[] resources = resolver.getResources(folderName);
		for (Resource resource : resources) {
			String json = StringUtils.EMPTY;				
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					// Process each line from the file
					json = json + line;
				}
			}
			processResponse(json);
		}
		System.out.println("17 - FINSCREENER RATING DONE ==> MANUAL");
	}
	
	private void processResponse(String json) throws JsonMappingException, JsonProcessingException
	{
		if(StringUtils.isNotBlank(json))
		{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json);
			JsonNode securitiesNode = rootNode.path("zoznamCennychPapierov");
			for (JsonNode securityNode : securitiesNode) {
				JsonNode entryNode = securityNode.path("cennyPapier").path("entry");
				Stock stock = new Stock();
				stock.setRating(new Rating());
				for (JsonNode keyValue : entryNode) {
					String key = keyValue.path("key").asText();
					String value = keyValue.path("value").asText();

					if ("ticker".equalsIgnoreCase(key)) {
						stock.setTicker(value);
					}
					else if ("slovne_odporucenie".equalsIgnoreCase(key)) {
						stock.getRating().setFinscreenerRating(value);
					}
					else if ("cielova_cena".equalsIgnoreCase(key)) {
						stock.getRating().setFinscreenerPT(UtilityService.stripStringToTwoDecimals(value, false));
					}
					else if ("pocet_odporucani".equalsIgnoreCase(key)) {
						stock.getRating().setFinscreenerAnalysts(value);
					}
					else if ("id_cenneho_papiera".equalsIgnoreCase(key)) {
						stock.setName(value);
					}
				}

				if(masterList.stream().anyMatch(x->x.getTicker().equalsIgnoreCase(stock.getTicker())))
				{
					list.stream()
                		.filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
                		.findFirst()
                		.ifPresentOrElse(
                			matchingElement -> {matchingElement.getRating().setFinscreenerRating(stock.getRating().getFinscreenerRating());
                			matchingElement.getRating().setFinscreenerAnalysts(stock.getRating().getFinscreenerAnalysts());},
                			() -> {
                				list.add(stock);
                			}
                		);
					//System.out.println(stock.getTicker() + ":" + stock.getName());
				}
			}
		}
	}
	
	private void updateLiveStock(List<Stock> finscreenerList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && finscreenerList != null && !finscreenerList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					finscreenerList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setFinscreenerRating(matchingElement.getRating().getFinscreenerRating());						            						            
						stock.getRating().setFinscreenerPT(matchingElement.getRating().getFinscreenerPT());
						stock.getRating().setFinscreenerAnalysts(matchingElement.getRating().getFinscreenerAnalysts());						            
					});
				}
			});
		}
	}
}