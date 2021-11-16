package org.lemurproject.mturkadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MTurkFilenameHelper {

	@Autowired
	private MTurkProperties properties;

	public String getHitFilename() {
		String hitFilename = String.join("", properties.getExperimentpath(), "hitids_", properties.getFilename());
		return hitFilename;
	}

	public String getFullJudgedDocFilename() {
		String fullFilename = String.join("", properties.getExperimentpath(), "full_judgeddocs_",
				properties.getFilename());
		return fullFilename;
	}

	public String getShortJudgedDocFilename() {
		String shortFilename = String.join("", properties.getExperimentpath(), "short_judgeddocs_",
				properties.getFilename());
		return shortFilename;
	}

}
