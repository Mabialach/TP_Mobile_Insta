package com.example.tachesmaster.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BdHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "tachemaster.db";
    public BdHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE utilisateurs (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, motdepasse TEXT)");
        db.execSQL("CREATE TABLE taches (id INTEGER PRIMARY KEY AUTOINCREMENT, utilisateur INTEGER, titre TEXT, description TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS utilisateurs");
        db.execSQL("DROP TABLE IF EXISTS taches");
        onCreate(db);
    }

    public boolean insererTache(int utilisateurId, String titre, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("utilisateur", utilisateurId);
        values.put("titre", titre);
        values.put("description", description);

        long result = db.insert("taches", null, values);
        return result != -1; // renvoie true si insertion réussie
    }

    public Cursor getTachesParUtilisateur(int utilisateurId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("DEBUG", "taches de : " + utilisateurId);
        return db.rawQuery("SELECT * FROM taches WHERE utilisateur = ?", new String[]{String.valueOf(utilisateurId)});
    }

    public void modifierTache(int id, String nouveauTitre, String nouvelleDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("titre", nouveauTitre);
        values.put("description", nouvelleDescription);
        db.update("taches", values, "id = ?", new String[]{String.valueOf(id)});
    }


    public void supprimerTache(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("taches", "id = ?", new String[]{String.valueOf(id)});
    }



}