package com.stock.v1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.stock.v1.service.db.TaskServiceDB;
import com.stock.v1.vo.Task;

@Service
public class TaskService{

	@Autowired
	Environment appProp;
	
	@Autowired
	TaskServiceDB taskServiceDB;

	public List<Task> getTasks()
	{
		return taskServiceDB.getTasks();
	}	
}