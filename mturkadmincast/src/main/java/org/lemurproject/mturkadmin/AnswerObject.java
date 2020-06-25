package org.lemurproject.mturkadmin;

public class AnswerObject {

	private String hitId;
	private String queryId;

	private String docId_1;
	private String docId_2;
	private String docId_3;
	private String docId_4;
	private String docId_5;

	private ResponseObject relevance0_1;
	private ResponseObject relevance1_1;
	private ResponseObject relevance2_1;
	private ResponseObject relevance3_1;
	private ResponseObject relevance4_1;

	private ResponseObject relevance0_2;
	private ResponseObject relevance1_2;
	private ResponseObject relevance2_2;
	private ResponseObject relevance3_2;
	private ResponseObject relevance4_2;

	private ResponseObject relevance0_3;
	private ResponseObject relevance1_3;
	private ResponseObject relevance2_3;
	private ResponseObject relevance3_3;
	private ResponseObject relevance4_3;

	private ResponseObject relevance0_4;
	private ResponseObject relevance1_4;
	private ResponseObject relevance2_4;
	private ResponseObject relevance3_4;
	private ResponseObject relevance4_4;

	private ResponseObject relevance0_5;
	private ResponseObject relevance1_5;
	private ResponseObject relevance2_5;
	private ResponseObject relevance3_5;
	private ResponseObject relevance4_5;

	private String score_1;
	private String score_2;
	private String score_3;
	private String score_4;
	private String score_5;

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getDoc1WorkerScore() {
		String score = "0";
		if (relevance4_1.getSelected().booleanValue()) {
			score = "4";
		} else if (relevance3_1.getSelected().booleanValue()) {
			score = "3";
		} else if (relevance2_1.getSelected().booleanValue()) {
			score = "2";
		} else if (relevance1_1.getSelected().booleanValue()) {
			score = "1";
		}
		return score;
	}

	public String getDoc2WorkerScore() {
		String score = "0";
		if (relevance4_2.getSelected().booleanValue()) {
			score = "4";
		} else if (relevance3_2.getSelected().booleanValue()) {
			score = "3";
		} else if (relevance2_2.getSelected().booleanValue()) {
			score = "2";
		} else if (relevance1_2.getSelected().booleanValue()) {
			score = "1";
		}
		return score;
	}

	public String getDoc3WorkerScore() {
		String score = "0";
		if (relevance4_3.getSelected().booleanValue()) {
			score = "4";
		} else if (relevance3_3.getSelected().booleanValue()) {
			score = "3";
		} else if (relevance2_3.getSelected().booleanValue()) {
			score = "2";
		} else if (relevance1_3.getSelected().booleanValue()) {
			score = "1";
		}
		return score;
	}

	public String getDoc4WorkerScore() {
		String score = "0";
		if (relevance4_4.getSelected().booleanValue()) {
			score = "4";
		} else if (relevance3_4.getSelected().booleanValue()) {
			score = "3";
		} else if (relevance2_4.getSelected().booleanValue()) {
			score = "2";
		} else if (relevance1_4.getSelected().booleanValue()) {
			score = "1";
		}
		return score;
	}

	public String getDoc5WorkerScore() {
		String score = "0";
		if (relevance4_5.getSelected().booleanValue()) {
			score = "4";
		} else if (relevance3_5.getSelected().booleanValue()) {
			score = "3";
		} else if (relevance2_5.getSelected().booleanValue()) {
			score = "2";
		} else if (relevance1_5.getSelected().booleanValue()) {
			score = "1";
		}
		return score;
	}

	public String getDocId_1() {
		return docId_1;
	}

	public void setDocId_1(String docId_1) {
		this.docId_1 = docId_1;
	}

	public ResponseObject getRelevance0_1() {
		return relevance0_1;
	}

	public void setRelevance0_1(ResponseObject relevance0_1) {
		this.relevance0_1 = relevance0_1;
	}

	public ResponseObject getRelevance4_1() {
		return relevance4_1;
	}

	public void setRelevance4_1(ResponseObject relevance4_1) {
		this.relevance4_1 = relevance4_1;
	}

	public String getDocId_2() {
		return docId_2;
	}

	public void setDocId_2(String docId_2) {
		this.docId_2 = docId_2;
	}

	public String getDocId_3() {
		return docId_3;
	}

	public void setDocId_3(String docId_3) {
		this.docId_3 = docId_3;
	}

	public String getDocId_4() {
		return docId_4;
	}

	public void setDocId_4(String docId_4) {
		this.docId_4 = docId_4;
	}

	public String getDocId_5() {
		return docId_5;
	}

	public void setDocId_5(String docId_5) {
		this.docId_5 = docId_5;
	}

	public ResponseObject getRelevance1_1() {
		return relevance1_1;
	}

	public void setRelevance1_1(ResponseObject relevance1_1) {
		this.relevance1_1 = relevance1_1;
	}

	public ResponseObject getRelevance2_1() {
		return relevance2_1;
	}

	public void setRelevance2_1(ResponseObject relevance2_1) {
		this.relevance2_1 = relevance2_1;
	}

	public ResponseObject getRelevance3_1() {
		return relevance3_1;
	}

	public void setRelevance3_1(ResponseObject relevance3_1) {
		this.relevance3_1 = relevance3_1;
	}

