package com.stock.v1.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.v1.cache.CookieCache;
import com.stock.v1.service.db.OptionsServiceDB;
import com.stock.v1.service.db.PatternServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Options;
import com.stock.v1.vo.Pattern;

@Service
public class OptionsService{

	@Autowired
	Environment appProp;

	@Autowired
	OptionsServiceDB optionsServiceDB;

	@Autowired
	PatternServiceDB patternServiceDB;

	public List<Options> getOptions()
	{
		List<Options> list = optionsServiceDB.getOptions();
		List<Options> histList = optionsServiceDB.getOptionsHistory(null);
		List<Pattern> patternList = patternServiceDB.getPatternHistory(null);
		List<Options> statusToBeUpdatedList = new ArrayList<>();

		list.forEach(option -> {
			List<Options> hList = optionsServiceDB.getOptionsHistory(option.getKey());
			String upPrice = hList.get(0).getPrice();
			String downPrice = hList.get(0).getPrice();
			double upChange = 0;
			double downChange = 0;			
			hList.remove(0);

			int upDays =0;
			ListIterator<Options> listIteratorUp = hList.listIterator();
			while (listIteratorUp.hasNext()) {
				Options opt = listIteratorUp.next();
				if(Double.valueOf(opt.getPrice()) < Double.valueOf(upPrice))
				{
					upChange = upChange + (Double.valueOf(upPrice) - Double.valueOf(opt.getPrice()));
					upDays++;					
					upPrice = opt.getPrice();
				}
				else
					break;
			}

			int downDays = 0;
			ListIterator<Options> listIteratorDown = hList.listIterator();
			while (listIteratorDown.hasNext()) {
				Options opt = listIteratorDown.next();
				if(Double.valueOf(opt.getPrice()) > Double.valueOf(downPrice))
				{
					downChange = downChange + (Double.valueOf(downPrice) - Double.valueOf(opt.getPrice()));						
					downDays++;
					downPrice = opt.getPrice();
				}
				else
					break;
			}

			option.setUpDays(upDays);
			option.setUpBy(UtilityService.stripStringToTwoDecimals(String.valueOf(upChange),false));
			option.setDownDays(downDays);
			option.setDownBy(UtilityService.stripStringToTwoDecimals(String.valueOf(downChange), false));

			if(updateOptionStatus(option))
				statusToBeUpdatedList.add(option);
		});

		if(!statusToBeUpdatedList.isEmpty())
		{
			optionsServiceDB.updateOptions(statusToBeUpdatedList, false);			
		}

		list.forEach(option -> {
			histList.forEach(hist -> {
				if(option.getKey().equalsIgnoreCase(hist.getKey()))
				{					
					if(StringUtils.isBlank(option.getALow()) || Double.valueOf(hist.getLow()) < Double.valueOf(option.getALow()))
					{
						option.setALow(hist.getLow());						
					}
					if(StringUtils.isBlank(option.getAHigh()) || Double.valueOf(hist.getHigh()) > Double.valueOf(option.getAHigh()))
					{
						option.setAHigh(hist.getHigh());						
					}
				}
			});
			option.setAddChange(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(option.getPrice()) - Double.valueOf(option.getEntry())), false));
		});

		list.forEach(option -> {
			patternList.forEach(pattern -> {
				if(option.getTicker().equalsIgnoreCase(pattern.getTicker()))
				{
					option.setPattern(pattern);
				}
			});			
		});

		/*
		 * list.forEach(option -> histList.stream() .filter(hist ->
		 * option.getKey().equalsIgnoreCase(hist.getKey())) .forEach(hist -> { if
		 * (Double.valueOf(hist.getLow()) < Double.valueOf(option.getALow())) {
		 * option.setALow(hist.getLow()); } if (Double.valueOf(hist.getHigh()) >
		 * Double.valueOf(option.getAHigh())) { option.setAHigh(hist.getHigh()); } }) );
		 */

		return list;		
	}

	private boolean updateOptionStatus(Options option)
	{
		try
		{
			if(StringUtils.isNotBlank(option.getHigh()) && StringUtils.isNotBlank(option.getExit()) &&
					!"REACHED".equalsIgnoreCase(option.getStatus()))
			{
				double high = Double.valueOf(option.getHigh());
				double exit = Double.valueOf(option.getExit());
				if(high >= exit)
				{
					option.setStatus("REACHED");
					option.setExitDate(UtilityService.formatLocalDateToString(LocalDate.now()));
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			System.err.println("Exception in updateOptionStatus =>" + ex);
		}
		return false;
	}

	public List<Options> getOptionsHistory(String name)
	{
		return optionsServiceDB.getOptionsHistory(name);
	}

	public boolean deleteOption(String key)
	{
		optionsServiceDB.deleteFromOptions(key);
		return optionsServiceDB.deleteFromOptionsHistory(key);
	}	

	public List<Options> populateOptionsHistory()
	{
		return optionsServiceDB.getOptions();
	}

	public List<Options> updateOptions(List<Options> list)
	{
		return optionsServiceDB.updateOptions(list, true);
	}

	public List<Options> populateOptions()
	{
		String url = Constants.ETRADE_OPTIONS;
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", CookieCache.getCookie("ETRADE_COOKIE"));
		headers.add("Stk1", CookieCache.getCookie("ETRADE_STK"));
		headers.add("Origin","https://us.etrade.com");
		headers.add("Referer","https://us.etrade.com/etx/pxy/watchlists");		
		headers.add("Sec-Fetch-Dest","empty");
		headers.add("Sec-Fetch-Mode","cors");
		headers.add("Sec-Fetch-Site","same-origin");
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

		JSONObject requestJSON = new JSONObject("{\"value\":{\"noteJSON\":{\"noteType\":\"WATCHLIST\",\"watchlistId\":\"147226646306\"},\"viewType\":\"portfolio\",\"watchListRequest\":{\"fromReactWatchLists\":true,\"isCustomView\":true,\"pagination\":{\"startPosNum\":1,\"posPerPage\":200},\"viewName\":\"TECHNICAL\",\"sortBy\":\"0\",\"sortOrder\":\"0\",\"watchlistID\":\"147226646306\"}}}");
		HttpEntity<String> request = new HttpEntity<>(requestJSON.toString(),headers);	
		String response = restTemplate.postForEntity(url, request, String.class).getBody();
		List<Options> list = processResponse(response);
		optionsServiceDB.addToOptions(list);
		return list;
	}

	private List<Options> processResponse(String json)
	{
		List<Options> list = new ArrayList<>();
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);

			if(jsonObject.has("data") && !jsonObject.isNull("data"))
			{
				JSONObject jsonObjectRes = (JSONObject)jsonObject.get("data");
				if(jsonObjectRes.has("watchListView") && !jsonObjectRes.isNull("watchListView"))
				{
					JSONObject jsonWatchListView = (JSONObject)jsonObjectRes.get("watchListView");
					if(jsonWatchListView.has("columnValues") && !jsonWatchListView.isNull("columnValues"))
					{
						JSONArray jsonArrayColumnValues = (JSONArray)jsonWatchListView.get("columnValues");
						for (int i = 0, size = jsonArrayColumnValues.length(); i < size; i++)
						{
							JSONObject jsonColumnValues = jsonArrayColumnValues.getJSONObject(i);
							if(jsonColumnValues != null && jsonColumnValues.has("entryValuesList") && !jsonColumnValues.isNull("entryValuesList"))
							{
								JSONArray jsonArrayEntryValuesList = (JSONArray)jsonColumnValues.get("entryValuesList");
								Options option = new Options();

								option.setName(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(0)));
								option.setKey(option.getName().replace(".", "_").replace("$", "").replace("'", "").replace(" ", "_"));
								option.setTicker(option.getName().split(" ")[0]);

								if(StringUtils.containsIgnoreCase(option.getName(), "Call"))
									option.setType("CALL");							        
								else if(StringUtils.containsIgnoreCase(option.getName(), "Put"))
									option.setType("PUT");

								option.setEntry(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(3)), false));								
								option.setPrice(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(4)), false));
								option.setChange(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(5)), false));
								String range = UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(6));
								option.setLow(UtilityService.stripStringToTwoDecimals(range.split("-")[0], false));
								option.setHigh(UtilityService.stripStringToTwoDecimals(range.split("-")[1], false));
								option.setOpen(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(7)), false));
								option.setAddedDate(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(8)));
								option.setAddedDate(UtilityService.formatDateString(option.getAddedDate(), "MM/dd/yyyy","yyyy-MM-dd"));
								option.setVolume(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(9)));
								option.setInterest(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(10)));
								String delta = UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(11));
								if (Double.valueOf(delta) < 0)
									delta = String.valueOf(-1 * Double.valueOf(delta));								
								option.setDelta(UtilityService.stripStringToTwoDecimals(delta, false));
								option.setTheta(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(12)), false));
								option.setGamma(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(13)), false));
								option.setIv(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(14)), false));
								option.setDaysToExpire(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(15)));
								option.setStatus("ACTIVE");

								list.add(option);					
							}
						}
					}
				}
			}
		}
		return list;
	}

	public List<Options> webullScan(String ticker, boolean put)
	{
		String url = appProp.getProperty("webull.options.scanner");
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);		
		headers.add("Access_token", CookieCache.getCookie("WEBULL_TOKEN"));

		JSONObject filter = new JSONObject();        
		// Add expireDate
		filter.put("options.screener.rule.expireDate", "gte=6&lte=28");        
		// Add volume
		filter.put("options.screener.rule.volume", "gte=5000&lte=140705");        
		// Add openInterest
		filter.put("options.screener.rule.openInterest", "gte=500&lte=20000");        
		// Add delta
		if(put)
			filter.put("options.screener.rule.delta", "gte=-0.6&lte=-0.25");
		else
			filter.put("options.screener.rule.delta", "gte=0.25&lte=0.6");
		
		// Add source as an array
		JSONArray sourceArray = new JSONArray();
		sourceArray.put(appProp.getProperty(ticker));
		filter.put("options.screener.rule.source", sourceArray);
		// Create the page object
		JSONObject page = new JSONObject();
		page.put("fetchSize", 200);
		// Create the main request JSON
		JSONObject requestJSON = new JSONObject();
		requestJSON.put("filter", filter);
		requestJSON.put("page", page);
		HttpEntity<String> request = new HttpEntity<>(requestJSON.toString(),headers);	
		String response = restTemplate.postForEntity(url, request, String.class).getBody();
		System.out.println(response);
		List<Options> optList = processWebullScanResponse(response);
		optList = fetchWebullBatch(optList);		
		return optList;
	}

	private List<Options> processWebullScanResponse(String json)
	{
		List<Options> list = new ArrayList<>();
		if(StringUtils.isNotBlank(json))
		{
			JSONObject jsonObject = new JSONObject(json);
			if(jsonObject.has("datas") && !jsonObject.isNull("datas"))
			{
				JSONArray jsonArrayDatas = (JSONArray)jsonObject.get("datas");
				for (int i = 0, size = jsonArrayDatas.length(); i < size; i++)
				{
					Options opt = new Options();
					JSONObject jsonData = jsonArrayDatas.getJSONObject(i);
					opt.setWebullId(jsonData.getInt("derivativeId"));
					JSONObject derivativeObj = jsonData.getJSONObject("derivative");		            
					opt.setKey(derivativeObj.getString("symbol"));
					opt.setTicker(derivativeObj.getString("unSymbol"));
					list.add(opt);
				}
			}
		}
		return list;
	}
	
	public List<Options> fetchWebullBatch(List<Options> optList)
	{
		String url = appProp.getProperty("webull.options.batch");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String joinedIds = optList.stream()
				.map(option -> String.valueOf(option.getWebullId())) // Convert Integer to String
				.collect(Collectors.joining(","));

		// Encode the joined string
		String encodedIds = URLEncoder.encode(joinedIds, StandardCharsets.UTF_8);
		try {
            // Create the HttpClient
            HttpClient client = HttpClient.newHttpClient();
            System.out.println("encodedIds ===>" + encodedIds);

            // Create the HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url+encodedIds))
                    .GET()
                    .build();

            // Send the GET request and receive the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response            
            System.out.println("Response Body: " + response.body());
            return processWebullBatchResponse(response.body());
        } catch (Exception e) {
            System.err.println("Exception in fetchWebullBatch ==>" + e);
        }		
		return new ArrayList<>();
	}
	
	public List<Options> processWebullBatchResponse(String response)
	{
		List<Options> list = new ArrayList<>();
		if(StringUtils.isNotBlank(response))
		{
			JSONArray jsonArray = new JSONArray(response);
			for (int i = 0, size = jsonArray.length(); i < size; i++)
			{
				Options opt = new Options();
				JSONObject jsonData = jsonArray.getJSONObject(i);
				opt.setTicker(jsonData.getString("unSymbol"));
				opt.setKey(jsonData.getString("symbol"));
				opt.setWebullId(jsonData.getInt("tickerId"));
				opt.setOpen(UtilityService.stripStringToTwoDecimals(jsonData.getString("open"), false));
				opt.setHigh(UtilityService.stripStringToTwoDecimals(jsonData.getString("high"), false));
				opt.setLow(UtilityService.stripStringToTwoDecimals(jsonData.getString("low"), false));
				opt.setPrice(UtilityService.stripStringToTwoDecimals(jsonData.getString("close"), false));
				opt.setVolume(jsonData.getString("volume"));
				opt.setInterest(String.valueOf(jsonData.getInt("openInterest")));
				opt.setDelta(UtilityService.stripStringToTwoDecimals(jsonData.getString("delta"), false));
				opt.setAddedDate(UtilityService.formatLocalDateToString(LocalDate.now()));
				list.add(opt);
			}
		}
		return list;		
	}
}