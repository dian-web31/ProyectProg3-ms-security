package com.jdh.ms_security.Configurations;

import com.jdh.ms_security.Interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { //El WebConfig es un paquete tal cual como Service, algo para cualquier parte del codigo
    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //Registry nos ayuda a ponerle un muro al proyecto, eso es el addInterceptors, estamos configurando la contruccion del muro
        //y ese muro se llamara securityInterceptor y todo lo que empiece por /api/** será protegido por el muro
        //y se omitira del muro todo lo que empiece por /api/public/**

        //Si quiero crear un nuevo permiso, debo comentar esta linea
        //Estamos hablando de que este es un muro en el cual no permitiremos crear mas cosas, mas permisos para los roles
//
//        registry.addInterceptor(securityInterceptor)
//                .addPathPatterns("/api/**")
//                .excludePathPatterns("/api/public/**");
    }
}