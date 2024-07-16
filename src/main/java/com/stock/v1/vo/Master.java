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
public class Master implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String ticker;
	public String name;
	public String price;
	public String trackingPrice;
	public String trackingDiff;
	public String change;
	public String earningsDate;
	public int upDays;
	public int downDays;
	public String upBy;
	public String downBy;
	public String upHigh;
	public String downLow;
	public String lastEarningsDate;
	public String nextEarningsDate;
	public Rating rating;
	public String earningsSuccess;
	public int earningsCount;
	public int daysToEarnings;
	public Pattern pattern;
	public int positiveStreak;
	public int negativeStreak;
	public double currAndLastPriceEffectDiff;
	public int buyStrength = 0;
	public int sellStrength = 0;
	public int buyTrend = 0;
	public int sellTrend = 0;
	public boolean isFuture = false;
	public String contractName;
	public String contractExpiry;
	public String contractPoint;
	public String contractMargin;
	public String openInterest;
	public String e;
	public String h;
	public String l;	
	public String profitLoss;
	public String maxProfit;
	public String maxLoss;
	public String entryDate;
	public boolean option = false;
}