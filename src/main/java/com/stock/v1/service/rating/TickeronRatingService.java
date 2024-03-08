package com.stock.v1.service.rating;

import java.io.BufferedReader;
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
import org.springframework.stereotype.Service;

import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class TickeronRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;

	public List<Stock> populateTickeronRatings() {

		List<Stock> list = new ArrayList<>();
		try
		{
			String folderName = appProp.getProperty("tickeron.rating.folder.name");

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
				if(StringUtils.isNotBlank(json))
				{
					JSONObject jsonObject = new JSONObject(json);

					if(jsonObject.has("Rows") && !jsonObject.isNull("Rows"))
					{
						JSONArray jsonArray = (JSONArray)jsonObject.get("Rows");
						for (int i = 0, size = jsonArray.length(); i < size; i++)
						{
							JSONObject jsonArrObj = jsonArray.getJSONObject(i);
							if(jsonArrObj != null)
							{
								Stock stock = new Stock();
								stock.setRating(new Rating());
								
								if(jsonArrObj.has("Common") && !jsonArrObj.isNull("Common"))
								{
									JSONObject jsonArrCommon = jsonArrObj.getJSONObject("Common");
									stock.setTicker(UtilityService.checkForPresence(jsonArrCommon, "Ticker"));
									stock.getRating().setTickeronRating(UtilityService.checkForPresence(jsonArrCommon, "BuyHoldSellScore"));
									stock.getRating().setTickeronRatingAt(UtilityService.checkForPresence(jsonArrCommon, "BuyHoldSellScorePrice"));
									stock.getRating().setTickeronRatingOn(UtilityService.checkForPresence(jsonArrCommon, "BuyHoldSellScoreTime"));
									if(StringUtils.isNotBlank(stock.getRating().getTickeronRatingOn()) && 
											stock.getRating().getTickeronRatingOn().length() >=10)
										stock.getRating().setTickeronRatingOn(stock.getRating().getTickeronRatingOn().substring(0, 10));
									stock.getRating().setTickeronAIRating(UtilityService.checkForPresence(jsonArrCommon, "AI_BuyHoldSell"));
									stock.getRating().setTickeronUnderOver(UtilityService.checkForPresence(jsonArrObj, "UnderOverFair"));
									list.add(stock);
								}
							}
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateTickeronRatings ==> "+ ex);
		}
		updateLiveStock(list);
		System.out.println("16 - TICKERON RATING DONE ==> MANUAL");
		return list;
	}
	
	private void updateLiveStock(List<Stock> tickeronList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && tickeronList != null && !tickeronList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					tickeronList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setTickeronRating(matchingElement.getRating().getTickeronRating());						            						            
						stock.getRating().setTickeronRatingAt(matchingElement.getRating().getTickeronRatingAt());
						stock.getRating().setTickeronRatingOn(matchingElement.getRating().getTickeronRatingOn());
						stock.getRating().setTickeronAIRating(matchingElement.getRating().getTickeronAIRating());
						stock.getRating().setTickeronUnderOver(matchingElement.getRating().getTickeronUnderOver());
					});
				}
			});
		}
	}
}