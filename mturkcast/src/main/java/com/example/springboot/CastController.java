package com.example.springboot;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CastController {

	@Autowired
	private CastService castService;

	@GetMapping({ "/cast", "/cast?" })
	public String document(HttpServletRequest request, Model model) throws FileNotFoundException {
		return "document";
	}

}
