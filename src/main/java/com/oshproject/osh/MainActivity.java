package com.oshproject.osh;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, RecordCallbacks {
	private MainFragment mMainFragment;
	private Integer mMaxLength = 60000;
	private static final String KEY_PROGRESS = "key_progress";
	private static final String TAG_MAIN_FRAGMENT = "main_fragment";
	private ProgressBar progressBarRecord;
	private View overlay;
	private WaveformView mRealtimeWaveformView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		overlay = findViewById(R.id.overlay_record);
		final Snackbar snackbarListen = Snackbar.make(overlay, "Listening . . .", Snackbar.LENGTH_SHORT).setAction("Action", null);
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		Button buttonClose = (Button) findViewById(R.id.buttonClose);
		progressBarRecord = (ProgressBar) findViewById(R.id.progressbarRecord);
		FragmentManager mFragmentManager = getSupportFragmentManager();
		mRealtimeWaveformView = (WaveformView) findViewById(R.id.waveformView);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);

		toggle.syncState();

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				overlay.setVisibility(View.VISIBLE);
				snackbarListen.show();
			}
		});

		navigationView.setNavigationItemSelectedListener(this);

		buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				overlay.setVisibility(View.GONE);
				snackbarListen.dismiss();
			}
		});

		progressBarRecord.setMax(mMaxLength);
		progressBarRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
					if (mMainFragment.isTaskRunning()) {
						Toast.makeText(getBaseContext(), "CANCEL", Toast.LENGTH_SHORT).show();
						mMainFragment.cancel();
					} else {
						Toast.makeText(getBaseContext(), "START", Toast.LENGTH_SHORT).show();
						mMainFragment.start(mMaxLength);
					}
				} else {
					requestAudioPermission();
				}
			}
		});

		// Restore saved instances if exist.
		if (savedInstanceState != null) {
			progressBarRecord.setProgress(savedInstanceState.getInt(KEY_PROGRESS));
		}

		mMainFragment = (MainFragment) mFragmentManager.findFragmentByTag(TAG_MAIN_FRAGMENT);

		// Creating new fragment if none.
		if (mMainFragment == null) {
			mMainFragment = new MainFragment();
			mFragmentManager.beginTransaction().add(mMainFragment, TAG_MAIN_FRAGMENT).commit();
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	// Save important instances
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt(KEY_PROGRESS, progressBarRecord.getProgress());
	}

	// MainFragment/RecordingTask Callback Implementations
	@Override
	public void onPreExecute(){
		Toast.makeText(this, "Start Recording", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProgressUpdate(Long progress) {
		progressBarRecord.setProgress(progress.intValue());
	}

	@Override
	public void onCancelled() {
		progressBarRecord.setProgress(mMaxLength);
		Toast.makeText(this, "Stop Recording", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPostExecute() {
		progressBarRecord.setProgress(mMaxLength);
		Toast.makeText(this, "Stop Recording", Toast.LENGTH_SHORT).show();
	}

	private void requestAudioPermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
			Snackbar.make(overlay, "Permission to use microphone needed.",
					Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{
							android.Manifest.permission.RECORD_AUDIO}, 10);
				}
			}).show();
		} else {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{
					android.Manifest.permission.RECORD_AUDIO}, 10);
		}
	}
}
