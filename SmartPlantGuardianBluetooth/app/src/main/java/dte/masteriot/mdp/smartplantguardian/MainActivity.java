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

public class MainActivity extends AppCompatActivity implements BluetoothHelper.BluetoothListener {

    private CircularProgressIndicator circularProgressIndicator;
    private TextView progressText;
    private LineChartView chartView;
    private float[] chartData;
    private int maxPoints = 7;

    private BluetoothHelper bluetoothHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        });
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId(); // Get the ID once

        if (itemId == R.id.action_info) {
            startActivity(new Intent(this, InfoActivity.class));
            return true;
        } else if (itemId == R.id.action_fao_data) {
            startActivity(new Intent(this, FaoActivity.class));
            return true;
        } else if (itemId == R.id.action_gardens) {
            startActivity(new Intent(this, GardensActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}


