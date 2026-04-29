package com.smartlogix.bff.controller;

import com.smartlogix.bff.exception.ApiError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    private final RestTemplate rest = new RestTemplate();

    // URLs de microservicios
    private final String URL_INVENTARIO = "http://localhost:8091/productos";
    private final String URL_PEDIDOS = "http://localhost:8092/pedidos";
    private final String URL_ENVIOS = "http://localhost:8093/envios";

    // =========================
    // 📦 INVENTARIO
    // =========================
    @GetMapping
    public ResponseEntity<Object> obtenerProductos() {

        logger.info("GET /api/productos");

        try {
            logger.debug("Llamando a: {}", URL_INVENTARIO);

            Object respuesta = rest.getForObject(URL_INVENTARIO, Object.class);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error en inventario: {}", e.getMessage());

            return ResponseEntity.status(503)
                    .body(new ApiError("Servicio de inventario no disponible", 503));
        }
    }

    // =========================
    // 📦 PEDIDOS
    // =========================
    @GetMapping("/pedidos")
    public ResponseEntity<Object> obtenerPedidos() {

        logger.info("GET /api/productos/pedidos");

        try {
            logger.debug("Llamando a: {}", URL_PEDIDOS);

            Object respuesta = rest.getForObject(URL_PEDIDOS, Object.class);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error en pedidos: {}", e.getMessage());

            return ResponseEntity.status(503)
                    .body(new ApiError("Servicio de pedidos no disponible", 503));
        }
    }

    // =========================
    // 🚚 ENVIOS
    // =========================
    @GetMapping("/envios")
    public ResponseEntity<Object> obtenerEnvios() {

        logger.info("GET /api/productos/envios");

        try {
            logger.debug("Llamando a: {}", URL_ENVIOS);

            Object respuesta = rest.getForObject(URL_ENVIOS, Object.class);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error en envios: {}", e.getMessage());

            return ResponseEntity.status(503)
                    .body(new ApiError("Servicio de envios no disponible", 503));
        }
    }

    // =========================
    // 🚚 CREAR PEDIDO
    // =========================
    @PostMapping("/pedidos")
public ResponseEntity<Object> crearPedido(@RequestBody Object pedido) {

    logger.info("POST /api/productos/pedidos");

    try {
        String URL = "http://localhost:8092/pedidos";

        logger.debug("Enviando a: {}", URL);

        RestTemplate rest = new RestTemplate();

        Object respuesta = rest.postForObject(URL, pedido, Object.class);

        return ResponseEntity.status(201).body(respuesta);

    } catch (Exception e) {
        logger.error("Error creando pedido: {}", e.getMessage());

        return ResponseEntity.status(503)
                .body(new ApiError("Servicio de pedidos no disponible", 503));
    }
}



}