package com.nsslawsproject.awsserver;

public class TanhAction implements ServerAction {
	
	private int mItterations;
	
	public TanhAction() {
		this((int) 2e6);
	}
	
	public TanhAction(int itterations) {
		mItterations = itterations;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		for(int i=0; i<mItterations; i++) {
			double a = Math.sqrt(Math.tanh(Math.sqrt(Math.PI)));
			//System.out.println(a);
		}
	}

}
