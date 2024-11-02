package com.stock.v1.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.v1.cache.CookieCache;
import com.stock.v1.service.db.PatternServiceDB;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Pattern;

@Service
public class PatternService{

	@Autowired
	Environment appProp;
	
	@Autowired
	PatternServiceDB patternServiceDB;	
	
	public List<Pattern> getPatternHistory(String ticker)
	{
		return patternServiceDB.getPatternHistory(ticker);
	}	
	public void deletePattern(List<String> tickerList)
	{
		patternServiceDB.deletePattern(tickerList);
	}
	
	public void addToPattern(List<Pattern> list)
	{
		patternServiceDB.addToPattern(list);		
	}	
	
	public List<Pattern> fetchPatternDetails(String ticker)
	{
		System.out.println("START -> fetchPatternDetails: " + new Date());
		if(UtilityService.excludedTick(ticker))
			return new ArrayList<>();
		String url = Constants.ETRADE_PATTRN_URL
				.replace("TICKER", ticker).replace("----",",");
		
		url = url + CookieCache.getCookie("ETRADE_TOKEN");

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
		HttpEntity <String> entity = new HttpEntity<>(headers);

		System.out.println(url);
		String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
		List<Pattern> list = new ArrayList<>();

		if(StringUtils.isNotBlank(response))
		{
			JSONObject json = new JSONObject(response);
			if(json.has("events") && !json.isNull("events"))
			{
				JSONArray array =  json.getJSONArray("events");
				for (int i = 0; i < array.length(); i++) {
			        JSONObject event = array.getJSONObject(i);
			        if(UtilityService.checkForPresence(event, "active") == "true")
			        {
			        	Pattern pattern = new Pattern();
			        	pattern.setTicker(ticker);
			        	pattern.setHistDate(UtilityService.formatLocalDateToString(LocalDate.now()));
			        	pattern.setStop(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(event, "deactivationPrice"), false));
			        	pattern.setId(UtilityService.checkForPresence(event, "eventId"));
			        	pattern.setName(UtilityService.checkForPresence(event, "eventLabel"));
			        	pattern.setTargetDate(UtilityService.checkForPresence(event, "lastPossibleActive"));
			        	if(StringUtils.isNotBlank(pattern.getTargetDate()) && pattern.getTargetDate().length() > 10)
			        		pattern.setTargetDate(pattern.getTargetDate().substring(0, 10));
			        	pattern.setStatus("Y");
			        	if(event.has("endPrices") && !event.isNull("endPrices"))
			        	{
			        		 JSONObject endPrices = (JSONObject) event.get("endPrices");
			        		 pattern.setEntry(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(endPrices, "breakout"), false));
			        	}
			        	if(event.has("eventType") && !event.isNull("eventType"))
			        	{
			        		 JSONObject eventType = (JSONObject) event.get("eventType");			        		 
			        		 pattern.setTrend(UtilityService.checkForPresence(eventType, "tradeType"));
			        	}
			        	if(event.has("targetPrice") && !event.isNull("targetPrice"))
			        	{
			        		 JSONObject targetPrice = (JSONObject) event.get("targetPrice");			        		 
			        		 pattern.setMinPT(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(targetPrice, "lower"), false));
			        		 pattern.setMaxPT(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresence(targetPrice, "upper"), false));
			        	}
			        	list.add(pattern);
			        }
				}
			}
		}		
		System.out.println("END -> fetchPatternDetails: " + ticker + ", COUNT - " + list.size() +" " +new Date());
		return list;
	}
}