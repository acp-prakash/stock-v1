package com.stock.v1.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.stock.v1.service.db.StockServiceDB;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class StockHistoryService{

	@Autowired
	StockServiceDB stockServiceDB;

	@Autowired
	StockService stockService;

	@Autowired
	private Environment appProp;

	public List<Stock> getStockHistory(String ticker) {
		return stockServiceDB.getStockHistory(ticker);
	}

	public List<Stock> getStockHistoryByDate(String ticker, String date) {
		return stockServiceDB.getStockHistoryByDate(ticker,date);
	}

	public String populateInitialStockHistory(String ticker, boolean isEtf){
		try
		{
			String urlString = appProp.getProperty("history.prices.stocks").replace("TICKER",ticker);
			if(isEtf)
				urlString = appProp.getProperty("history.prices.etf").replace("TICKER",ticker);
				
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set headers
			connection.setRequestProperty("Accept", "application/json, text/plain, */*");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,ta;q=0.8");
			connection.setRequestProperty("Origin", "https://www.nasdaq.com");
			connection.setRequestProperty("Priority", "u=1, i");
			connection.setRequestProperty("Referer", "https://www.nasdaq.com/");
			connection.setRequestProperty("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"");
			connection.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
			connection.setRequestProperty("Sec-Ch-Ua-Platform", "\"Windows\"");
			connection.setRequestProperty("Sec-Fetch-Dest", "empty");
			connection.setRequestProperty("Sec-Fetch-Mode", "cors");
			connection.setRequestProperty("Sec-Fetch-Site", "same-site");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");

			// Connect
			connection.connect();

			// Get response stream
			InputStream inputStream = connection.getInputStream();
			String encoding = connection.getContentEncoding();

			// Handle decompression if needed
			if ("gzip".equalsIgnoreCase(encoding)) {
				inputStream = new GZIPInputStream(inputStream);
			} else if ("deflate".equalsIgnoreCase(encoding)) {
				inputStream = new java.util.zip.InflaterInputStream(inputStream);
			} else if ("br".equalsIgnoreCase(encoding)) {
				//inputStream = new org.brotli.dec.BrotliInputStream(inputStream);
			} // zstd support is not included in the JDK, so an external library is needed for this.

			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// Print response
			System.out.println(response.toString());
			List<Stock> list = new ArrayList<>();
			if(response.toString().contains("Symbol not exists"))
				populateInitialStockHistory(ticker, true);
			else
				list = readStockHistory(response.toString());

			// Check if the list is not empty
			if (list != null && !list.isEmpty()) {
				List<Stock> histList = stockServiceDB.getStockHistory(ticker);
				if(histList != null && !histList.isEmpty())
				{
					List<String> histDates = histList.stream()
							.map(Stock::getDate)
							.collect(Collectors.toList());
					list.removeIf(stock -> histDates.contains(stock.getDate()));
				}
				// Add stock history to the database and return the result
				if (stockServiceDB.addToStockHistory(list)) {
					return "SUCCESS";
				}
			}
		}catch (Exception ex) {
			System.err.println(ticker + " <populateInitialStockHistory> " + ex);
			return "FAILURE";
		}
		return "SUCCESS";		
	}

	public String populateInitialStockHistoryAll() {
		try {
			// Get the list of Master objects
			List<Master> masterList = stockService.getMasterList();

			// Iterate through the Master list
			for (Master master : masterList) {
				// Call populateInitialStockHistory for each Ticker
				populateInitialStockHistory(master.getTicker(), false);
			}
			return "SUCCESS";
		} catch (Exception ex) {
			System.err.println("ERROR ==> populateInitialStockHistoryAll ==> "+ ex);
		}
		return "FAILURE";
	}

	private List<Stock> readStockHistory(String jsonResponse) {
		List<Stock> list = new ArrayList<>();

		if(StringUtils.isNotBlank(jsonResponse))
		{
			JSONObject json = new JSONObject(jsonResponse);
			if(json.has("data") && !json.isNull("data"))
			{
				JSONObject jsonData = (JSONObject)json.get("data");
				if(jsonData.has("tradesTable") && !jsonData.isNull("tradesTable"))
				{
					JSONObject jsonTradesTable = (JSONObject)jsonData.get("tradesTable");
					if(jsonTradesTable.has("rows") && !jsonTradesTable.isNull("rows"))
					{
						JSONArray array =  jsonTradesTable.getJSONArray("rows");
						for (int i = 0; i < array.length(); i++) {
							Stock stock = new Stock();
							stock.setRating(new Rating());
							stock.setTicker(UtilityService.checkForPresence(jsonData, "symbol"));
							stock.setDate(UtilityService.checkForPresence(array.getJSONObject(i), "date"));
							stock.setDate(UtilityService.formatDateString(stock.getDate(), "MM/dd/yyyy", "yyyy-MM-dd"));
							stock.setPrice(UtilityService.checkForPresence(array.getJSONObject(i), "close").replace("$", ""));
							stock.setVolume(UtilityService.checkForPresence(array.getJSONObject(i), "volume").replace(",", ""));
							stock.setOpen(UtilityService.checkForPresence(array.getJSONObject(i), "open").replace("$", ""));
							stock.setHigh(UtilityService.checkForPresence(array.getJSONObject(i), "high").replace("$", ""));
							stock.setLow(UtilityService.checkForPresence(array.getJSONObject(i), "low").replace("$", ""));
							list.add(stock);
						}
					}
				}
			}
		}

		return list;
	}
}