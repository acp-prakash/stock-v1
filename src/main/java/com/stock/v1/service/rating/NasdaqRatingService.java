package com.stock.v1.service.rating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class NasdaqRatingService{

	@Autowired
	Environment appProp;
	
	@Autowired
	StockServiceDB stockServiceDB;
	
	public List<Stock> populateNasdaqRatings() {
	    List<Stock> list = new ArrayList<>();

	    System.out.println("9 - NASDAQ RATING DONE ==> AUTO");
	    
	    if(list.isEmpty())
	    	return list;
	    try {
	        List<Master> masterList = stockServiceDB.getMasterList();

	        for (Master master : masterList) {
	            String url = prepareNasdaqRatingURL(master.getTicker());
	            String response = makeHttpRequest(url);

	            if (response != null) {
	                Stock stock = extractStockFromResponse(response);
	                if (stock != null) {
	                	list.add(stock);
	                }
	            }
	        }
	    } catch (Exception ex) {
	        // Log or handle the exception appropriately
	    	System.err.println("ERROR ==> populateNasdaqRatings ==> "+ ex);
	    }
	    updateLiveStock(list);
	    return list;
	}

	private String prepareNasdaqRatingURL(String ticker) {
	    String nasdaqRatingUrl = appProp.getProperty("nasdaq.rating.url");
	    return nasdaqRatingUrl.replace("TICKER", ticker);
	}

	private String makeHttpRequest(String url) {
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = prepareHeaders();
	        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML,MediaType.APPLICATION_XML ));
			headers.add("Accept-Encoding", "gzip, deflate, br");
			headers.add("Accept-Language","en-US,en;q=0.9,ta;q=0.8");
			headers.add("Cache-Control","max-age=0");
			headers.add("Cookie", "_rdt_uuid=1694884688909.17f4c155-a3ad-4005-b864-5961a36aa3aa; _hjSessionUser_3109028=eyJpZCI6IjYyOWIyZjhlLWY2YjUtNTkzZC1hODk5LTQyM2NkZTI5MzMwNCIsImNyZWF0ZWQiOjE2OTgxMTE2NjE2NDYsImV4aXN0aW5nIjp0cnVlfQ==; _hjSessionUser_3109024=eyJpZCI6ImFiYTg4NmFhLTYxZGYtNTRmZi05YzY0LTNiYzJmZmNkNGM4NyIsImNyZWF0ZWQiOjE2OTgzNTUzODk0MjQsImV4aXN0aW5nIjp0cnVlfQ==; _hjSessionUser_1904744=eyJpZCI6IjMxNjJhOWYxLTk4MTEtNTAwZi04YjlmLWM2NzI2MjU5NDZhZSIsImNyZWF0ZWQiOjE2OTg1MzcwMjMxMzMsImV4aXN0aW5nIjp0cnVlfQ==; _mkto_trk=id:303-QKM-463&token:_mch-nasdaq.com-1698593634656-45794; _fbp=fb.1.1698593636086.1130731926; pbjs_sharedId=739735d4-1c2d-485b-bb95-bc4db1884730; _cc_id=124704f316778bf6957edc8e39672bc8; pbjs_sharedId_cst=yyzLLLEsNg%3D%3D; OptanonConsent=isGpcEnabled=0&datestamp=Fri+Nov+03+2023+22%3A20%3A36+GMT-0500+(Central+Daylight+Time)&version=202310.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&landingPath=https%3A%2F%2Fwww.nasdaq.com%2F&groups=C0004%3A1%2CC0001%3A1%2CC0003%3A1%2CC0002%3A1; _gcl_au=1.1.2070178857.1699068037; _uetvid=0263bcf054b511ee8f30957fc8eb25c7; FCNEC=%5B%5B%22AKsRol8MMK3dtUaH6H6ROZx5VS1slwn5S0kWHdkqgCn-TYcci3N4idVuGShmxsTmt-7kXYffvg6PQXrZjUqKKi66T3qXlsRe0z0dKEC8LBY6TIzZt_8iwrWLm_OU1er_58O_1Nh3liTuAOgqPVpjZjsBJRSgeHS3Zw%3D%3D%22%5D%2Cnull%2C%5B%5D%5D; __gads=ID=093c178c7d5005ff-22557601c8e70049:T=1698593630:RT=1699068036:S=ALNI_Maan5lK7h9A5w2geXqTqwonXrhcew; __gpi=UID=00000d9d6c6ebe62:T=1698593630:RT=1699068036:S=ALNI_Mbuq-wngkj2R6kJ7LHkfBa_bgDJCw; _clck=dd1bdm|2|fgf|0|1397; _biz_uid=90fd6ceac06d432b903485384b8b39f9; _biz_nA=3; _biz_flagsA=%7B%22Version%22%3A1%2C%22Mkto%22%3A%221%22%2C%22ViewThrough%22%3A%221%22%2C%22XDomain%22%3A%221%22%7D; _biz_pendingA=%5B%5D; _ga=GA1.2.2083018465.1698593634; _ga_4VZJSZ76VC=GS1.1.1699068037.2.0.1699068293.60.0.0; ak_bmsc=9C8AB9ACFFAD43272F20C695EF8A4D27~000000000000000000000000000000~YAAQG97aF0DJKpmLAQAAZB2SpxUHt4SyBQf6n7ujVbI91bgC13yXx7fqb4p4GpgAqBsiGq5olbghW3z7M94eeL76j9Cc4JgI4BNEibGTTLRoEZBKFjkj/YTh//owVZZ0B0h0EVUFJ1fhJqmr9Nde1Ly8S3Kt763Jm7s5lTmc+Zpj4YMkTJF8mEuR/LJAunqsa3fOrANMKLzZUZWBMZl7VYXwLeebkwiIhPivturLcpSp82B5cGA2Shj4MqIRHMAuvWU0uoduWzK3R0ftFkx9uDSDpzuw8fOREgE1PPsi7vLKbUOoN03N3EAosJL45uTImNCv5qEDmJfYw2xuFAqkh11wFo93B1P2b++e0MU8d+i3JI7R5fMiZPCP92ozgtdqJG5v2nkzteVpe9fGxk98kLoVZgWfuKdIjPu/xL0aOwfOPL4=; bm_sv=EA43E3686C9666DF01C6BCA02202F3C4~YAAQG97aF5rNKpmLAQAAKSiSpxVv6qo8rVbh0U6AF/VKlpTxx2UPRK5zysp6C66TmCmqhaIfRLncK8FvZDYqZ2AP0j86CuHGJWkdSEYy1Y3eUBx/Ps1C2UUiPnvgJzUEmxR0qDY6z4m6HJTKOPgfNOE4nUZiJsWaPzA1kDCgw72CUMbhT/Zw9v6F4A5EcHFFFzwAN9tfS/EgCvu1kXms4/zvVKKjC9yFFNF8VEXRYbjVI0g92tMnQ76NTS0GIFGC~1");
			headers.add("Sec-Ch-Ua", "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"");
			headers.add("Sec-Ch-Ua-Mobile", "?0");
			headers.add("Sec-Ch-Ua-Platform", "\"Windows\"");
			headers.add("Sec-Fetch-Dest", "document");
			headers.add("Sec-Fetch-Mode", "navigate");
			headers.add("Sec-Fetch-Site", "none");
			headers.add("Sec-Fetch-User", "?1");
			headers.add("Upgrade-Insecure-Requests", "1");
			headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36");
			
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
	    } catch (RestClientException ex) {
	        // Log or handle the HTTP request exception
	    	System.err.println("ERROR ==> populateNasdaqRatings ==> "+ ex);
	        return null;
	    }
	}

	private HttpHeaders prepareHeaders() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_XML));
	    // Add necessary headers here
	    // ...

	    return headers;
	}

	private Stock extractStockFromResponse(String response) {
	    try {
	        JSONObject jsonObject = new JSONObject(response);

	        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
	            JSONObject jsonDataObject = jsonObject.getJSONObject("data");

	            Stock stock = new Stock();
	            stock.setRating(new Rating());
	            stock.setTicker(UtilityService.checkForPresence(jsonDataObject, "symbol"));
	            stock.getRating().setNasdaqRating(UtilityService.checkForPresence(jsonDataObject, "meanRatingType"));
	            return stock;
	        }
	    } catch (JSONException ex) {
	        // Log or handle JSON processing exceptions
	    	System.err.println("ERROR ==> populateNasdaqRatings ==> "+ ex);
	    }

	    return null;
	}
	
	private void updateLiveStock(List<Stock> nasdaqList)
	{
		stockServiceDB.populateLiveStockList();
		List<Stock> liveList = LiveStockCache.getLiveStockList();
		if(liveList != null && !liveList.isEmpty() && nasdaqList != null && !nasdaqList.isEmpty())
		{
			liveList.stream().forEach(stock -> {
				if (stock != null) {
					nasdaqList.stream().filter(x -> x.getTicker().equalsIgnoreCase(stock.getTicker()))
					.findFirst() // Get the first matching element, if any
					.ifPresent(matchingElement -> {
						stock.getRating().setNasdaqRating(matchingElement.getRating().getNasdaqRating());						            
					});
				}
			});
		}
	}

}