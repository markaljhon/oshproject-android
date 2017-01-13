package com.oshproject.osh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

/**
 * TODO: document your custom view class.
 */
public class WaveformView extends View {
	public static final int MODE_RECORDING = 1;

	private static final int HISTORY_SIZE = 6;

	private Paint mStrokePaint, mMarkerPaint;

	// Used in draw
	private int brightness;
	private Rect drawRect;

	private int width, height;
	private float xStep, centerY;
	private short[] mSamples;
	private LinkedList<float[]> mHistoricalData;
	private int colorDelta = 255 / (HISTORY_SIZE + 1);

	public WaveformView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public WaveformView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public WaveformView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.WaveformView, defStyle, 0);

		float strokeThickness = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness, 1f);
		int mStrokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
				ContextCompat.getColor(context, R.color.default_waveform));
		int mMarkerColor = a.getColor(R.styleable.WaveformView_playbackIndicatorColor,
				ContextCompat.getColor(context, R.color.default_playback_indicator));

		a.recycle();

		mStrokePaint = new Paint();
		mStrokePaint.setColor(mStrokeColor);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(strokeThickness);
		mStrokePaint.setAntiAlias(true);

		mMarkerPaint = new Paint();
		mMarkerPaint.setStyle(Paint.Style.STROKE);
		mMarkerPaint.setStrokeWidth(0);
		mMarkerPaint.setAntiAlias(true);
		mMarkerPaint.setColor(mMarkerColor);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		width = getMeasuredWidth();
		height = getMeasuredHeight();
		xStep = width / (1 * 1.0f);
		centerY = height / 2f;
		drawRect = new Rect(0, 0, width, height);

		if (mHistoricalData != null) {
			mHistoricalData.clear();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		LinkedList<float[]> temp = mHistoricalData;
		if (temp != null) {
			brightness = colorDelta;
			for (float[] p : temp) {
				mStrokePaint.setAlpha(brightness);
				canvas.drawLines(p, mStrokePaint);
				brightness += colorDelta;
			}
		}
	}

	public void setSamples(short[] samples) {
		mSamples = samples;
		onSamplesChanged();
	}

	private void onSamplesChanged() {
			if (mHistoricalData == null)
				mHistoricalData = new LinkedList<>();
			LinkedList<float[]> temp = new LinkedList<>(mHistoricalData);

			// For efficiency, we are reusing the array of points.
			float[] waveformPoints;
			if (temp.size() == HISTORY_SIZE) {
				waveformPoints = temp.removeFirst();
			} else {
				waveformPoints = new float[width * 4];
			}

			drawRecordingWaveform(mSamples, waveformPoints);
			temp.addLast(waveformPoints);
			mHistoricalData = temp;
			postInvalidate();
	}

	void drawRecordingWaveform(short[] buffer, float[] waveformPoints) {
		float lastX = -1;
		float lastY = -1;
		int pointIndex = 0;
		float max = Short.MAX_VALUE;

		// For efficiency, we don't draw all of the samples in the buffer, but only the ones
		// that align with pixel boundaries.
		for (int x = 0; x < width; x++) {
			int index = (int) (((x * 1.0f) / width) * buffer.length);
			short sample = buffer[index];
			float y = centerY - ((sample / max) * centerY);

			if (lastX != -1) {
				waveformPoints[pointIndex++] = lastX;
				waveformPoints[pointIndex++] = lastY;
				waveformPoints[pointIndex++] = x;
				waveformPoints[pointIndex++] = y;
			}

			lastX = x;
			lastY = y;
		}
	}
}
