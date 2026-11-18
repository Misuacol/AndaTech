package com.example.andatech;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.pagina_inicio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            // esto iria mas en el perfil de usuario que es donde hay que poner lo datos
            String nombre = usuario.getDisplayName();
            String correo = usuario.getEmail();
            String telefono = usuario.getPhoneNumber();
            Uri urlFoto = usuario.getPhotoUrl();
            String uid = usuario.getUid();
            TextView Saludo = findViewById(R.id.textoSaludo);

            if (nombre != null && !nombre.trim().isEmpty()) {
                Saludo.setText("Buenas tardes, " + nombre);
            } else {
                Saludo.setText("Buenas tardes");
            }

            String proveedores = "";
            for(int n=0; n<usuario.getProviderData().size(); n++){
                proveedores += usuario.getProviderData().get(n).getProviderId() + ", ";
            }
            // Nombre.setText(nombre);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        TextView acercaDe = findViewById(R.id.menu_acerca_de);
        acercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AcercaDeActivity.class);
                startActivity(intent);
            }
        });

        ImageView menuPerfil = findViewById(R.id.menu_perfil);
        menuPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

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