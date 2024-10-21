package com.jdh.ms_security.Controllers;
import java.util.Map;
import java.util.UUID;

import com.jdh.ms_security.Models.Session;
import com.jdh.ms_security.Models.User;
import com.jdh.ms_security.Repositories.SessionRepository;
import com.jdh.ms_security.Repositories.UserRepository;
import com.jdh.ms_security.Services.EncryptionService;
import com.jdh.ms_security.Services.NotificationService;
import com.jdh.ms_security.Services.SecondFactor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jdh.ms_security.Services.JwtService;

import java.io.IOException;
import java.util.HashMap;

@CrossOrigin
@RestController
@RequestMapping("/api/public/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;
    @Autowired
    private SecondFactor theSecondFactor;
    @Autowired
    private SessionRepository theSessionRepository;
    @Autowired
    private NotificationService theNotificacionService;

    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody User theNewUser,
                                         final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if (theActualUser != null && theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            token = theJwtService.generateToken(theActualUser);
            theActualUser.setPassword("");
            theResponse.put("token", token);
            theResponse.put("user", theActualUser);
            return theResponse;
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return theResponse;
        }

    }

    @PostMapping("/second-validation/{userId}")
    public ResponseEntity<HashMap<String, Object>> factorAuthentication(@RequestBody Session theSession, @PathVariable String userId) {
        HashMap<String, Object> theResponse = new HashMap<>();

        // Verifica si hay una sesión válida para el usuario y el token
        Session validSession = theSessionRepository.getSessionByUserId(userId, theSession.getToken());

        //Identificamos el usuario y traemos su email
        User theUser=this.theUserRepository.findById(userId).orElse(null);
        String email = theUser.getEmail();

        if (validSession != null && userId != null) {
            //Crear un codigo random
            String code2FA = UUID.randomUUID().toString();

            // Guarda el token en la sesión
            validSession.setcode2FA(code2FA); // Guarda el 2FA
            theSessionRepository.save(validSession); // Guarda la sesión actualizada

            // Prepara la respuesta
            theResponse.put("code2FA", code2FA); // 2FA que generaste
            theResponse.put("email", email); // Usuario autenticado

            // Llamar al servicio de segundo factor para validar el correo y enviar el código
            theSecondFactor.ValidationEmail(email, code2FA);

            return ResponseEntity.ok(theResponse);

        } else {
            // Sesión no encontrada o no válida
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Restablecimiento de contraseña (envía correo con nueva contraseña)
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> userRequest) {
        String email = userRequest.get("email");

        // Buscar usuario por email
        User theUser = theUserRepository.getUserByEmail(email);
        if (theUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        // creamos la nueva contraseña
        String newPassword = UUID.randomUUID().toString();
        newPassword = newPassword.substring(0,5);
        theUser.setPassword(theEncryptionService.convertSHA256(newPassword)); // Encriptar la nueva contraseña
        theUserRepository.save(theUser); // Guardar el usuario actualizado

        theNotificacionService.NewPassword(email,newPassword);

        return ResponseEntity.ok("Contraseña cambiada y enviada por correo:{theResponse}");
    }

}


