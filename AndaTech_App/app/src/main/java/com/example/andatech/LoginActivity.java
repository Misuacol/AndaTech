package com.example.andatech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_login);

        // Conectar los botones
        Button btnIniciarSesion = findViewById(R.id.loginBtnIniciarSesion);
        Button btnGoogle = findViewById(R.id.loginBtnIniciarSesionGoogle);

        // Conectar los campos
        EditText correo = findViewById(R.id.loginCorreo);
        EditText contrasena = findViewById(R.id.loginContraseña);

        // Inicio de sesión con correo
        btnIniciarSesion.setOnClickListener(v -> {
            iniciarSesionCorreo(correo.getText().toString().trim(),
                    contrasena.getText().toString());
        });

        // Inicio de sesión con Google
        btnGoogle.setOnClickListener(v -> login());

        TextView irRegistro = findViewById(R.id.loginIrRegistro);

        irRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        });
        //para que si ya has iniciado sesión te lleve directamente a la página de inicio
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            login();
        }
    }

    private void login() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            if (usuario.isEmailVerified()) {
                Toast.makeText(this, "Inició sesión: " + usuario.getDisplayName() + " - " + usuario.getEmail(), Toast.LENGTH_LONG).show();
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            } else {
                usuario.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Se le ha enviado un correo de verificación.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show();
                    }
                });
                FirebaseAuth.getInstance().signOut();
            }
        } else {

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);
        }
    }
    private void iniciarSesionCorreo(String correo, String contrasena) {

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this,
                    "Introduce el correo y la contraseña",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser usuario =
                                FirebaseAuth.getInstance().getCurrentUser();

                        if (usuario != null && usuario.isEmailVerified()) {

                            Intent i = new Intent(this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        } else if (usuario != null) {

                            usuario.sendEmailVerification();

                            Toast.makeText(this,
                                    "Debes verificar tu correo electrónico",
                                    Toast.LENGTH_LONG).show();

                            FirebaseAuth.getInstance().signOut();
                        }

                    } else {
                        Toast.makeText(this,
                                "Correo o contraseña incorrectos",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                login();
            } else {
                String s = "";
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) s = "Cancelado";
                else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK:
                        s = "Sin conexión a Internet";
                        break;
                    case ErrorCodes.PROVIDER_ERROR:
                        s = "Error en proveedor";
                        break;
                    case ErrorCodes.DEVELOPER_ERROR:
                        s = "Error desarrollador";
                        break;
                    default:
                        s = "Otros errores de autentificación";
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }
}