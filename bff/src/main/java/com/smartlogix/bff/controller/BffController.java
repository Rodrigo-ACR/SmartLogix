package com.smartlogix.bff.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/productos")
public class BffController {

    private final String URL = "http://localhost:8091/productos";

    @GetMapping
    public Object obtenerProductos() {
        RestTemplate rest = new RestTemplate();
        return rest.getForObject(URL, Object.class);
    }
}