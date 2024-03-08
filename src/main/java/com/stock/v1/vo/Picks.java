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
public class Picks implements Serializable {

	private static final long serialVersionUID = 1L;

	public String ticker;
	public String entry;
	public String exit1;
	public String exit2;
	public String stop;
	public String targetDate;
	public String addedDate;
	public String addPrice;
	public String currPrice;
	public String stopReached = "N";
	public String stopReachedDate;
	public String target1Met = "N";
	public String target1MetDate;
	public String target2Met = "N";
	public String target2MetDate;	
	public Master master;
	public String result = "PENDING";
	public String diff;
	public String maxDiff;
	public String h;
	public String l;
}