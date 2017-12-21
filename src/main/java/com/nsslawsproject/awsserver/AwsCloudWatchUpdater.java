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
	// the aws cloudwatch updater is one of the backbone components of the server.
	// it is responsible for sending the running thread count average metric to the
	// aws cloud watch.
	
	private RunningAverageCalculator mRunningAverage; // the server's running average component
	private ReadWriteInt mInterval; // the update interval - given by the client (for configurability)
	private boolean mRun; // run the updater while mRun is true
	
	public AwsCloudWatchUpdater(RunningAverageCalculator runningAverage, ReadWriteInt interval) {
		mRunningAverage = runningAverage;
		mInterval = interval;
		this.setPriority(MAX_PRIORITY); // this thread gets top priority because it's important for the whole system stability
	}
	
	@Override
	public void run() {
		super.run();
//		this section is unneeded but was part of the example for custom metrics:
//		=========================================================================
//		/*
//         * The ProfileCredentialsProvider will return your [default]
//         * credential profile by reading from the credentials file located at
//         * (~/.aws/credentials).
//         */
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
        	    .withDimensions(dimension)
        	    .withStorageResolution(1); //sets this datum to be High-Resolution!!!
        	
        	
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
				Thread.sleep(mInterval.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void halt() {
		// stop the aws cloudwatch updater 
		mRun = false;
	}

}
