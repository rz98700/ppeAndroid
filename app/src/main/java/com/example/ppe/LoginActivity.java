package com.example.ppe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// ✅ Cet import manque

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etNom, etMotDePasse;
    private Button btnLogin;
    private TextView tvError;
    private RequestQueue requestQueue;

    // Clé pour SharedPreferences
    public static final String PREFS_NAME = "PPEPrefs";
    public static final String KEY_TOKEN   = "jwt_token";
    public static final String KEY_NOM     = "nom";
    public static final String KEY_ID_USER = "id_user";
    public static final String KEY_ADMIN   = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si déjà connecté → aller direct à MainActivity
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_TOKEN)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);
        etNom        = findViewById(R.id.etNom);
        etMotDePasse = findViewById(R.id.etMotDePasse);
        btnLogin     = findViewById(R.id.btnLogin);
        tvError      = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String nom = etNom.getText().toString().trim();
        String mdp = etMotDePasse.getText().toString().trim();

        if (nom.isEmpty() || mdp.isEmpty()) {
            tvError.setText("Remplissez tous les champs");
            return;
        }

        btnLogin.setEnabled(false);
        tvError.setText("");

        try {
            JSONObject body = new JSONObject();
            body.put("nom", nom);
            body.put("mot_de_passe", mdp);

            String url = "http://192.168.0.106/~rayan.zhouri/ppe/public/index.php/api/login";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                // Stockage stateless : on garde juste le token
                                String token  = response.getString("token");
                                String nomRep = response.getString("nom");
                                int idUser    = response.getInt("id_user");
                                int admin     = response.getInt("admin");

                                SharedPreferences.Editor editor =
                                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString(KEY_TOKEN, token);
                                editor.putString(KEY_NOM, nomRep);
                                editor.putInt(KEY_ID_USER, idUser);
                                editor.putInt(KEY_ADMIN, admin);
                                editor.apply();

                                // Aller à MainActivity
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            tvError.setText("Erreur de parsing");
                            btnLogin.setEnabled(true);
                        }
                    },
                    error -> {
                        tvError.setText("❌ Identifiants incorrects");
                        btnLogin.setEnabled(true);
                    }
            );

            requestQueue.add(request);

        } catch (Exception e) {
            tvError.setText("Erreur inattendue");
            btnLogin.setEnabled(true);
        }
    }
}