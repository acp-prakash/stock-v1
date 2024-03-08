package com.stock.v1.service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.stock.v1.utils.DBConstants;
import com.stock.v1.vo.Task;

@Service
public class TaskServiceDB{

	@Autowired
	@Qualifier("ihelpJdbcTemplate")
	private JdbcTemplate ihelpJdbcTemplate;
	
	public List<Task> getTasks()
	{
		String sql = DBConstants.GET_TASKS;		
		List<Map<String, Object>> retResultMap = ihelpJdbcTemplate.queryForList(sql);

	    return retResultMap.stream()
	            .map(this::mapToTasks)
	            .filter(task -> task != null)  // Filter out any null results
	            .collect(Collectors.toList());
	}	
	
	private Task mapToTasks(Map<String, Object> retRes) {
		Task task = new Task();
	    retRes.forEach((key, value) -> {
	        if (value != null) {
	            switch (key.toUpperCase()) {	              
	                case "SEQ":
	                	task.setSeq((String) value);
	                    break;	                
	                case "NAME":
	                	task.setName((String) value);
	                    break;
	                case "SKIP":
	                	task.setSkip((String) value);
	                    break;
	                case "STATUS":
	                	task.setStatus((String) value);
	                    break;
	                case "RUN_DATE":
	                	task.setRunDate((String) value);
	                    break;
	                case "TOKEN":
	                	task.setToken((String) value);
	                    break;
	                case "COOKIE_1":
	                	task.setCookie1((String) value);
	                    break;
	                case "COOKIE_2":
	                	task.setCookie2((String) value);
	                    break;	               
	                default:
	                    break;  // Handle other keys if needed
	            }
	        }
	    });
	    return task;
	}
	
	public boolean addToTask(Task task) {
		String sql = "INSERT INTO STOCK_TASKS (SEQ, NAME) "
				+ "VALUES (?, ?)";

		try (Connection conn = ihelpJdbcTemplate.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, task.getSeq());
			ps.setString(2, task.getName());

			int[] batchResult = ps.executeBatch();

			// Check if all the batch statements were successful
			for (int result : batchResult) {
				if (result == PreparedStatement.EXECUTE_FAILED) {
					return false;
				}
			}
			return true;
		} catch (SQLException ex) {
			System.err.println("ERROR ==> addToTask ==> "+ ex);
			return false;
		}
	}
}