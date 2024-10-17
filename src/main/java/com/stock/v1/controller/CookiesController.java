package com.stock.v1.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stock.v1.cache.CookieCache;
import com.stock.v1.utils.Constants;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class CookiesController {

	@CrossOrigin
	@GetMapping(Constants.CONTEXT_GET_COOKIES)
	public @ResponseBody Map<String, String> getCookies()
	{
		return CookieCache.getAllCookies();		
	}
	
	@CrossOrigin
	@PostMapping(Constants.CONTEXT_ADD_COOKIES)
	public @ResponseBody String addCookies(HttpServletRequest request)
	{
		Map<String, String> cookies = CookieCache.getAllCookies();
		if(StringUtils.isNotBlank(request.getParameter("BARCHART_TOKEN")))
			cookies.put("BARCHART_TOKEN", request.getParameter("BARCHART_TOKEN"));
		if(StringUtils.isNotBlank(request.getParameter("BARCHART_COOKIE")))
			cookies.put("BARCHART_COOKIE", request.getParameter("BARCHART_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("ETRADE_TOKEN")))
			cookies.put("ETRADE_TOKEN", request.getParameter("ETRADE_TOKEN"));
		if(StringUtils.isNotBlank(request.getParameter("ETRADE_COOKIE")))
			cookies.put("ETRADE_COOKIE", request.getParameter("ETRADE_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("ETRADE_STK")))
			cookies.put("ETRADE_STK", request.getParameter("ETRADE_STK"));
		if(StringUtils.isNotBlank(request.getParameter("SEEKINGALPHA_COOKIE")))
			cookies.put("SEEKINGALPHA_COOKIE", request.getParameter("SEEKINGALPHA_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("ZACKS_COOKIE")))
			cookies.put("ZACKS_COOKIE", request.getParameter("ZACKS_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("ZEN_COOKIE")))
			cookies.put("ZEN_COOKIE", request.getParameter("ZEN_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("STOCK_ANALYSIS_COOKIE")))
			cookies.put("STOCK_ANALYSIS_COOKIE", request.getParameter("STOCK_ANALYSIS_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("MARKETBEAT_COOKIE")))
			cookies.put("MARKETBEAT_COOKIE", request.getParameter("MARKETBEAT_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("INVESTINGCOM_COOKIE")))
			cookies.put("INVESTINGCOM_COOKIE", request.getParameter("INVESTINGCOM_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("PORTFOLIO123_COOKIE")))
			cookies.put("PORTFOLIO123_COOKIE", request.getParameter("PORTFOLIO123_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("PORTFOLIO123_TOKEN")))
			cookies.put("PORTFOLIO123_TOKEN", request.getParameter("PORTFOLIO123_TOKEN"));
		if(StringUtils.isNotBlank(request.getParameter("STOCKINVEST_US_COOKIE")))
			cookies.put("STOCKINVEST_US_COOKIE", request.getParameter("STOCKINVEST_US_COOKIE"));
		if(StringUtils.isNotBlank(request.getParameter("STOCKINVEST_US_TOKEN")))
			cookies.put("STOCKINVEST_US_TOKEN", request.getParameter("STOCKINVEST_US_TOKEN"));
		
		CookieCache.setCookies(cookies);
		return "SUCCESS";
	}
	
	@CrossOrigin
	@PostMapping(Constants.CONTEXT_CLEAR_COOKIES)
	public @ResponseBody String clearCookies()
	{
		CookieCache.clearAllCookies();
		return "SUCCESS";
	}
}