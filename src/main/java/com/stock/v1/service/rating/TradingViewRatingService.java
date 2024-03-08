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
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class TradingViewRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;

	public List<Stock> populateTradingViewRatings() {

		List<Stock> list = new ArrayList<>();
		try
		{
			list = new ArrayList<>();
			String folderName = appProp.getProperty("tradingview.rating.folder.name");

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
					if(jsonObject.has("data") && !jsonObject.isNull("data"))
					{
						List<Master> masterList = stockServiceDB.getMasterList();
						JSONArray jsonArray = (JSONArray)jsonObject.get("data");
						for (int i = 0, size = jsonArray.length(); i < size; i++)
						{
							JSONObject jsonDataObj = jsonArray.getJSONObject(i);
							if(jsonDataObj != null && jsonDataObj.has("d") && !jsonDataObj.isNull("d"))
							{
								JSONArray dArray = (JSONArray)jsonDataObj.get("d");
								Stock stock = new Stock();
								stock.setRating(new Rating());								
								stock.setTicker(UtilityService.checkForPresenceNoKey(dArray.get(0)));
								if(masterList.stream().anyMatch(x->x.getTicker().equalsIgnoreCase(stock.getTicker())))
								{
									stock.getRating().setTradingViewAnalystsRating(UtilityService.getTradingViewRating(UtilityService.checkForPresenceNoKey(dArray.get(4)), "ANALYST"));
									stock.getRating().setTradingViewTechRating(UtilityService.getTradingViewRating(UtilityService.checkForPresenceNoKey(dArray.get(5)),null));								
									stock.getRating().setTradingViewMARating(UtilityService.getTradingViewRating(UtilityService.checkForPresenceNoKey(dArray.get(6)),null));
									stock.getRating().setTradingViewOSRating(UtilityService.getTradingViewRating(UtilityService.checkForPresenceNoKey(dArray.get(7)),null));
									stock.getRating().setTradingViewBullBearPower(UtilityService.stripStringToTwoDecimals(UtilityService.checkForPresenceNoKey(dArray.get(8)), false));
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
			System.err.println("ERROR ==> populateTradingViewRatings ==> "+ ex);
		}
		updateLiveStock(list);
		System.out.println("15 - TRADING VIEW RATING DONE ==> MANUAL");
		return list;
	}
	
	private void updateLiveStock(List<Stock> tradingViewList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && tradingViewList != null && !tradingViewList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					tradingViewList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setTradingViewAnalystsRating(matchingElement.getRating().getTradingViewAnalystsRating());						            						            
						stock.getRating().setTradingViewTechRating(matchingElement.getRating().getTradingViewTechRating());
						stock.getRating().setTradingViewMARating(matchingElement.getRating().getTradingViewMARating());
						stock.getRating().setTradingViewOSRating(matchingElement.getRating().getTradingViewOSRating());
						stock.getRating().setTradingViewBullBearPower(matchingElement.getRating().getTradingViewBullBearPower());
					});
				}
			});
		}
	}
}