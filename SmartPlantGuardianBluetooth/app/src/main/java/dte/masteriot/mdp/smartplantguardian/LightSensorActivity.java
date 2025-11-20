package dte.masteriot.mdp.smartplantguardian;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightSensorActivity extends AppCompatActivity implements SensorEventListener {

    private TextView luxValue, luxDescription;
    private SensorManager sensorManager;
    private Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);

        luxValue = findViewById(R.id.luxValue);
        luxDescription = findViewById(R.id.luxDescription);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        luxValue.setText(String.format("%.2f lx", lux));

        // Simple description based on lux
        if(lux < 50) {
            luxDescription.setText("Very dim light — plant may need more sun");
        } else if(lux < 200) {
            luxDescription.setText("Moderate light — suitable for shade plants");
        } else {
            luxDescription.setText("Bright light — good for sun-loving plants");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Optional: handle sensor accuracy changes
    }
}
