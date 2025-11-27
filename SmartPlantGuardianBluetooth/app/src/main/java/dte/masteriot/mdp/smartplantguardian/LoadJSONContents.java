package dte.masteriot.mdp.smartplantguardian;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

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
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            JSONObject root = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
            JSONArray graphArray = root.getJSONArray("@graph"); // main array

            for (int i = 0; i < graphArray.length(); i++) {
                JSONObject garden = graphArray.getJSONObject(i);
                String name = garden.optString("title", "N/A");

                String street = "N/A";
                String district = "N/A";

                if (garden.has("address")) {
                    JSONObject address = garden.getJSONObject("address");
                    street = address.optString("street-address", "N/A");
                    if (address.has("district")) {
                        String districtId = address.getJSONObject("district").optString("@id", "");
                        // extract last part of URL as district name
                        if (!districtId.isEmpty()) {
                            String[] parts = districtId.split("/");
                            district = parts[parts.length - 1];
                        }
                    }
                }

                result.append("Garden Name: ").append(name).append("\n")
                        .append("Address: ").append(street).append("\n")
                        .append("District: ").append(district).append("\n\n");
            }

            // send to handler
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("text", result.toString());
            msg.setData(bundle);
            handler.sendMessage(msg);

        } catch (Exception e) {
            Log.e("LoadJSONContents", "Failed to load JSON", e);
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("text", "Failed to load gardens data: " + e.getMessage());
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}
