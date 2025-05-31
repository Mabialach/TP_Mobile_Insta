package com.example.tachesmaster.pages;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tachesmaster.R;
import com.example.tachesmaster.bd.BdHelper;

public class AjouterTaches extends AppCompatActivity {
    EditText titreTache, descriptionTache;
    Button saveButton;
    BdHelper db;
    int utilisateurId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout); // Assure-toi d’avoir ce layout

        titreTache = findViewById(R.id.titreTache);
        descriptionTache = findViewById(R.id.descriptionTache);
        saveButton = findViewById(R.id.saveBtn);
        db = new BdHelper(this);

        utilisateurId = getIntent().getIntExtra("USER_ID", -1);

        saveButton.setOnClickListener(v -> {
            String titre = titreTache.getText().toString();
            String description = descriptionTache.getText().toString();

            if (titre.isEmpty()) {
                Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = db.insererTache(utilisateurId, titre, description);
            if (inserted) {
                Toast.makeText(this, "Tâche ajoutée", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Erreur d’ajout", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
