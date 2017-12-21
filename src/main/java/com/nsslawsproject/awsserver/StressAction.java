package com.nsslawsproject.awsserver;

import java.io.IOException;

public class StressAction implements ServerAction {
	// this is a task that uses the unix "stress" command to stress the server.
	// it uses several parameters that are received from the client and are being
	// used by the stress command.
	
	private java.lang.Runtime rt;
	private int mDefaultCores = 1; // default number of cores to stress on
	private int mDefaultTimeout = 1; // default timeout for the stress action
	private int mDefaultIterations = 1; // how many stressing operation of the given parameters to perform
	
	private void SystemStress(int cores, int timeout) {
		// run the system stress action
		rt = java.lang.Runtime.getRuntime();
		try {
			// we are using the nice command to give this the lowest priority possible for backbone components integrity
			Process p = rt.exec("sudo nice -39 stress -c "+cores+" -t "+timeout+"\n");
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		this.execute(mDefaultCores, mDefaultTimeout, mDefaultIterations);
	}

	@Override
	public void execute(int cores, int timeout, int iterations) {
		for(int i=0; i<iterations; i++) {
			SystemStress(cores, timeout);
		}
	}

}
