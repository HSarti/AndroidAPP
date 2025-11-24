package dte.masteriot.mdp.smartplantguardian;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadJSONContents implements Runnable {

    private final Handler handler;
    private final String urlString;

    public LoadJSONContents(Handler handler, String urlString) {
        this.handler = handler;
        this.urlString = urlString;
    }

    @Override
    public void run() {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            reader.close();

            // Send result to handler
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("text", result.toString());
            msg.setData(bundle);
            handler.sendMessage(msg);

        } catch (Exception e) {
            Log.e("LoadJSONContents", "Failed to load JSON", e);
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("text", "Failed to load JSON: " + e.getMessage());
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}
