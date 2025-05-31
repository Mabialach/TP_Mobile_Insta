package com.example.tachesmaster.pages;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tachesmaster.bd.BdHelper;
import com.example.tachesmaster.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Inscription extends AppCompatActivity {
    EditText email, motdepasse;
    Button registerBtn;
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
        setContentView(R.layout.inscription);

        email = findViewById(R.id.email);
        motdepasse = findViewById(R.id.motdepasse);
        registerBtn = findViewById(R.id.registerBtn);
        db = new BdHelper(this);

        registerBtn.setOnClickListener(v -> {
            String mail = email.getText().toString();
            String hashedPass = hashPassword(motdepasse.getText().toString());

            SQLiteDatabase database = db.getWritableDatabase();
            @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT * FROM utilisateurs WHERE email=?", new String[]{mail});

            if (cursor.getCount() > 0) {
                Toast.makeText(this, "Email déjà utilisé", Toast.LENGTH_SHORT).show();
            } else {
                ContentValues values = new ContentValues();
                values.put("email", mail);
                values.put("motdepasse", hashedPass);
                long id = database.insert("utilisateurs", null, values);
                if (id != -1) {
                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
