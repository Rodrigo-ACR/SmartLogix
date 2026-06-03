package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.model.EstadoPedido;
import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository repo;

    public PedidoService(PedidoRepository repo) {
        this.repo = repo;
    }

    public List<Pedido> listarTodos() {
        return repo.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Pedido crear(Pedido p) {

        if (p.getCliente() == null || p.getCliente().isBlank()) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        if (p.getProductoId() == null) {
            throw new RuntimeException("El producto es obligatorio");
        }

        if (p.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        p.setFecha(LocalDate.now());
        p.setEstado(EstadoPedido.CREADO);

        return repo.save(p);
    }

    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {

        Pedido pedido = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        return repo.save(pedido);
    }

    public Pedido actualizar(Long id, Pedido nuevo) {

        Pedido existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (nuevo.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        existente.setCliente(nuevo.getCliente());
        existente.setProductoId(nuevo.getProductoId());
        existente.setNombreProducto(nuevo.getNombreProducto());
        existente.setCantidad(nuevo.getCantidad());
        existente.setFecha(nuevo.getFecha());

        return repo.save(existente);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado");
        }
        repo.deleteById(id);
    }
}