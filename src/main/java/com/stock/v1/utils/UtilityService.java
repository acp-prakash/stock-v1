package com.stock.v1.utils;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

public class UtilityService{
	
	public static String formatLocalDateToString(LocalDate localDate) {
	    if (localDate == null) {
	        return null; // or return an empty string or handle it as needed
	    }
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Change the format as needed
	    String formattedDate = localDate.format(formatter);
	    
	    return formattedDate;
	}

	
	public static String formatDateString(String inputDate, String inputPattern, String outputPattern) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputPattern);

            LocalDate date = LocalDate.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing the date: " + e.getMessage());
            return ""; // Return an empty string or handle the error as needed
        }
    }
			
	public static List<Stock> readCsvFileForStockHistory(String filePath, String ticker) {
        List<Stock> list = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            List<String[]> csvData = csvReader.readAll();

            for (String[] arr : csvData) {
                Stock stock = createStockFromCsvRow(arr);
                stock.setTicker(ticker.toUpperCase());
                list.add(stock);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

        return list;
    }
	
    private static Stock createStockFromCsvRow(String[] csvRow) {
        Stock stock = new Stock();
        stock.setRating(new Rating());
        if (csvRow.length >= 6) {
            stock.setDate(formatDateString(csvRow[0], "MM/dd/yyyy", "yyyy-MM-dd"));
            stock.setPrice(csvRow[1].replace("$", ""));
            stock.setVolume(csvRow[2]);
            stock.setOpen(csvRow[3].replace("$", ""));
            stock.setHigh(csvRow[4].replace("$", ""));
            stock.setLow(csvRow[5].replace("$", ""));
        }
        return stock;
    }
    
    public static String checkForPresence(JSONObject jsonObject, String key) {
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            Object value = jsonObject.get(key);

            if (value instanceof Number || value instanceof Boolean) {
                return String.valueOf(value);
            } else if (value instanceof String) {
                return (String) value;
            }
        }
        return StringUtils.EMPTY;
    }
    
    public static String checkForPresenceNoKey(Object jsonObject)
	{
		if(jsonObject != null && !JSONObject.NULL.equals(jsonObject))
		{			
			if (jsonObject instanceof Number || jsonObject instanceof Boolean) {
                return String.valueOf(jsonObject);
            } else if (jsonObject instanceof String) {
                return (String) jsonObject;
            }
		}
		return StringUtils.EMPTY;
	}
    
    public static String stripStringToTwoDecimals(String input, boolean multiply) {
        try {
        	
        	if(StringUtils.isBlank(input) || "unch".equalsIgnoreCase(input))
        		return input;
        	input = input.trim();
            // Parse the input string to a double
            double value = Double.parseDouble(input);
            if(multiply)
            	value = value * 100;            	

            // Create a DecimalFormat object with the desired format
            DecimalFormat df = new DecimalFormat("#.##");

            // Format the double to a string with two decimal places
            return df.format(value);
        } catch (NumberFormatException e) {
            // Handle parsing errors
            System.err.println("Error parsing the input: " + e.getMessage());
            return input; // Return the original input string if parsing fails
        }
    }
    
    public static String getInvestingComRating(String score)
    {
    	if(StringUtils.isBlank(score) || "N/A".equalsIgnoreCase(score))
    		return score;
    	
    	double value = Double.parseDouble(score);
    	if(value <= 1.5)
    		return "Strong Buy";
    	else if(value <= 2.5)
    		return "Buy";
    	else if(value <= 3.5)
    		return "Hold";
    	else if(value <= 4.5)
    		return "Sell";
    	else
    		return "Strong Sell";    	
    }
    
    public static String getSeekingAlphaRating(String score)
    {
    	if(StringUtils.isBlank(score) || "N/A".equalsIgnoreCase(score))
    		return score;
    	
    	double value = Double.parseDouble(score);
    	if(value >= 4.5)
    		return "Strong Buy";
    	else if(value >= 3.5)
    		return "Buy";
    	else if(value >= 2.5)
    		return "Hold";
    	else if(value >= 1.5)
    		return "Sell";
    	else
    		return "Strong Sell";    	
    }
    
    public static String getTradingViewRating(String score, String type)
    {    	
    	if(StringUtils.isBlank(score) || "N/A".equalsIgnoreCase(score))
    		return score;
	
    	double value = Double.parseDouble(score);
    	
    	if("ANALYST".equalsIgnoreCase(type))
    	{
    		if(value < 1.25)
    			return "Strong Buy";
    		else if(value < 1.75)
    			return "Buy";
    		else if(value < 2.25)
    			return "Hold";
    		else if(value < 2.75)
    			return "Sell";
    		else if(value <= 3)
    			return "Strong Sell";
    		else
    			return "Hold";
    	}
    	else
    	{
    		if(value  >= -1 && value < -.5)
    			return "Strong Sell";
    		else if(value >= -.5 && value < -.1)
    			return "Sell";
    		else if(value >= -.1 && value <= .1)
    			return "Hold";
    		else if(value > .1 && value <= .5)
    			return "Buy";
    		else if(value > .5 && value <= 1)
    			return "Strong Buy";
    		else
    			return "Hold";    		
    	}
    }
    
    public static String getPortfolio123Rating(String score)
    {
    	if(StringUtils.isBlank(score) || "N/A".equalsIgnoreCase(score))
    		return score;
    	
    	double value = Double.parseDouble(score);
    	if(value <= 1)
    		return "Strong Buy";
    	else if(value <= 2)
    		return "Buy";
    	else if(value <= 3)
    		return "Hold";
    	else if(value <= 4)
    		return "Sell";
    	else
    		return "Strong Sell";    	
    }
    
    public static String compareDates(String date1, String date2)
    {
    	// Define the date formatter
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    	// Parse the strings into LocalDate objects
    	LocalDate d1 = LocalDate.parse(date1, formatter);
    	LocalDate d2 = LocalDate.parse(date2, formatter);

    	// Compare the dates
    	if (d1.isAfter(d2))
    		return "A";
    	else if (d1.isBefore(d2))
    		return "B";
    	else if (d1.isEqual(d2))
    		return "E";
    	else
    		return "";
    }
    
    public static boolean excludedTick(String ticker)
    {
    	if("ESH24".equalsIgnoreCase(ticker) || 
				"ESM24".equalsIgnoreCase(ticker) || "ESU24".equalsIgnoreCase(ticker) || 
				"ESZ24".equalsIgnoreCase(ticker) || "ESH25".equalsIgnoreCase(ticker) || 
				"ESM25".equalsIgnoreCase(ticker) || "ESU25".equalsIgnoreCase(ticker) || 
				"ESZ25".equalsIgnoreCase(ticker) || "ESH26".equalsIgnoreCase(ticker) || 
				"ESM26".equalsIgnoreCase(ticker) || "ESU26".equalsIgnoreCase(ticker) ||
				"ESZ26".equalsIgnoreCase(ticker) || "QRH24".equalsIgnoreCase(ticker) || 
				"QRM24".equalsIgnoreCase(ticker) || "QRU24".equalsIgnoreCase(ticker) || 
				"QRZ24".equalsIgnoreCase(ticker) || "QRH25".equalsIgnoreCase(ticker) ||
				"$VIX".equalsIgnoreCase(ticker)  || "VIH24".equalsIgnoreCase(ticker) ||
				"VIJ24".equalsIgnoreCase(ticker)  || "VIK24".equalsIgnoreCase(ticker) ||
				"VIM24".equalsIgnoreCase(ticker)  || "VIN24".equalsIgnoreCase(ticker) ||
				"VIQ24".equalsIgnoreCase(ticker)  || "VIU24".equalsIgnoreCase(ticker) ||				
				"EWH24".equalsIgnoreCase(ticker)  || "EWM24".equalsIgnoreCase(ticker) ||
				"EWU24".equalsIgnoreCase(ticker)  || "EWZ24".equalsIgnoreCase(ticker) ||
				"EWH25".equalsIgnoreCase(ticker)  ||				
				"VIV24".equalsIgnoreCase(ticker)  || "VIX24".equalsIgnoreCase(ticker))    		
    		return true;
    	return false;
    }
}