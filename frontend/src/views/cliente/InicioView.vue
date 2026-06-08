<template>
    <div>
        <NavbarCliente />

        <!-- CARRUSEL CATEGORÍAS -->
        <section class="carousel-section">
            <div class="carousel-track">
                <div v-for="(cat, i) in categorias" :key="i" class="carousel-card" :style="{ background: cat.color }">
                    <span class="cat-emoji">{{ cat.emoji }}</span>
                    <span class="cat-nombre">{{ cat.nombre }}</span>
                </div>
            </div>
        </section>

        <!-- HERO -->
        <section class="hero container">
            <div class="hero-text">
                <span class="hero-tag">Bienvenido, {{ nombre }} 👋</span>
                <h1>Descubre productos de <span class="accent">PyMEs locales</span></h1>
                <p>Apoya el comercio local y recibe tus productos directo desde el productor.</p>
            </div>
            <div class="hero-search">
                <input v-model="busqueda" type="text" placeholder="Buscar productos..." class="search-input" />
            </div>
        </section>

        <!-- CATÁLOGO -->
        <section class="catalogo container">
            <div class="catalogo-header">
                <h2>Catálogo</h2>
                <span class="text-muted">{{ productosFiltrados.length }} productos</span>
            </div>

            <div v-if="loading" class="loading-grid">
                <div v-for="i in 8" :key="i" class="skeleton-card"></div>
            </div>

            <div v-else class="productos-grid">
                <div v-for="p in productosFiltrados" :key="p.id" class="producto-card card"
                    @click="$router.push('/producto/' + p.id)">
                    <div class="producto-img">
                        <img v-if="p.imagen1" :src="p.imagen1" :alt="p.nombre" @error="onImgError" />
                        <div v-else class="producto-img-placeholder">
                            🛍️
                        </div>
                        <span v-if="p.stock <= 5 && p.stock > 0" class="badge badge-warning stock-badge">
                            Últimas {{ p.stock }} unidades
                        </span>
                        <span v-if="p.stock === 0" class="badge badge-danger stock-badge">
                            Agotado
                        </span>
                    </div>
                    <div class="producto-info">
                        <h3>{{ p.nombre }}</h3>
                        <p class="producto-desc">{{ p.descripcion || 'Sin descripción' }}</p>
                        <div class="producto-footer">
                            <span class="producto-precio">${{ formatPrecio(p.precio) }}</span>
                            <button class="btn btn-primary btn-sm" @click.stop="agregarCarrito(p)"
                                :disabled="p.stock === 0">
                                + Agregar
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <p v-if="!loading && productosFiltrados.length === 0" class="empty-state">
                No se encontraron productos 😔
            </p>
        </section>

        <!-- CARRITO FLOTANTE -->
        <div v-if="carrito.length > 0" class="carrito-fab" @click="mostrarCarrito = true">
            🛒 <span class="carrito-count">{{ totalItems }}</span>
            <span class="carrito-total">${{ formatPrecio(totalPrecio) }}</span>
        </div>

        <!-- MODAL CARRITO -->
        <div v-if="mostrarCarrito" class="modal-overlay" @click.self="mostrarCarrito = false">
            <div class="modal">
                <div class="modal-header">
                    <h3>Tu carrito</h3>
                    <button @click="mostrarCarrito = false" class="btn-close">✕</button>
                </div>
                <div class="modal-body">
                    <div v-for="item in carrito" :key="item.id" class="carrito-item">
                        <div class="carrito-item-info">
                            <span class="carrito-item-nombre">{{ item.nombre }}</span>
                            <span class="carrito-item-precio">${{ formatPrecio(item.precio) }}</span>
                        </div>
                        <div class="carrito-item-actions">
                            <button @click="decrementar(item)" class="qty-btn">−</button>
                            <span>{{ item.qty }}</span>
                            <button @click="incrementar(item)" class="qty-btn">+</button>
                            <button @click="quitarCarrito(item)" class="btn-remove">🗑</button>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="carrito-resumen">
                        <span>Total</span>
                        <span class="total-grande">${{ formatPrecio(totalPrecio) }}</span>
                    </div>
                    <button class="btn btn-primary w-full" @click="confirmarPedido" :disabled="procesando">
                        {{ procesando ? 'Procesando...' : 'Confirmar pedido' }}
                    </button>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
