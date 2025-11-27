package dte.masteriot.mdp.smartplantguardian;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

public class FamilyActionsHistoryActivity extends AppCompatActivity {

    private TextView historyText;
    private Button btnClearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_actions_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Plant Care History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        historyText = findViewById(R.id.historyText);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        loadHistory();

        btnClearHistory.setOnClickListener(v -> {
            ActionStorage.clearFamilyActions(FamilyActionsHistoryActivity.this);
            loadHistory();
        });
    }

    private void loadHistory() {
        List<String> actions = ActionStorage.getFamilyActions(this);

        if (actions.isEmpty()) {
            historyText.setText("History cleared! 🌱");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String action : actions) sb.append(action).append("\n");
            historyText.setText(sb.toString());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
