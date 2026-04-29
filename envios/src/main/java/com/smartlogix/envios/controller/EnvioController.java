package com.smartlogix.envios.controller;

import com.smartlogix.envios.model.Envio;
import com.smartlogix.envios.repository.EnvioRepository;
import com.smartlogix.envios.exception.ApiError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/envios")
@CrossOrigin(origins = "*")
public class EnvioController {

    private static final Logger logger = LoggerFactory.getLogger(EnvioController.class);

    private final EnvioRepository repo;

    public EnvioController(EnvioRepository repo) {
        this.repo = repo;
    }

    // GET TODOS
    @GetMapping
    public List<Envio> listar() {
        logger.info("GET /envios");
        return repo.findAll();
    }

    // GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtener(@PathVariable Long id) {

        logger.info("GET /envios/{}", id);

        return repo.findById(id)
                .map(e -> ResponseEntity.ok((Object) e))
                .orElseGet(() -> {
                    logger.error("Envio no encontrado ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("Envio no encontrado", 404));
                });
    }

    // POST
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Envio e) {

        logger.info("POST /envios");

        Envio nuevo = repo.save(e);

        logger.info("Envio creado ID: {}", nuevo.getId());

        return ResponseEntity.status(201).body((Object) nuevo);
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id, @RequestBody Envio nuevo) {

        logger.info("PUT /envios/{}", id);

        return repo.findById(id)
                .map(e -> {
                    e.setDireccion(nuevo.getDireccion());
                    e.setEstado(nuevo.getEstado());
                    e.setFecha(nuevo.getFecha());

                    repo.save(e);

                    logger.info("Envio actualizado ID: {}", id);
                    return ResponseEntity.ok((Object) e);
                })
                .orElseGet(() -> {
                    logger.error("No se pudo actualizar envio ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("Envio no existe", 404));
                });
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {

        logger.info("DELETE /envios/{}", id);

        return repo.findById(id)
                .map(e -> {
                    repo.delete(e);
                    logger.info("Envio eliminado ID: {}", id);
                    return ResponseEntity.ok((Object) "Envio eliminado");
                })
                .orElseGet(() -> {
                    logger.error("No se pudo eliminar envio ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("Envio no existe", 404));
                });
    }
}