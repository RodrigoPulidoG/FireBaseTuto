package com.example.rodpro.firebasetuto;

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
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.rodpro.firebasetuto.Model.Persona;

public class MainActivity extends AppCompatActivity {

    private List<Persona> personaList = new ArrayList<Persona>();
    ArrayAdapter<Persona> personaArrayAdapter;

    private EditText nombre, apellido, correo, pass;
    private ListView listView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona persona_seleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre = findViewById(R.id.et_nombre);
        apellido = findViewById(R.id.et_apellido);
        correo = findViewById(R.id.et_correo);
        pass = findViewById(R.id.et_pass);
        listView = findViewById(R.id.lista_personas);
        
        inicializarFireBase();
        listarDatos();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                persona_seleccionada = (Persona) parent.getItemAtPosition(position);
                nombre.setText(persona_seleccionada.getNombre());
                apellido.setText(persona_seleccionada.getApellido());
                correo.setText(persona_seleccionada.getCorreo());
                pass.setText(persona_seleccionada.getContraseña());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                personaList.clear();
                for (DataSnapshot objShaptshot : dataSnapshot.getChildren()){
                    Persona p = objShaptshot.getValue(Persona.class);
                    personaList.add(p);

                    personaArrayAdapter = new ArrayAdapter<Persona> (MainActivity.this, android.R.layout.simple_list_item_1, personaList);
                    listView.setAdapter(personaArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String n = nombre.getText().toString();
        String a = apellido.getText().toString();
        String c = correo.getText().toString();
        String p = pass.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:
                if (n.equals("")||a.equals("")||c.equals("")||p.equals("")){
                    validacion();
                }else {
                    Persona person = new Persona();
                    person.setUid(UUID.randomUUID().toString());
                    person.setNombre(n);
                    person.setApellido(a);
                    person.setCorreo(c);
                    person.setContraseña(p);
                    databaseReference.child("Persona").child(person.getUid()).setValue(person);
                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                    borrarET();
                }
                break;
            case R.id.icon_save:
                Persona per = new Persona();
                per.setUid(persona_seleccionada.getUid());
                per.setNombre(nombre.getText().toString().trim());
                per.setApellido(apellido.getText().toString().trim());
                per.setCorreo(correo.getText().toString().trim());
                per.setContraseña(pass.getText().toString().trim());
                databaseReference.child("Persona").child(per.getUid()).setValue(per);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                borrarET();
                break;
            case R.id.icon_delete:
                Persona pers = new Persona();
                pers.setUid(persona_seleccionada.getUid());
                databaseReference.child("Persona").child(pers.getUid()).removeValue();
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                borrarET();
                break;
            default: break;
        }
        return true;
    }

    private void borrarET() {
        nombre.setText("");
        apellido.setText("");
        correo.setText("");
        pass.setText("");
    }

    private void validacion() {
        String name = nombre.getText().toString();
        String last_name = apellido.getText().toString();
        String email = correo.getText().toString();
        String password = pass.getText().toString();
        if (name.equals("")){
            nombre.setError("Required");
        }else if (last_name.equals("")){
            apellido.setError("Required");
        }else if (email.equals("")){
            correo.setError("Required");
        }else if (password.equals("")){
            pass.setError("Required");
        }
    }
}
