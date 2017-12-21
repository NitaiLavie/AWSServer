package com.nsslawsproject.awsserver;

public interface ServerAction {
	// this is a general interface for the client task action
	public void execute();
	public void execute(int cores, int timeout, int iterations);
	
}
