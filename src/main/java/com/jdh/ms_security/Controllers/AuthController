package com.jdh.ms_security.Controllers;

import com.jdh.ms_security.Services.OAuth2Service;
import com.jdh.ms_security.Services.RequestService;
import com.jdh.ms_security.entities.EmailContent;
import com.jdh.ms_security.entities.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private OAuth2Service oauth2Service;

    @Autowired
    private RequestService requestService;

    @PostMapping("/login")
    public ResponseEntity<String> sendEmail(@RequestBody EmailContent emailContent) {
        requestService.sendEmail(emailContent);
        return new ResponseEntity<>("Email sent", HttpStatus.OK);
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getUsers() {
        List<UserEntity> users = requestService.getUsers();
        return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Hello from secure endpoint", HttpStatus.OK);
    }

    @GetMapping("/google")
    public RedirectView authenticateWithGoogle(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String authUrl = oauth2Service.getGoogleAuthUrl(state);
        return new RedirectView(authUrl);
    }

    @GetMapping("/github")
    public RedirectView authenticateWithGithub(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String authUrl = oauth2Service.getGitHubAuthUrl(state);
        return new RedirectView(authUrl);
    }

    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider,
                                      @RequestParam String code,
                                      @RequestParam String state,
                                      HttpSession session) {
        System.out.println("ID de sesión en callback: " + session.getId());
        System.out.println("Estado recibido en callback: " + state);

        String sessionState = (String) session.getAttribute("oauth_state");
        System.out.println("Estado recuperado de la sesión: " + sessionState);

        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.badRequest().body("Estado inválido o sesión expirada");
        }

        // Limpiar el estado de la sesión después de usarlo
        session.removeAttribute("oauth_state");

        try {
            if ("google".equalsIgnoreCase(provider)) {
                Map<String, Object> tokenResponse = oauth2Service.getGoogleAccessToken(code);
                String accessToken = (String) tokenResponse.get("access_token");
                if (accessToken == null) {
                    return ResponseEntity.badRequest().body("No se pudo obtener el token de acceso");
                }

                Map<String, Object> userInfo = oauth2Service.getGoogleUserInfo(accessToken);
                return ResponseEntity.ok(userInfo);
            } else if ("github".equalsIgnoreCase(provider)) {
                Map<String, Object> tokenResponse = oauth2Service.getGitHubAccessToken(code);
                String accessToken = (String) tokenResponse.get("access_token");
                if (accessToken == null) {
                    return ResponseEntity.badRequest().body("No se pudo obtener el token de acceso de GitHub");
                }

                Map<String, Object> userInfo = oauth2Service.getGitHubUserInfo(accessToken);
                return ResponseEntity.ok(userInfo);
            } else {
                return ResponseEntity.badRequest().body("Proveedor no soportado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la autenticación: " + e.getMessage());
        }
    }
}

