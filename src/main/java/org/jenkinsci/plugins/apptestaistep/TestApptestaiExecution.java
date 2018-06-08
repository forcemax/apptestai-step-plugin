package org.jenkinsci.plugins.apptestaistep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import hudson.EnvVars;
import hudson.model.TaskListener;

public class TestApptestaiExecution extends AbstractStepExecutionImpl {
    private static final long serialVersionUID = -6718328636399912927L;

    private boolean testInitiated = false;
    private Integer step_count = 0;
    private String result;

    private TestApptestaiStep step;
    private ApptestaiService service;
    
    protected transient PrintStream logger = null;
    protected transient TaskListener listener;
    protected transient EnvVars envVars;
    
    public TestApptestaiExecution(StepContext context, TestApptestaiStep step, ApptestaiService service) throws IOException, InterruptedException {
        super(context);
        this.step = step;
        this.service = service;
        listener = context.get(TaskListener.class);
        envVars = context.get(EnvVars.class);
    }

	@Override
	public boolean start() {
		try {
			logger = listener.getLogger();

			if (service == null || service.getAccessKey() == null || service.getApkFilePath() == null || service.getProjectId() == null) {
				getContext().onFailure(new AssertionError("accessKey, projectId, apkFilePath is necessary value."));
				return true;
			}
			
			String filepath = service.getApkFilePath();
			File apkFile = new File(filepath);
			if (!apkFile.exists()) {
				getContext().onFailure(new AssertionError("APK file is not accessible."));
				return true;
			}
			apkFile = null;
			
			if (!testInitiated) {
				logger.println("do initTest");
				logger.flush();

				testInitiated = initTest();
			}
			
			if (testInitiated && service.getTestSetId() != null) {
				logger.println("test Initiated");
				logger.flush();

	            new Thread("pollingAPI") {
	                @Override
	                public void run() {
	                    try {
	                    	pollingTest();
	                    }
	                    catch (Exception e) {
	                        TestApptestaiExecution.this.getContext().onFailure(e);
	                    }
	                }
	            }.start();
	            
	            return false;
			} else {
				logger.println("test not Initiated");
				logger.flush();
				
				if (service == null)
					logger.println("service is null.");
				else
					logger.println("service : " + service.toString());
				getContext().onFailure(new AssertionError("Test not Initiated."));
				return true;
			}
		} catch (Exception e) {
			logger.println(e.getMessage());
			getContext().onFailure(new AssertionError(e.getMessage()));
			return true;
		}
	}

    @Override
    public void stop(Throwable cause) throws Exception {
        getContext().onFailure(cause);
    }

    @Override
    public void onResume() {
        listener = null;
        try {
            listener = getContext().get(TaskListener.class);
            envVars = getContext().get(EnvVars.class);
        } catch (Exception e) {
            TestApptestaiExecution.this.getContext().onFailure(e);
            return;
        }
    	
        logger = listener.getLogger();
        super.onResume();
        
        try {
			start();
		} catch (Exception e) {
			logger.println("onResume Exception. " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    private boolean pollingTest() {
    	try {
    		logger = listener.getLogger();
    		
			logger.println("in pollingTest");
			logger.flush();
    		
	    	CloseableHttpClient httpClient = HttpClients.createDefault();
	    	HttpGet checkComplete = new HttpGet(service.getCheckCompleteAPI());
	
	    	logger.println("check test is running. : " + service.getTestSetId());
	    	logger.flush();
	    	
	    	boolean isComplete = false;
	    	while (!isComplete) {
	    		long sTime = System.currentTimeMillis();
	    		step_count++;
	    		CloseableHttpResponse response = httpClient.execute(checkComplete);
	    		
		    	if(response.getStatusLine().getStatusCode() != 200){
					throw new RuntimeException("API call fail.");
				}
	    		HttpEntity responseEntity = response.getEntity();
	    		String responseString = EntityUtils.toString(responseEntity);

		    	ObjectMapper mapper = new ObjectMapper();
	    		
		    	logger.println("check api response " + responseString);
		    	TestCompleteResponse jsonResponse = mapper.readValue(responseString, TestCompleteResponse.class);
		    	if (!jsonResponse.isComplete()) {
		    		long duration = System.currentTimeMillis() - sTime;
		    		Thread.sleep(15000 > duration ? 15000 - duration : 1);
		    		logger.println("Test progressing... " + String.valueOf(step_count * 15) + " seconds.");
		    		logger.flush();
		    		continue;
		    	}
		    	isComplete = true;
		    	result = jsonResponse.getData().getResultXml();
		    	getContext().onSuccess(result);
	    	}
    	} catch (Exception  e) {
    		logger.println("exception occur.");
    		logger.println(e.getMessage());
    		return false;
    	} 
    	return true;
    }

	private boolean initTest() {
		logger = listener.getLogger();
		
		logger.println("in initTest");
		logger.flush();

		String buildTag;
    	try {
	    	buildTag = envVars.get("BUILD_TAG");
	        if (buildTag == null || buildTag.isEmpty()) {
	            throw new RuntimeException("Project name cannot found.");
	        }
    	} catch (Exception e) {
    		return false;
    	}
    	
    	String body = "{\"pid\": {projectId}, \"test_set_name\": \"{buildTag}\"}"
    			.replace("{projectId}", String.valueOf(service.getProjectId()))
    			.replace("{buildTag}", buildTag);
    	
    	CloseableHttpResponse response;
    	try {
	    	CloseableHttpClient httpClient = HttpClients.createDefault();
	    	HttpPost uploadFile = new HttpPost(service.getTestStartAPI());
	    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    	builder.addTextBody("data", body, ContentType.TEXT_PLAIN);
	
	    	// This attaches the file to the POST:
	    	File f = new File(service.getApkFilePath());
	    	builder.addBinaryBody(
	    	    "apk_file",
	    	    new FileInputStream(f),
	    	    ContentType.APPLICATION_OCTET_STREAM,
	    	    f.getName()
	    	);
	    	HttpEntity multipart = builder.build();
	    	uploadFile.setEntity(multipart);
	    	
    		response = httpClient.execute(uploadFile);
    		
	    	if(response.getStatusLine().getStatusCode() != 200){
				throw new RuntimeException("API call fail.");
			}
    	} catch (Exception e) {
    		return false;
    	}
    	
    	try {
    		HttpEntity responseEntity = response.getEntity();
    		String responseString = EntityUtils.toString(responseEntity);
    		logger.println("start api response " + responseString);
    		
    		ObjectMapper mapper = new ObjectMapper();
    		
    		TestStartResponse jsonResponse = mapper.readValue(responseString, TestStartResponse.class);
    		service.setTestSetId(jsonResponse.getData().getTsid());
    		step_count = 0;
    		
	    	logger.println("Test started. ts_id : " + service.getTestSetId());
	    	logger.flush();
    	} catch (Exception e) {
    		return false;
    	}
    	
    	return true;
	}
	
}
