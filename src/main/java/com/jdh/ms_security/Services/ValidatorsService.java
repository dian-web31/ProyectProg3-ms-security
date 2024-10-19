package com.jdh.ms_security.Services;

import com.jdh.ms_security.Models.*;
import com.jdh.ms_security.Repositories.PermissionRepository;
import com.jdh.ms_security.Repositories.RolePermissionRepository;
import com.jdh.ms_security.Repositories.UserRepository;
import com.jdh.ms_security.Repositories.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidatorsService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PermissionRepository thePermissionRepository;
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    //En esta clase vendra la tarea mas larga
    private static final String BEARER_PREFIX = "Bearer ";
    public boolean validationRolePermission(HttpServletRequest request,
                                            String url,
                                            String method){
        boolean success=false;
        //analizamos el token, primero pedimos el usuario
        User theUser=this.getUser(request);
        if(theUser!=null){
            System.out.println("Antes URL "+url+" metodo "+method);
            //Tras encontrar el usuario, buscaremos ahora el url
            //usamos esta expresion regular 0-9a-fA-F y si encuentra algun valor numerico o alfabetico, lo reemplaza con el interrogante
            url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");
            System.out.println("URL "+url+" metodo "+method);
            Permission thePermission=this.thePermissionRepository.getPermission(url,method);

            List<UserRole> roles=this.theUserRoleRepository.getRolesByUser(theUser.get_id());
            //Por cada uno de los roles se verifica si existe un permiso, sera dentro de la clase UserRole
            int i=0;
            //Tenemos la condicion de success porque si se encontro el role, entonces no hace falta que siga buscando los demas roles
            while(i<roles.size() && success==false){
                UserRole actual=roles.get(i);
                Role theRole=actual.getRole();
                if(theRole!=null && thePermission!=null){
                    System.out.println("Rol "+theRole.get_id()+ " Permission "+thePermission.get_id());
                    RolePermission theRolePermission=this.theRolePermissionRepository.getRolePermission(theRole.get_id(),thePermission.get_id());
                    if (theRolePermission!=null){
                        success=true;
                    }
                }else{
                    success=false;
                }
                i+=1;
            }

        }
        return success;
    }
    //Este será el usuario que solicitamos revisar
    public User getUser(final HttpServletRequest request) {
        User theUser=null;
        //Obtenemos el encabezado de la carta
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Header "+authorizationHeader);
        //Si viene una autorizacion y si el authorizationHeader viene la palabra BEARER, entonces adentro vendra un token
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            System.out.println("Bearer Token: " + token);
            //Ahora con el token iremos al jwtService para descifrarlo
            User theUserFromToken=jwtService.getUserFromToken(token);
            if(theUserFromToken!=null) {
                //Buscamos el usuario en la base de datos
                theUser= this.theUserRepository.findById(theUserFromToken.get_id())
                        .orElse(null);
                        //Si no se encontro el usuario, entonces se retornara el null
            }
        }
        return theUser;
    }
}
