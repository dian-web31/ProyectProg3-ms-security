package com.jdh.ms_security.Interceptors;

import com.jdh.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component //Este decorador nos ayuda a implementar el HandlerInterceptor este nos sirve para usar los metodos preHandle
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    private ValidatorsService validatorService;
    @Override //Analiza cuando la peticion apenas este entrando
    public boolean preHandle(HttpServletRequest request, //Dentro de la request viene el token, el metodo, el body
                             HttpServletResponse response,
                             Object handler)
            throws Exception { //acá tenemos la excepcion en caso de que el metodo falle
        boolean success=this.validatorService.validationRolePermission(request,request.getRequestURI(),request.getMethod());
        return success; //con el success decimos si la puerta se dejara abrir o no
        //y aca como sabemos que necesitamos la request (La carta) y desde ahi sabemos la URL y el metodo
    }

    @Override //analiza cuando la peticion ya va de salida
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    }
}
