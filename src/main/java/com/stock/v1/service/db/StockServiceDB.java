package com.stock.v1.service.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.utils.DBConstants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Monitor;
import com.stock.v1.vo.Rating;
import com.stock.v1.vo.Stock;

@Service
public class StockServiceDB{

	@Autowired
	@Qualifier("ihelpJdbcTemplate")
	private JdbcTemplate ihelpJdbcTemplate;
	
	public void populateLiveStockList()
	{
		List<Stock> liveStockList = LiveStockCache.getLiveStockList();
		if(liveStockList == null || liveStockList.isEmpty())
		{
			liveStockList = getLatestStockHistory();
			LiveStockCache.setLiveStockList(liveStockList);
		}
	}
	
	public List<Master> getMasterList() {
		System.out.println( "getMasterList - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_MASTER_LIST);

	    return retResultMap.stream()
	            .map(this::mapToMaster)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}	
	
	private Master mapToMaster(Map<String, Object> retRes) {
		Master master = new Master();
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {	              
	                case "TICKER":
	                	master.setTicker((String) value);
	                    break;	                
	                case "NAME":
	                	master.setName((String) value);
	                    break;	                
	                case "LAST_EARNING_DATE":
	                	master.setLastEarningsDate((String) value);
	                    break;
	                case "NEXT_EARNING_DATE":
	                	master.setNextEarningsDate((String) value);
	                    break;
	                case "SKIP_EARNING":
	                	master.setNextEarningsDate((String) value);
	                    break;
	                case "PRICE":
	                	master.setPrice((String) value);
	                    break;
	                case "CHANGE":
	                	master.setChange((String) value);
	                    break;
	                case "UP":
	                	master.setUpDays(Integer.valueOf((String) value));
	                    break;
	                case "PRICE_UP":
	                	master.setUpBy((String) value);
	                    break;
	                case "DOWN":
	                	master.setDownDays(Integer.valueOf((String) value));
	                    break;
	                case "PRICE_DOWN":
	                	master.setDownBy((String) value);
	                    break;
	                case "UP_HIGH":
	                	master.setUpHigh((String) value);
	                    break;
	                case "DOWN_LOW":
	                	master.setDownLow((String) value);
	                    break;
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    if(StringUtils.isNotBlank(master.getNextEarningsDate()) && 
	    		!"N/A".equalsIgnoreCase(master.getNextEarningsDate()) &&
	    	!"NA".equalsIgnoreCase(master.getNextEarningsDate()))
	    {
	    	String date = master.getNextEarningsDate();
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

	        // Parse the string to a LocalDate object
	        LocalDate earningDate = LocalDate.parse(date, formatter);	    	
	        // Get today's date
	        LocalDate currentDate = LocalDate.now();
	        // Calculate the difference in days
	        long daysDifference = ChronoUnit.DAYS.between(currentDate, earningDate);
	        master.setDaysToEarnings((int)daysDifference);	        
	    }
	    if(UtilityService.excludedTick(master.getTicker()))
        	master.setFuture(true);
	    return master;
	}
	
	public List<Stock> getLatestStockHistory() {
		System.out.println( "getLatestStockHistory - DB CALL");
		List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_LATEST_STOCK_HISTORY);

	    return retResultMap.stream()
	            .map(this::mapToStock)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}
	
	
	public List<Stock> getStockHistory(String ticker) {
		System.out.println( "getStockHistory - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_STOCK_HISTORY, ticker);

	    return retResultMap.stream()
	            .map(this::mapToStock)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}
	
	public List<Stock> getStockHistoryByDescOrder(String ticker) {
		System.out.println( "getStockHistoryByDescOrder - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_STOCK_HISTORY_DESC_ORDER, ticker);

	    return retResultMap.stream()
	            .map(this::mapToStock)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}
	
	public List<Stock> getStockHistoryByDate(String ticker, String date) {
		System.out.println( "getStockHistoryByDate - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_STOCK_HISTORY_BY_DATE, ticker, date);

	    return retResultMap.stream()
	            .map(this::mapToStock)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}
	
	public Stock getStockHistoryForADate(String ticker, String date) {
		System.out.println( "getStockHistoryForADate - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_STOCK_HISTORY_FOR_A_DATE, ticker, date);

	    return retResultMap.stream()
	            .map(this::mapToStock)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .findFirst()
	            .orElse(null);
	}
	
	public List<Stock> getTop2StockHistory(String ticker) {
		System.out.println( "getTop2StockHistory - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_STOCK_HISTORY_TOP_2, ticker);

	    return retResultMap.stream()
	            .map(this::mapToStock)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}

	private Stock mapToStock(Map<String, Object> retRes) {
	    Stock stock = new Stock();
	    stock.setRating(new Rating());
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {
	                case "HIST_DATE":
	                    stock.setDate(((String) value));
	                    break;
	                case "TICKER":
	                    stock.setTicker((String) value);
	                    break;
	                case "PRICE":
	                    stock.setPrice((String) value);
	                    break;
	                case "OPEN":
	                    stock.setOpen((String) value);
	                    break;
	                case "HIGH":
	                    stock.setHigh((String) value);
	                    break;
	                case "LOW":
	                    stock.setLow((String) value);
	                    break;
	                case "CHANGE":
	                    stock.setChange((String) value);
	                    break;
	                case "VOLUME":
	                    stock.setVolume((String) value);
	                    break;	                    
	                case "CONTRACT_NAME":
	                    stock.setContractName((String) value);
	                    break;	                    
	                case "CONTRACT_EXP":
	                    stock.setContractExpiry((String) value);
	                    break;	                    
	                case "CONTRACT_POINT":
	                    stock.setContractPoint((String) value);
	                    break;	                    
	                case "CONTRACT_MARGIN":
	                    stock.setContractMargin((String) value);
	                    break;	                    
	                case "OPEN_INT":
	                    stock.setOpenInterest((String) value);
	                    break;
	                case "EARNINGS_DAY":
	                    stock.setEarningDay((String) value);
	                    break;
	                case "EARNINGS_DATE":
	                    stock.setEarningsDate((String) value);
	                    break;
	                case "PREV_PRICE":
	                    stock.setPrevPrice((String) value);
	                    break;
	                case "NEXT_PRICE":
	                    stock.setNextPrice((String) value);
	                    break;
	                case "BARCHART_ANALYSTS":
	                	stock.getRating().setBtAnalysts((String) value);
	                    break;
	                case "BARCHART_ANALYSTS_RATING":
	                	stock.getRating().setBtAnalystRating((String) value);
	                    break;
	                case "BARCHART_SHORT_RATING":
	                	stock.getRating().setBtShortRating((String) value);
	                    break;
	                case "BARCHART_LONG_RATING":
	                	stock.getRating().setBtLongRating((String) value);
	                    break;
	                case "BARCHART_RATING":
	                	stock.getRating().setBtRating((String) value);
	                    break;
	                case "BARCHART_TREND":
	                	stock.getRating().setBtTrend((String) value);
	                    break;
	                case "PRICE_CHANGE_5":
	                    stock.setPriceChg5((String) value);
	                    break;
	                case "PRICE_CHANGE_10":
	                    stock.setPriceChg10((String) value);
	                    break;
	                case "ZACKS_RANK":
	                	stock.getRating().setZacksRank((String) value);
	                    break;
	                case "ZACKS_RATING":
	                	stock.getRating().setZacksRating((String) value);
	                    break;
	                case "SIUS_SCORE":
	                	stock.getRating().setSiusScore((String) value);
	                    break;
	                case "SIUS_RATING":
	                	stock.getRating().setSiusRating((String) value);
	                    break;
	                case "SIUS_DAYS":
	                	stock.getRating().setSiusDays((String) value);
	                    break;
	                case "SIUS_GCS":
	                	stock.setGCShortDate((String) value);
	                    break;
	                case "SIUS_DCS":
	                	stock.setDCShortDate((String) value);
	                    break;
	                case "SIUS_GCL":
	                	stock.setGCLongDate((String) value);
	                    break;
	                case "SIUS_DCL":
	                	stock.setDCLongDate((String) value);
	                    break;
	                case "NASDAQ_RATING":
	                	stock.getRating().setNasdaqRating((String) value);
	                    break;
	                case "TIP_RATING":
	                	stock.getRating().setTipRating((String) value);
	                    break;
	                case "TIP_BUYHOLDSELL":
	                	stock.getRating().setTipBuyHoldSell((String) value);
	                    break;
	                case "STREET_RATING":
	                	stock.getRating().setStreetRating((String) value);
	                    break;
	                case "STREET_SCORE":
	                	stock.getRating().setStreetScore((String) value);
	                    break;
	                case "MARKETBEAT_RATING":
	                	stock.getRating().setMarkbeatRating((String) value);
	                    break;
	                case "MARKETBEAT_PT":
	                	stock.getRating().setMarkbeatPT((String) value);
	                    break;
	                case "MARKETBEAT_UPDOWN":
	                	stock.getRating().setMarkbeatUpDown((String) value);
	                    break;
	                case "ZEN_RATING":
	                	stock.getRating().setZenRating((String) value);
	                    break;
	                case "ZEN_PT":
	                	stock.getRating().setZenTarget((String) value);
	                    break;
	                case "ZEN_UPDOWN":
	                	stock.getRating().setZenTargetUpDown((String) value);
	                    break;
	                case "ZEN_SCORE":
	                	stock.getRating().setZenScore((String) value);
	                    break;
	                case "STANALYSIS_RATING":
	                	stock.getRating().setStockAnalysisRating((String) value);
	                    break;
	                case "STANALYSIS_PT":
	                	stock.getRating().setStockAnalysisTarget((String) value);
	                    break;
	                case "STANALYSIS_UPDOWN":
	                	stock.getRating().setStockAnalysisTargetUpDown((String) value);
	                    break;
	                case "INVOBSERVE_SCORE":
	                	stock.getRating().setInvestObserveScore((String) value);
	                    break;
	                case "INVOBSERVE_LOW_PT":
	                	stock.getRating().setInvestObserveLT((String) value);
	                    break;
	                case "INVOBSERVE_HIGH_PT":
	                	stock.getRating().setInvestObserveHT((String) value);
	                    break;
	                case "INVESTING_RATING":
	                	stock.getRating().setInvestComRating((String) value);
	                    break;
	                case "INVESTING_PT":
	                	stock.getRating().setInvestComTarget((String) value);
	                    break;
	                case "SEEKALPHA_QUANT_RATING":
	                	stock.getRating().setSeekAlphaQuantRating((String) value);
	                    break;
	                case "SEEKALPHA_WALLSTREET_RATING":
	                	stock.getRating().setSeekAlphaWallstreetRating((String) value);
	                    break;
	                case "SEEKALPHA_ANALYST_RATING":
	                	stock.getRating().setSeekAlphaAnalystsRating((String) value);
	                    break;
	                case "TRADINGVIEW_ANALYST_RATING":
	                	stock.getRating().setTradingViewAnalystsRating((String) value);
	                    break;
	                case "TRADINGVIEW_TECH_RATING":
	                	stock.getRating().setTradingViewTechRating((String) value);
	                    break;
	                case "TRADINGVIEW_MA_RATING":
	                	stock.getRating().setTradingViewMARating((String) value);
	                    break;
	                case "TRADINGVIEW_OS_RATING":
	                	stock.getRating().setTradingViewOSRating((String) value);
	                    break;
	                case "TRADINGVIEW_BULL_BEAR_POWER":
	                	stock.getRating().setTradingViewBullBearPower((String) value);
	                    break;
	                case "TICKERON_RATING":
	                	stock.getRating().setTickeronRating((String) value);
	                    break;
	                case "TICKERON_RATING_AT":
	                	stock.getRating().setTickeronRatingAt((String) value);
	                    break;
	                case "TICKERON_RATING_ON":
	                	stock.getRating().setTickeronRatingOn((String) value);
	                    break;
	                case "TICKERON_AI_RATING":
	                	stock.getRating().setTickeronAIRating((String) value);
	                    break;
	                case "TICKERON_UNDER_OVER":
	                	stock.getRating().setTickeronUnderOver((String) value);
	                    break;
	                case "PORTFOLIO123_RATING":
	                	stock.getRating().setPortfolio123Rating((String) value);
	                    break;
	                case "PORTFOLIO123_HIGH_PT":
	                	stock.getRating().setPortfolio123HighPT((String) value);
	                    break;
	                case "PORTFOLIO123_LOW_PT":
	                	stock.getRating().setPortfolio123LowPT((String) value);
	                    break;
	                case "PORTFOLIO123_ANALYSTS":
	                	stock.getRating().setPortfolio123Analysts((String) value);
	                    break;
	                case "FINSCREENER_RATING":
	                	stock.getRating().setFinscreenerRating((String) value);
	                    break;
	                case "FINSCREENER_PT":
	                	stock.getRating().setFinscreenerPT((String) value);
	                    break;
	                case "FINSCREENER_ANALYSTS":
	                	stock.getRating().setFinscreenerAnalysts((String) value);
	                    break;
	                case "MARKETEDGE_RATING":
	                	stock.getRating().setMarketEdgeRating((String) value);
	                    break;
	                case "MARKETEDGE_CONF_SCORE":
	                	stock.getRating().setMarketEdgeConfScore((String) value);
	                    break;	               
	                case "NEXT_EARNING_DATE":
	                	stock.setNextEarningsDate((String) value);
	                    break;
	                case "BUY_TREND":
	                	stock.setBuyTrend(((BigDecimal) value).intValue());
	                    break;
	                case "SELL_TREND":
	                	stock.setSellTrend(((BigDecimal) value).intValue());
	                    break;
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    return stock;
	}
	
	public boolean addToStockHistory(List<Stock> list) {
		System.out.println( "addToStockHistory - DB CALL");
        String sql = "INSERT INTO STOCK_HISTORY (HIST_DATE, TICKER, PRICE, VOLUME, OPEN, HIGH, LOW, CHANGE,"
        		+ "NEXT_EARNING_DATE,PREV_PRICE, BARCHART_ANALYSTS,BARCHART_ANALYSTS_RATING,BARCHART_SHORT_RATING,"
        		+ "BARCHART_LONG_RATING,BARCHART_RATING,BARCHART_TREND,PRICE_CHANGE_5, "
        		+ "PRICE_CHANGE_10,ZACKS_RANK,ZACKS_RATING,SIUS_SCORE,SIUS_RATING, "
        		+ "SIUS_DAYS,SIUS_GCS,SIUS_DCS,SIUS_GCL,SIUS_DCL,NASDAQ_RATING,TIP_RATING,TIP_BUYHOLDSELL,STREET_RATING, "
        		+ "STREET_SCORE,MARKETBEAT_RATING,MARKETBEAT_PT,MARKETBEAT_UPDOWN,"
        		+ "ZEN_RATING,ZEN_PT,ZEN_UPDOWN,ZEN_SCORE,STANALYSIS_RATING,STANALYSIS_PT,STANALYSIS_UPDOWN,"
        		+ "INVOBSERVE_SCORE,INVOBSERVE_LOW_PT,INVOBSERVE_HIGH_PT,INVESTING_RATING,INVESTING_PT,"
        		+ "SEEKALPHA_QUANT_RATING,SEEKALPHA_WALLSTREET_RATING,SEEKALPHA_ANALYST_RATING,"
        		+ "TRADINGVIEW_ANALYST_RATING,TRADINGVIEW_TECH_RATING,TRADINGVIEW_MA_RATING,"
        		+ "TRADINGVIEW_OS_RATING,TRADINGVIEW_BULL_BEAR_POWER,"
        		+ "TICKERON_RATING,TICKERON_RATING_AT,TICKERON_RATING_ON,TICKERON_AI_RATING,TICKERON_UNDER_OVER,"
        		+ "PORTFOLIO123_RATING,PORTFOLIO123_HIGH_PT,PORTFOLIO123_LOW_PT,PORTFOLIO123_ANALYSTS,"
        		+ "FINSCREENER_RATING,FINSCREENER_PT,FINSCREENER_ANALYSTS,MARKETEDGE_RATING,MARKETEDGE_CONF_SCORE, "
        		+ "BUY_TREND, SELL_TREND, CONTRACT_NAME, CONTRACT_EXP, CONTRACT_POINT, CONTRACT_MARGIN, OPEN_INT) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                + ", ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (Stock stock : list) {
            	int i = 1;
                ps.setString(i, stock.getDate());i++;
                ps.setString(i, stock.getTicker());i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getPrice(), false));i++;
                ps.setString(i, stock.getVolume());i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getOpen(), false));i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getHigh(), false));i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getLow(), false));i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getChange(), false));i++;
                ps.setString(i, stock.getNextEarningsDate());i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getPrevPrice(), false));i++;
                ps.setString(i, stock.getRating().getBtAnalysts());i++;
                ps.setString(i, stock.getRating().getBtAnalystRating());i++;
                ps.setString(i, stock.getRating().getBtShortRating());i++;
                ps.setString(i, stock.getRating().getBtLongRating());i++;
                ps.setString(i, stock.getRating().getBtRating());i++;
                ps.setString(i, stock.getRating().getBtTrend());i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getPriceChg5(), false));i++;
                ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getPriceChg10(), false));i++;
                ps.setString(i, stock.getRating().getZacksRank());i++;
                ps.setString(i, stock.getRating().getZacksRating());i++;                           
                ps.setString(i, stock.getRating().getSiusScore());i++;
                ps.setString(i, stock.getRating().getSiusRating());i++;
                ps.setString(i, stock.getRating().getSiusDays());i++;                
                ps.setString(i, stock.getGCShortDate());i++;
                ps.setString(i, stock.getDCShortDate());i++;
                ps.setString(i, stock.getGCLongDate());i++;
                ps.setString(i, stock.getDCLongDate());i++;
                ps.setString(i, stock.getRating().getNasdaqRating());i++;
                ps.setString(i, stock.getRating().getTipRating());i++;
                ps.setString(i, stock.getRating().getTipBuyHoldSell());i++;
                ps.setString(i, stock.getRating().getStreetRating());i++;
                ps.setString(i, stock.getRating().getStreetScore());i++;
                ps.setString(i, stock.getRating().getMarkbeatRating());i++;
                ps.setString(i, stock.getRating().getMarkbeatPT());i++;
                ps.setString(i, stock.getRating().getMarkbeatUpDown());i++;                
                ps.setString(i, stock.getRating().getZenRating());i++;
                ps.setString(i, stock.getRating().getZenTarget());i++;
                ps.setString(i, stock.getRating().getZenTargetUpDown());i++;
                ps.setString(i, stock.getRating().getZenScore());i++;                
                ps.setString(i, stock.getRating().getStockAnalysisRating());i++;
                ps.setString(i, stock.getRating().getStockAnalysisTarget());i++;
                ps.setString(i, stock.getRating().getStockAnalysisTargetUpDown());i++;            
                ps.setString(i, stock.getRating().getInvestObserveScore());i++;
                ps.setString(i, stock.getRating().getInvestObserveLT());i++;
                ps.setString(i, stock.getRating().getInvestObserveHT());i++;
                ps.setString(i, stock.getRating().getInvestComRating());i++;
                ps.setString(i, stock.getRating().getInvestComTarget());i++;                
                ps.setString(i, stock.getRating().getSeekAlphaQuantRating());i++;
                ps.setString(i, stock.getRating().getSeekAlphaWallstreetRating());i++;
                ps.setString(i, stock.getRating().getSeekAlphaAnalystsRating());i++;                
                ps.setString(i, stock.getRating().getTradingViewAnalystsRating());i++;
                ps.setString(i, stock.getRating().getTradingViewTechRating());i++;
                ps.setString(i, stock.getRating().getTradingViewMARating());i++;
                ps.setString(i, stock.getRating().getTradingViewOSRating());i++;
                ps.setString(i, stock.getRating().getTradingViewBullBearPower());i++;                
                ps.setString(i, stock.getRating().getTickeronRating());i++;
                ps.setString(i, stock.getRating().getTickeronRatingAt());i++;
                ps.setString(i, stock.getRating().getTickeronRatingOn());i++;
                ps.setString(i, stock.getRating().getTickeronAIRating());i++;
                ps.setString(i, stock.getRating().getTickeronUnderOver());i++;                
                ps.setString(i, stock.getRating().getPortfolio123Rating());i++;
                ps.setString(i, stock.getRating().getPortfolio123HighPT());i++;
                ps.setString(i, stock.getRating().getPortfolio123LowPT());i++;
                ps.setString(i, stock.getRating().getPortfolio123Analysts());i++;                
                ps.setString(i, stock.getRating().getFinscreenerRating());i++;
                ps.setString(i, stock.getRating().getFinscreenerPT());i++;
                ps.setString(i, stock.getRating().getFinscreenerAnalysts());i++;
                ps.setString(i, stock.getRating().getMarketEdgeRating());i++;
                ps.setString(i, stock.getRating().getMarketEdgeConfScore());i++;
                ps.setInt(i, stock.getBuyTrend());i++;
                ps.setInt(i, stock.getSellTrend());i++;                
                ps.setString(i, stock.getContractName());i++;
                ps.setString(i, stock.getContractExpiry());i++;
                ps.setString(i, stock.getContractPoint());i++;
                ps.setString(i, stock.getContractMargin());i++;
                ps.setString(i, stock.getOpenInterest());i++;
                
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
        	System.err.println("ERROR ==> addToStockHistory ==> "+ ex);
            return false;
        }
    }
	
	public boolean deleteFromStockHistory(String date) {
		System.out.println( "deleteFromStockHistory - DB CALL");
	    String sql = "DELETE FROM STOCK_HISTORY WHERE HIST_DATE = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, date);	       
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> deleteFromStockHistory ==> "+ ex);
	        return false;
	    }
	}
	
	public boolean addToMaster(List<Stock> list) {
		System.out.println( "addToMaster - DB CALL");
        String sql = "INSERT INTO STOCK_MASTER (TICKER, NAME, LAST_EARNING_DATE, NEXT_EARNING_DATE) "
                + "VALUES (?, ?, ?, ?)";
        
        List<Master> masterList = getMasterList();
        
        try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
        	for (Stock stock : list) {
        		if(masterList.stream().anyMatch(x -> x.getTicker().equalsIgnoreCase(stock.getTicker())))
        			continue;        		
        		int i = 1;
        		ps.setString(i, stock.getTicker());i++;
        		ps.setString(i, stock.getName());i++;
        		ps.setString(i, stock.getLastEarningsDate());i++;
        		ps.setString(i, stock.getNextEarningsDate());i++;
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
        	System.err.println("ERROR ==> addToMaster ==> "+ ex);
            return false;
        }
    }
	
	public boolean updateMaster(Master master) {
		System.out.println( "updateMaster - DB CALL");
	    String sql = "UPDATE STOCK_MASTER SET NEXT_EARNING_DATE = ? WHERE TICKER = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	    		PreparedStatement ps = conn.prepareStatement(sql)) {
	    	ps.setString(1, master.getLastEarningsDate());
	    	ps.setString(2, master.getTicker());
	    	int rowsAffected = ps.executeUpdate();
	    	return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> updateMaster ==> "+ ex);
	    	return false;
	    }
	}
	
	public boolean updatePrevPrice(Stock stock) {
		System.out.println( "updatePrevPrice - DB CALL");
	    String sql = "UPDATE STOCK_HISTORY SET PREV_PRICE = ? WHERE HIST_DATE = ? AND TICKER = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, stock.getPrevPrice());
	        ps.setString(2, stock.getDate());
	        ps.setString(3, stock.getTicker());
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> updatePrevPrice ==> "+ ex);
	        return false;
	    }
	}
	
	public boolean updateNextPrice(Stock stock) {
		System.out.println( "updateNextPrice - DB CALL");
	    String sql = "UPDATE STOCK_HISTORY SET NEXT_PRICE = ? WHERE HIST_DATE = ? AND TICKER = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, stock.getNextPrice());
	        ps.setString(2, stock.getDate());
	        ps.setString(3, stock.getTicker());
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> updateNextPrice ==> "+ ex);
	        return false;
	    }
	}
	
	public boolean markEarningDay(Stock stock) {
		System.out.println( "markEarningDay - DB CALL");
	    String sql = "UPDATE STOCK_HISTORY SET EARNINGS_DAY = 'Y' WHERE HIST_DATE = ? AND TICKER = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, stock.getDate());
	        ps.setString(2, stock.getTicker());
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> markEarningDay ==> "+ ex);
	        return false;
	    }
	}
	
	public boolean updateLivePriceToMaster() {
		System.out.println( "updateLivePriceToMaster - DB CALL");
        String sql = "UPDATE STOCK_MASTER SET PRICE=?, CHANGE=? WHERE TICKER=? ";
        
        List<Stock> liveList = LiveStockCache.getLiveStockList();
        
        try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
        	for (Stock stock : liveList) {        		
        		int i = 1;

        		ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getPrice(), false));i++;        		
        		ps.setString(i, UtilityService.stripStringToTwoDecimals(stock.getChange(), false));i++;        		
        		ps.setString(i, stock.getTicker());i++;
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
        	System.err.println("ERROR ==> updateLivePriceToMaster ==> "+ ex);
            return false;
        }
	}

	public boolean updatePriceUpDown(Master master) {
		System.out.println( "updatePriceUpDown - DB CALL");
	    String sql = "UPDATE STOCK_MASTER SET PRICE=?, CHANGE=?, UP=?, PRICE_UP=?, DOWN=?, PRICE_DOWN=?, UP_HIGH=?, DOWN_LOW=? WHERE TICKER = ?";

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, master.getPrice());
	        ps.setString(2, UtilityService.stripStringToTwoDecimals(master.getChange(), false));
	        ps.setString(3, String.valueOf(master.getUpDays()));
	        ps.setString(4, UtilityService.stripStringToTwoDecimals(master.getUpBy(), false));
	        ps.setString(5, String.valueOf(master.getDownDays()));
	        ps.setString(6, UtilityService.stripStringToTwoDecimals(master.getDownBy(), false));
	        ps.setString(7, UtilityService.stripStringToTwoDecimals(master.getUpHigh(), false));
	        ps.setString(8, UtilityService.stripStringToTwoDecimals(master.getDownLow(), false));
	        ps.setString(9, master.getTicker());
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> updatePriceUpDown ==> "+ ex);
	        return false;
	    }
	}
	
	public List<Monitor> getMonitorList() {
		System.out.println( "getMonitorList - DB CALL");
	    List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(DBConstants.GET_MONITOR_LIST);

	    return retResultMap.stream()
	            .map(this::mapToMonitor)
	            .filter(stock -> stock != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}
	
	private Monitor mapToMonitor(Map<String, Object> retRes) {
		Monitor monitor = new Monitor();
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {	              
	                case "TICKER":
	                	monitor.setTicker((String) value);
	                    break;	                
	                case "MONITOR_PRICE":
	                	monitor.setMonitorPrice((String) value);
	                    break;	                
	                case "CURRENT_PRICE":
	                	monitor.setCurrentPrice((String) value);
	                    break;
	                case "MONITOR_CHG":
	                	monitor.setMonitorChg((String) value);
	                    break;
	                case "CURRENT_CHG":
	                	monitor.setCurrentChg((String) value);
	                    break;
	                case "UP":
	                	monitor.setUpDays((String) value);
	                    break;
	                case "PRICE_UP":
	                	monitor.setUpPrice((String) value);
	                    break;
	                case "DOWN":
	                	monitor.setDownDays((String) value);
	                    break;
	                case "PRICE_DOWN":
	                	monitor.setDownPrice((String) value);
	                    break;
	                case "TARGET":
	                	monitor.setTarget((String) value);
	                    break;
	                case "TARGET_DATE":
	                	monitor.setTargetDate((String) value);
	                    break;
	                case "COMMENTS":
	                	monitor.setComments((String) value);
	                    break;
	                case "STATUS":
	                	monitor.setStatus((String) value);
	                    break;
	                case "ADD_DATE":
	                	monitor.setAddDate((String) value);
	                    break;
	                case "OFF_DATE":
	                	monitor.setOffDate((String) value);
	                    break;
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    
	    return monitor;
	}
	
	public boolean addToMonitor(List<Monitor> list) {
		System.out.println( "addToMonitor - DB CALL");
        String sql = "INSERT INTO MONITOR (TICKER, MONITOR_PRICE, CURRENT_PRICE, MONITOR_CHG, CURRENT_CHG, UP, PRICE_UP,"
        		+ " DOWN, PRICE_DOWN, TARGET, TARGET_DATE, COMMENTS, STATUS, ADD_DATE, OFF_DATE) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
        	for (Monitor monitor : list) {        		        		
        		int i = 1;
        		ps.setString(i, monitor.getTicker());i++;
        		ps.setString(i, monitor.getMonitorPrice());i++;
        		ps.setString(i, monitor.getCurrentPrice());i++;
        		ps.setString(i, monitor.getMonitorChg());i++;
        		ps.setString(i, monitor.getCurrentChg());i++;
        		ps.setString(i, monitor.getUpDays());i++;
        		ps.setString(i, monitor.getUpPrice());i++;
        		ps.setString(i, monitor.getDownDays());i++;
        		ps.setString(i, monitor.getDownPrice());i++;
        		ps.setString(i, monitor.getTarget());i++;
        		ps.setString(i, monitor.getTargetDate());i++;
        		ps.setString(i, monitor.getComments());i++;
        		ps.setString(i, monitor.getStatus());i++;
        		ps.setString(i, monitor.getAddDate());i++;
        		ps.setString(i, monitor.getOffDate());i++;
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
        	System.err.println("ERROR ==> addToMonitor ==> "+ ex);
            return false;
        }
    }

	public boolean updateMonitor(Monitor monitor) {
		System.out.println( "updateMonitor - DB CALL");

	    String sql = "UPDATE MONITOR SET MONITOR_PRICE=?,CURRENT_PRICE=?,MONITOR_CHG=?,CURRENT_CHG=?,"
	    		+ "UP=?,PRICE_UP=?,DOWN=?,PRICE_DOWN=?,TARGET=?, TARGET_DATE=?,COMMENTS=?,STATUS=?,"
	    		+ "ADD_DATE=?,OFF_DATE=? WHERE TICKER = ?";
	    
	    List<Monitor> monitorList = getMonitorList();
	    
	    monitorList.stream().forEach(x -> {
			if (monitor.getTicker().equalsIgnoreCase(x.getTicker())) {
				
				if(StringUtils.isBlank(monitor.getMonitorPrice()))
					monitor.setMonitorPrice(x.getMonitorPrice());
				if(StringUtils.isBlank(monitor.getCurrentPrice()))
					monitor.setCurrentPrice(x.getCurrentPrice());
				if(StringUtils.isBlank(monitor.getMonitorChg()))
					monitor.setMonitorChg(x.getMonitorChg());
				if(StringUtils.isBlank(monitor.getCurrentChg()))
					monitor.setCurrentChg(x.getCurrentChg());
				if(StringUtils.isBlank(monitor.getUpDays()))
					monitor.setUpDays(x.getUpDays());
				if(StringUtils.isBlank(monitor.getUpPrice()))
					monitor.setUpPrice(x.getUpPrice());
				if(StringUtils.isBlank(monitor.getDownDays()))
					monitor.setDownDays(x.getDownDays());
				if(StringUtils.isBlank(monitor.getDownPrice()))
					monitor.setDownPrice(x.getDownPrice());
				if(StringUtils.isBlank(monitor.getTarget()))
					monitor.setTarget(x.getTarget());
				if(StringUtils.isBlank(monitor.getTargetDate()))
					monitor.setTargetDate(x.getTargetDate());
				if(StringUtils.isBlank(monitor.getComments()))
					monitor.setComments(x.getComments());
				else
					monitor.setComments(monitor.getComments() +"~"+x.getComments());					
				if(StringUtils.isBlank(monitor.getStatus()))
					monitor.setStatus(x.getStatus());
				if(StringUtils.isBlank(monitor.getAddDate()))
					monitor.setAddDate(x.getAddDate());
				if(StringUtils.isBlank(monitor.getOffDate()))
					monitor.setOffDate(x.getOffDate());				
			}
		});

	    try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
	    		PreparedStatement ps = conn.prepareStatement(sql)) {
	    	
	    	int i = 1;	    	
    		ps.setString(i, monitor.getMonitorPrice());i++;
    		ps.setString(i, monitor.getCurrentPrice());i++;
    		ps.setString(i, monitor.getMonitorChg());i++;
    		ps.setString(i, monitor.getCurrentChg());i++;
    		ps.setString(i, monitor.getUpDays());i++;
    		ps.setString(i, monitor.getUpPrice());i++;
    		ps.setString(i, monitor.getDownDays());i++;
    		ps.setString(i, monitor.getDownPrice());i++;
    		ps.setString(i, monitor.getTarget());i++;
    		ps.setString(i, monitor.getTargetDate());i++;
    		ps.setString(i, monitor.getComments());i++;
    		ps.setString(i, monitor.getStatus());i++;
    		ps.setString(i, monitor.getAddDate());i++;
    		ps.setString(i, monitor.getOffDate());i++;
    		ps.setString(i, monitor.getTicker());i++;
    		
	    	int rowsAffected = ps.executeUpdate();
	    	return rowsAffected > 0;
	    } catch (SQLException ex) {
	    	System.err.println("ERROR ==> updateMonitor ==> "+ ex);
	    	return false;
	    }
	}
}