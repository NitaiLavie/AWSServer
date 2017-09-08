package com.nsslawsproject.awsserver;

import java.io.IOException;

public class StressAction implements ServerAction {
	
	private java.lang.Runtime rt;
	private int mDefaultCores = 1;
	private int mDefaultTimeout = 1;
	private int mDefaultIterations = 1;
	
	public void SystemStress(int cores, int timeout) {
		rt = java.lang.Runtime.getRuntime();
		try {
			Process p = rt.exec("stress -c "+cores+" -t "+timeout+"\n");
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
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
			//double a = Math.sqrt(Math.tanh(Math.sqrt(Math.PI)));
			//System.out.println(a);
		}
	}

}
