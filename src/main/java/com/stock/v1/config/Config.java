package com.stock.v1.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {

	@Bean
	@ConfigurationProperties(prefix="ihelp.datasource")
	public DataSource ihelpDataSource() {
	    return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "ihelpJdbcTemplate")
	public JdbcTemplate ihelpJdbcTemplate(DataSource ihelpDataSource) throws SQLException{
		Connection connection = DataSourceUtils.getConnection(ihelpDataSource);
		connection.setAutoCommit(false);
		return new JdbcTemplate(ihelpDataSource);
	}
}