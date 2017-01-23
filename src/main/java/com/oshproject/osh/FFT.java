package com.oshproject.osh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

class FFT implements Runnable{
	private ArrayList<Double> mSignalFrame;
	private static ThreadPoolExecutor mThreadPoolExecutor;

	@Override
	public void run() {

	}

	void execute(Double[] signalFrame) {
		this.mSignalFrame = (ArrayList) Arrays.asList(signalFrame);
		mThreadPoolExecutor.execute(this);
	}

	void close() {
		mThreadPoolExecutor.shutdown();
	}

	void shutdownNow() {
		mThreadPoolExecutor.shutdownNow();
	}

	boolean isRunning() {
		return !mThreadPoolExecutor.isTerminated();
	}

//	ComplexData getFFT() {
//
//	}
}