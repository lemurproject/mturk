package com.example.springboot;

import java.io.FileNotFoundException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

	// inject via application.properties
//	@Value("${welcome.message:test}")
//	private String message = "Hello World";
	
	@Autowired
	private HelloService helloService;

	@GetMapping({"/hello"})
	public String welcome(Map<String, Object> model) throws FileNotFoundException {
		Map<Integer, Map<Integer, String>> queries = helloService.getQueries();
		helloService.getQrels();
		
		String name = helloService.getName();
		model.put("message", name);
		//model.put("message", this.message);
		return "welcome";
	}
	
	

}
