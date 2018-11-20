package com.atproj.rxshakedetector.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.atproj.rxshakedetector.factories.SensorEventObservableFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by andre on 15-Nov-18.
 */

public class ShakeDetector {

    public static final int THRESHOLD = 13;
    public static final int SHAKES_COUNT = 3;
    public static final int SHAKES_PERIOD = 1;

    public static Observable<?> create(Context context) {
        return createAccelerationObservable(context)
                .filter(sensorEvent -> Math.abs(sensorEvent.values[0]) > THRESHOLD)
                .map(sensorEvent -> new XEvent(sensorEvent.timestamp, sensorEvent.values[0]))
                .buffer(2, 1)
                .filter(buf -> buf.get(0).x * buf.get(1).x < 0)
                .map(buf -> buf.get(1).timestamp / 1000000000f)
                .buffer(SHAKES_COUNT, 1)
                .filter(buf -> buf.get(SHAKES_COUNT - 1) - buf.get(0) < SHAKES_PERIOD)
                .throttleFirst(SHAKES_PERIOD, TimeUnit.SECONDS);
    }

    private static Observable<SensorEvent> createAccelerationObservable(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensorList == null || sensorList.isEmpty()) {
            throw new IllegalStateException("Device has no linear acceleration sensor");
        }

        return SensorEventObservableFactory.createSensorEventObservable(sensorList.get(0), sensorManager);
    }

    private static class XEvent {
        public final long timestamp;
        public final float x;

        private XEvent(long timestamp, float x) {
            this.timestamp = timestamp;
            this.x = x;
        }

        @Override
        public String toString() {
            return "timestamp: " + timestamp + "; x:" + x;
        }
    }
}
