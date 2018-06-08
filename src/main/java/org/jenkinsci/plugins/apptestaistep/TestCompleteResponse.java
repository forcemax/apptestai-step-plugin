package org.jenkinsci.plugins.apptestaistep;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCompleteResponse implements Serializable {
	private static final long serialVersionUID = 2735187098509865973L;

	private String reason;
	private String result;
	private Integer errorCode;
	private boolean complete;
	private Data data;
	
	class Data {
		private String resultXml;
		
		public Data() {
		}

		@JsonProperty("result_xml")
		public String getResultXml() {
			return resultXml;
		}

		@JsonProperty("result_xml")
		public void setResultXml(String resultXml) {
			this.resultXml = resultXml;
		}
	}
	
	public TestCompleteResponse() {
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
