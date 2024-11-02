package com.stock.v1.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.WebSocket.MyWebSocketHandler;
import com.stock.v1.service.OptionsService;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Options;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OptionsController {
	
	@Autowired
	OptionsService optionsService;
	
	@Autowired
	MyWebSocketHandler handler;
	
	@GetMapping({Constants.CONTEXT_PATH_SLASH, Constants.CONTEXT_HOME, Constants.CONTEXT_PATH_WELCOME, Constants.CONTEXT_OPTIONS})
	public ModelAndView loadOptionsView(HttpServletRequest request, String ticker, String user)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.OPTIONS.value);
		if(StringUtils.isBlank(user))
			user = (String) request.getSession().getAttribute("USER");
		mv.addObject("USER", user);
		request.getSession().setAttribute("USER", user);
		return mv;
	}
	
	@GetMapping(Constants.CONTEXT_OPTIONS_HISTORY)
	public ModelAndView loadOptionsHistoryView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.OPTIONS_HISTORY.value);
		mv.addObject("TICKER",ticker);
		return mv;
	}
	
	@CrossOrigin	
	@GetMapping("/options/getOptions")
	public @ResponseBody List<Options> getOptions()
	{
		List<Options> list = optionsService.getOptions();
		handler.sendMessage(list);
		return list;
	}

	@CrossOrigin
	@GetMapping("/options/history")
	public @ResponseBody List<Options> getOptionsHistory(String ticker)
	{
		return optionsService.getOptionsHistory(ticker);
	}
	
	@CrossOrigin
	@PostMapping("/options/delete")
	public @ResponseBody boolean deleteOption(String ticker)
	{
		return optionsService.deleteOption(ticker);
	}
	
	@CrossOrigin	
	@PostMapping("/options/populateOptions")
	public @ResponseBody List<Options> populateOptions()
	{		
		List<Options> list = optionsService.populateOptions();
		handler.sendMessage(optionsService.getOptions());
		return list;
	}
	
	@CrossOrigin	
	@PostMapping("/options/updateOptions")
	public @ResponseBody List<Options> updateOptions(@RequestBody List<Options> updatedList)
	{		
		return optionsService.updateOptions(updatedList);		
	}
}