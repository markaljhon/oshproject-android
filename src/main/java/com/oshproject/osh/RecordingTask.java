package com.oshproject.osh;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Task for recording. Main Task.
class RecordingTask extends AsyncTask<Integer, Long, Void> {

	private RecordCallbacks mRecordCallbacks;
	protected boolean RunningState = false;
	private static final int SAMPLE_RATE = 44100;
	private short[] tempAudioBuffer;
	private AudioRecord mAudioRecord;
	private int iBufferSize;
	private PreProcess mPreProcess;
	private MFCC mMFCC;

	RecordingTask(RecordCallbacks recordCallbacks) {
		mRecordCallbacks = recordCallbacks;
		iBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		RunningState = false;

		if (iBufferSize == AudioRecord.ERROR || iBufferSize == AudioRecord.ERROR_BAD_VALUE) {
			iBufferSize = SAMPLE_RATE * 2;
		}

		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufferSize);
		tempAudioBuffer = new short[iBufferSize];
	}

	@Override
	protected void onPreExecute() {
		RunningState = true;
		Log.v("RecordingTask:", String.format("Pre: %d", iBufferSize));
	}


	@Override
	protected Void doInBackground(Integer... maxLength) {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		mPreProcess = new PreProcess();
		mMFCC = new MFCC();
		mPreProcess.mWindow.setFrameCallback(mMFCC);
		mAudioRecord.startRecording();

		while (!isCancelled()/* && time <= maxLength[0]*/ && RunningState) {
			mAudioRecord.read(tempAudioBuffer, 0, tempAudioBuffer.length);
			mPreProcess.execute(tempAudioBuffer);
			//publishProgress(SystemClock.currentThreadTimeMillis()*100);
		}
		mPreProcess.close();
		mAudioRecord.stop();
		mAudioRecord.release();
		Log.v("RecordingTask:", "ENDED");
		RunningState = false;
		return null;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		mRecordCallbacks.onProgressUpdate(progress[0]);
	}

	@Override
	protected void onCancelled() {
		if(mPreProcess != null && mPreProcess.isRunning())
			mPreProcess.shutdownNow();
		RunningState = false;
	}

	@Override
	protected void onPostExecute(Void ignore) {
	}
}