package karolinakaminska.github.com;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LightSensor implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor;
    Set<LightSensorListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<LightSensorListener, Boolean>());

    public LightSensor(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        for (LightSensorListener i : listeners) {
            i.onLightChanged(sensorEvent.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void addListener(LightSensorListener lightSensorListener) {
        listeners.add(lightSensorListener);
    }

    public void removeListener(LightSensorListener lightSensorListener) {
        listeners.remove(lightSensorListener);
    }
}
