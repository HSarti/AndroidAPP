package dte.masteriot.mdp.smartplantguardian;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GardensActivity extends AppCompatActivity {

    private static final String GARDENS_URL = "https://datos.madrid.es/egob/catalogo/200761-0-parques-jardines.json";

    private Button btnLoadGardens;
    private TextView tvGardensContent;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gardens);

        btnLoadGardens = findViewById(R.id.btnLoadGardens);
        tvGardensContent = findViewById(R.id.tvGardensContent);

        executor = Executors.newSingleThreadExecutor();

        btnLoadGardens.setOnClickListener(v -> loadGardensData());
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = msg.getData().getString("text");
            tvGardensContent.setText(text != null ? text : "No data received");
            btnLoadGardens.setEnabled(true);
        }
    };

    private void loadGardensData() {
        btnLoadGardens.setEnabled(false);
        tvGardensContent.setText("Loading gardens data...");
        executor.execute(new LoadJSONContents(handler, GARDENS_URL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
