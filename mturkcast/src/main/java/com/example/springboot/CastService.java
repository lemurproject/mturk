package com.example.springboot;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Component("castService")
public class CastService {

	@Autowired
	private GlobalProperties properties;

	private int userNum = 0;
	private List<TaskInformation> tasks;

	public CastService() throws IOException, URISyntaxException {
		// Load CSV data
		Reader reader = Files.newBufferedReader(Paths.get("mturk_cast_all.csv"));
		Iterator<HITObject> csvHITs = readAll(reader);
		HITObject first = csvHITs.next();
		HITObject second = csvHITs.next();
		System.out.println("Got HITs from CSV");
	}

	public Iterator<HITObject> readAll(Reader reader) throws IOException {
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		return csvToBean.iterator();
	}

}
