package dte.masteriot.mdp.smartplantguardian;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionStorage {

    private static final String PREFS_NAME = "PlantActions";
    private static final String KEY_FAMILY_ACTIONS = "family_actions";
    private static final String KEY_SENSOR_UPDATES = "sensor_updates";
    private static final String KEY_LAST_MOISTURE = "last_moisture_value";

    // Save a family action
    public static void saveFamilyAction(Context context, String action) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String current = prefs.getString(KEY_FAMILY_ACTIONS, "");
        current += action + "\n";
        prefs.edit().putString(KEY_FAMILY_ACTIONS, current).apply();
    }

    // Get family actions
    public static List<String> getFamilyActions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String all = prefs.getString(KEY_FAMILY_ACTIONS, "");
        if (all.isEmpty()) return new ArrayList<>();
        return Arrays.asList(all.split("\n"));
    }

    // Clear family actions
    public static void clearFamilyActions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_FAMILY_ACTIONS).apply();
    }

    // Save a sensor update only if value changed
    public static void saveSensorUpdate(Context context, int newValue, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int lastValue = prefs.getInt(KEY_LAST_MOISTURE, -1);

        if (newValue != lastValue) {
            String current = prefs.getString(KEY_SENSOR_UPDATES, "");
            current += message + "\n";
            prefs.edit()
                    .putString(KEY_SENSOR_UPDATES, current)
                    .putInt(KEY_LAST_MOISTURE, newValue)
                    .apply();
        }
    }

    // Get sensor updates
    public static List<String> getSensorUpdates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String all = prefs.getString(KEY_SENSOR_UPDATES, "");
        if (all.isEmpty()) return new ArrayList<>();
        return Arrays.asList(all.split("\n"));
    }

    // Clear sensor updates
    public static void clearSensorUpdates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_SENSOR_UPDATES).remove(KEY_LAST_MOISTURE).apply();
    }
}
