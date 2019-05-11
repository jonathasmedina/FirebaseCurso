package com.example.jonathas.firebasecurso;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Buscar extends AppCompatActivity {

    EditText editTextBuscaNome;
    ListView listViewBuscaNome;

    List<Pessoa> pessoaList = new ArrayList<>();
    ArrayAdapter<Pessoa> pessoaArrayAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        editTextBuscaNome = findViewById(R.id.editTextBuscaNome);
        listViewBuscaNome = findViewById(R.id.listViewBuscar);

        iniciarFirebase();

        eventoEdit();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        pesquisarPalavra("");
    }

    private void eventoEdit() {
        editTextBuscaNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra = editTextBuscaNome.getText().toString().trim();
                pesquisarPalavra(palavra);

            }
        });
    }


    private void pesquisarPalavra(String palavra){
        Query query;

        if (palavra.equals("")) {
            query = databaseReference.child("Pessoa").orderByChild("nome");
        }
        else{
            query = databaseReference.child("Pessoa").orderByChild("nome").
                    startAt(palavra).endAt(palavra + "\uf8ff");
        }

        pessoaList.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot1: dataSnapshot.getChildren()) {
                        Pessoa p = objDataSnapshot1.getValue(Pessoa.class);
                        pessoaList.add(p);
                }

                pessoaArrayAdapter = new ArrayAdapter<>(Buscar.this,
                        android.R.layout.simple_list_item_1, pessoaList);

                listViewBuscaNome.setAdapter(pessoaArrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(Buscar.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
