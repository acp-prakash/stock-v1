package com.stock.v1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
import com.stock.v1.cache.LiveStockCache;
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
			double add = Double.valueOf(option.getAddPrice());
			double curr = Double.valueOf(option.getPrice());
			double diff = ((curr - add)/add)*100;
			option.setPctPL(String.valueOf((int)diff));
		});

		list.forEach(option -> {			
			histList.forEach(hist -> {
				if(option.getKey().equalsIgnoreCase(hist.getKey()))
				{					
					double add = Double.valueOf(option.getAddPrice());
					if(StringUtils.isBlank(option.getALow()) || Double.valueOf(hist.getLow()) < Double.valueOf(option.getALow()))
					{
						option.setALow(hist.getLow());
						double low = Double.valueOf(option.getALow());
						double diff = (((low - add)/add) * 100);
						if (diff > 0)
							diff = 0;						
						option.setPctMaxL(String.valueOf((int)diff));
					}
					if(StringUtils.isBlank(option.getAHigh()) || Double.valueOf(hist.getHigh()) > Double.valueOf(option.getAHigh()))
					{
						option.setAHigh(hist.getHigh());
						double high = Double.valueOf(option.getAHigh());
						double diff = (((high - add)/add) * 100);
						if (diff < 0)
							diff = 0;
						option.setPctMaxP(String.valueOf((int)diff));
					}
				}
			});			
		});

		list.forEach(option -> {
			LiveStockCache.getLiveStockList().forEach(stock -> {
				if(option.getTicker().equalsIgnoreCase(stock.getTicker()))
				{
					option.setStockPrice(stock.getPrice());
					option.setStockPriceChg(UtilityService.stripStringToTwoDecimals(stock.getChange(), false));
				}
			});			
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
		return optionsServiceDB.updateOptions(list);
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
								
								option.setAddPrice(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(jsonArrayEntryValuesList.get(3)), false));
								option.setEntry(option.getAddPrice());
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
}