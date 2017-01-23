package com.oshproject.osh;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Frames the signal. A Hamming Window Function.
class Window implements Runnable{
	private int mFrameLength;
	private int mWindowLength;
	private double mSampleRate = 44100; // Default sample rate is 44.1kHz.
	private double mAudioSignal;
	private List<Double> mWindowBuffer;
	private List<Double> tempWindowBuffer;
//	private int mCurrentSamplePosition = 0; // the 'n' in hamming window function.
	private List<List<Double>> WindowedSignalList = new ArrayList<List<Double>>(); // hold frames
	private static ThreadPoolExecutor mThreadPoolExecutor;
	private FrameCallback mFrameCallback = null;

	Window(double frameLengthInSeconds, double windowLengthInSeconds, double sampleRate) {
		this.mFrameLength = (int) (frameLengthInSeconds * sampleRate);
		this.mWindowLength = (int) (windowLengthInSeconds * sampleRate);
		if(this.mWindowLength % 2 != 0)
			this.mWindowLength++;
		this.mSampleRate = sampleRate;
//		this.mWindowBuffer = new Double[mWindowLength];
//		this.tempWindowBuffer = new Double[mWindowLength];
		this.mWindowBuffer = new ArrayList<Double>();
		this.tempWindowBuffer = new ArrayList<Double>();
		mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	Window(double frameLengthInSeconds, double windowLengthInSeconds) {
		this.mFrameLength = (int) (frameLengthInSeconds * mSampleRate);
		this.mWindowLength = (int) (windowLengthInSeconds * mSampleRate);
		if(this.mWindowLength % 2 != 0)
			this.mWindowLength++;
//		this.mWindowBuffer = new Double[mWindowLength];
//		this.tempWindowBuffer = new Double[mWindowLength];
		this.mWindowBuffer = new ArrayList<Double>();
		this.tempWindowBuffer = new ArrayList<Double>();
		mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	public void run() {
		double mAudioSignal = this.mAudioSignal;
		double windowedSignal;

		// Catch overlapped signals.
		if(mWindowBuffer.size() > mFrameLength) {
//			tempWindowBuffer[mCurrentSamplePosition - mFrameLength] = (0.54 - (0.46 * Math.cos((2 * Math.PI * (mCurrentSamplePosition - mFrameLength)) / (mWindowLength - 1)))) * mAudioSignal;
			tempWindowBuffer.add((0.54 - (0.46 * Math.cos((2 * Math.PI * (tempWindowBuffer.size())) / (mWindowLength - 1)))) * mAudioSignal);
//			Log.v("Window: ", String.format("tempWindowBuffer: %f", mWindowBuffer[(int)(mCurrentSamplePosition - mFrameLength)]));
		}

//		mWindowBuffer[mCurrentSamplePosition] = (0.54 - (0.46 * Math.cos((2 * Math.PI * mCurrentSamplePosition) / (mWindowLength - 1)))) * mAudioSignal;
		mWindowBuffer.add((0.54 - (0.46 * Math.cos((2 * Math.PI * mWindowBuffer.size()) / (mWindowLength - 1)))) * mAudioSignal);
//		Log.v("Window: ", String.format("mWindowBuffer: %f", mWindowBuffer.get(mWindowBuffer.size()-1)));

		// End of Window.
		if(mWindowBuffer.size() == mWindowLength) {
			if(mFrameCallback == null)
				WindowedSignalList.add(mWindowBuffer);
			else
				mFrameCallback.onFrameUpdate(mWindowBuffer);
//			mCurrentSamplePosition = mFrameLength;
//			mWindowBuffer = tempWindowBuffer.clone();
			mWindowBuffer = tempWindowBuffer;
//			tempWindowBuffer = new Double[mWindowLength];
			tempWindowBuffer = new ArrayList<Double>();
		}

//		mCurrentSamplePosition++;

		if(mThreadPoolExecutor.getQueue().size() == 0 && mThreadPoolExecutor.isShutdown()) {
			Log.e("Window: ", String.format("ENDED: %d", tempWindowBuffer.size()));
			if(tempWindowBuffer.size() != 0) {
				while(mWindowBuffer.size() != mWindowLength) {
					mWindowBuffer.add(0.0);
				}
				mFrameCallback.onFrameUpdate(mWindowBuffer);
			}
		}
	}

	void execute(double audioSignal) {
		this.mAudioSignal = audioSignal;
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

	void setFrameCallback(FrameCallback frameCallback) {
		this.mFrameCallback = frameCallback;
	}

}
