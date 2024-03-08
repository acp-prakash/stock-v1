package com.stock.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.service.PicksService;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Picks;

@Controller
public class PicksController {

	@Autowired
	PicksService picksService;	

	@GetMapping(Constants.CONTEXT_PICKS)
	public ModelAndView loadPicksView()
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.PICKS.value);
		return mv;
	}
	
	@GetMapping({Constants.CONTEXT_PATH_SLASH, Constants.CONTEXT_HOME, Constants.CONTEXT_PATH_WELCOME, Constants.CONTEXT_PICKS_USER})
	public ModelAndView loadPicksUserView()
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.PICKS_USER.value);
		return mv;
	}
	
	@CrossOrigin	
	@PostMapping(Constants.CONTEXT_GET_PICKS)
	public @ResponseBody List<Picks> getPicks()
	{
		return picksService.getPicks();
	}	
}