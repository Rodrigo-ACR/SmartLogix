package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.PedidoRepository;
import com.smartlogix.pedidos.exception.ApiError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoRepository repo;

    public PedidoController(PedidoRepository repo) {
        this.repo = repo;
    }

    // GET TODOS
    @GetMapping
    public List<Pedido> listar() {
        logger.info("GET /pedidos");
        return repo.findAll();
    }

    // GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtener(@PathVariable Long id) {

        logger.info("GET /pedidos/{}", id);

        return repo.findById(id)
                .map(p -> ResponseEntity.ok((Object) p))
                .orElseGet(() -> {
                    logger.error("Pedido no encontrado ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("Pedido no encontrado", 404));
                });
    }

    // POST
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Pedido p) {

        logger.info("POST /pedidos");

        Pedido nuevo = repo.save(p);

        logger.info("Pedido creado ID: {}", nuevo.getId());

        return ResponseEntity.status(201).body((Object) nuevo);
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id, @RequestBody Pedido nuevo) {

        logger.info("PUT /pedidos/{}", id);

        return repo.findById(id)
                .map(p -> {
                    p.setCliente(nuevo.getCliente());
                    p.setProducto(nuevo.getProducto());
                    p.setCantidad(nuevo.getCantidad());
                    p.setFecha(nuevo.getFecha());

                    repo.save(p);

                    logger.info("Pedido actualizado ID: {}", id);
                    return ResponseEntity.ok((Object) p);
                })
                .orElseGet(() -> {
                    logger.error("No se pudo actualizar pedido ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("Pedido no existe", 404));
                });
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {

        logger.info("DELETE /pedidos/{}", id);

        return repo.findById(id)
                .map(p -> {
                    repo.delete(p);
                    logger.info("Pedido eliminado ID: {}", id);
                    return ResponseEntity.ok((Object) "Pedido eliminado");
                })
                .orElseGet(() -> {
                    logger.error("No se pudo eliminar pedido ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("Pedido no existe", 404));
                });
    }
}