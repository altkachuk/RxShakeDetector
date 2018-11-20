package com.atproj.rxshakedetector.ui.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.atproj.rxshakedetector.R;
import com.atproj.rxshakedetector.factories.SensorEventObservableFactory;
import com.atproj.rxshakedetector.utils.SensorPlotter;
import com.atproj.rxshakedetector.utils.ShakeDetector;
import com.atproj.rxshakedetector.utils.SoundUtil;
import com.jjoe64.graphview.GraphView;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";

    private final List<SensorPlotter> plotters = new ArrayList<>(3);

    private Observable<?> shakeObservable;
    private Disposable shakeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPlotters();

        shakeObservable = ShakeDetector.create(getApplicationContext());
    }

    private void setupPlotters() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> gravSensor = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        List<Sensor> accSensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> linearAccSensor = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);

        plotters.add(new SensorPlotter("GRAV", findViewById(R.id.graph1), SensorEventObservableFactory.createSensorEventObservable(gravSensor.get(0), sensorManager)));
        plotters.add(new SensorPlotter("ACC", findViewById(R.id.graph2), SensorEventObservableFactory.createSensorEventObservable(accSensor.get(0), sensorManager)));
        plotters.add(new SensorPlotter("LIN", findViewById(R.id.graph3), SensorEventObservableFactory.createSensorEventObservable(linearAccSensor.get(0), sensorManager)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // classic mode
        /*shakeDisposable = shakeObservable.subscribeWith(
                new DisposableObserver<SensorEvent>() {
                    @Override
                    public void onNext(SensorEvent event) {
                        SoundUtil.beep();
                    }
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable t) {

                    }
                }
        );*/
        Observable.fromArray(plotters).subscribe(
                plotters -> {for (SensorPlotter p : plotters) {p.onResume();}});

        // lambda mode
        shakeDisposable = shakeObservable.subscribe(
                event -> {
                    SoundUtil.beep();
                    Log.d(TAG, "value: " + event.toString());
                },
                error -> {
                    Log.d(TAG, error.getMessage());
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Observable.fromArray(plotters).subscribe(
                plotters -> {for (SensorPlotter p : plotters) {p.onPause();}});
        shakeDisposable.dispose();
    }
}
