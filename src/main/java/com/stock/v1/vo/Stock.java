package com.stock.v1.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String ticker;
	public String name;
	public String price;
	public String open;
	public String high;
	public String low;
	public String change;
	public String date;
	public String volume;
	public String earningDay;
	public String earningsDate;	
	public String prevPrice;
	public String nextPrice;	
	public String priceChg5;
	public String priceChg10;	
	public String lastEarningsDate;
	public String nextEarningsDate;	
	public Rating rating;
	public String trackingPrice;
	public int buyStrength;
	public int sellStrength;
	public int buyTrend;
	public int sellTrend;	
	public String contractName;
	public String contractExpiry;
	public String contractPoint;
	public String contractMargin;
	public String openInterest;
	public String gCShortDate;
	public String dCShortDate;
	public String gCLongDate;
	public String dCLongDate;
}