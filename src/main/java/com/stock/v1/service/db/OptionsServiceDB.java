package com.stock.v1.service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stock.v1.utils.DBConstants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Options;

@Service
public class OptionsServiceDB{

	@Autowired
	@Qualifier("ihelpJdbcTemplate")
	private JdbcTemplate ihelpJdbcTemplate;

	public List<Options> getOptions()
	{
		String sql = DBConstants.GET_OPTIONS;		
		List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(sql);

		return retResultMap.stream()
				.map(this::mapToOptions)
				.filter(options -> options != null)  // Filter out any null results
				.collect(Collectors.toList());
	}	

	private Options mapToOptions(Map<String, Object> retRes) {
		Options options = new Options();
		retRes.forEach((key, value) -> {
			if (value != null) {
				switch (key.toUpperCase()) {	              
				case "KEY":
					options.setKey((String) value);
					break;
				case "HIST_DATE":
					options.setHistDate((String) value);
					break;	                
				case "TICKER":
					options.setTicker((String) value);
					break;
				case "NAME":
					options.setName((String) value);
					break;				
				case "TYPE":
					options.setType((String) value);
					break;
				case "ENTRY":
					options.setEntry((String) value);
					break;
				case "EXIT":
					options.setExit((String) value);
					break;
				case "PRICE":
					options.setPrice((String) value);
					break;
				case "CHANGE":
					options.setChange((String) value);
					break;
				case "OPEN":
					options.setOpen((String) value);
					break;
				case "HIGH":
					options.setHigh((String) value);
					break;
				case "LOW":
					options.setLow((String) value);
					break;
				case "VOLUME":
					options.setVolume((String) value);
					break;
				case "INTEREST":
					options.setInterest((String) value);
					break;
				case "PRICE_ON_ADD":
					options.setAddPrice((String) value);
					break;
				case "ADDED_DATE":
					options.setAddedDate((String) value);
					break;
				case "STATUS":
					options.setStatus((String) value);
					break;
				case "SOURCE":
					options.setSource((String) value);
					break;
				case "EXIT_DATE":
					options.setExitDate((String) value);
					break;				
				case "DAYS":
					options.setDaysToExpire((String) value);
					break;
				case "DELTA":
					options.setDelta((String) value);
					break;
				case "THETA":
					options.setTheta((String) value);
					break;
				case "GAMMA":
					options.setGamma((String) value);
					break;
				case "IV":
					options.setIv((String) value);
					break;
				default:
					break;  // Handle other keys if needed
				}
			}
		});
		return options;
	}

	public List<Options> getOptionsHistory(String key)
	{
		String sql = DBConstants.GET_OPTIONS_HISTORY;
		
		if(StringUtils.isNotBlank(key))
			sql = sql + " where key='" +key+ "' order by hist_date desc";
		else
			sql = sql + " order by key, hist_date desc";
			
		List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(sql);

		return retResultMap.stream()
				.map(this::mapToOptions)
				.filter(options -> options != null)  // Filter out any null results
				.collect(Collectors.toList());
	}
	
	public void mergeOptions(List<Options> existingOptions, List<Options> addList) {
	    if (existingOptions != null && !existingOptions.isEmpty() &&
	    		addList != null && !addList.isEmpty()) {

	        // Create a map for faster lookups of existing options by key
	        Map<String, Options> existingOptionsMap = new HashMap<>();
	        for (Options ext : existingOptions) {
	            existingOptionsMap.put(ext.getKey().toLowerCase(), ext);
	        }

	        // List to collect new options that don't exist in the map
	        List<Options> newOptions = new ArrayList<>();

	        // Iterate through optionsList and update existing or add new
	        for (Options add : addList) {
	            Options existingOption = existingOptionsMap.get(add.getKey().toLowerCase());
	            if (existingOption != null) {
	                // Update existing option
	                existingOption.setPrice(add.getPrice());
	                existingOption.setChange(add.getChange());
	                existingOption.setLow(add.getLow());
	                existingOption.setHigh(add.getHigh());
	                existingOption.setOpen(add.getOpen());
	                existingOption.setVolume(add.getVolume());
	                existingOption.setInterest(add.getInterest());
	                existingOption.setDelta(add.getDelta());
	                existingOption.setTheta(add.getTheta());
	                existingOption.setGamma(add.getGamma());
	                existingOption.setIv(add.getIv());
	                existingOption.setDaysToExpire(add.getDaysToExpire());
	            } else {
	                // Collect new option
	                newOptions.add(add);
	            }
	        }

	        // Add all new options to the existing options list
	        if (!newOptions.isEmpty()) {
	            existingOptions.addAll(newOptions);
	        }
	    }
	}
	
