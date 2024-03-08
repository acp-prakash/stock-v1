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
public class Pattern implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String histDate;
	public String ticker;
	public String id;
	public String entry;
	public String minPT;
	public String maxPT;
	public String stop;
	public String targetDate;
	public String name;
	public String trend;
	public String status;
	public int bull;
	public int bear;
	public int count;
	public Master all = new Master();
	public String fromPtPc;
}