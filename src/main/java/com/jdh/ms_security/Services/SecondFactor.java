package com.jdh.ms_security.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondFactor {

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public void ValidationEmail(String email, String code2FA) {
        RestTemplate restTemplate = new RestTemplate();
        String url = notificationServiceUrl +"validation";

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("code2FA", code2FA);

        try {
            restTemplate.postForObject(url, request, Map.class);
            System.out.println("Código enviado correctamente al correo");
        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
            // Manejo de error o reintento
        }
    }

}
