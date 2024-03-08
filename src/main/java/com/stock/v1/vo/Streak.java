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
public class Streak implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int currentPositiveStreak = 0;
	public int maxPositiveStreak = 0;
	public int currentNegativeStreak = 0;
	public int maxNegativeStreak = 0;
}