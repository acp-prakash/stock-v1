package com.stock.v1.service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.MasterStocksCache;
import com.stock.v1.cache.PatternsCache;
import com.stock.v1.utils.DBConstants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Pattern;

@Service
public class PatternServiceDB{

	@Autowired
	@Qualifier("ihelpJdbcTemplate")
	private JdbcTemplate ihelpJdbcTemplate;
	
	private List<Master> masList;
	
	public List<Pattern> getPatternHistory(String ticker)
	{
		List<Pattern> patternList = null;//PatternsCache.getPatternHistory();
		if(patternList == null || patternList.isEmpty())
		{
			System.out.println("START -> getPatternHistory: DB CALL " + new Date());
			masList = MasterStocksCache.getMasterStocks();
			
			List<Pattern> min15PatList = getPatternList(false);
			List<Pattern> dailyPatList = getPatternList(true);

			dailyPatList.forEach(dailyPattern -> {
				min15PatList.stream()
			        .filter(min15Pattern -> dailyPattern.getTicker().equalsIgnoreCase(min15Pattern.getTicker()))
			        .forEach(min15Pattern -> {
			            // Update counts
			            int totalCount = min15Pattern.getCount() + dailyPattern.getCount();
			            min15Pattern.setTcount(totalCount);
			            dailyPattern.setTcount(totalCount);

			            // Update bulls
			            int totalBull = min15Pattern.getBull() + dailyPattern.getBull();
			            min15Pattern.setTbull(totalBull);
			            dailyPattern.setTbull(totalBull);

			            // Update bears
			            int totalBear = min15Pattern.getBear() + dailyPattern.getBear();
			            min15Pattern.setTbear(totalBear);
			            dailyPattern.setTbear(totalBear);
			        });
			});
			patternList = new ArrayList<>();
			
			patternList.addAll(min15PatList);
			patternList.addAll(dailyPatList);

			System.out.println("END -> getPatternHistory: " + new Date());

			PatternsCache.setPatternHistory(patternList);
		}
        
        if(StringUtils.isNotBlank(ticker))
			return patternList.stream().filter(x -> x.getTicker().equalsIgnoreCase(ticker)).toList();
        else
        	return patternList;      
	}
	
