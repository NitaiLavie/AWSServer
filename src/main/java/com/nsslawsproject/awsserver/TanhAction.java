package com.nsslawsproject.awsserver;

public class TanhAction implements ServerAction {
	// a tanh action to be performed as the client task
	
	private int mDefaultCores = 1; // this parameter is degenerated
	private int mDefaultTimeout = 1; // this parameter is degenerated
	private int mDefaultIterations = 1; // the number of tanh actions to be performed in a loop

	@Override
	public void execute() {
		this.execute(mDefaultCores, mDefaultTimeout, mDefaultIterations);
	}

	@Override
	public void execute(int cores, int timeout, int iterations) {
		// running tanh operations in a loop
		for(int i=0; i<iterations; i++) {
			double a = Math.sqrt(Math.tanh(Math.sqrt(Math.PI)));
		}
	}

}
