package com.stock.v1.service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.EarningsCache;
import com.stock.v1.utils.DBConstants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Earnings;

@Service
public class EarningsServiceDB{

	@Autowired
	@Qualifier("ihelpJdbcTemplate")
	private JdbcTemplate ihelpJdbcTemplate;
	
	public List<Earnings> getEarningsHistory()
	{
		List<Earnings> list = EarningsCache.getEarningsHistory();
		if(list != null && !list.isEmpty())
			return list;
		System.out.println( "getEarningsHistory - DB CALL");
		List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_EARNINGS_HISTORY);

		list = retResultMap.stream()
	            .map(this::mapToEarnings)
	            .filter(earning -> earning != null)  // Filter out any null results
	            .collect(Collectors.toList());
		EarningsCache.setEarningsHistory(list);
		return list;
	}	
	
	private Earnings mapToEarnings(Map<String, Object> retRes) {
		Earnings earning = new Earnings();
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {	              
	                case "TICKER":
	                	earning.setTicker((String) value);
	                    break;	                
	                case "EARNING_DATE":
	                	earning.setDate((String) value);
	                    break;
	                case "EARNING_TIME":
	                	earning.setTime((String) value);
	                    break;
	                case "EPS_EST":
	                	earning.setEpsEst((String) value);
	                    break;
	                case "EPS_ACT":
	                	earning.setEpsAct((String) value);
	                    break;
	                case "PRICE_EFFECT":
	                	earning.setPriceEffect((String) value);
	                    break;
	                case "PRICE_BEFORE":
	                	earning.setPriceBefore((String) value);
	                    break;
	                case "PRICE_AFTER":
	                	earning.setPriceAfter((String) value);
	                    break;
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    return earning;
	}
	
	public boolean addToEarnings(List<Earnings> earningsList) {
		System.out.println( "addToEarnings - DB CALL");
		List<Earnings> list = getEarningsHistory();
		String sql = "INSERT INTO STOCK_EARNING_HISTORY (EARNING_DATE, EARNING_TIME, TICKER, EPS_EST, EPS_ACT) "
                + "VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (Earnings earning : earningsList) {
				boolean exists = false;
				for (Earnings lst : list) {
					if(lst.getTicker().equalsIgnoreCase(earning.getTicker()) &&
							lst.getDate().equalsIgnoreCase(earning.getDate()))
						exists = true;
				}
				if(!exists) {
					ps.setString(1, earning.getDate());
					ps.setString(2, earning.getTime());
					ps.setString(3, earning.getTicker());
					ps.setString(4, earning.getEpsEst());
					ps.setString(5, earning.getEpsAct());
					ps.addBatch();
					EarningsCache.getEarningsHistory().add(earning);
				}            	
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
        	System.err.println("ERROR ==> addToEarnings ==> "+ ex);
            return false;
        }
    }
	
	public boolean updatePriceEffect(Earnings earning) {
		System.out.println( "updatePriceEffect - DB CALL");
	    String sql = "UPDATE STOCK_EARNING_HISTORY SET PRICE_BEFORE=?, PRICE_AFTER=?, PRICE_EFFECT=? WHERE EARNING_DATE = ? AND TICKER = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, UtilityService.stripStringToTwoDecimals(earning.getPriceBefore(), false));
	        ps.setString(2, UtilityService.stripStringToTwoDecimals(earning.getPriceAfter(), false));
	        ps.setString(3, UtilityService.stripStringToTwoDecimals(earning.getPriceEffect(), false));
	        ps.setString(4, earning.getDate());
	        ps.setString(5, earning.getTicker());
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> updatePriceEffect ==> "+ ex);
	        return false;
	    }
	}
}