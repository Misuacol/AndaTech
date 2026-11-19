package com.example.andatech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarContrasenaActivity extends AppCompatActivity {

    private EditText etPassActual, etPassNueva, etPassConfirmar;
    private Button btnGuardarPass;
    private TextView btnVolverPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_cambiar_contrasena);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        etPassActual = findViewById(R.id.etPassActual);
        etPassNueva = findViewById(R.id.etPassNueva);
        etPassConfirmar = findViewById(R.id.etPassConfirmar);
        btnGuardarPass = findViewById(R.id.btnGuardarPass);
        btnVolverPerfil = findViewById(R.id.btnVolverPerfil);
        ImageView logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CambiarContrasenaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnVolverPerfil.setOnClickListener(v -> finish());
        btnGuardarPass.setOnClickListener(v -> actualizarContrasena());
    }

    private void actualizarContrasena() {
        String actual = etPassActual.getText().toString().trim();
        String nueva = etPassNueva.getText().toString().trim();
        String confirmar = etPassConfirmar.getText().toString().trim();

        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nueva.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nueva.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null && usuario.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(usuario.getEmail(), actual);

            usuario.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                if (reauthTask.isSuccessful()) {
                    usuario.updatePassword(nueva).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                            finish(); // Regresa automáticamente a PerfilActivity
                        } else {
                            Toast.makeText(this, "Error al actualizar: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
