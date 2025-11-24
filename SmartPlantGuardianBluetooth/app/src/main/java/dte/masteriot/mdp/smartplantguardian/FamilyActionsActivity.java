package dte.masteriot.mdp.smartplantguardian;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        btnWater.setOnClickListener(v -> publishAction("Watered the plant"));
        btnFertilize.setOnClickListener(v -> publishAction("Fertilized the plant"));
    }

    private void publishAction(String action) {
        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String message = "I " + action + " at " + timestamp;

        // Save to family actions history
        ActionStorage.saveFamilyAction(this, message);

        // Publish via MQTT
        mqttHelper.publish(message);
    }



    @Override
    public void onMessageReceived(String message) {
        runOnUiThread(() -> {
            String current = tvMessages.getText().toString();
            tvMessages.setText(current + "\n" + message);
        });
    }
}
