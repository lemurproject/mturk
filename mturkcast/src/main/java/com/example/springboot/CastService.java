package com.example.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("castService")
public class CastService {
	
	@Autowired
	private GlobalProperties properties;
	
	private int userNum = 0;
	private List<TaskInformation> tasks;
	
	public CastService() {
	}

}
