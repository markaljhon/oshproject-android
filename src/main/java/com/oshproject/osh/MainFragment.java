package com.oshproject.osh;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainFragment extends Fragment {
	private static final String LOG_TAG = MainFragment.class.getSimpleName();

	protected RecordCallbacks mRecordCallbacks;
	private boolean mRunningState = false;
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

	// taga simula ng recording task
	public void start(Integer maxLength) {
		mRunningState = true;
		mRecordingTask.execute(maxLength);
	}

	// taga tigil ng recording task
	public void cancel() {
		mRunningState = false;
		mRecordingTask.cancel(true);
		mRecordingTask = null;
		mRecordingTask = new RecordingTask(mRecordCallbacks);
	}

	public boolean isTaskRunning() {
		return mRecordingTask.RunningState;
	}
}