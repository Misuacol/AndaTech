package com.example.andatech;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombre, tvCorreo, tvTelefono, tvUid, tvProveedores;
    private ImageView ivFotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvUid = findViewById(R.id.tvUid);
        tvProveedores = findViewById(R.id.tvProveedores);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            String nombre = usuario.getDisplayName();
            String correo = usuario.getEmail();
            String telefono = usuario.getPhoneNumber();
            String uid = usuario.getUid();
            Uri urlFoto = usuario.getPhotoUrl();
            if (urlFoto != null) {
                Glide.with(this)
                        .load(urlFoto)
                        .circleCrop() // Corta la foto en forma de círculo estético
                        .placeholder(android.R.drawable.sym_def_app_icon) // Icono por defecto mientras carga
                        .error(android.R.drawable.sym_def_app_icon)       // Icono si falla la carga
                        .into(ivFotoPerfil);
            }

            StringBuilder proveedores = new StringBuilder();
            if (usuario.getProviderData() != null) {
                for (int n = 0; n < usuario.getProviderData().size(); n++) {
                    proveedores.append(usuario.getProviderData().get(n).getProviderId());
                    if (n < usuario.getProviderData().size() - 1) {
                        proveedores.append(", ");
                    }
                }
            }

            tvNombre.setText(nombre != null && !nombre.isEmpty() ? nombre : "Sin nombre");
            tvCorreo.setText(correo != null ? correo : "Sin correo");
            tvTelefono.setText(telefono != null && !telefono.isEmpty() ? telefono : "No registrado");
            tvUid.setText(uid);
            tvProveedores.setText(proveedores.toString());
        }

        TextView acercaDe = findViewById(R.id.menu_acerca_de);
        acercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, AcercaDeActivity.class);
                startActivity(intent);
            }
        });

        TextView inicio = findViewById(R.id.menu_andadores);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}