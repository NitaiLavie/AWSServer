package com.nsslawsproject.awsserver;

//aws imports
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;


public class AwsCloudWatchUpdater extends Thread {
	
	private RunningAverageCalculator mRunningAverage;
	private boolean mRun;
	
	public AwsCloudWatchUpdater(RunningAverageCalculator runningAverage) {
		mRunningAverage = runningAverage;
	}
	
	@Override
	public void run() {
		super.run();
		/*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
//        AWSCredentials credentials = null;
//        try {
//            credentials = new ProfileCredentialsProvider().getCredentials();
//        } catch (Exception e) {
//            throw new AmazonClientException(
//                    "Cannot load the credentials from the credential profiles file. " +
//                    "Please make sure that your credentials file is at the correct " +
//                    "location (~/.aws/credentials), and is in valid format.",
//                    e);
//        }
        
        // setting up the custom metric
        final AmazonCloudWatch cw =
        	    AmazonCloudWatchClientBuilder.defaultClient();

        	Dimension dimension = new Dimension()
        	    .withName("SERVER_METRICS")
        	    .withValue("RUNNING_THREADS");

        	MetricDatum datum = new MetricDatum()
        	    .withMetricName("AVG_THREAD_COUNT")
        	    .withUnit(StandardUnit.Count)
        	    .withDimensions(dimension);
        	
        	
//        	PutMetricDataRequest request = new PutMetricDataRequest()
//        		    .withNamespace("AWSSERVER/METRICS");
        mRun=true;
        while(mRun) {
        	datum.setValue(mRunningAverage.getRunningAverage());
        	PutMetricDataRequest request = new PutMetricDataRequest()
        		    .withNamespace("AWSSERVER/METRICS")
        			.withMetricData(datum);
        	PutMetricDataResult response = cw.putMetricData(request);
        	try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public void halt() {
		mRun = false;
	}

}
