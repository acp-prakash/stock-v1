package com.stock.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.v1.service.TaskService;
import com.stock.v1.utils.Constants;
import com.stock.v1.utils.Constants.PAGES;
import com.stock.v1.vo.Task;

@Controller
public class TaskController {

	@Autowired
	TaskService taskService;

	@GetMapping(Constants.CONTEXT_TASK)
	public ModelAndView loadTasksView(String ticker)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(PAGES.TASK.value);
		return mv;
	}	
	
	@GetMapping(Constants.CONTEXT_GET_TASKS)
	public List<Task> getTasks()
	{		
		return taskService.getTasks();
	}
}