package dte.masteriot.mdp.smartplantguardian;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothHelper {

    public interface BluetoothListener {
        void onNewSoilMoistureData(int moisturePercent);
    }

    private final Context context;
    private final BluetoothListener listener;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private Thread btThread;
    private boolean running = false;

    private static final String DEVICE_NAME = "ESP32-SOIL";
    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothHelper(Context context, BluetoothListener listener) {
        this.context = context;
        this.listener = listener;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void start() {
        if (running) return;
        running = true;

        btThread = new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    running = false;
                    return;
                }
            }

            BluetoothDevice device = null;
            for (BluetoothDevice d : bluetoothAdapter.getBondedDevices()) {
                if (DEVICE_NAME.equals(d.getName())) {
                    device = d;
                    break;
                }
            }

            if (device == null) {
                running = false;
                return;
            }

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();

                byte[] buffer = new byte[1024];
                int bytes;

                while (running) {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        try {
                            String readMessage = new String(buffer, 0, bytes).trim();
                            int moisture = Integer.parseInt(readMessage);
                            listener.onNewSoilMoistureData(moisture);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stop();
            }
        });

        btThread.start();
    }

    public void stop() {
        running = false;
        if (btThread != null) {
            btThread.interrupt();
            btThread = null;
        }
        try {
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
