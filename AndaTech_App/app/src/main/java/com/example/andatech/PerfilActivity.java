package com.example.andatech;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_perfil);

    // Obtener el usuario conectado
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if(usuario !=null) {

        String nombre = usuario.getDisplayName();
        String correo = usuario.getEmail();
        String telefono = usuario.getPhoneNumber();
        Uri urlFoto = usuario.getPhotoUrl();
        String uid = usuario.getUid();

        // Mostrar nombre
        TextView tvNombre = findViewById(R.id.perfilNombre);
        tvNombre.setText(nombre != null ? nombre : "No disponible");

        // Mostrar correo
        TextView tvCorreo = findViewById(R.id.perfilCorreo);
        tvCorreo.setText(correo != null ? correo : "No disponible");

        // Mostrar teléfono
        TextView tvTelefono = findViewById(R.id.perfilTelefono);
        tvTelefono.setText(telefono != null ? telefono : "No disponible");

        // Mostrar UID
        TextView tvUid = findViewById(R.id.perfilUid);
        tvUid.setText(uid);

        // Mostrar foto de perfil
        ImageView ivFotoPerfil = findViewById(R.id.perfilFoto);
        if (urlFoto != null) {
            Glide.with(this)
                   .load(urlFoto)
                   .circleCrop()
                   .into(ivFotoPerfil);
        }

    } else {
        volverAlLogin();
    }

        // Botón cerrar sesión
        TextView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            volverAlLogin();
        });
    }
    private void volverAlLogin() {
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
