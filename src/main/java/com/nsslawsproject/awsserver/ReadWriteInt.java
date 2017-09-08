package com.nsslawsproject.awsserver;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteInt {
	private Integer mValue;
	private ReadWriteLock mLock;
	
	public ReadWriteInt(int value) {
		mLock = new ReentrantReadWriteLock();
		mLock.writeLock().lock();
		mValue = value;
		mLock.writeLock().unlock();
	}
	
	public void set(int value) {
		mLock.readLock().lock();
		if(value != mValue.intValue()) {
			mLock.readLock().unlock();
			mLock.writeLock().lock();
			mValue = value;
			mLock.writeLock().unlock();
		} else {
			mLock.readLock().unlock();
		}
	}
	
	public int get() {
		mLock.readLock().lock();
		int returnValue = mValue.intValue();
		mLock.readLock().unlock();
		return returnValue;
	}

}
