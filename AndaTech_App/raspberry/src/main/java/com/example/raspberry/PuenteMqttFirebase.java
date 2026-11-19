package com.example.raspberry;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PuenteMqttFirebase {

    public static void main(String[] args) {
        try {
            FileInputStream serviceAccount = new FileInputStream("credenciales.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://andatech-7c91c-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);

            DatabaseReference refEstado = FirebaseDatabase.getInstance().getReference("Andador1/estado_actual");
            DatabaseReference refAlertas = FirebaseDatabase.getInstance().getReference("Andador1/alertas");

            System.out.println("Conectado a Firebase correctamente.");

            MqttClient clienteMqtt = new MqttClient("tcp://localhost:1883", "raspberry");

            clienteMqtt.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Conexión MQTT perdida: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    try {
                        String payload = new String(message.getPayload());
                        JSONObject json = new JSONObject(payload);

                        // Actualizar estado_actual
                        Map<String, Object> estado = new HashMap<>();
                        estado.put("temperatura", json.optDouble("temperatura", 0.0));
                        estado.put("humedad", json.optDouble("humedad", 0.0));
                        estado.put("bateria", json.optInt("bateria", 100));
                        refEstado.setValueAsync(estado);


                        // Guardar alertas
                        if (json.optBoolean("es_alerta", false)) {
                            String horaActual = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

                            Map<String, Object> alerta = new HashMap<>();
                            alerta.put("titulo", json.optString("titulo", "Alerta"));
                            alerta.put("descripcion", json.optString("descripcion", ""));
                            alerta.put("fecha", horaActual);

                            refAlertas.push().setValueAsync(alerta);
                            System.out.println("¡Alerta subida a Firebase!");
                        }

                    } catch (Exception e) {
                        System.out.println("Error leyendo JSON: " + e.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            org.eclipse.paho.client.mqttv3.MqttConnectOptions opcionesMqtt = new org.eclipse.paho.client.mqttv3.MqttConnectOptions();
            opcionesMqtt.setAutomaticReconnect(true);
            opcionesMqtt.setCleanSession(true);

            clienteMqtt.connect(opcionesMqtt);
            clienteMqtt.subscribe("andatech/andador1");
            System.out.println("Escuchando mensajes MQTT en andatech/andador1...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}