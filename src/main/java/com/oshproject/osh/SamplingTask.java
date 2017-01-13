package com.oshproject.osh;

import android.os.SystemClock;
import android.util.Log;

// Task for getting audio samples and convert it to amplitude
class SamplingTask implements Runnable{
	private short[] shAudioBuffer;
	private long lnSamples;

	SamplingTask(short[] shAudioBuffer, long lnSamples) {
		this.shAudioBuffer = shAudioBuffer;
		this.lnSamples = lnSamples;
	}

	@Override
	public void run() {
		int count = 1;
		for (double j:shAudioBuffer)
			Log.v("SamplingTask: ", String.format("%f %d %d %d %d", j/32768.0, SystemClock.currentThreadTimeMillis(), lnSamples, shAudioBuffer.length, count++));
	}
}