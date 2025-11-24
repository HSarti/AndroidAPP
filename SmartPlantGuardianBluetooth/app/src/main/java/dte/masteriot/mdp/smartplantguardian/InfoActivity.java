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

        // Buttons from activity_info.xml
        Button btnWatering = findViewById(R.id.btnWatering);
        Button btnPlantTypes = findViewById(R.id.btnPlantTypes);
        Button btnSymptoms = findViewById(R.id.btnSymptoms);
        Button btnLight = findViewById(R.id.btnLight);
        Button btnSoil = findViewById(R.id.btnSoil);

        // Open web pages with detailed information

        // Best watering practices
        btnWatering.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://extension.umn.edu/yard-and-garden-news/watering-houseplants"));
            startActivity(i);
        });

        // Tips by plant type / general houseplants overview
        btnPlantTypes.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.rhs.org.uk/plants/types/houseplants"));
            startActivity(i);
        });

        // Symptoms of overwatering / underwatering
        btnSymptoms.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.houseplant.co.uk/blogs/indoor-plant-care/how-can-i-tell-if-my-indoor-plant-is-overwatered-or-underwatered"));
            startActivity(i);
        });

        // Light recommendations for indoor plants
        btnLight.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://extension.umn.edu/planting-and-growing-guides/lighting-indoor-plants"));
            startActivity(i);
        });

        // Soil and fertilizer tips for houseplants
        btnSoil.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://soiltesting.cahnr.uconn.edu/fertilizing-houseplants/"));
            startActivity(i);
        });

        Button btnFao = findViewById(R.id.btnFao);

        btnFao.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.fao.org/soils-portal/en/"));
            startActivity(i);
        });

    }
}

