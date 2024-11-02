package com.jdh.ms_security.Controllers;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.jdh.ms_security.Models.Permission;
import com.jdh.ms_security.Models.Session;
import com.jdh.ms_security.Models.User;
import com.jdh.ms_security.Repositories.SessionRepository;
import com.jdh.ms_security.Repositories.UserRepository;
import com.jdh.ms_security.Services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private ValidatorsService theValidatorsService;
    @Autowired
    private SessionController theSessionController;

    //El profesor nos pasa este end point para usarlo como respuesta del adonis
    @PostMapping("permissions-validation")
    //este request tiene la peticion que vamos a utilizar
    public boolean permissionsValidation(final HttpServletRequest request,
                                         @RequestBody Permission thePermission) {
        //se analiza si existen los permisos
        boolean success=this.theValidatorsService.validationRolePermission(request,thePermission.getUrl(),thePermission.getMethod());
        return success;
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody User theNewUser,
                                         final HttpServletResponse response) throws IOException {
        HashMap<String, Object> responseBody = new HashMap<>();
        //Verificaremos si el usuario existe
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if (theActualUser != null && theActualUser.getPassword().
                equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {

            //Crearemos el codigo para el segundo factor de autenticacion
            String code2fa = UUID.randomUUID().toString();

            //Asignamos y guardamos el codigo al usuario
            theActualUser.setcode2fa(code2fa);
            this.theUserRepository.save(theActualUser);

            //Llamar al servicio de segundo factor para validar el correo y enviar el código
            theSecondFactor.ValidationEmail(theNewUser.getEmail(), code2fa);

            //Creamos la respuesta a la peticion
            responseBody.put("message","Codigo de segundo factor de autenticacion enviado a su correo");
            responseBody.put("status", "success");
            return ResponseEntity.ok(responseBody);
        } else {
            responseBody.put("message","El usuario o contraseña son incorrectos");
            responseBody.put("status","error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

    }

    @PostMapping("/second-validation")
    public ResponseEntity<HashMap<String, Object>> factorAuthentication(@RequestBody Map<String, String> requestBody) {
        HashMap<String, Object> theResponse = new HashMap<>();
        //crearemos la instancia de la session
        Session newSession = new Session();

        //Asignaremos una fecha para la terminacion de la session
        // LocalDateTime expirationDate = LocalDateTime.now().plusHours(1);

        //Encontramos al usuario a partir de su codigo
        String code2fa = requestBody.get("code2fa");
        User theActualUser = this.theUserRepository.getUserByCode2fa(code2fa);
        System.out.println(code2fa);

        //Validamos si el usuario fue encontrado
        if (theActualUser != null && theActualUser.getcode2fa().equals(code2fa)){
            //Creamos el token y la expiracion de la session
            String expirationDate = "2025-01-01";
            String token = theJwtService.generateToken(theActualUser);

            theActualUser.setcode2fa("");
            this.theUserRepository.save(theActualUser);

            //Asignaremos el token y la fecha de expiracion a la session
            newSession.setToken(token);
            newSession.setExpiration(expirationDate);
            //creamos la session
            newSession = this.theSessionRepository.save(newSession);

            // Llamar a matchUser para asociar el usuario con la sesión
            Session matchedSession = theSessionController.matchUser(newSession.get_id(), theActualUser.get_id());

            theResponse.put("message","Codigo de autenticacion verificado. Sesion creada");
            theResponse.put("token",token);
            return ResponseEntity.ok(theResponse);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(theResponse);
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

        return ResponseEntity.ok("Contraseña cambiada y enviada por correo: ");
    }

}


