package com.stock.v1.utils;

public class DBConstants {
	
	public static final String GET_MASTER_LIST = "select * from stock_master order by ticker";
	public static final String GET_MONITOR_LIST = "select * from MONITOR order by ADD_DATE desc";
	public static final String GET_STOCK_HISTORY = "select * from stock_history where ticker = ? order by hist_date";
	public static final String GET_STOCK_HISTORY_DESC_ORDER = "select * from stock_history where ticker = ? order by hist_date desc";
	public static final String GET_STOCK_HISTORY_BY_DATE = "select * from stock_history where ticker = ? and to_date(hist_date, 'yyyy-mm-dd') >= to_date(?, 'yyyy-mm-dd') order by hist_date";
	public static final String GET_STOCK_HISTORY_FOR_A_DATE = "select * from stock_history where ticker = ? and to_date(hist_date, 'yyyy-mm-dd') = to_date(?, 'yyyy-mm-dd')";
	public static final String GET_STOCK_HISTORY_TOP_2 = "SELECT * FROM (SELECT * FROM stock_history WHERE ticker = ? ORDER BY hist_date DESC) WHERE ROWNUM <= 2";
	public static final String GET_EARNINGS_HISTORY = "select * from stock_earning_history";
	public static final String GET_LATEST_STOCK_HISTORY = "WITH RankedRows AS ( "
			+ "		    SELECT sh.*, ROW_NUMBER() OVER (PARTITION BY ticker ORDER BY hist_date desc) AS rn "
			+ "		    FROM stock_history sh) "
			+ "		SELECT * FROM RankedRows WHERE rn = 1";
	public static final String GET_TASKS = "select * from stock_tasks";
	public static final String GET_OPTIONS = "select * from stock_options";
	public static final String GET_OPTIONS_HISTORY = "select * from stock_options_history";
	
	public static final String GET_PATTERN_HISTORY = "select * from stock_pattern_history";
	
	
	

}