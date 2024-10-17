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
public class Monitor implements Serializable {

	private static final long serialVersionUID = 1L;

	public String ticker;
	public String monitorPrice;
	public String currentPrice;
	public String monitorChg;
	public String currentChg;	
	public String upDays;
	public String downDays;
	public String upPrice;
	public String downPrice;
	public String target;
	public String targetDate;
	public String comments;
	public String status;
	public String addDate;
	public String offDate;
	public Master master;
}