	public List<Options> mergeOptionsForUpdate(List<Options> existingOptions, List<Options> updatedList) {
	    List<Options> toBeUpdatedList = new ArrayList<>();
		if (existingOptions != null && !existingOptions.isEmpty() &&
	    		updatedList != null && !updatedList.isEmpty()) {

	        // Create a map for faster lookups of existing options by key
	        Map<String, Options> existingOptionsMap = new HashMap<>();
	        for (Options ext : existingOptions) {
	            existingOptionsMap.put(ext.getKey().toLowerCase(), ext);
	        }

	        // Iterate through optionsList and update existing or add new
	        for (Options update : updatedList) {
	            Options existingOption = existingOptionsMap.get(update.getKey().toLowerCase());
	            if (existingOption != null) {
	                // Update existing option
	                existingOption.setAddPrice(update.getAddPrice());
	                existingOption.setEntry(update.getAddPrice());
	                existingOption.setExit(update.getExit());
	                existingOption.setStatus(update.getStatus());
	                existingOption.setExitDate(update.getExitDate());
	                existingOption.setSource(update.getSource());
	                toBeUpdatedList.add(existingOption);
	            }
	        }
	    }
		return toBeUpdatedList;
	}

	public boolean addToOptions(List<Options> addList) {
		String sql = "insert into STOCK_OPTIONS (KEY, TICKER, NAME, TYPE, PRICE, CHANGE, OPEN,"
				+ "HIGH, LOW, VOLUME, INTEREST, PRICE_ON_ADD, ADDED_DATE, DAYS, DELTA, THETA, GAMMA, IV,"
				+ "STATUS) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		List<Options> existingOptions = getOptions();
		if(existingOptions != null && !existingOptions.isEmpty())
		{
			mergeOptions(existingOptions, addList);
		}

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (Options option : existingOptions) {
				deleteFromOptions(option.getKey());
				int i = 1;
				ps.setString(i, option.getKey());i++;
				ps.setString(i, option.getTicker());i++;
				ps.setString(i, option.getName());i++;
				ps.setString(i, option.getType());i++;
				ps.setString(i, option.getPrice());i++;
				ps.setString(i, option.getChange());i++;
				ps.setString(i, option.getOpen());i++;
				ps.setString(i, option.getHigh());i++;
				ps.setString(i, option.getLow());i++;
				ps.setString(i, option.getVolume());i++;
				ps.setString(i, option.getInterest());i++;
				ps.setString(i, option.getAddPrice());i++;
				ps.setString(i, option.getAddedDate());i++;
				ps.setString(i, option.getDaysToExpire());i++;
				ps.setString(i, option.getDelta());i++;
				ps.setString(i, option.getTheta());i++;
				ps.setString(i, option.getGamma());i++;
				ps.setString(i, option.getIv());i++;
				ps.setString(i, option.getStatus());i++;

				ps.addBatch();
			}

			int[] batchResult = ps.executeBatch();

			// Check if all the batch statements were successful
			for (int result : batchResult) {
				if (result == PreparedStatement.EXECUTE_FAILED) {
					return false;
				}
			}
			addToOptionsHistory(existingOptions);
			return true;
		} catch (SQLException ex) {
			System.err.println("ERROR ==> addToOptions ==> "+ ex);
			return false;
		}
	}
	
	public List<Options> updateOptions(List<Options> updatedList)
	{
		List<Options> toBeUpdatedList = new ArrayList<>();
		String sql = "insert into STOCK_OPTIONS (KEY, TICKER, NAME, TYPE, PRICE, CHANGE, OPEN,"
				+ "HIGH, LOW, VOLUME, INTEREST, PRICE_ON_ADD, ADDED_DATE, DAYS, DELTA, THETA, GAMMA, IV,"
				+ "STATUS, ENTRY, EXIT, SOURCE, EXIT_DATE) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		List<Options> existingOptions = getOptions();
		
		if(existingOptions != null && !existingOptions.isEmpty())
		{
			toBeUpdatedList = mergeOptionsForUpdate(existingOptions, updatedList);
		}
		if(toBeUpdatedList != null && !toBeUpdatedList.isEmpty())
		{
			try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
					PreparedStatement ps = conn.prepareStatement(sql)) {

				for (Options option : updatedList) {
					deleteFromOptions(option.getKey());
					int i = 1;
					ps.setString(i, option.getKey());i++;
					ps.setString(i, option.getTicker());i++;
					ps.setString(i, option.getName());i++;
					ps.setString(i, option.getType());i++;
					ps.setString(i, option.getPrice());i++;
					ps.setString(i, option.getChange());i++;
					ps.setString(i, option.getOpen());i++;
					ps.setString(i, option.getHigh());i++;
					ps.setString(i, option.getLow());i++;
					ps.setString(i, option.getVolume());i++;
					ps.setString(i, option.getInterest());i++;
					ps.setString(i, option.getAddPrice());i++;
					ps.setString(i, option.getAddedDate());i++;
					ps.setString(i, option.getDaysToExpire());i++;
					ps.setString(i, option.getDelta());i++;
					ps.setString(i, option.getTheta());i++;
					ps.setString(i, option.getGamma());i++;
					ps.setString(i, option.getIv());i++;
					ps.setString(i, option.getStatus());i++;				
					ps.setString(i, option.getEntry());i++;
					ps.setString(i, option.getExit());i++;
					ps.setString(i, option.getSource());i++;
					ps.setString(i, option.getExitDate());i++;
					ps.addBatch();
				}

				int[] batchResult = ps.executeBatch();

				// Check if all the batch statements were successful
				/*
				 * for (int result : batchResult) { if (result ==
				 * PreparedStatement.EXECUTE_FAILED) { return false; } }
				 */
				addToOptionsHistory(toBeUpdatedList);				
			} catch (SQLException ex) {
				System.err.println("ERROR ==> addToOptions ==> "+ ex);				
			}
		}
		return existingOptions;
	}
	
	public boolean addToOptionsHistory(List<Options> addList) {
		String sql = "insert into STOCK_OPTIONS_HISTORY (KEY,HIST_DATE, TICKER, NAME, TYPE, PRICE, CHANGE, OPEN,"
				+ "HIGH, LOW, VOLUME, INTEREST, PRICE_ON_ADD, ADDED_DATE, DAYS, DELTA, THETA, GAMMA, IV, STATUS, ENTRY, EXIT, SOURCE, EXIT_DATE) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (Options option : addList) {
				deleteFromOptionsHistory(option.getKey(), UtilityService.formatLocalDateToString(LocalDate.now()));
				int i = 1;
				ps.setString(i, option.getKey());i++;
				ps.setString(i, UtilityService.formatLocalDateToString(LocalDate.now()));i++;
				ps.setString(i, option.getTicker());i++;
				ps.setString(i, option.getName());i++;
				ps.setString(i, option.getType());i++;
				ps.setString(i, option.getPrice());i++;
				ps.setString(i, option.getChange());i++;
				ps.setString(i, option.getOpen());i++;
				ps.setString(i, option.getHigh());i++;
				ps.setString(i, option.getLow());i++;
				ps.setString(i, option.getVolume());i++;
				ps.setString(i, option.getInterest());i++;
				ps.setString(i, option.getAddPrice());i++;
				ps.setString(i, option.getAddedDate());i++;
				ps.setString(i, option.getDaysToExpire());i++;
				ps.setString(i, option.getDelta());i++;
				ps.setString(i, option.getTheta());i++;
				ps.setString(i, option.getGamma());i++;
				ps.setString(i, option.getIv());i++;
				ps.setString(i, option.getStatus());i++;				
				ps.setString(i, option.getEntry());i++;
				ps.setString(i, option.getExit());i++;
				ps.setString(i, option.getSource());i++;
				ps.setString(i, option.getExitDate());i++;

				ps.addBatch();
			}

			int[] batchResult = ps.executeBatch();

			// Check if all the batch statements were successful
			for (int result : batchResult) {
				if (result == PreparedStatement.EXECUTE_FAILED) {
					return false;
				}
			}
			return true;
		} catch (SQLException ex) {
			System.err.println("ERROR ==> addToOptionsHistory ==> "+ ex);
			return false;
		}
	}
	
	public boolean deleteFromOptions(String key) {
	    String sql = "DELETE FROM STOCK_OPTIONS WHERE KEY = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, key);	       
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> deleteFromOptions ==> "+ ex);
	        return false;
	    }
	}
	
	public boolean deleteFromOptionsHistory(String key, String histDate) {
	    String sql = "DELETE FROM STOCK_OPTIONS_HISTORY WHERE KEY = ? and HIST_DATE = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, key);
	        ps.setString(2, histDate);
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> deleteFromOptionsHistory ==> "+ ex);
	        return false;
	    }
	}
	
	public boolean deleteFromOptionsHistory(String key) {
	    String sql = "DELETE FROM STOCK_OPTIONS_HISTORY WHERE KEY = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, key);
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> deleteFromOptionsHistory ==> "+ ex);
	        return false;
	    }
	}
}