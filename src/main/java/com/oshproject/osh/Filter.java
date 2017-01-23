package com.oshproject.osh;

// Filters the signal. A low pass filter.
class Filter {
	private double mAlpha;
	private double mPastSignal = 0;

	Filter() {
		this.mAlpha = 0.94; // Default: 0.94 Alpha.
	}

	Filter(double alpha) {
		this.mAlpha = alpha;
	}

	double getFilteredSignal(double mCurrentSignal) {
		mPastSignal = (mCurrentSignal - (mAlpha * mPastSignal));
		return mPastSignal;
	}
}