import NavbarCliente from "../../components/NavbarCliente.vue";
import { getProductos, crearPedido } from "../../services/api";
import "@/assets/styles/inicioview.css";

const CATEGORIAS = [
    { nombre: "Electrónica", emoji: "💻", color: "linear-gradient(135deg,#0a1a2e,#00001a)" },
    { nombre: "Ropa", emoji: "👕", color: "linear-gradient(135deg,#2e1a2e,#1a0a1a)" },
    { nombre: "Alimentos", emoji: "🍯", color: "linear-gradient(135deg,#2e2a0a,#1a1500)" },
    { nombre: "Hogar", emoji: "🏠", color: "linear-gradient(135deg,#1a2e1a,#0a1a0a)" },
    { nombre: "Deportes", emoji: "⚽", color: "linear-gradient(135deg,#2e1a0a,#1a0a00)" },
    { nombre: "Belleza", emoji: "💄", color: "linear-gradient(135deg,#2e0a1a,#1a000a)" },
    { nombre: "Juguetes", emoji: "🧸", color: "linear-gradient(135deg,#2e2e0a,#1a1a00)" },
    { nombre: "Mascotas", emoji: "🐾", color: "linear-gradient(135deg,#0a2e2e,#001a1a)" },
];

export default {
    components: { NavbarCliente },

    data() {
        return {
            productos: [],
            busqueda: "",
            loading: true,
            carrito: [],
            mostrarCarrito: false,
            procesando: false,
            nombre: localStorage.getItem("nombre") || "Cliente",
            categorias: [],
        };
    },

    computed: {
        productosFiltrados() {
            if (!this.busqueda) return this.productos;
            const q = this.busqueda.toLowerCase();
            return this.productos.filter(p =>
                p.nombre?.toLowerCase().includes(q) ||
                p.descripcion?.toLowerCase().includes(q)
            );
        },
        totalItems() {
            return this.carrito.reduce((s, i) => s + i.qty, 0);
        },
        totalPrecio() {
            return this.carrito.reduce((s, i) => s + i.precio * i.qty, 0);
        }
    },

    mounted() {
        this.cargarProductos();
        this.categorias = [...CATEGORIAS, ...CATEGORIAS];
    },

    methods: {
        async cargarProductos() {
            this.loading = true;
            try {
                this.productos = await getProductos();
            } catch {
                this.productos = [];
            }
            this.loading = false;
        },

        agregarCarrito(p) {
            const existe = this.carrito.find(i => i.id === p.id);
            if (existe) {
                if (existe.qty < p.stock) existe.qty++;
            } else {
                this.carrito.push({ ...p, qty: 1 });
            }
        },

        decrementar(item) {
            if (item.qty > 1) item.qty--;
            else this.quitarCarrito(item);
        },

        incrementar(item) {
            const original = this.productos.find(p => p.id === item.id);
            if (original && item.qty < original.stock) item.qty++;
        },

        quitarCarrito(item) {
            this.carrito = this.carrito.filter(i => i.id !== item.id);
        },

        async confirmarPedido() {
            this.procesando = true;
            const clienteNombre = localStorage.getItem("nombre") || "Cliente";
            try {
                for (const item of this.carrito) {
                    await crearPedido({
                        cliente: clienteNombre,
                        productoId: item.id,
                        nombreProducto: item.nombre,
                        cantidad: item.qty
                    });
                }
                this.carrito = [];
                this.mostrarCarrito = false;
                this.$router.push("/mis-pedidos");
            } catch {
                alert("Error al crear el pedido");
            }
            this.procesando = false;
        },

        formatPrecio(n) {
            return Number(n).toLocaleString("es-CL");
        },

        onImgError(e) {
            e.target.style.display = "none";
        }
    }
}
</script>