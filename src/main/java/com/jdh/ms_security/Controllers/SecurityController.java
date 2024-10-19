package com.jdh.ms_security.Controllers;
import java.util.UUID;
import com.jdh.ms_security.Models.User;
import com.jdh.ms_security.Repositories.UserRepository;
import com.jdh.ms_security.Services.EncryptionService;
import com.jdh.ms_security.Services.NotificationService;
import com.jdh.ms_security.Services.SecondFactor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody User theNewUser,
                                         final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if (theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
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

    @PostMapping("/second-validation")
    public HashMap<String, Object> autentication(@RequestBody User theNewUser,
                                                 final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        //Crear un codigo random
        String code = UUID.randomUUID().toString();
        String token = "";
        // Obtener el usuario actual desde la base de datos
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        token = theJwtService.generateToken(theActualUser);

        // Evitar enviar la contraseña en la respuesta
        theActualUser.setPassword("");

        // Agregar el token, usuario y código generado a la respuesta
        theResponse.put("token", token);
        theResponse.put("user", theActualUser);
        theResponse.put("code", code);

        // Llamar al servicio de segundo factor para validar el correo y enviar el código
        theSecondFactor.ValidationEmail(theNewUser.getEmail(), code);

        return theResponse;

    }
}

