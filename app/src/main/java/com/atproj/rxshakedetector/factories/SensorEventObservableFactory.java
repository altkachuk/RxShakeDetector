package com.atproj.rxshakedetector.factories;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.MainThread;

import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Cancellable;

/**
 * Created by andre on 15-Nov-18.
 */

public class SensorEventObservableFactory {

    public static Observable<SensorEvent> createSensorEventObservable(Sensor sensor, SensorManager sensorManager) {

        return Observable.create(subscriber -> {
            MainThreadDisposable.verifyMainThread();

            SensorEventListener listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (subscriber.isDisposed()) {
                        return;
                    }

                    subscriber.onNext(sensorEvent);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);

            // TODO: unregister listener in main thread when being unsubscribed
            // ...
        });
    }
}
