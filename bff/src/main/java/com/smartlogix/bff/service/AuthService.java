package com.smartlogix.bff.service;

import com.smartlogix.bff.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class AuthService {

    // RestTemplate @LoadBalanced inyectado desde RestTemplateConfig
    private final RestTemplate rest;

    public AuthService(RestTemplate rest) {
        this.rest = rest;
    }

    // Nombre logico registrado en Eureka (sin puerto)
    private final String URL_USUARIOS = "http://usuarios/usuarios";

    public Usuario login(String correo, String password) {

        // Armamos el body para enviar al MS Usuarios
        Map<String, String> body = Map.of(
                "correo", correo,
                "password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Usuario> response = rest.postForEntity(
                    URL_USUARIOS + "/login",
                    request,
                    Usuario.class);

            return response.getBody();

        } catch (Exception e) {
            // Si MS Usuarios devuelve 400 o 404, retornamos null
            return null;
        }
    }
}