package com.example.tachesmaster.pages;

import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tachesmaster.R;
import com.example.tachesmaster.bd.BdHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BdHelper db;
    ListView listeTaches;
    int utilisateurId;

    private ActivityResultLauncher<Intent> ajoutTacheLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Récupérer l'ID utilisateur depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        utilisateurId = prefs.getInt("utilisateurId", -1); // -1 est la valeur par défaut si l'ID n'existe pas

        // Vérifier si l'utilisateur est bien connecté
        if (utilisateurId == -1) {
            // Si l'utilisateur n'est pas connecté, rediriger vers l'écran de connexion
            Intent intent = new Intent(MainActivity.this, Connexion.class);
            startActivity(intent);
            finish();
        } else {
            db = new BdHelper(this);
            listeTaches = findViewById(R.id.listeTaches);

            ajoutTacheLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            afficherTaches();
                        }
                    }
            );

            FloatingActionButton ajouterBtn = findViewById(R.id.ajoutBtn);
            ajouterBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AjouterTaches.class);
                intent.putExtra("USER_ID", utilisateurId);
                ajoutTacheLauncher.launch(intent);
            });

            afficherTaches();

            Button deconnexionBtn = findViewById(R.id.deconnexionBtn);
            deconnexionBtn.setOnClickListener(v -> {
                // Supprimer l'ID utilisateur des SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("utilisateurId");
                editor.apply();

                // Rediriger vers la page de connexion
                Intent intent = new Intent(MainActivity.this, Connexion.class);
                startActivity(intent);
                finish();
            });

        }
    }

    private void afficherTaches() {
        Cursor cursor = db.getTachesParUtilisateur(utilisateurId);
        List<String> taches = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String titre = cursor.getString(cursor.getColumnIndexOrThrow("titre"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                taches.add(titre + " - " + description);
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taches);
        listeTaches.setAdapter(adapter);

        listeTaches.setOnItemLongClickListener((adapterView, view, position, id) -> {
            if (cursor.moveToPosition(position)) {
                int tacheId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titre = cursor.getString(cursor.getColumnIndexOrThrow("titre"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                // Afficher options modifier/supprimer
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Tâche : " + titre)
                        .setMessage("Que veux-tu faire ?")
                        .setPositiveButton("Modifier", (dialog, which) -> {
                            afficherDialogModification(tacheId, titre, description);
                        })
                        .setNegativeButton("Supprimer", (dialog, which) -> {
                            db.supprimerTache(tacheId);
                            afficherTaches(); // rafraîchir la liste
                        })
                        .setNeutralButton("Annuler", null)
                        .show();
            }
            return true;
        });

        Log.d("DEBUG", "Nombre de tâches récupérées : " + taches.size());
    }

    private void afficherDialogModification(int tacheId, String titre, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier la tâche");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputTitre = new EditText(this);
        inputTitre.setHint("Titre");
        inputTitre.setText(titre);
        layout.addView(inputTitre);

        final EditText inputDescription = new EditText(this);
        inputDescription.setHint("Description");
        inputDescription.setText(description);
        layout.addView(inputDescription);

        builder.setView(layout);

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            String nouveauTitre = inputTitre.getText().toString();
            String nouvelleDescription = inputDescription.getText().toString();
            db.modifierTache(tacheId, nouveauTitre, nouvelleDescription);
            afficherTaches(); // rafraîchir la liste
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }



}
