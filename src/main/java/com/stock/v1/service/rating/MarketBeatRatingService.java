package com.stock.v1.service.rating;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class MarketBeatRatingService{

	@Autowired
	Environment appProp;

	@Autowired
	StockServiceDB stockServiceDB;

	private List<Stock> list = new ArrayList<>();

	public List<Stock> populateMarketBeatRatings() {

		try
		{
			list = new ArrayList<>();
			String auto = appProp.getProperty("marketbeat.rating.auto");
			if("Y".equalsIgnoreCase(auto))			
				processAuto();			
			else
				processManual();
		}
		catch(Exception ex)
		{
			System.err.println("ERROR ==> populateMarketBeatRatings ==>" + ex);
		}
		updateLiveStock(list);
		return list;
	}

	private void processAuto() throws JsonMappingException, JsonProcessingException
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie",StringUtils.isNotBlank(CookieCache.getCookie("MARKETBEAT_COOKIE"))? CookieCache.getCookie("MARKETBEAT_COOKIE"): Constants.MARKETBEAT_COOKIE);
		headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<>(headers);
	    String response = restTemplate.exchange(Constants.MARKETBEAT_URL, HttpMethod.GET, entity, String.class).getBody();
		processResponse(response);
		System.out.println("3 - MARKETBEAT RATING DONE ==> AUTO");
	}
	
	private void processManual() throws IOException
	{
		String folderName = appProp.getProperty("marketbeat.rating.folder.name");

		// Use the class loader to access resources
		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

		// Resolve resources using the folder name and ticker
		Resource[] resources = resolver.getResources(folderName + "/" + "MY.html");
		InputStream inputStream = resources[0].getInputStream();
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, resultStream);
		String htmlResult = resultStream.toString(StandardCharsets.UTF_8.name());
		processResponse(htmlResult);		
		System.out.println("3 - MARKETBEAT RATING DONE ==> MANUAL");
	}

	private void processResponse(String htmlResult)
	{
		Document doc = Jsoup.parse(htmlResult);

		// Locate the table by its tag or any identifying class or ID
		Element table = doc.select("table").first();
		
		if (table != null) {
			Elements rows = table.select("tr"); // Select all rows in the table

			for (Element row : rows) {
				Elements cells = row.select("td"); // Select cells in the row
				
				Element tdElement = row.select("td[data-sort-value]").first();
				if(tdElement == null)
					continue;

		        // Extract the value from the attribute
		        String attributeValue = tdElement.attr("data-sort-value");

				if(cells != null && cells.size() >=5)
				{
					Stock stock = new Stock();
					stock.setRating(new Rating());
					stock.setTicker(attributeValue);
					stock.getRating().setMarkbeatRating(cells.get(2).text());
					stock.getRating().setMarkbeatPT(cells.get(3).text().replace("$", ""));
					stock.getRating().setMarkbeatUpDown(cells.get(4).text());
					
					list.add(stock);
				}					
			}
			if(!list.isEmpty())
			{
				List<Master> masList = stockServiceDB.getMasterList();
				List<Master> missingList = masList.stream().filter(mas -> list.stream().noneMatch(x->x.getTicker().equalsIgnoreCase(mas.getTicker()))).collect(Collectors.toList());
				missingList.stream().forEach(x-> System.out.println(x.getTicker()));
			}
			else
				System.err.println("NO WATCHLIST");
		} else {
			System.out.println("Table not found in the HTML");
		}
	}

	private void updateLiveStock(List<Stock> marketBeatList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && marketBeatList != null && !marketBeatList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					marketBeatList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setMarkbeatPT(matchingElement.getRating().getMarkbeatPT());
						stock.getRating().setMarkbeatUpDown(matchingElement.getRating().getMarkbeatUpDown());
						stock.getRating().setMarkbeatRating(matchingElement.getRating().getMarkbeatRating());
					});
				}
			});
		}
	}
}