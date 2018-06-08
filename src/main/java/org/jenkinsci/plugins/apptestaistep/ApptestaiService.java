package org.jenkinsci.plugins.apptestaistep;

import java.io.Serializable;

public class ApptestaiService implements Serializable {
    private static final long serialVersionUID = 472389472389L;
    
    public static final String SERVICE_URL = "https://api.apptest.ai/";
    public static final String TEST_START_API = SERVICE_URL + "test_set/queuing?access_key={access_key}";
    public static final String CHECK_COMPLETE_API = SERVICE_URL + "test_set/{ts_id}/ci_info?access_key={access_key}";
    		
    
    private String accessKey;
    private Integer projectId;
    private String apkFilePath;
    private Integer testSetId;
    
    public ApptestaiService() {}

    public ApptestaiService(String accessKey, Integer projectId, String apkFilePath) {
        this.setAccessKey(accessKey);
        this.setProjectId(projectId);
        this.setApkFilePath(apkFilePath);
    }

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getApkFilePath() {
		return apkFilePath;
	}

	public void setApkFilePath(String apkFilePath) {
		this.apkFilePath = apkFilePath;
	}

	public Integer getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Integer testSetId) {
		this.testSetId = testSetId;
	}
    
    public String getTestStartAPI() {
    	if ( accessKey != null )
    		return TEST_START_API.replace("{access_key}", accessKey);
    	else 
    		return null;
    }
    
    public String getCheckCompleteAPI() {
    	if ( accessKey != null && testSetId != null) 
    		return CHECK_COMPLETE_API.replace("{access_key}", accessKey).replace("{ts_id}", String.valueOf(testSetId));
    	else
    		return null;
    }

	@Override
	public String toString() {
		return "ApptestaiService [accessKey=" + accessKey + ", projectId=" + projectId + ", apkFilePath=" + apkFilePath
				+ ", testSetId=" + testSetId + "]";
	}
}
