package com.nsslawsproject.awsserver;

import java.io.IOException;

public class TanhAction implements ServerAction {
	
	private int mItterations;
	private java.lang.Runtime rt;
	
	public TanhAction() {
		this((int) 2e6);
	}
	
	public TanhAction(int itterations) {
		mItterations = itterations;
	}
	public void SystemStress(int time) {
		rt = java.lang.Runtime.getRuntime();
		try {
			rt.exec("stress -c 1 -t " + time + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		for(int i=0; i<mItterations; i++) {
			SystemStress(1);
//			double a = Math.sqrt(Math.tanh(Math.sqrt(Math.PI)));
			//System.out.println(a);
		}
	}

}
