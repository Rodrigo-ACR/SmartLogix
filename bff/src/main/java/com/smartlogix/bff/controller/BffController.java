package com.smartlogix.bff.controller;

import com.smartlogix.bff.exception.ApiError;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

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

        // =================================================
        // URLS MICROSERVICIOS
        // =================================================

        private final String URL_INVENTARIO = "http://host.docker.internal:8091/productos";

        private final String URL_PEDIDOS = "http://host.docker.internal:8092/pedidos";

        private final String URL_ENVIOS = "http://host.docker.internal:8093/envios";

        // =================================================
        // INVENTARIO
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
        // PEDIDOS
        // =================================================

        @GetMapping("/pedidos")
        @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackPedidos")
        public ResponseEntity<Object> obtenerPedidos() {

                logger.info("GET /api/productos/pedidos");

                Object respuesta = rest.getForObject(
                                URL_PEDIDOS,
                                Object.class);

                return ResponseEntity.ok(respuesta);
        }

        // =================================================
        // ENVIOS
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
        // CREAR PEDIDO
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
        // CREAR PRODUCTO
        // =================================================

        @PostMapping
        @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackCrearProducto")
        public ResponseEntity<Object> crearProducto(
                        @RequestBody Object producto) {

                logger.info("POST /api/productos");

                Object respuesta = rest.postForObject(
                                URL_INVENTARIO,
                                producto,
                                Object.class);

                return ResponseEntity.status(201)
                                .body(respuesta);
        }

        // =================================================
        // EDITAR PRODUCTO
        // =================================================

        @PutMapping("/{id}")
        @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackEditar")
        public ResponseEntity<?> editar(
                        @PathVariable Long id,
                        @RequestBody Object producto) {

                logger.info("PUT /api/productos/{}", id);

                String URL = "http://host.docker.internal:8091/productos/" + id;

                rest.put(URL, producto);

                return ResponseEntity.ok("Actualizado");
        }

        // =================================================
        // ELIMINAR PRODUCTO
        // =================================================

        @DeleteMapping("/{id}")
        @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackEliminar")
        public ResponseEntity<?> eliminar(
                        @PathVariable Long id) {

                logger.info("DELETE /api/productos/{}", id);

                String URL = "http://host.docker.internal:8091/productos/" + id;

                rest.delete(URL);

                return ResponseEntity.ok("Eliminado");
        }

        // =================================================
        // FALLBACK INVENTARIO
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
        // FALLBACK PEDIDOS
        // =================================================

        public ResponseEntity<Object> fallbackPedidos(
                        Throwable e) {

                logger.error(
                                "CircuitBreaker PEDIDOS: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "Pedidos temporalmente no disponibles",
                                                503));
        }

        // =================================================
        // FALLBACK ENVIOS
        // =================================================

        public ResponseEntity<Object> fallbackEnvios(
                        Throwable e) {

                logger.error(
                                "CircuitBreaker ENVIOS: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "Envios temporalmente no disponibles",
                                                503));
        }

        // =================================================
        // FALLBACK CREAR PEDIDO
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
        // FALLBACK CREAR PRODUCTO
        // =================================================

        public ResponseEntity<Object> fallbackCrearProducto(
                        Object producto,
                        Throwable e) {

                logger.error(
                                "CircuitBreaker CREAR PRODUCTO: {}",
                                e.getMessage());

                return ResponseEntity.status(503)
                                .body(new ApiError(
                                                "No se pudo crear producto",
                                                503));
        }

        // =================================================
        // FALLBACK EDITAR
        // =================================================

        public ResponseEntity<?> fallbackEditar(
                        Long id,
                        Object producto,
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
        // FALLBACK ELIMINAR
        // =================================================

        public ResponseEntity<?> fallbackEliminar(
                        Long id,
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