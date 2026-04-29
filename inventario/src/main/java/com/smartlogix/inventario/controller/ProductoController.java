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
@CrossOrigin("*")
public class ProductoController {

    private final ProductoRepository repo;

    public ProductoController(ProductoRepository repo) {
        this.repo = repo;
    }

    // 🔹 LISTAR
    @GetMapping
    public List<Producto> listar() {
        return repo.findAll();
    }

    // 🔹 CREAR
    @PostMapping
    public Producto guardar(@RequestBody Producto producto) {
        return repo.save(producto);
    }

    // 🔹 EDITAR
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto p) {

        return repo.findById(id)
        .map(prod -> {
            prod.setNombre(p.getNombre());
            prod.setPrecio(p.getPrecio());
            prod.setImagenUrl(p.getImagenUrl());
            repo.save(prod);
            // Agregamos un (Object) para que coincida con el tipo de abajo
            return ResponseEntity.ok((Object) prod); 
        })
        .orElse(ResponseEntity.status(404).body("No existe"));
    }

    // 🔹 ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("No existe");
        }

        repo.deleteById(id);
        return ResponseEntity.ok("Eliminado");
    }
}