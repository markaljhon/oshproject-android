package com.oshproject.osh;

import android.os.SystemClock;
import android.util.Log;

// Task for getting audio samples and convert it to amplitude
class SamplingTask implements Runnable{
	private short[] mAudioBuffer;
	private long lnSamples;

	SamplingTask(short[] mAudioBuffer, long lnSamples) {
		this.mAudioBuffer = mAudioBuffer;
		this.lnSamples = lnSamples;
	}

	@Override
	public void run() {
		int count = 1;
		for (long j:mAudioBuffer)
			Log.v("SamplingTask: ", String.format("%f %d %d %d %d", j/32768.0, SystemClock.currentThreadTimeMillis(), lnSamples, mAudioBuffer.length, count++));
	}
}