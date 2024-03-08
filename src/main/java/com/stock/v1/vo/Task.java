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
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String seq;
	public String name;
	public String skip;	
	public String runDate;
	public String status;
	public String token;
	public String cookie1;
	public String cookie2;		
}