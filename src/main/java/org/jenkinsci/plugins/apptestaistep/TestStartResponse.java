package org.jenkinsci.plugins.apptestaistep;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestStartResponse implements Serializable {
	private static final long serialVersionUID = 348239472389423L;
	
	private String reason;
	private String result;
	private Integer errorCode;
	private Data data;
	
	class Data {
		private Integer testCount;
		private Integer tsid;
		
		public Data() {
		}
		
		@JsonProperty("test_count")
		public Integer getTestCount() {
			return testCount;
		}
		@JsonProperty("test_count")
		public void setTestCount(Integer testCount) {
			this.testCount = testCount;
		}
		public Integer getTsid() {
			return tsid;
		}
		public void setTsid(Integer tsid) {
			this.tsid = tsid;
		}
	}
	
	public TestStartResponse() {
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
}
