package com.oshproject.osh;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Task para sa recording
class RecordingTask extends AsyncTask<Integer, Long, Void> {

	private RecordCallbacks mRecordCallbacks;
	boolean RunningState = false;
	private static final int SAMPLE_RATE = 44100;
	private short[] shAudioBuffer;
	private AudioRecord mAudioRecord;
	private int iBufferSize;
	private static ExecutorService mExecutorService;

	RecordingTask(RecordCallbacks recordCallbacks) {
		mRecordCallbacks = recordCallbacks;
		iBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		RunningState = false;

		if (iBufferSize == AudioRecord.ERROR || iBufferSize == AudioRecord.ERROR_BAD_VALUE) {
			iBufferSize = SAMPLE_RATE * 2;
		}

		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufferSize);
		mExecutorService = new ThreadPoolExecutor(1, 1,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	@Override
	protected void onPreExecute() {
		Log.v("RecordingTask:", String.format("Pre: %d", iBufferSize));
		RunningState = true;
		mRecordCallbacks.onPreExecute();
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
	}


	@Override
	protected Void doInBackground(Integer... maxLength) {
		mAudioRecord.startRecording();

		long lnSamples = 0;
		while (!isCancelled()/* && time <= maxLength[0]*/) {
			shAudioBuffer = null;
			shAudioBuffer = new short[iBufferSize];
			lnSamples += mAudioRecord.read(shAudioBuffer, 0, shAudioBuffer.length);
			mExecutorService.execute(new SamplingTask(shAudioBuffer, lnSamples));
//			new Thread(new SamplingTask(shAudioBuffer, lnSamples)).start();
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					for (double j:shApublishProgress(time);udioBuffer)
//						Log.v("RecordingTask: ", String.format("%f %d %d %d %d", j/32768.0, SystemClock.currentThreadTimeMillis(), lnSamples, count++, time));
//				}
//			}).start();
			publishProgress(SystemClock.currentThreadTimeMillis());
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		mRecordCallbacks.onProgressUpdate(progress[0]);
	}

	@Override
	protected void onCancelled() {
		RunningState = false;
		mAudioRecord.stop();
		mAudioRecord.release();
		mRecordCallbacks.onCancelled();
	}

	@Override
	protected void onPostExecute(Void ignore) {
		RunningState = false;
		mAudioRecord.stop();
		mAudioRecord.release();
		mRecordCallbacks.onPostExecute();
	}
}