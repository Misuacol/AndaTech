package com.example.andatech;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_inicio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            String nombre = usuario.getDisplayName();
            TextView Saludo = findViewById(R.id.textoSaludo);

            if (nombre != null && !nombre.trim().isEmpty()) {
                Saludo.setText("Buenas tardes, " + nombre);
            } else {
                Saludo.setText("Buenas tardes");
            }

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //recycler para mostrar las alertas en la pagina de inicio
        RecyclerView recyclerAlertas = findViewById(R.id.recyclerAlertas);

        List<Alerta> listaAlertas = new ArrayList<>();

        listaAlertas.add(new Alerta(
                "Posible caída detectada",
                "Se ha registrado una posible caída.",
                "19:45"
        ));

        listaAlertas.add(new Alerta(
                "Sistema funcionando correctamente",
                "El sensor está conectado y funcionando.",
                "18:30"
        ));

        AlertasAdapter adapter = new AlertasAdapter(listaAlertas);

        recyclerAlertas.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlertas.setAdapter(adapter);
        //añadido temporalmente
        recyclerAlertas.postDelayed(() -> {
            adapter.agregarAlerta(new Alerta(
                    "Nueva caída detectada",
                    "Se ha recibido una nueva alerta del sensor.",
                    "20:15"
            ));
        }, 3000);
    }// onCreate()

    //para el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // Para abrir el perfil al pulsar el icono
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.perfil) {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
            return true;
        }
        //para que al pulsar el icono de acerca de te lleve a la página de acerca de
        if (item.getItemId() == R.id.acercaDe) {
            Intent intent = new Intent(this, AcercaDeActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}