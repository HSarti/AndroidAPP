package dte.masteriot.mdp.smartplantguardian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

public class MainActivity extends AppCompatActivity implements BluetoothHelper.BluetoothListener {

    private CircularProgressIndicator circularProgressIndicator;
    private TextView progressText;
    private LineChartView chartView;
    private float[] chartData;
    private int maxPoints = 7;
    private TextView soilStatusText;
    private BluetoothHelper bluetoothHelper;
    private Handler sensorTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable sensorTimeoutRunnable;
    private static final int SENSOR_TIMEOUT_MS = 60000; // 60 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soilStatusText = findViewById(R.id.soilStatusText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnFamilyActions = findViewById(R.id.btnFamilyActions);
        Button btnLightSensor = findViewById(R.id.btnLightSensor);

        btnFamilyActions.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FamilyActionsActivity.class))
        );

        btnLightSensor.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LightSensorActivity.class))
        );

        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WateringHistoryActivity.class))
        );


        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
        progressText = findViewById(R.id.progressText);
        chartView = findViewById(R.id.lineChartView);

        chartData = new float[maxPoints];
        for (int j = 0; j < maxPoints; j++) chartData[j] = 0;
        chartView.setData(chartData);
        startSensorTimeoutChecker();
        // Request permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        } else {
            startBluetooth();
        }
    }

    private void startBluetooth() {
        bluetoothHelper = new BluetoothHelper(this, this);
        bluetoothHelper.start();
    }
    private void startSensorTimeoutChecker() {
        sensorTimeoutRunnable = () -> runOnUiThread(() -> {
            // Triggered if no data is received within SENSOR_TIMEOUT_MS
            soilStatusText.setText("Sensor not sending data!");
            soilStatusText.setTextColor(Color.parseColor("#616161"));
            Toast.makeText(MainActivity.this, "Sensor disconnected or off", Toast.LENGTH_SHORT).show();
        });
        // Schedule the first check
        resetSensorTimeout();
    }

    private void resetSensorTimeout() {
        sensorTimeoutHandler.removeCallbacks(sensorTimeoutRunnable);
        sensorTimeoutHandler.postDelayed(sensorTimeoutRunnable, SENSOR_TIMEOUT_MS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startBluetooth();
        } else {
            Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewSoilMoistureData(int moisturePercent) {
        runOnUiThread(() -> {
            circularProgressIndicator.setProgress(moisturePercent);
            progressText.setText(moisturePercent + " %");

            for (int j = 0; j < chartData.length - 1; j++) chartData[j] = chartData[j + 1];
            chartData[chartData.length - 1] = moisturePercent;
            chartView.setData(chartData);
// Soil status with text + background colors
            if (moisturePercent < 40) {  // Dry
                soilStatusText.setText("Plant is Dry!");
                soilStatusText.setTextColor(Color.parseColor("#D32F2F")); // Softer red
            } else if (moisturePercent <= 80) {  // Healthy
                soilStatusText.setText("Plant is Healthy!");
                soilStatusText.setTextColor(Color.parseColor("#388E3C")); // Softer green
            } else {  // Overwatered
                soilStatusText.setText("Plant is too Wet!");
                soilStatusText.setTextColor(Color.parseColor("#FFA000")); // Orange
            }

            // Optional: round corners
            soilStatusText.setPadding(16, 16, 16, 16);
        });
        // Save sensor update only if value changed
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String status = moisturePercent < 40 ? "Plant is dry:" :
                moisturePercent <= 80 ? "Plant is Healthy:" :
                        "Soil is too wet:";
        String message = status + " ->" + moisturePercent + "% at " + timestamp;

        ActionStorage.saveSensorUpdate(MainActivity.this, moisturePercent, message);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothHelper != null) bluetoothHelper.stop();
    }


    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Increase menu text size for better readability
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new AbsoluteSizeSpan(20, true), 0, s.length(), 0); // 26sp for bigger text
            menuItem.setTitle(s);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_fao_data) {
            // “How to Use the App”
            startActivity(new Intent(this, FaoActivity.class));
            return true;

        } else if (itemId == R.id.action_info) {
            // Formerly “Tips & FAO”
            startActivity(new Intent(this, InfoActivity.class));
            return true;

        } else if (itemId == R.id.action_gardens) {
            // Madrid Gardens Data
            startActivity(new Intent(this, GardensActivity.class));
            return true;

        } else if (itemId == R.id.action_family_history) {
            // Watering Family History
            startActivity(new Intent(this, FamilyActionsHistoryActivity.class));
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}


