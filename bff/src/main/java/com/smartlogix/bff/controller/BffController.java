package com.smartlogix.bff.controller;

import com.smartlogix.bff.exception.ApiError;

import com.smartlogix.bff.exception.GlobalExceptionHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class BffController {

        private final GlobalExceptionHandler globalExceptionHandler;

        private static final Logger logger = LoggerFactory.getLogger(BffController.class);

        private final RestTemplate rest = new RestTemplate();

        // =================================================
        // URLS MICROSERVICIOS
        // =================================================
        private final String URL_INVENTARIO = "http://host.docker.internal:8091/productos";

        private final String URL_PEDIDOS = "http://host.docker.internal:8092/pedidos";

        private final String URL_ENVIOS = "http://host.docker.internal:8093/envios";

        BffController(GlobalExceptionHandler globalExceptionHandler) {
                this.globalExceptionHandler = globalExceptionHandler;
        }

        // =================================================
        //  INVENTARIO
        // =================================================

        @GetMapping

        @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackInventario")
        public ResponseEntity<Object> obtenerProductos() {

                logger.info("GET /api/productos");

                Object respuesta = rest.getForObject(
                                URL_INVENTARIO,
                                Object.class);

                return ResponseEntity.ok(respuesta);
        }

        // =================================================
        //  PEDIDOS
        // =================================================
        @GetMapping("/pedidos")
        @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackPedidos")
        public ResponseEntity<Object> obtenerPedidos(
                        HttpServletRequest request) {

                logger.info("GET /api/productos/pedidos");

                String rol = (String) request.getAttribute("rol");

                //  SOLO ADMIN
                if (rol == null || !rol.equals("ADMIN")) {

                        return ResponseEntity.status(403)
                                        .body(new ApiError(
                                                        "No autorizado",
                                                        403));
                }

                Object respuesta = rest.getForObject(
                                URL_PEDIDOS,
                                Object.class);

                return ResponseEntity.ok(respuesta);
        }

        // =================================================
        //  ENVIOS
        // =================================================
        @GetMapping("/envios")
        @CircuitBreaker(name = "envios", fallbackMethod = "fallbackEnvios")
        public ResponseEntity<Object> obtenerEnvios() {

                logger.info("GET /api/productos/envios");

                Object respuesta = rest.getForObject(
                                URL_ENVIOS,
                                Object.class);

                return ResponseEntity.ok(respuesta);
        }

        // =================================================
        //  CREAR PEDIDO
        // =================================================
        @PostMapping("/pedidos")
        @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackCrearPedido")
        public ResponseEntity<Object> crearPedido(
                        @RequestBody Object pedido) {

                logger.info("POST /api/productos/pedidos");

                Object respuesta = rest.postForObject(
                                URL_PEDIDOS,
                                pedido,
                                Object.class);

                return ResponseEntity.status(201)
                                .body(respuesta);
        }

        // =================================================
        //  EDITAR PRODUCTO
        // =================================================
        @PutMapping("/{id}")
        @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackEditar")
        public ResponseEntity<?> editar(
                        @PathVariable Long id,
                        @RequestBody Object producto,
                        HttpServletRequest request) {

                String rol = (String) request.getAttribute("rol");

                //  SOLO ADMIN
                if (rol == null || !rol.equals("ADMIN")) {

                        return ResponseEntity.status(403)
                                        .body(new ApiError(
                                                        "No autorizado",
                                                        403));
                }

                String URL = "http://host.docker.internal:8091/productos/" + id;

                rest.put(URL, producto);

                return ResponseEntity.ok("Actualizado");
        }

        // =================================================
        //  ELIMINAR PRODUCTO
        // =================================================
        @DeleteMapping("/{id}")
        @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackEliminar")
        public ResponseEntity<?> eliminar(
                        @PathVariable Long id,
                        HttpServletRequest request) {

                String rol = (String) request.getAttribute("rol");

                //  SOLO ADMIN
                if (rol == null || !rol.equals("ADMIN")) {

                        return ResponseEntity.status(403)
                                        .body(new ApiError(
                                                        "No autorizado",
                                                        403));
                }

                String URL = "http://host.docker.internal:8091/productos/" + id;

                rest.delete(URL);

                return ResponseEntity.ok("Eliminado");
        }

        // =================================================
        //  FALLBACK INVENTARIO
        // =================================================
        public ResponseEntity<Object> fallbackInventario(
                        Throwable e) {

                logger.error(
                                "CircuitBreaker INVENTARIO: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "Inventario temporalmente no disponible",
                                                503));
        }

        // =================================================
        //  FALLBACK PEDIDOS
        // =================================================
        public ResponseEntity<Object> fallbackPedidos(
                        HttpServletRequest request,
                        Throwable e) {

                logger.error(
                                "CircuitBreaker PEDIDOS: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "Pedidos temporalmente no disponible",
                                                503));
        }

        // =================================================
        //  FALLBACK ENVIOS
        // =================================================
        public ResponseEntity<Object> fallbackEnvios(
                        Throwable e) {

                logger.error(
                                "CircuitBreaker ENVIOS: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "Envios temporalmente no disponible",
                                                503));
        }

        // =================================================
        //  FALLBACK CREAR PEDIDO
        // =================================================
        public ResponseEntity<Object> fallbackCrearPedido(
                        Object pedido,
                        Throwable e) {

                logger.error(
                                "CircuitBreaker CREAR PEDIDO: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "No se pudo crear pedido",
                                                503));
        }

        // =================================================
        //  FALLBACK EDITAR
        // =================================================
        public ResponseEntity<?> fallbackEditar(
                        Long id,
                        Object producto,
                        HttpServletRequest request,
                        Throwable e) {

                logger.error(
                                "CircuitBreaker EDITAR: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "No se pudo editar producto",
                                                503));
        }

        // =================================================
        //  FALLBACK ELIMINAR
        // =================================================
        public ResponseEntity<?> fallbackEliminar(
                        Long id,
                        HttpServletRequest request,
                        Throwable e) {

                logger.error(
                                "CircuitBreaker ELIMINAR: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "No se pudo eliminar producto",
                                                503));
        }
}