package org.lemurproject.mturkadmin;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateMTurkQualAndHits {

	@Autowired
	private CreateMTurkQualificationType createQualType;

	@Autowired
	private CreateMTurkHits mturk;

	public void createHits(MTurkProperties properties) throws IOException {
		String qualificationType = createQualType.createQualification(properties);
		properties.setQualificationType(qualificationType);
		mturk.createHits(properties);
	}

}
