package com.jdh.ms_security.Services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Value("${apis.ms-notification-url}")
    private String notificationServiceUrl; // URL del microservicio de notificaciones

    //Metodo para Saludar a los nuevos usuarios
    public void SendlWelcome(String email, String username) {
        RestTemplate restTemplate = new RestTemplate();
        String url = notificationServiceUrl + "welcom"; // endpoint debe coincidir con el de ms-notificacion

        // Crear el cuerpo de la solicitud que espera el servicio de notificaciones
        Map<String, String> request = new HashMap<>();
        request.put("recipient", email);
        request.put("username", username);

        restTemplate.postForObject(url, request, String.class);
    }

    //Metodo para Eliminar Usuarios
    public void SendDelete(String email, String username) {
        RestTemplate restTemplate = new RestTemplate();
        String url = notificationServiceUrl + "delete"; // endpoint debe coincidir con el de ms-notificacion

        // Crear el cuerpo de la solicitud que espera el servicio de notificaciones
        Map<String, String> request = new HashMap<>();
        request.put("recipient", email);
        request.put("username", username);

        restTemplate.delete(url, request, String.class);
    }

    //Metodo para Actualizar Usuarios
    public void SendUpdate(String email, String username) {
        RestTemplate restTemplate = new RestTemplate();
        String url = notificationServiceUrl + "newuser"; // endpoint debe coincidir con el de ms-notificacion

        // Crear el cuerpo de la solicitud que espera el servicio de notificaciones
        Map<String, String> request = new HashMap<>();
        request.put("recipient", email);
        request.put("username", username);

        restTemplate.put(url, request, String.class);
    }

    public void NewPassword(String email, String password){
        RestTemplate restTemplate = new RestTemplate();
        String url = notificationServiceUrl + "reset-password";

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);

        restTemplate.put(url, request, String.class);

    }
}
