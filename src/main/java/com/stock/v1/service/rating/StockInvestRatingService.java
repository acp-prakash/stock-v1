package com.stock.v1.service.rating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.stock.v1.cache.CookieCache;
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
				//processAuto();
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateStockInvestUSRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}

	private void processAuto() throws IOException
	{
		
		URL url = new URL(Constants.STOCKINVEST_US_URL_1);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
		connection.setRequestProperty("Host", "stockinvest.us");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setRequestProperty("Connection", "keep-alive");		
		connection.setRequestProperty("Cookie",  StringUtils.isNotBlank(CookieCache.getCookie("STOCKINVEST_US_COOKIE"))? CookieCache.getCookie("STOCKINVEST_US_COOKIE"): Constants.STOCKINVEST_US_COOKIE);
		/*connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,ta;q=0.8");		
		connection.setRequestProperty("Priority", "u=1, i");
		connection.setRequestProperty("Referer", "https://stockinvest.us/watchlist/335307");
		connection.setRequestProperty("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"");
		connection.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
		connection.setRequestProperty("Sec-Ch-Ua-Platform", "\"Windows\"");
		connection.setRequestProperty("Sec-Fetch-Dest", "empty");
		connection.setRequestProperty("Sec-Fetch-Mode", "cors");
		connection.setRequestProperty("Sec-Fetch-Site", "same-origin");*/
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
		connection.setRequestProperty("X-Xsrf-Token",StringUtils.isNotBlank(CookieCache.getCookie("STOCKINVEST_US_TOKEN"))? CookieCache.getCookie("STOCKINVEST_US_TOKEN"): Constants.STOCKINVEST_US_TOKEN);


		// Connect
		connection.connect();

		// Get response stream
		InputStream inputStream = connection.getInputStream();
		String encoding = connection.getContentEncoding();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json, text/plain, */*");
		headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
		headers.set("Accept-Language", "en-US,en;q=0.9,ta;q=0.8");
		headers.add("Cookie",  StringUtils.isNotBlank(CookieCache.getCookie("STOCKINVEST_US_COOKIE"))? CookieCache.getCookie("STOCKINVEST_US_COOKIE"): Constants.STOCKINVEST_US_COOKIE);
		headers.set("Priority", "u=1, i");
		headers.set("Referer", "https://stockinvest.us/watchlist/335307");
		headers.set("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"");
		headers.set("Sec-Ch-Ua-Mobile", "?0");
		headers.set("Sec-Ch-Ua-Platform", "\"Windows\"");
		headers.set("Sec-Fetch-Dest", "empty");
		headers.set("Sec-Fetch-Mode", "cors");
		headers.set("Sec-Fetch-Site", "same-origin");
		headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
		headers.add("X-Xsrf-Token",StringUtils.isNotBlank(CookieCache.getCookie("STOCKINVEST_US_TOKEN"))? CookieCache.getCookie("STOCKINVEST_US_TOKEN"): Constants.STOCKINVEST_US_TOKEN);
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
	    String response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_1, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		/*response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_2, HttpMethod.GET, entity, String.class).getBody();
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
		response = restTemplate.exchange(Constants.STOCKINVEST_US_URL_13, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);*/
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
			
			if(jsonObject.has("screener"))
			{
				JSONArray jsonArray = (JSONArray)jsonObject.get("screener");
				for (int i = 0, size = jsonArray.length(); i < size; i++)
				{
					JSONObject jsonDataObj = jsonArray.getJSONObject(i);
					if(jsonDataObj != null)
					{
						Stock stock = new Stock();
						String tick = UtilityService.checkForPresence(jsonDataObj, "symbol");
						stock.setTicker(tick);
						
						String gs = UtilityService.checkForPresence(jsonDataObj, "goldencross_3");
						if("0000-00-00".equalsIgnoreCase(gs))
							gs = null;
						stock.setGCShortDate(gs);

						String gl = UtilityService.checkForPresence(jsonDataObj, "gc_12_in");
						if(!"100000".equalsIgnoreCase(gl))
						{
							LocalDate currentDate = LocalDate.now();
							LocalDate calculatedDate = currentDate.minusDays(Long.valueOf(gl));
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							gl = calculatedDate.format(formatter);
						}
						else
							gl = null;
						stock.setGCLongDate(gl);
						
						String ds = UtilityService.checkForPresence(jsonDataObj, "deathcross_3");
						if("0000-00-00".equalsIgnoreCase(ds))
							ds = null;
						stock.setDCShortDate(ds);

						String dl = UtilityService.checkForPresence(jsonDataObj, "dc_12_in");
						if(!"100000".equalsIgnoreCase(dl))
						{
							LocalDate currentDate = LocalDate.now();
							LocalDate calculatedDate = currentDate.minusDays(Long.valueOf(dl));
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							dl = calculatedDate.format(formatter);
						}
						else
							dl = null;
						stock.setDCLongDate(dl);
						
						list.stream().forEach(x -> {
						    if (x.getTicker().equalsIgnoreCase(stock.getTicker())) {
						        x.setGCShortDate(stock.getGCShortDate());
						        x.setGCLongDate(stock.getGCLongDate());
						        x.setDCShortDate(stock.getDCShortDate());
						        x.setDCLongDate(stock.getDCLongDate());
						    }
						});
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
						stock.setGCShortDate(matchingElement.getGCShortDate());
						stock.setGCLongDate(matchingElement.getGCLongDate());
						stock.setDCShortDate(matchingElement.getDCShortDate());
						stock.setDCLongDate(matchingElement.getDCLongDate());
					});
				}
			});
		}
	}
}