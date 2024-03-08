package com.stock.v1.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.v1.cache.LiveStockCache;
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
		List<Master> masterList = stockService.getMasterList();	
		
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
							if("A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getTargetDate())))
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
							if("A".equalsIgnoreCase(UtilityService.compareDates(hist.getDate(), pick.getTargetDate())))
								pick.setResult("MISSED");
						}
					}
				}
			}
		}
		
		picksList.forEach( x -> 
		x.setMaxDiff(UtilityService.stripStringToTwoDecimals(String.valueOf(Double.valueOf(x.getL()) - Double.valueOf(x.getEntry())), false)));

		return picksList;
	}
}