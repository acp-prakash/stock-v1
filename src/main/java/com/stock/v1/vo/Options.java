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
public class Options implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String key;
	public String histDate;
	public String name;
	public String ticker;	
	public String type;
	public String entry;
	public String exit;
	public String price;	
	public String open;
	public String high;
	public String low;
	public String change;
	public String addChange;
	public String volume;
	public String interest;
	public String addedDate;
	public String status;
	public String source;
	public String exitDate;
	public Stock stock;	
	public String daysToExpire;
	public String delta;
	public String theta;
	public String gamma;
	public String iv;	
	public String aHigh;
	public String aLow;
	public int upDays;
	public int downDays;
	public String upBy;
	public String downBy;
	public Pattern pattern;		
}