	public ResponseObject getRelevance0_2() {
		return relevance0_2;
	}

	public void setRelevance0_2(ResponseObject relevance0_2) {
		this.relevance0_2 = relevance0_2;
	}

	public ResponseObject getRelevance1_2() {
		return relevance1_2;
	}

	public void setRelevance1_2(ResponseObject relevance1_2) {
		this.relevance1_2 = relevance1_2;
	}

	public ResponseObject getRelevance2_2() {
		return relevance2_2;
	}

	public void setRelevance2_2(ResponseObject relevance2_2) {
		this.relevance2_2 = relevance2_2;
	}

	public ResponseObject getRelevance3_2() {
		return relevance3_2;
	}

	public void setRelevance3_2(ResponseObject relevance3_2) {
		this.relevance3_2 = relevance3_2;
	}

	public ResponseObject getRelevance4_2() {
		return relevance4_2;
	}

	public void setRelevance4_2(ResponseObject relevance4_2) {
		this.relevance4_2 = relevance4_2;
	}

	public ResponseObject getRelevance0_3() {
		return relevance0_3;
	}

	public void setRelevance0_3(ResponseObject relevance0_3) {
		this.relevance0_3 = relevance0_3;
	}

	public ResponseObject getRelevance1_3() {
		return relevance1_3;
	}

	public void setRelevance1_3(ResponseObject relevance1_3) {
		this.relevance1_3 = relevance1_3;
	}

	public ResponseObject getRelevance2_3() {
		return relevance2_3;
	}

	public void setRelevance2_3(ResponseObject relevance2_3) {
		this.relevance2_3 = relevance2_3;
	}

	public ResponseObject getRelevance3_3() {
		return relevance3_3;
	}

	public void setRelevance3_3(ResponseObject relevance3_3) {
		this.relevance3_3 = relevance3_3;
	}

	public ResponseObject getRelevance4_3() {
		return relevance4_3;
	}

	public void setRelevance4_3(ResponseObject relevance4_3) {
		this.relevance4_3 = relevance4_3;
	}

	public ResponseObject getRelevance0_4() {
		return relevance0_4;
	}

	public void setRelevance0_4(ResponseObject relevance0_4) {
		this.relevance0_4 = relevance0_4;
	}

	public ResponseObject getRelevance1_4() {
		return relevance1_4;
	}

	public void setRelevance1_4(ResponseObject relevance1_4) {
		this.relevance1_4 = relevance1_4;
	}

	public ResponseObject getRelevance2_4() {
		return relevance2_4;
	}

	public void setRelevance2_4(ResponseObject relevance2_4) {
		this.relevance2_4 = relevance2_4;
	}

	public ResponseObject getRelevance3_4() {
		return relevance3_4;
	}

	public void setRelevance3_4(ResponseObject relevance3_4) {
		this.relevance3_4 = relevance3_4;
	}

	public ResponseObject getRelevance4_4() {
		return relevance4_4;
	}

	public void setRelevance4_4(ResponseObject relevance4_4) {
		this.relevance4_4 = relevance4_4;
	}

	public String getScore_1() {
		return score_1;
	}

	public void setScore_1(String score_1) {
		this.score_1 = score_1;
	}

	public String getScore_2() {
		return score_2;
	}

	public void setScore_2(String score_2) {
		this.score_2 = score_2;
	}

	public String getScore_3() {
		return score_3;
	}

	public void setScore_3(String score_3) {
		this.score_3 = score_3;
	}

	public String getScore_4() {
		return score_4;
	}

	public void setScore_4(String score_4) {
		this.score_4 = score_4;
	}

	public String getScore_5() {
		return score_5;
	}

	public void setScore_5(String score_5) {
		this.score_5 = score_5;
	}

	public ResponseObject getRelevance0_5() {
		return relevance0_5;
	}

	public void setRelevance0_5(ResponseObject relevance0_5) {
		this.relevance0_5 = relevance0_5;
	}

	public ResponseObject getRelevance1_5() {
		return relevance1_5;
	}

	public void setRelevance1_5(ResponseObject relevance1_5) {
		this.relevance1_5 = relevance1_5;
	}

	public ResponseObject getRelevance2_5() {
		return relevance2_5;
	}

	public void setRelevance2_5(ResponseObject relevance2_5) {
		this.relevance2_5 = relevance2_5;
	}

	public ResponseObject getRelevance3_5() {
		return relevance3_5;
	}

	public void setRelevance3_5(ResponseObject relevance3_5) {
		this.relevance3_5 = relevance3_5;
	}

	public ResponseObject getRelevance4_5() {
		return relevance4_5;
	}

	public void setRelevance4_5(ResponseObject relevance4_5) {
		this.relevance4_5 = relevance4_5;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof AnswerObject) {
			AnswerObject answer2 = (AnswerObject) obj;
			if (this.docId_1.equals(answer2.docId_1) && this.docId_2.equals(answer2.docId_2)
					&& this.docId_3.equals(answer2.docId_3) && this.docId_4.equals(answer2.docId_4)
					&& this.docId_5.equals(answer2.docId_5)) {
				equals = true;
			}
		}
		return equals;
	}

	public String getHitId() {
		return hitId;
	}

	public void setHitId(String hitId) {
		this.hitId = hitId;
	}

}
