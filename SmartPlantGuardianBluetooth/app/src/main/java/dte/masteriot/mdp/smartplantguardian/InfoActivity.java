package dte.masteriot.mdp.smartplantguardian;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Get references to buttons from activity_info.xml
        Button btnWatering = findViewById(R.id.btnWatering);
        Button btnMaintenance = findViewById(R.id.btnMaintenance);
        Button btnVacation = findViewById(R.id.btnVacation);
        Button btnFAO = findViewById(R.id.btnFAO);

        // Tips for watering plants - YouTube video
        btnWatering.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=jPOqMb0D4YQ")
            );
            startActivity(i);
        });

        // Tips for plant maintenance - YouTube video
        btnMaintenance.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=On7_JGLnScs")
            );
            startActivity(i);
        });

        // Tips for when you travel - YouTube video
        btnVacation.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=QNa0HNuz3kc")
            );
            startActivity(i);
        });

        // FAO official website
        btnFAO.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.fao.org/soils-portal/en/")
            );
            startActivity(i);
        });
    }
}
