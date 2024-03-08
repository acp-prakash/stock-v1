package com.stock.v1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;

@Controller
public class LearningController {
	
	@GetMapping(Constants.CONTEXT_OPTIONS_WS)
	public ModelAndView loadWebSocketView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.OPTIONS_WS.value);
		return mv;
	}	
}