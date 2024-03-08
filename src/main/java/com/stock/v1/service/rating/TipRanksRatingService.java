package com.stock.v1.service.rating;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
public class TipRanksRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateTipRankRatings() {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("tipranks.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateTipRankRatings ==>" + ex);
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
	    String response = restTemplate.exchange(Constants.TIPRANKS_URL, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		System.out.println("4 - TIPRANKS RATING DONE ==> AUTO");
	}
	
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("tipranks.rating.folder.name");

		// Use the class loader to access resources
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

		// Resolve resources using the folder name and ticker
		Resource[] resources = resolver.getResources(folderName + "/" + "tiprating.json");
		InputStream inputStream = resources[0].getInputStream();
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, resultStream);
		String tipRankJson = resultStream.toString(StandardCharsets.UTF_8.name());
		processResponse(tipRankJson);		
		System.out.println("4 - TIPRANKS RATING DONE ==> MANUAL");
	}

	private void processResponse(String tipRankJson)
	{
		if(StringUtils.isNotBlank(tipRankJson))
		{
			JSONObject jsonObject = new JSONObject(tipRankJson);

			if(jsonObject.has("data"))
			{
				JSONArray jsonArray = (JSONArray)jsonObject.get("data");
				for (int i = 0, size = jsonArray.length(); i < size; i++)
				{						
					JSONObject jsonDataObj = jsonArray.getJSONObject(i);
					if(jsonDataObj != null)
					{
						Stock stock = new Stock();
						stock.setRating(new Rating());
						stock.setTicker(UtilityService.checkForPresence(jsonDataObj, "ticker"));
						if(jsonDataObj.has("analystConsensus") && !jsonDataObj.isNull("analystConsensus"))
						{
							JSONObject jsonAnalystObj = (JSONObject)jsonDataObj.get("analystConsensus");
							stock.getRating().setTipRating(UtilityService.checkForPresence(jsonAnalystObj, "consensus"));
							if(jsonAnalystObj.has("distribution"))
							{
								JSONObject jsonDistObj = (JSONObject)jsonAnalystObj.get("distribution");
								stock.getRating().setTipBuyHoldSell(UtilityService.checkForPresence(jsonDistObj, "buy"));
								stock.getRating().setTipBuyHoldSell(stock.getRating().getTipBuyHoldSell() +"*"+ UtilityService.checkForPresence(jsonDistObj, "hold"));
								stock.getRating().setTipBuyHoldSell(stock.getRating().getTipBuyHoldSell() +"*"+ UtilityService.checkForPresence(jsonDistObj, "sell"));
								list.add(stock);
							}
						}
					}
				}					
			}
		}
	}

	private void updateLiveStock(List<Stock> tipList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && tipList != null && !tipList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					tipList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setTipRating(matchingElement.getRating().getTipRating());
						stock.getRating().setTipBuyHoldSell(matchingElement.getRating().getTipBuyHoldSell());
					});
				}
			});
		}
	}
}