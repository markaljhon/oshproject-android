package com.oshproject.osh;

interface RecordCallbacks {
	void onPreExecute();
	void onProgressUpdate(Long progress);
	void onCancelled();
	void onPostExecute();
}
