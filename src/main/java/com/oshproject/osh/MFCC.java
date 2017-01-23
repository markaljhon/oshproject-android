package com.oshproject.osh;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// Task for Mel Frequency Cepstral Coefficients Algorithm.
class MFCC implements FrameCallback{
	protected List<List<Double>> FrameList = new ArrayList<List<Double>>(); // hold frames
	private int currentFrameIndex = 0;

	@Override
	public void onFrameUpdate(List<Double> signalFrame) {
		this.FrameList.add(signalFrame);
		Log.e("MFCC: ",  String.format("ENDED: %d %d", FrameList.size(), FrameList.get(0).size()));
	}

	void setNewFrameList(List<List<Double>> frameList) {
		this.FrameList = frameList;
	}

	void appendFrameList(List<List<Double>> frameList) {
		this.FrameList.addAll(frameList);
	}


}
