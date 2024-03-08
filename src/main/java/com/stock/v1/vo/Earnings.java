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
public class Earnings implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String ticker;
	public String date;
	public String epsEst;
	public String epsAct;
	public String time;
	public String priceEffect;
	public String priceBefore;
	public String PriceOn;
	public String PriceAfter;
	public String isBeat;
	public String currPrice;
	public int positiveStreak;
	public int negativeStreak;
	public double currAndLastPriceEffectDiff;
}