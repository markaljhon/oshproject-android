package com.oshproject.osh;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

// Task for playing audio
class PlayingTask implements Runnable{
	private AudioTrack track;
	private static Filter mFilter = new Filter(0.94);
	private short[] mAudioBuffer;

	PlayingTask() {
		track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
		track.play();
	}

	@Override
	public void run() {
//		short[] tempAudioBuffer = mAudioBuffer;
//		int i = 0;
//		for (short j:mAudioBuffer) {
//			Log.v("PlayingTask: ", String.format("%f %d", j / 32768.0, j));
//			tempAudioBuffer[i++] = mFilter.preEmphasis(j);
//		}
//		track.write(tempAudioBuffer, 0, tempAudioBuffer.length);
	}

	PlayingTask play(short[] mAudioBuffer) {
		this.mAudioBuffer = mAudioBuffer;
		track.write(mAudioBuffer, 0, mAudioBuffer.length);
		return this;
	}
}