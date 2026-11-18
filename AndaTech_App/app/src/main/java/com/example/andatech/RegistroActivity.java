package com.example.andatech;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistroActivity extends AppCompatActivity {

    private EditText nombre, correo, contrasena;
    private Button btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_registro);

        nombre = findViewById(R.id.registroNombre);
        correo = findViewById(R.id.registroCorreo);
        contrasena = findViewById(R.id.registroContrasena);
        btnCrear = findViewById(R.id.registroBtnCrear);

        btnCrear.setOnClickListener(v -> crearCuenta());
    }

    private void crearCuenta() {

        String nombreUsuario = nombre.getText().toString().trim();
        String email = correo.getText().toString().trim();
        String password = contrasena.getText().toString();

        if (nombreUsuario.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        UserProfileChangeRequest perfil =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nombreUsuario)
                                        .build();

                        FirebaseAuth.getInstance().getCurrentUser()
                                .updateProfile(perfil)
                                .addOnCompleteListener(actualizacion -> {

                                    FirebaseAuth.getInstance().getCurrentUser()
                                            .sendEmailVerification()
                                            .addOnCompleteListener(envio -> {

                                                if (envio.isSuccessful()) {

                                                    Toast.makeText(this,
                                                            "Cuenta creada. Revisa tu correo para verificarla.",
                                                            Toast.LENGTH_LONG).show();

                                                    finish();

                                                } else {

                                                    Toast.makeText(this,
                                                            "La cuenta se creó, pero no se pudo enviar el correo: "
                                                                    + envio.getException().getMessage(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                });

                    } else {
                        Toast.makeText(this,
                                "No se pudo crear la cuenta: "
                                        + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
