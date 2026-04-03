package com.example.ppe; // ✅ MANQUANT
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ppe.Camera;
import com.example.ppe.CameraAdapter;
import com.example.ppe.LoginActivity;
import com.example.ppe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraAdapter.OnCameraActionListener {

    private RequestQueue fileRequetesWS;
    private RecyclerView recyclerView;
    private CameraAdapter adapter;
    private List<Camera> cameraList = new ArrayList<>();
    private TextView tvLastUpdate;
    private Button btnDeconnexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileRequetesWS = Volley.newRequestQueue(this);

        recyclerView = findViewById(R.id.recyclerView);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        btnDeconnexion = findViewById(R.id.btnDeconnexion);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CameraAdapter(this, cameraList, this);
        recyclerView.setAdapter(adapter);

        btnDeconnexion.setOnClickListener(v -> deconnexion());        requestCameras();
    }

    // ✅ UNE SEULE requestCameras() — avec le token JWT
    private void requestCameras() {
        String url = "http://192.168.0.106/~rayan.zhouri/ppe/public/index.php/api/cameras";

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(LoginActivity.KEY_TOKEN, "");

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::processCameras,
                this::gereErreursWS
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        fileRequetesWS.add(jsonRequest); // ✅ gardez ça
    }

    private void processCameras(JSONObject reponse) {
        try {
            boolean success = reponse.getBoolean("success");

            if (success) {
                JSONArray camerasArray = reponse.getJSONArray("cameras");
                cameraList.clear();

                for (int i = 0; i < camerasArray.length(); i++) {
                    JSONObject camJson = camerasArray.getJSONObject(i);
                    Camera camera = new Camera(
                            camJson.getInt("id"),
                            camJson.getString("url"),
                            camJson.getString("description"),
                            camJson.getInt("est_priv") == 1
                    );
                    cameraList.add(camera);
                }

                runOnUiThread(() -> {
                    adapter.updateCameras(cameraList);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH'h'mm", Locale.FRANCE);
                    tvLastUpdate.setText("🕒 Dernière mise à jour : " + sdf.format(new Date()));
                    Toast.makeText(this, cameraList.size() + " caméra(s) chargée(s)", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (JSONException e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "❌ Erreur JSON", Toast.LENGTH_LONG).show();
                Log.e("CAMERAS", "Erreur JSON", e);
            });
        }
    }
    private void deconnexion() {
        // Efface le token et toutes les données stockées
        getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Retourne à LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void gereErreursWS(com.android.volley.VolleyError error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "❌ Problème de communication", Toast.LENGTH_LONG).show();
            Log.e("CAMERAS", "Erreur Volley", error);
        });
    }

    @Override
    public void onCaptureClick(Camera camera) {
        Toast.makeText(this, "Capture sur " + camera.getDescription(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Camera camera) {
        Toast.makeText(this, "Suppression de " + camera.getDescription(), Toast.LENGTH_SHORT).show();
    }
}