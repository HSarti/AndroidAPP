package dte.masteriot.mdp.smartplantguardian;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;
import android.widget.Button;

public class WateringHistoryActivity extends AppCompatActivity {

    private TextView historyText;
    private Button btnClearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watering_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Plant Watering History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyText = findViewById(R.id.historyText);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        loadHistory();

        btnClearHistory.setOnClickListener(v -> {
            ActionStorage.clearSensorUpdates(this);
            historyText.setText("History cleared! 🌱");
        });
    }

    private void loadHistory() {
        List<String> updates = ActionStorage.getSensorUpdates(this);

        if (updates.isEmpty()) {
            historyText.setText("No sensor updates found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String update : updates) sb.append(update).append("\n");
            historyText.setText(sb.toString());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
