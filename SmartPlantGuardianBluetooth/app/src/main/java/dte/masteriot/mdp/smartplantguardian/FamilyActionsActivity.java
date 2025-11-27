package dte.masteriot.mdp.smartplantguardian;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FamilyActionsActivity extends AppCompatActivity implements MqttHelper.MessageListener {

    private MqttHelper mqttHelper;
    private TextView tvMessages;
    private Button btnWater, btnFertilize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_actions);

        tvMessages = findViewById(R.id.tvMessages);
        btnWater = findViewById(R.id.btnWater);
        btnFertilize = findViewById(R.id.btnFertilize);

        mqttHelper = new MqttHelper(this);

        // Show last actions at startup
        refreshLastActions();

        btnWater.setOnClickListener(v -> publishAction("Watered the plant"));
        btnFertilize.setOnClickListener(v -> publishAction("Fertilized the plant"));
    }

    private void publishAction(String action) {
        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String message = "I " + action + " at " + timestamp;

        // Save last action (water/fertilize)
        ActionStorage.saveFamilyAction(this, message);

        // Publish via MQTT
        mqttHelper.publish(message);

        // Update displayed last actions
        refreshLastActions();
    }

    private void refreshLastActions() {
        List<String> lastActions = ActionStorage.getLastWaterAndFertilize(this);

        if (!lastActions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String a : lastActions) sb.append(a).append("\n");
            tvMessages.setText(sb.toString().trim());
        } else {
            tvMessages.setText("Your recent plant care appears here...");
        }
    }

    @Override
    public void onMessageReceived(String message) {
        runOnUiThread(() -> {
            // Save incoming message if relevant
            ActionStorage.saveFamilyAction(this, message);

            // Refresh last actions
            refreshLastActions();
        });
    }
}
