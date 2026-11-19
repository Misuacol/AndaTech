package com.example.andatech;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textoTemperatura, textoHumedad, textoBateria;
    private List<Alerta> listaAlertas;
    private AlertasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_inicio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

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
            TextView saludo = findViewById(R.id.textoSaludo);

            if (nombre != null && !nombre.trim().isEmpty()) {
                saludo.setText("Buenas tardes, " + nombre);
            } else {
                saludo.setText("Buenas tardes");
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Enlazar TextViews de la tarjeta de estado de Andador1
        textoTemperatura = findViewById(R.id.textoTemperatura);
        textoHumedad = findViewById(R.id.textoHumedad);
        textoBateria = findViewById(R.id.textoBateria);

        // Configurar RecyclerView para las alertas reales
        RecyclerView recyclerAlertas = findViewById(R.id.recyclerAlertas);
        listaAlertas = new ArrayList<>();
        adapter = new AlertasAdapter(listaAlertas);

        recyclerAlertas.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlertas.setAdapter(adapter);

        // Iniciar la escucha en tiempo real con Firebase
        escucharDatosFirebase();
    }

    private void escucharDatosFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // 1. Escuchar estado actual del andador (Temperatura, Humedad y Batería)
        DatabaseReference refEstado = database.getReference("Andador1/estado_actual");
        refEstado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double temp = snapshot.child("temperatura").getValue(Double.class);
                    Double hum = snapshot.child("humedad").getValue(Double.class);
                    Integer bat = snapshot.child("bateria").getValue(Integer.class);

                    if (temp != null) {
                        textoTemperatura.setText(String.format(Locale.getDefault(), "%.1f °C", temp));
                    }
                    if (hum != null) {
                        textoHumedad.setText(String.format(Locale.getDefault(), "%.0f %%", hum));
                    }
                    if (bat != null) {
                        textoBateria.setText(bat + " %");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error leyendo sensores: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Escuchar alertas reales en tiempo real
        DatabaseReference refAlertas = database.getReference("Andador1/alertas");
        refAlertas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAlertas.clear();
                for (DataSnapshot dato : snapshot.getChildren()) {
                    Alerta alerta = dato.getValue(Alerta.class);
                    if (alerta != null) {
                        // Añade en la posición 0 para que la alerta más reciente salga arriba
                        listaAlertas.add(0, alerta);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error leyendo alertas: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Para el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Para abrir el perfil o acerca de al pulsar el icono
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.perfil) {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.acercaDe) {
            Intent intent = new Intent(this, AcercaDeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
