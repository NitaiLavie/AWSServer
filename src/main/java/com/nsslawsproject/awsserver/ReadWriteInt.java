package com.nsslawsproject.awsserver;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
// this class is meant to enable synchronized int instance and
// seperate the write lock from the read lock.
// additionally when the written value is the same as the current value - 
// no need to write it!
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
			// if get a new value - lock and write it
			mLock.readLock().unlock();
			mLock.writeLock().lock();
			mValue = value;
			mLock.writeLock().unlock();
		} else {
			// else no need to change the value
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
