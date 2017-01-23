package com.oshproject.osh;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Task for getting audio samples and convert it to amplitude.
class PreProcess implements Runnable{
	private short[] mAudioBuffer;
	private Filter mFilter;
	protected Window mWindow;
	private static ThreadPoolExecutor mThreadPoolExecutor;
	private int i = 0;

	PreProcess() {
		mFilter = new Filter(0.94); // Default: 0.94 alpha
		mWindow = new Window(0.010, 0.020); // Default: 10ms Frame Length. 20ms Window Length. 4100 Sample Rate.
		mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	PreProcess(double alpha, double frameLengthInSeconds, double windowLengthInSeconds, double sampleRate) {
		mFilter = new Filter(alpha);
		mWindow = new Window(frameLengthInSeconds, windowLengthInSeconds, sampleRate);
		mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	PreProcess(double alpha, double frameLengthInSeconds, double windowLengthInSeconds) {
		mFilter = new Filter(alpha);
		mWindow = new Window(frameLengthInSeconds, windowLengthInSeconds);
		mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	public void run() {
		short[] mAudioBuffer = this.mAudioBuffer.clone();
		for (double mAudioSignal:mAudioBuffer) {
			mAudioSignal = mFilter.getFilteredSignal(mAudioSignal / 32768.0);
//			Log.v("PreProcessTask: ", String.format("%f %d", mAudioSignal, i++));
			// windowing signal in different thread.
			mWindow.execute(mAudioSignal);
		}

		if(mThreadPoolExecutor.getQueue().size() == 0 && mThreadPoolExecutor.isShutdown()) {
			mWindow.close();
			Log.e("PreProcessTask: ", String.format("ENDED %d",i));
		}

	}

	void execute(short[] audioBuffer) {
		this.mAudioBuffer = audioBuffer.clone();
		i += audioBuffer.length;
		mThreadPoolExecutor.execute(this);
	}

	void close() {
		mThreadPoolExecutor.shutdown();
	}

	void shutdownNow() {
		mWindow.shutdownNow();
		mThreadPoolExecutor.shutdownNow();
	}

	boolean isRunning() {
		return !mThreadPoolExecutor.isTerminated();
	}
}