package com.stock.v1.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.LiveStockCache;
import com.stock.v1.cache.MasterStocksCache;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.UtilityService;
import com.stock.v1.vo.Master;
import com.stock.v1.vo.Picks;
import com.stock.v1.vo.Stock;

@Service
public class PicksService{

	@Autowired
	StockService stockService;

	public List<Picks> getPicks()
	{
		List<Picks> picksList = LiveStockCache.getPicks();
		//List<Master> masterList = stockService.getMasterList();	
		List<Master> masterList = MasterStocksCache.getMasterStocks();

		picksList.forEach(x -> masterList.forEach(y -> {
			if (x.getTicker().equalsIgnoreCase(y.getTicker())) {
				x.setMaster(y);				
				x.setDiff(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(y.getPrice()) - Double.valueOf(x.getEntry())), false));
			}
		})	
				);

		for(Picks pick : picksList)
		{
			List<Stock> histList = stockService.getStockHistoryByDate(pick.getTicker(), pick.getAddedDate());
			if(histList != null)
			{
				for(Stock hist : histList)
				{
					if(Constants.LONG.equalsIgnoreCase(pick.getType()))
					{
						procssLONG(hist, pick);						
					}
					else if(Constants.SHORT.equalsIgnoreCase(pick.getType()))
					{
						procssSHORT(hist, pick);
					}
				}
			}
		}

		picksList.forEach( x -> {if(x.getL() != null && Constants.LONG.equalsIgnoreCase(x.getType())) {
			double currPL = 1000 * (Double.valueOf(x.getMaster().getPrice()) - Double.valueOf(x.getAddPrice()));
			double profit = 1000 * (Double.valueOf(x.getH()) - Double.valueOf(x.getAddPrice()));			
			double loss = 1000 * (Double.valueOf(x.getAddPrice()) - Double.valueOf(x.getL()));
			if(loss <= 0)
				loss = 0;
			x.setProfitLoss(UtilityService.stripStringToTwoDecimals(String.valueOf(currPL), false));
			x.setMaxProfit(UtilityService.stripStringToTwoDecimals(String.valueOf(profit), false));
			x.setMaxLoss(UtilityService.stripStringToTwoDecimals(String.valueOf(loss), false));
			x.setMaxDiff(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(x.getL()) - Double.valueOf(x.getEntry())), false));}});
		picksList.forEach( x -> {if(x.getH() != null && Constants.SHORT.equalsIgnoreCase(x.getType())) {
			double currPL = 1000 * (Double.valueOf(x.getAddPrice()) - Double.valueOf(x.getMaster().getPrice()));
			double profit = 1000 * (Double.valueOf(x.getAddPrice()) - Double.valueOf(x.getL()));
			double loss = 1000 * (Double.valueOf(x.getH()) - Double.valueOf(x.getAddPrice()));
			if(profit <= 0)
				profit = 0;
			x.setProfitLoss(UtilityService.stripStringToTwoDecimals(String.valueOf(currPL), false));
			x.setMaxProfit(UtilityService.stripStringToTwoDecimals(String.valueOf(profit), false));
			x.setMaxLoss(UtilityService.stripStringToTwoDecimals(String.valueOf(loss), false));
			x.setMaxDiff(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(x.getEntry()) - Double.valueOf(x.getH())), false));}});

		return picksList;
	}

	private void procssLONG(Stock hist, Picks pick)
	{
		if("A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getAddedDate())))
		{
			if(StringUtils.isBlank(pick.getH()) || Double.valueOf(pick.getH()) <  Double.valueOf(hist.getHigh()))
				pick.setH(hist.getHigh());

			if(StringUtils.isBlank(pick.getL()) || Double.valueOf(pick.getL()) >  Double.valueOf(hist.getLow()))
				pick.setL(hist.getLow());

			if(!"Y".equalsIgnoreCase(pick.getTarget1Met()) && !"Y".equalsIgnoreCase(pick.getTarget2Met()) && 
					!"Y".equalsIgnoreCase(pick.getStopReached()) && Double.valueOf(hist.getLow()) <= Double.valueOf(pick.getStop()))
			{
				pick.setStopReached("Y");
				pick.setStopReachedDate(hist.getDate());
				pick.setResult("STOPPED");
			}

			if(!"Y".equalsIgnoreCase(pick.getTarget1Met()))
			{
				if(Double.valueOf(hist.getHigh()) >= Double.valueOf(pick.getExit1()))
				{
					pick.setTarget1Met("Y");
					pick.setTarget1MetDate(hist.getDate());
					if("A".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())) ||
							"E".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())))
					{
						if("Y".equalsIgnoreCase(pick.getStopReached()))
							pick.setResult("STOP-HIT-T1");
						else
							pick.setResult("HIT-T1");
					}
					else
						pick.setResult("DELAY-HIT-T1");
				}
				if(!"Y".equalsIgnoreCase(pick.getTarget1Met()) &&
						!"Y".equalsIgnoreCase(pick.getTarget2Met()) &&
						"A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getTargetDate())))
					pick.setResult("MISSED");
			}

			if(!"Y".equalsIgnoreCase(pick.getTarget2Met()))
			{
				if(Double.valueOf(hist.getHigh()) >= Double.valueOf(pick.getExit2()))
				{
					pick.setTarget2Met("Y");
					pick.setTarget2MetDate(hist.getDate());
					if("A".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())) ||
							"E".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())))
					{
						if("Y".equalsIgnoreCase(pick.getStopReached()))
							pick.setResult("STOP-HIT-T2");
						else
							pick.setResult("HIT-T2");
					}
					else
						pick.setResult("DELAY-HIT-T2");
				}
				if(!"Y".equalsIgnoreCase(pick.getTarget1Met()) &&
						!"Y".equalsIgnoreCase(pick.getTarget2Met()) &&
						"A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getTargetDate())))
					pick.setResult("MISSED");
			}
		}
	}

	private void procssSHORT(Stock hist, Picks pick)
	{
		if("A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getAddedDate())))
		{
			if(StringUtils.isBlank(pick.getH()) || Double.valueOf(pick.getH()) <  Double.valueOf(hist.getHigh()))
				pick.setH(hist.getHigh());

			if(StringUtils.isBlank(pick.getL()) || Double.valueOf(pick.getL()) >  Double.valueOf(hist.getLow()))
				pick.setL(hist.getLow());

			if(!"Y".equalsIgnoreCase(pick.getTarget1Met()) && !"Y".equalsIgnoreCase(pick.getTarget2Met()) && 
					!"Y".equalsIgnoreCase(pick.getStopReached()) && Double.valueOf(hist.getHigh()) >= Double.valueOf(pick.getStop()))
			{
				pick.setStopReached("Y");
				pick.setStopReachedDate(hist.getDate());
				pick.setResult("STOPPED");
			}

			if(!"Y".equalsIgnoreCase(pick.getTarget1Met()))
			{
				if(Double.valueOf(hist.getLow()) <= Double.valueOf(pick.getExit1()))
				{
					pick.setTarget1Met("Y");
					pick.setTarget1MetDate(hist.getDate());
					if("A".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())) ||
							"E".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())))
					{
						if("Y".equalsIgnoreCase(pick.getStopReached()))
							pick.setResult("STOP-HIT-T1");
						else
							pick.setResult("HIT-T1");
					}
					else
						pick.setResult("DELAY-HIT-T1");
				}
				if(!"Y".equalsIgnoreCase(pick.getTarget1Met()) &&
						!"Y".equalsIgnoreCase(pick.getTarget2Met()) &&
						"A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getTargetDate())))
					pick.setResult("MISSED");
			}

			if(!"Y".equalsIgnoreCase(pick.getTarget2Met()))
			{
				if(Double.valueOf(hist.getLow()) <= Double.valueOf(pick.getExit2()))
				{
					pick.setTarget2Met("Y");
					pick.setTarget2MetDate(hist.getDate());
					if("A".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())) ||
							"E".equalsIgnoreCase(UtilityService.compareDates(pick.getTargetDate(), hist.getDate())))
					{
						if("Y".equalsIgnoreCase(pick.getStopReached()))
							pick.setResult("STOP-HIT-T2");
						else
							pick.setResult("HIT-T2");
					}
					else
						pick.setResult("DELAY-HIT-T2");
				}
				if(!"Y".equalsIgnoreCase(pick.getTarget1Met()) &&
						!"Y".equalsIgnoreCase(pick.getTarget2Met()) &&
						"A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getTargetDate())))
					pick.setResult("MISSED");
			}
		}
	}	
}