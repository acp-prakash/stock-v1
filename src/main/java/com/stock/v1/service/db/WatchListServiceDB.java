package com.stock.v1.service.db;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stock.v1.utils.DBConstants;
import com.stock.v1.vo.Master;

@Service
public class WatchListServiceDB{

	@Autowired
	@Qualifier("ihelpJdbcTemplate")
	private JdbcTemplate ihelpJdbcTemplate;
	
	public List<Master> getWatchList()
	{
		System.out.println( "getWatchList- DB CALL");
		String sql = DBConstants.GET_WATCHLIST;
		List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(sql);

	    return retResultMap.stream()
	            .map(this::mapToWatchList)
	            .filter(watch -> watch != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}
	
	public void addWatchList(String ticker)
	{
		try
		{		
			System.out.println( "addWatchList- DB CALL");
			ihelpJdbcTemplate.update(DBConstants.ADD_WATCHLIST, ticker);
		}
		catch(Exception ex)
		{
			System.out.println("EXCEPTION in addWatchList =>" + ex);
		}
	}
	
	public void deleteWatchList(String ticker)
	{
		try
		{	
			System.out.println( "deleteWatchList- DB CALL");
			ihelpJdbcTemplate.update(DBConstants.DELETE_WATCHLIST, ticker);
		}
		catch(Exception ex)
		{
			System.out.println("EXCEPTION in deleteWatchList =>" + ex);
		}
	}
	
	private Master mapToWatchList(Map<String, Object> retRes) {
		Master watch = new Master();
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {	              
	                case "TICKER":
	                	watch.setTicker((String) value);
	                    break;	                
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    return watch;
	}	
}