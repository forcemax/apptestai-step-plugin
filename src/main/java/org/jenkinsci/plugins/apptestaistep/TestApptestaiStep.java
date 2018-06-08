package org.jenkinsci.plugins.apptestaistep;

import java.io.Serializable;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class TestApptestaiStep extends Step implements Serializable {
	private static final long serialVersionUID = -121395699794391874L;
	
	private ApptestaiService service;

    @DataBoundConstructor
    public TestApptestaiStep(String accessKey, Integer projectId, String apkFilePath) {
    	service = new ApptestaiService(accessKey, projectId, apkFilePath);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new TestApptestaiExecution(context, this, service);
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(TestApptestaiExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "testApptestai";
        }

        @Override
        public String getDisplayName() {
            return "Start and wait Apptest.ai test completed.";
        }
    }
}
