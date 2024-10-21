package com.jdh.ms_security.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class OAuth2Service {
    

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${google.auth.uri}")
    private String googleAuthUrl;

    @Value("${google.user.info.uri}")
    private String googleUserInfoUri;

    @Value("${google.token.uri}")
    private String googleTokenUrl;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${github.auth.uri}")
    private String githubAuthUrl;

    @Value("${github.user.info.uri}")
    private String githubUserInfoUri;

    @Value("${github.token.uri}")
    private String githubTokenUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Genera la URL de autenticación para Google
    public String getGoogleAuthUrl(String state) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(googleAuthUrl)
                .queryParam("client_id", googleClientId)  // ID del cliente
                .queryParam("redirect_uri", googleRedirectUri)  // URL de redirección después de autenticación
                .queryParam("response_type", "code")  // Tipo de respuesta
                .queryParam("scope", "openid profile email")  // Scopes que pides (permisos)
                .queryParam("state", state)  // Estado para prevenir ataques CSRF
                .queryParam("access_type", "offline")  // Para obtener un refresh token
                .queryParam("prompt", "consent");  // Forzar consentimiento del usuario cada vez
        return uriBuilder.toUriString();  // Devuelve la URL de autenticación
    }

    // Método para generar la URL de autenticación de GitHub
    public String getGitHubAuthUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl(githubAuthUrl)
                .queryParam("client_id", githubClientId)
                .queryParam("redirect_uri", githubRedirectUri)
                .queryParam("scope", "read:user")  // Puedes ajustar los scopes según lo que necesites
                .queryParam("state", state)
                .build().toUriString();
    }

    public Map<String, Object> getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUrl, request, Map.class);
        return response.getBody();
    }

    public Map<String, Object> getGitHubAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // Tipo de contenido correcto

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", githubClientId);
        params.add("client_secret", githubClientSecret);
        params.add("code", code);
        params.add("redirect_uri", githubRedirectUri);

        // Crear la entidad que se enviará en la solicitud HTTP
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Realizar la solicitud POST para obtener el token de GitHub
        ResponseEntity<Map> response = restTemplate.postForEntity(githubTokenUrl, request,Map.class);
        System.out.println("Response from GitHub token endpoint: " + response.getBody());

        // Retorna el cuerpo de la respuesta que contiene el token de acceso
        return response.getBody();
    }

    public Map<String, Object> getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Añadir token de acceso en el encabezado de autorización

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Realiza la solicitud a la API de Google
        ResponseEntity<Map> response = restTemplate.exchange(
                googleUserInfoUri, // La URI de Google para obtener la información del usuario
                HttpMethod.GET,
                entity,
                Map.class
        );

        // Retorna el cuerpo de la respuesta que contiene la información del usuario
        return response.getBody();
    }

    public Map<String, Object> getGitHubUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Añadir token de acceso en el encabezado de autorización

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Realizar la solicitud GET para obtener la información del usuario desde GitHub
        ResponseEntity<Map> response = restTemplate.exchange(
                githubUserInfoUri, // La URI de GitHub para obtener la información del usuario
                HttpMethod.GET,
                entity,
                Map.class
        );

        // Retorna el cuerpo de la respuesta que contiene la información del usuario
        return response.getBody();
    }

}
