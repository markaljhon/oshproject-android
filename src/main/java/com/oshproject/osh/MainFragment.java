package com.oshproject.osh;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

 public class MainFragment extends Fragment {
	private static final String LOG_TAG = MainFragment.class.getSimpleName();

	protected RecordCallbacks mRecordCallbacks;
	private  RecordingTask mRecordingTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof RecordCallbacks)) {
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}
		mRecordCallbacks = (RecordCallbacks) activity;
		mRecordingTask = new RecordingTask(mRecordCallbacks);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// Start recording thread.
	public void startRecord(Integer maxLength) {
		if (mRecordingTask == null || !mRecordingTask.RunningState)
			mRecordingTask = new RecordingTask(mRecordCallbacks);
		mRecordingTask.execute(maxLength);
	}

	// Drop recording thread. Stop all threads related to recording task.
	public void cancelRecord() {
		mRecordingTask.cancel(true);
	}

	// Stop only the recording in recording thread.
	public void stopRecord() {
		mRecordingTask.RunningState = false;
	}

	public boolean isTaskRunning() {
		return mRecordingTask.RunningState;
	}
}