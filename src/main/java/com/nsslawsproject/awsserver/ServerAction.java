package com.nsslawsproject.awsserver;

public interface ServerAction {
	public void execute();
	public void execute(int cores, int timeout, int iterations);
	
}
