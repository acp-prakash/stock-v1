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
public class Rating implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String btAnalysts;
	public String btAnalystRating;
	public String btShortRating;
	public String btLongRating;
	public String btRating;
	public String btTrend;
	public String zacksRank;
	public String zacksRating;
	public String siusScore;
	public String siusRating;
	public String siusDays;	
	public String nasdaqRating;
	public String tipRating;
	public String tipBuyHoldSell;
	public String streetRating;
	public String streetScore;	
	public String markbeatPT;
	public String markbeatUpDown;
	public String markbeatRating;	
	public String zenRating;
	public String zenTarget;
	public String zenTargetUpDown;
	public String zenScore;	
	public String stockAnalysisRating;
	public String stockAnalysisTarget;
	public String stockAnalysisTargetUpDown;
	public String investObserveScore;
	public String investObserveLT;
	public String investObserveHT;	
	public String investComRating;
	public String investComTarget;	
	public String seekAlphaQuantRating;
	public String seekAlphaWallstreetRating;
	public String seekAlphaAnalystsRating;	
	public String tradingViewTechRating;
	public String tradingViewAnalystsRating;
	public String tradingViewMARating;
	public String tradingViewOSRating;
	public String tradingViewBullBearPower;	
	public String tickeronRating;
	public String tickeronRatingAt;
	public String tickeronRatingOn;
	public String tickeronAIRating;
	public String tickeronUnderOver;
	public String portfolio123Rating;
	public String portfolio123HighPT;
	public String portfolio123LowPT;
	public String portfolio123Analysts;	
	public String finscreenerRating;
	public String finscreenerPT;
	public String finscreenerAnalysts;	
	public String marketEdgeRating;
	public String marketEdgeConfScore;
}