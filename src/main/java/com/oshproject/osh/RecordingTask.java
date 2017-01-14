package com.oshproject.osh;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
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
	private short[] tempAudioBuffer;
	private AudioRecord mAudioRecord;
	private int iBufferSize;
	private static ExecutorService mExecutorService;
	long lnSamples = 0;AudioTrack track;

	RecordingTask(RecordCallbacks recordCallbacks) {
		mRecordCallbacks = recordCallbacks;
		iBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		RunningState = false;

		if (iBufferSize == AudioRecord.ERROR || iBufferSize == AudioRecord.ERROR_BAD_VALUE) {
			iBufferSize = SAMPLE_RATE * 2;
		}

		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, iBufferSize);
		mExecutorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		track = new AudioTrack(AudioManager.STREAM_ALARM, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
		AudioRecord.OnRecordPositionUpdateListener mUpdateListener =
				new AudioRecord.OnRecordPositionUpdateListener()
				{
					short[] mAudioBuffer = new short[44100];

					@Override
					public void onPeriodicNotification(AudioRecord recorder) {
						mAudioBuffer = null;
						mAudioBuffer = new short[44100];
						lnSamples += mAudioRecord.read(mAudioBuffer, 0, mAudioBuffer.length);
						//mExecutorService.execute(new SamplingTask(mAudioBuffer, lnSamples));
//						mExecutorService.execute(new PlayingTask(mAudioBuffer));
						//track.write(mAudioBuffer, 0, mAudioBuffer.length);
						//track.play();
					}

					@Override
					public void onMarkerReached(AudioRecord recorder)
					{
						// do nothing.
					}
				};
//		mAudioRecord.setRecordPositionUpdateListener(mUpdateListener);
//		mAudioRecord.setPositionNotificationPeriod(44100);

		tempAudioBuffer = new short[iBufferSize];
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
		PlayingTask mPlayingTask = new PlayingTask();
		int i = 0;
		mAudioRecord.startRecording();

		while (!isCancelled()/* && time <= maxLength[0]*/) {
			mAudioRecord.read(tempAudioBuffer, 0, tempAudioBuffer.length);
//			track.write(tempAudioBuffer, 0, tempAudioBuffer.length);
//			if (track.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
//				track.play();
			mExecutorService.execute(mPlayingTask.play(tempAudioBuffer));
			//publishProgress(SystemClock.currentThreadTimeMillis()*100);
		}
		return null;
	}

	long time = 0;

	@Override
	protected void onProgressUpdate(Long... progress) {
		mRecordCallbacks.onProgressUpdate(progress[0]);
		time = progress[0];
	}

	@Override
	protected void onCancelled() {
		Log.v("RecordingTask: ", String.format("%d",time));
		RunningState = false;
		mAudioRecord.stop();
		mAudioRecord.release();
		mRecordCallbacks.onCancelled();
	}

	@Override
	protected void onPostExecute(Void ignore) {
		Log.v("RecordingTask: ", String.format("%d",time));
		RunningState = false;
		mAudioRecord.stop();
		mAudioRecord.release();
		mRecordCallbacks.onPostExecute();
	}
}