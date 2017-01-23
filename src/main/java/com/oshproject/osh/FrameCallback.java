package com.oshproject.osh;

import java.util.List;

interface FrameCallback {
	void onFrameUpdate(List<Double> signalFrame);
}
