package com.example.andatech;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseUser usuario;
    private TextView tvNombre, tvCorreo, tvTelefono, tvCambiarContrasena;
    private ImageView ivFotoPerfil, editNombre, editCorreo, editTelefono;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            volverAlLogin();
            return;
        }

        // Vincular vistas
        tvNombre = findViewById(R.id.perfilNombre);
        tvCorreo = findViewById(R.id.perfilCorreo);
        tvTelefono = findViewById(R.id.perfilTelefono);
        tvCambiarContrasena = findViewById(R.id.tvCambiarContrasena);
        ivFotoPerfil = findViewById(R.id.perfilFoto);

        editNombre = findViewById(R.id.editNombre);
        editCorreo = findViewById(R.id.editCorreo);
        editTelefono = findViewById(R.id.editTelefono);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Subraya visualmente el enlace de cambiar contraseña
        tvCambiarContrasena.setPaintFlags(tvCambiarContrasena.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        cargarDatosPerfil();
        configurarListeners();
    }

    private void cargarDatosPerfil() {
        String nombre = usuario.getDisplayName();
        String correo = usuario.getEmail();
        String telefono = usuario.getPhoneNumber();
        Uri urlFoto = usuario.getPhotoUrl();

        tvNombre.setText(nombre != null && !nombre.isEmpty() ? nombre : "No disponible");
        tvCorreo.setText(correo != null ? correo : "No disponible");
        tvTelefono.setText(telefono != null && !telefono.isEmpty() ? telefono : "No disponible");

        if (urlFoto != null) {
            Glide.with(this)
                    .load(urlFoto)
                    .circleCrop()
                    .into(ivFotoPerfil);
        }

        // Ocultar edición de correo y contraseña para usuarios de Google
        if (esUsuarioDeGoogle()) {
            editCorreo.setVisibility(View.GONE);
            tvCambiarContrasena.setVisibility(View.GONE);
        }
    }

    private boolean esUsuarioDeGoogle() {
        for (UserInfo profile : usuario.getProviderData()) {
            if ("google.com".equals(profile.getProviderId())) {
                return true;
            }
        }
        return false;
    }

    private void configurarListeners() {
        editNombre.setOnClickListener(v -> mostrarDialogoNombre());
        editCorreo.setOnClickListener(v -> mostrarDialogoCorreo());
        editTelefono.setOnClickListener(v -> mostrarDialogoTelefono());

        tvCambiarContrasena.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, CambiarContrasenaActivity.class);
            startActivity(intent);
        });
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            volverAlLogin();
        });

        ImageView logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // MODIFICAR NOMBRE
    private void mostrarDialogoNombre() {
        EditText input = new EditText(this);
        input.setHint("Nuevo nombre");
        if (usuario.getDisplayName() != null) {
            input.setText(usuario.getDisplayName());
        }

        new AlertDialog.Builder(this)
                .setTitle("Modificar nombre")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();
                    if (nuevoNombre.isEmpty()) {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nuevoNombre)
                            .build();

                    usuario.updateProfile(updates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tvNombre.setText(nuevoNombre);
                            Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error al actualizar nombre", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // MODIFICAR CORREO (CON VERIFICACIÓN)
    private void mostrarDialogoCorreo() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        final EditText inputNuevoCorreo = new EditText(this);
        inputNuevoCorreo.setHint("Nuevo correo electrónico");
        inputNuevoCorreo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(inputNuevoCorreo);

        final EditText inputPasswordActual = new EditText(this);
        inputPasswordActual.setHint("Contraseña actual");
        inputPasswordActual.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPasswordActual);

        new AlertDialog.Builder(this)
                .setTitle("Cambiar correo electrónico")
                .setMessage("Te enviaremos un enlace de verificación al nuevo correo.")
                .setView(layout)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String nuevoCorreo = inputNuevoCorreo.getText().toString().trim();
                    String password = inputPasswordActual.getText().toString().trim();

                    if (nuevoCorreo.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Rellena ambos campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (usuario.getEmail() != null) {
                        AuthCredential credential = EmailAuthProvider.getCredential(usuario.getEmail(), password);
                        usuario.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {
                                usuario.verifyBeforeUpdateEmail(nuevoCorreo)
                                        .addOnCompleteListener(verifyTask -> {
                                            if (verifyTask.isSuccessful()) {
                                                Toast.makeText(this,
                                                        "Enlace enviado a " + nuevoCorreo + ". Confírmalo y vuelve a iniciar sesión.",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(this,
                                                        "Error: " + verifyTask.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // MODIFICAR TELÉFONO (Firebase Auth SMS)

    private void mostrarDialogoTelefono() {
        EditText input = new EditText(this);
        input.setHint("+34600123456 (con prefijo de país)");
        input.setInputType(InputType.TYPE_CLASS_PHONE);

        new AlertDialog.Builder(this)
                .setTitle("Vincular / Cambiar teléfono")
                .setMessage("Firebase enviará un código SMS para verificar el número.")
                .setView(input)
                .setPositiveButton("Enviar SMS", (dialog, which) -> {
                    String telefono = input.getText().toString().trim();
                    if (!telefono.startsWith("+")) {
                        Toast.makeText(this, "Incluye el prefijo del país (ej. +34)", Toast.LENGTH_LONG).show();
                        return;
                    }
                    enviarCodigoSms(telefono);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void enviarCodigoSms(String numeroTelefono) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(numeroTelefono)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        actualizarTelefonoConCredencial(credential, numeroTelefono);
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(PerfilActivity.this, "Error SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        pedirCodigoSmsAlUsuario(verificationId, numeroTelefono);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void pedirCodigoSmsAlUsuario(String verificationId, String numeroTelefono) {
        EditText inputCodigo = new EditText(this);
        inputCodigo.setHint("Código SMS de 6 dígitos");
        inputCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("Introduce el código")
                .setView(inputCodigo)
                .setPositiveButton("Verificar", (dialog, which) -> {
                    String codigo = inputCodigo.getText().toString().trim();
                    if (!codigo.isEmpty()) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codigo);
                        actualizarTelefonoConCredencial(credential, numeroTelefono);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void actualizarTelefonoConCredencial(PhoneAuthCredential credential, String numeroTelefono) {
        usuario.updatePhoneNumber(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvTelefono.setText(numeroTelefono);
                Toast.makeText(this, "Teléfono actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar teléfono: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void volverAlLogin() {
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.perfil) {
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