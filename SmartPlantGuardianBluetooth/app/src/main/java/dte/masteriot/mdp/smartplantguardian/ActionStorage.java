package dte.masteriot.mdp.smartplantguardian;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionStorage {

    private static final String PREFS_NAME = "PlantActions";

    private static final String KEY_FAMILY_ACTIONS = "family_actions"; // full history
    private static final String KEY_LAST_WATER = "last_water_action";
    private static final String KEY_LAST_FERTILIZE = "last_fertilize_action";

    private static final String KEY_SENSOR_UPDATES = "sensor_updates";
    private static final String KEY_LAST_MOISTURE = "last_moisture_value";

    // Save family action: append to full history AND update last water/fertilize
    public static void saveFamilyAction(Context context, String action) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update full history
        String currentHistory = prefs.getString(KEY_FAMILY_ACTIONS, "");
        currentHistory += action + "\n";
        editor.putString(KEY_FAMILY_ACTIONS, currentHistory);

        // Update last water/fertilize
        String lower = action.toLowerCase();
        if (lower.contains("water")) {
            editor.putString(KEY_LAST_WATER, action);
        } else if (lower.contains("fertil")) {
            editor.putString(KEY_LAST_FERTILIZE, action);
        }

        editor.apply();
    }

    // Get full history
    public static List<String> getFamilyActions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String all = prefs.getString(KEY_FAMILY_ACTIONS, "");
        if (all.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(all.split("\n")));
    }

    // Get last water + last fertilize
    public static List<String> getLastWaterAndFertilize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<String> result = new ArrayList<>();

        String lastWater = prefs.getString(KEY_LAST_WATER, null);
        String lastFertilize = prefs.getString(KEY_LAST_FERTILIZE, null);

        if (lastWater != null) result.add(lastWater);
        if (lastFertilize != null) result.add(lastFertilize);

        return result;
    }

    // Clear last water + fertilize only
    public static void clearLastWaterAndFertilize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_LAST_WATER)
                .remove(KEY_LAST_FERTILIZE)
                .apply();
    }

    // Clear full family history
    public static void clearFamilyActions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_FAMILY_ACTIONS)
                .remove(KEY_LAST_WATER)
                .remove(KEY_LAST_FERTILIZE)
                .apply();
    }

    // SENSOR LOGIC (unchanged)
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

    public static List<String> getSensorUpdates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String all = prefs.getString(KEY_SENSOR_UPDATES, "");
        if (all.isEmpty()) return new ArrayList<>();
        String[] arr = all.split("\n");
        List<String> result = new ArrayList<>();
        for (String s : arr) if (!s.isEmpty()) result.add(s);
        return result;
    }

    public static void clearSensorUpdates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_SENSOR_UPDATES).remove(KEY_LAST_MOISTURE).apply();
    }
}
