package com.smartlogix.inventario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogix.inventario.model.Producto;
import com.smartlogix.inventario.repository.ProductoRepository;
import com.smartlogix.inventario.exception.ApiError;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoRepository repo;

    public ProductoController(ProductoRepository repo) {
        this.repo = repo;
    }

    //GET TODOS
    @GetMapping
    public List<Producto> listar() {
        logger.info("Listando todos los productos");
        return repo.findAll();
    }

    //GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        logger.info("Buscando producto con ID: {}", id);

        return repo.findById(id)
                .<ResponseEntity<?>>map(p -> {
                    logger.info("Producto encontrado: {}", p.getNombre());
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    logger.error("Producto NO encontrado con ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("El producto no fue encontrado", 404));
                });
    }

    //POST (CREAR)
    @PostMapping
    public Producto guardar(@RequestBody Producto p) {
        logger.info("Guardando producto: {}", p.getNombre());
        return repo.save(p);
    }

    //PUT (ACTUALIZAR)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto nuevo) {

        logger.info("Actualizando producto ID: {}", id);

        return repo.findById(id)
                .<ResponseEntity<?>>map(p -> {
                    p.setNombre(nuevo.getNombre());
                    p.setCantidad(nuevo.getCantidad());
                    p.setPrecio(nuevo.getPrecio());

                    repo.save(p);

                    logger.info("Producto actualizado: {}", p.getNombre());
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    logger.error("No se pudo actualizar, producto no existe ID: {}", id);
                    return ResponseEntity.status(404)
                            .body(new ApiError("No se pudo actualizar, producto no existe", 404));
                });
    }

    //DELETE
    @DeleteMapping("/{id}")
public ResponseEntity<?> eliminar(@PathVariable Long id) {

    logger.info("Eliminando producto ID: {}", id);

    return repo.findById(id)
            .<ResponseEntity<?>>map(p -> {
                repo.delete(p);
                logger.info("Producto eliminado: {}", p.getNombre());
                return ResponseEntity.ok("Producto eliminado correctamente");
            })
            .orElseGet(() -> {
                logger.error("No se pudo eliminar, producto no existe ID: {}", id);
                return ResponseEntity.status(404)
                        .body(new ApiError("No se pudo eliminar, producto no existe", 404));
            });
}

}