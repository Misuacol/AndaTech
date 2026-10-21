package com.example.andatech;

import android.content.Intent;
import android.os.Bundle;
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
        login();
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
            List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build());
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);
        }
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