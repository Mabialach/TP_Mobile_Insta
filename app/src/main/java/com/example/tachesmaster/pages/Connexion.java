package com.example.tachesmaster.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tachesmaster.R;
import com.example.tachesmaster.bd.BdHelper;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class Connexion extends AppCompatActivity {
    EditText email, motdepasse;
    Button loginBtn;
    BdHelper db;

    private String hashPassword(String motdepasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motdepasse.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        // Vérifier si l'utilisateur est déjà connecté
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int utilisateurId = prefs.getInt("utilisateurId", -1); // Récupère l'ID utilisateur (par défaut -1 si non trouvé)
        if (utilisateurId != -1) {
            // Si l'utilisateur est déjà connecté, passer directement à l'écran principal
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_ID", utilisateurId);
            startActivity(intent);
            finish(); // Fermer l'activité actuelle pour empêcher de revenir en arrière
        }

        email = findViewById(R.id.email);
        motdepasse = findViewById(R.id.motdepasse);
        loginBtn = findViewById(R.id.loginBtn);
        db = new BdHelper(this);

        loginBtn.setOnClickListener(v -> {
            String mail = email.getText().toString();
            String hashedPass = hashPassword(motdepasse.getText().toString());

            SQLiteDatabase database = db.getReadableDatabase();
            @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT id FROM utilisateurs WHERE email=? AND motdepasse=?", new String[]{mail, hashedPass});

            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(0);

                // Stocker l'ID utilisateur dans SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("utilisateurId", userId);
                editor.apply();

                // Passer à l'écran principal
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Identifiants invalides", Toast.LENGTH_SHORT).show();
            }
        });

        Button createAccountBtn = findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Connexion.this, Inscription.class);
            startActivity(intent);
        });

    }
}
