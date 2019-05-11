package com.example.jonathas.firebasecurso;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText edNome, edEmail;
    ListView listView;
    Pessoa pessoaSelecionada;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<Pessoa> pessoaList = new ArrayList<>();
    ArrayAdapter<Pessoa> pessoaArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edNome = findViewById(R.id.editTextNome);
        edEmail = findViewById(R.id.editTextEmail);
        listView = findViewById(R.id.listView1);

        iniciarFirebase();

        eventoDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa) parent.getItemAtPosition(position);
                edNome.setText(pessoaSelecionada.getNome());
                edEmail.setText(pessoaSelecionada.getEmail());
            }
        });



    }

    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //limpar a lista;
                //carregar os dados novamente na lista;
                pessoaList.clear();

                for (DataSnapshot obDataSnapshot1: dataSnapshot.getChildren()) {
                    Pessoa p = obDataSnapshot1.getValue(Pessoa.class);
                    pessoaList.add(p);
                }

                pessoaArrayAdapter = new ArrayAdapter<>(
                        MainActivity.this, android.R.layout.simple_list_item_1,
                        pessoaList);

                listView.setAdapter(pessoaArrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_novo) {
            // novo ovjeto pessoa, popular objeto, salvar obj no banco
            Pessoa p = new Pessoa();

            p.setNome(edNome.getText().toString());
            p.setEmail(edEmail.getText().toString());
            p.setId(UUID.randomUUID().toString());

            databaseReference.child("Pessoa").child(p.getNome()).setValue(p);

            limparCampos();

        }
        if (id == R.id.menu_atualiza) {
            Pessoa p = new Pessoa();
            p.setId(pessoaSelecionada.getId());
            p.setEmail(edEmail.getText().toString());
            p.setNome(edNome.getText().toString());

            databaseReference.child("Pessoa").child(p.getNome()).setValue(p);

            limparCampos();
        }
        if (id == R.id.menu_deleta) {
            Pessoa p = new Pessoa();
            p.setNome(pessoaSelecionada.getNome());

            databaseReference.child("Pessoa").child(p.getNome()).removeValue();

            limparCampos();

        }

        if (id == R.id.menu_busca) {
            Intent intent = new Intent(MainActivity.this, Buscar.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void limparCampos() {
        edEmail.setText("");
        edNome.setText("");
    }
}
