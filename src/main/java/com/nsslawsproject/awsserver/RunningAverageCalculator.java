package com.nsslawsproject.awsserver;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RunningAverageCalculator extends Thread {
	private Double mAverage = 0.0;
	private AtomicInteger mThreadCounter;
	private Integer[] mRunningThreadsArray;
	private int mArrayIndex = 0;
	private int mFullSlots = 0;
	private boolean mRun;
	private final int mArrayLength = 60;
	private final Lock mAverageLock;
	
	public RunningAverageCalculator(AtomicInteger threadCounter) {
		mThreadCounter = threadCounter;
		mRunningThreadsArray = new Integer[mArrayLength];
		mAverageLock = new ReentrantLock();
	}
	
	@Override
	public void run() {
		super.run();
		mRun=true;
		int newVal=0;
		int oldVal=0;
		while(mRun) {
			newVal = mThreadCounter.get();
			if(mFullSlots < mArrayLength) oldVal = 0;
			else oldVal = mRunningThreadsArray[mArrayIndex];
			
			if(mFullSlots < mArrayLength) {
				mAverageLock.lock();
					mAverage = ((mAverage * mFullSlots) + newVal - oldVal) / (mFullSlots+1);
				mAverageLock.unlock();
				mFullSlots ++;
			} else {
				mAverageLock.lock();
					mAverage = ((mAverage * mFullSlots) + newVal - oldVal) / mFullSlots;
				mAverageLock.unlock();
			}
			
			
			
			mRunningThreadsArray[mArrayIndex] = newVal;
			
			if(mArrayIndex < mArrayLength-1) mArrayIndex++;
			else mArrayIndex = 0;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void halt() {
		mRun=false;
	}
	
	public double getRunningAverage() {
		double average;
		mAverageLock.lock();
			average = mAverage.doubleValue();
		mAverageLock.unlock();
		return average;
	}
}
