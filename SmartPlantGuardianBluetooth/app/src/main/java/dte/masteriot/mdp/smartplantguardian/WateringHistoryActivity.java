package dte.masteriot.mdp.smartplantguardian;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WateringHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watering_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Watering History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
