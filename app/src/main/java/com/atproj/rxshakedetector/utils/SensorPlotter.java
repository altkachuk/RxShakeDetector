package com.atproj.rxshakedetector.utils;

import android.graphics.Color;
import android.hardware.SensorEvent;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by andre on 20-Nov-18.
 */

public class SensorPlotter {

    public static final int MAX_DATA_POINTS = 50;
    public static final int VIEWPORT_SECONDS = 5;
    public static final int FPS = 10;

    private final String name;
    private final Observable<SensorEvent> sensorEventObservable;

    protected final LineGraphSeries<DataPoint> seriesX;
    protected final LineGraphSeries<DataPoint> seriesY;
    protected final LineGraphSeries<DataPoint> seriesZ;

    private final long start =  System.currentTimeMillis();
    private long lastUpdated = start;

    private Disposable disposable;

    public SensorPlotter(String name, GraphView graphView, Observable<SensorEvent> sensorEventObservable) {
        this.name = name;
        this.sensorEventObservable = sensorEventObservable;

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(20);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);

        seriesX = new LineGraphSeries<>();
        seriesY = new LineGraphSeries<>();
        seriesZ = new LineGraphSeries<>();
        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.BLUE);

        graphView.addSeries(seriesX);
        graphView.addSeries(seriesY);
        graphView.addSeries(seriesZ);
    }

    public void onResume() {
        disposable = sensorEventObservable.subscribe(this::onSensorChanged);
    }

    public void onPause() {
        disposable.dispose();
    }

    private void onSensorChanged(SensorEvent event) {
        if (!canUpdateUI()) {
            return;
        }

        appendData(seriesX, event.values[0]);
        appendData(seriesY, event.values[1]);
        appendData(seriesZ, event.values[2]);
    }

    private boolean canUpdateUI() {
        long now = System.currentTimeMillis();
        if (now - lastUpdated < 1000 / FPS) {
            return false;
        }
        lastUpdated = now;
        return true;
    }

    private void appendData(LineGraphSeries<DataPoint> series, double value) {
        series.appendData(new DataPoint(getX(), value), true, MAX_DATA_POINTS);
    }

    private long getX() {
        return System.currentTimeMillis() - start;
    }

}
