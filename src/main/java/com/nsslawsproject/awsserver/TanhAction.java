package com.nsslawsproject.awsserver;

public class TanhAction implements ServerAction {
	
	private int mDefaultCores = 1;
	private int mDefaultTimeout = 1;
	private int mDefaultIterations = 1;

	@Override
	public void execute() {
		this.execute(mDefaultCores, mDefaultTimeout, mDefaultIterations);
	}

	@Override
	public void execute(int cores, int timeout, int iterations) {
		for(int i=0; i<iterations; i++) {
			double a = Math.sqrt(Math.tanh(Math.sqrt(Math.PI)));
			//System.out.println(a);
		}
	}

}
