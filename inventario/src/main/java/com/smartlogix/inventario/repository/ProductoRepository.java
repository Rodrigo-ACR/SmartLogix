package com.smartlogix.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartlogix.inventario.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}