	private List<Pattern> getPatternList(boolean daily)
	{
		List<Map<String, Object>> retResultMap = null;
		if(daily)
			retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_PATTERN_HISTORY_DAILY);
		else
			retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_PATTERN_HISTORY);
			
		List<Pattern> patternList = new ArrayList<>();

		patternList  = retResultMap.stream()
				.map(this::mapToPattern)
				.filter(pattern -> pattern != null)  // Filter out any null results
				.peek(pattern -> {
	                if (daily) 
	                    pattern.setType("DAILY"); // Set type if daily
	                else
	                	pattern.setType("15MIN");	                	
	            })
				.collect(Collectors.toList());


		// Create a map to store matching patterns
		Map<String, Long> longCountByTicker = patternList.stream()
				.filter(pattern -> "long".equalsIgnoreCase(pattern.getTrend()) && "Y".equalsIgnoreCase(pattern.getStatus()))
				.collect(Collectors.groupingBy(Pattern::getTicker, Collectors.counting()));

		Map<String, Long> shortCountByTicker = patternList.stream()
				.filter(pattern -> "short".equalsIgnoreCase(pattern.getTrend()) && "Y".equalsIgnoreCase(pattern.getStatus()))
				.collect(Collectors.groupingBy(Pattern::getTicker, Collectors.counting()));

		// Loop through patternList and update the bull attribute
		patternList.forEach(pattern -> {
			String tick = pattern.getTicker();
			long bullCount = 0;
			long bearCount = 0;
			if (longCountByTicker.containsKey(tick)) {
				bullCount = longCountByTicker.get(tick);
				pattern.setBull((int) bullCount); // Assuming bull is an integer
			}
			if (shortCountByTicker.containsKey(tick)) {
				bearCount = shortCountByTicker.get(tick);
				pattern.setBear((int) bearCount); // Assuming bear is an integer
			}
			pattern.setCount((int) bullCount + (int) bearCount);
		});
		return patternList;
	}
	
	private Pattern mapToPattern(Map<String, Object> retRes) {
		Pattern pattern = new Pattern();
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {
		            case "HIST_DATE":
	                	pattern.setHistDate((String) value);
	                    break;
		            case "TICKER":
	                	pattern.setTicker((String) value);
	                    break;
	                case "ID":
	                	pattern.setId((String) value);
	                    break;
	                case "ENTRY":
	                	pattern.setEntry((String) value);
	                    break;
	                case "MINPT":
	                	pattern.setMinPT((String) value);
	                    break;
	                case "MAXPT":
	                	pattern.setMaxPT((String) value);
	                    break;
	                case "STOP":
	                	pattern.setStop((String) value);
	                    break;
	                case "TARGET_DATE":
	                	pattern.setTargetDate((String) value);
	                    break;
	                case "NAME":
	                	pattern.setName((String) value);
	                    break;
	                case "TREND":
	                	pattern.setTrend((String) value);
	                    break;
	                case "STATUS":
	                	pattern.setStatus((String) value);
	                    break;
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    masList.stream().filter(x->x.getTicker().equalsIgnoreCase(pattern.getTicker())) .findFirst() // Get the first matching element, if any
        .ifPresent(matchingElement -> {
        	pattern.setAll(matchingElement);
        });

	    if(StringUtils.isNotBlank(pattern.getAll().getPrice()))
	    {
	    	if("short".equalsIgnoreCase(pattern.getTrend()) && StringUtils.isNotBlank(pattern.getEntry()))
	    	{
	    		double entry = Double.valueOf(pattern.getEntry());
	    		double price = Double.valueOf(pattern.getAll().getPrice());
	    		pattern.setFromPtPc(UtilityService.stripStringToTwoDecimals(String.valueOf(((price - entry)/entry)*100), false));
	    	}
	    	else if("long".equalsIgnoreCase(pattern.getTrend()) && StringUtils.isNotBlank(pattern.getEntry()))
	    	{
	    		double entry = Double.valueOf(pattern.getEntry());
	    		double price = Double.valueOf(pattern.getAll().getPrice());
    			pattern.setFromPtPc(UtilityService.stripStringToTwoDecimals(String.valueOf(((entry - price)/price)*100), false));
	    	}
	    }    
	    
	    return pattern;
	}

	public boolean addToPattern(List<Pattern> patternList, boolean daily) {
		System.out.println( "addToPattern- DB CALL - " + patternList.size());
		//List<Pattern> list = getPatternHistory(ticker);
		String tableName = daily ? "stock_pattern_history_daily" : "stock_pattern_history";
		String sql = "INSERT INTO " + tableName + " (HIST_DATE,TICKER,ID,ENTRY,MINPT,MAXPT,"
				+ "STOP,TARGET_DATE,NAME,TREND,STATUS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		/*List<Pattern> existingPatternList = list.stream()
		        .filter(lst -> patternList.stream()
		                .anyMatch(pat -> lst.getId().equalsIgnoreCase(pat.getId())))
		        .collect(Collectors.toList());
		existingPatternList.forEach(this::deletePattern);*/
		
		/*List<Pattern> removedPatternList = list.stream()
		        .filter(lst -> patternList.stream()
		                .noneMatch(pat -> lst.getId().equalsIgnoreCase(pat.getId())))
		        .collect(Collectors.toList());
		removedPatternList.forEach(this::markPatternInActive);*/

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (Pattern pattern : patternList) {
				/*boolean exists = false;
				for (Pattern lst : list) {
					if(lst.getId().equalsIgnoreCase(pattern.getId()))
						exists = true;
				}
				if(!exists) {*/
					int i = 1;
					ps.setString(i, pattern.getHistDate());i++;
	                ps.setString(i, pattern.getTicker());i++;
	                ps.setString(i, pattern.getId());i++;
	                ps.setString(i, pattern.getEntry());i++;
	                ps.setString(i, pattern.getMinPT());i++;
	                ps.setString(i, pattern.getMaxPT());i++;
	                ps.setString(i, pattern.getStop());i++;
	                ps.setString(i, pattern.getTargetDate());i++;
	                ps.setString(i, pattern.getName());i++;	                
	                ps.setString(i, pattern.getTrend());i++;
	                ps.setString(i, pattern.getStatus());i++;

					ps.addBatch();
				//}
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
        	System.err.println("ERROR ==> addToPattern ==> "+ ex);
            return false;
        }
    }

	public boolean markPatternInActive(Pattern inactive) {
		System.out.println( "markPatternInActive- DB CALL");
		String sql = "UPDATE STOCK_PATTERN_HISTORY SET STATUS='N' WHERE STATUS='Y' and ID = ?";

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, inactive.getId());			
			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException ex) {
			System.out.println(inactive.getId());
			System.err.println(ex);
			return false;
		}
	}
	
	public boolean deletePatternById(Pattern pattern) {
		System.out.println( "deletePatternById- DB CALL");
		String sql = "DELETE FROM STOCK_PATTERN_HISTORY WHERE ID = ?";

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, pattern.getId());			
			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException ex) {
			System.err.println("Exception in deletePatternById =>" + ex);
			return false;
		}
	}
	
	public boolean deletePattern(List<String> tickerList) {
		System.out.println( "deletePattern- DB CALL - " + tickerList);
		List<String> tableNames = List.of("STOCK_PATTERN_HISTORY", "STOCK_PATTERN_HISTORY_DAILY");		

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection())
		{
			for (String tableName : tableNames) {
				String sql = "DELETE FROM " + tableName + " WHERE TICKER = ?";
				try (PreparedStatement ps = conn.prepareStatement(sql)) {

					// Set auto-commit to false to manage transaction manually
					//conn.setAutoCommit(false);

					// Loop through each ticker in the list and add to batch
					for (String ticker : tickerList) {
						ps.setString(1, ticker.toUpperCase());
						ps.addBatch(); // Add to batch
					}

					// Execute batch
					int[] result = ps.executeBatch();

					// Commit the transaction
					//conn.commit();

					// Check if any delete operation failed
					for (int rowsAffected : result) {
						if (rowsAffected == 0) {
							return false; // If any delete operation affected 0 rows, return false
						}
					}					
				}
			}
			return true; // All deletes were successful
		} catch (SQLException ex) {
			System.err.println("Exception in deletePattern => " + ex);
			return false;
		}
	}
}