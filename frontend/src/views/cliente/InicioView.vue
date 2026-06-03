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

<style scoped>
/* CARRUSEL CATEGORÍAS */
.carousel-section {
    overflow: hidden;
    padding: 20px 0;
    border-bottom: 1px solid var(--border);
    mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
    -webkit-mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
}

.carousel-track {
    display: flex;
    gap: 12px;
    width: max-content;
    animation: scroll 25s linear infinite;
}

.carousel-track:hover {
    animation-play-state: paused;
}

@keyframes scroll {
    from {
        transform: translateX(0);
    }

    to {
        transform: translateX(-50%);
    }
}

.carousel-card {
    min-width: 140px;
    padding: 16px 20px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--border);
    cursor: pointer;
    transition: var(--transition);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    user-select: none;
}

.carousel-card:hover {
    border-color: var(--accent);
    transform: translateY(-3px);
}

.cat-emoji {
    font-size: 1.8rem;
}

.cat-nombre {
    font-size: 0.85rem;
    font-weight: 600;
    color: var(--text-secondary);
}

/* HERO */
.hero {
    padding: 60px 24px 40px;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.hero-tag {
    display: inline-block;
    background: var(--accent-glow);
    color: var(--accent);
    padding: 6px 14px;
    border-radius: 999px;
    font-size: 0.85rem;
    font-weight: 600;
    margin-bottom: 12px;
}

.hero-text h1 {
    font-size: 2.5rem;
    line-height: 1.2;
    margin-bottom: 12px;
}

.accent {
    color: var(--accent);
}

.hero-text p {
    color: var(--text-secondary);
    font-size: 1.1rem;
}

.search-input {
    width: 100%;
    max-width: 500px;
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: 999px;
    padding: 14px 24px;
    color: var(--text-primary);
    font-size: 1rem;
    transition: var(--transition);
}

.search-input:focus {
    border-color: var(--accent);
    box-shadow: 0 0 0 3px var(--accent-glow);
    outline: none;
}

/* CATÁLOGO */
.catalogo {
    padding-bottom: 80px;
}

.catalogo-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
}

.text-muted {
    color: var(--text-muted);
    font-size: 0.9rem;
}

.productos-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
}

.producto-card {
    cursor: pointer;
    overflow: hidden;
}

.producto-img {
    position: relative;
    height: 200px;
    background: var(--bg-hover);
    overflow: hidden;
}

.producto-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.producto-card:hover .producto-img img {
    transform: scale(1.05);
}

.producto-img-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 3rem;
    background: var(--bg-hover);
}

.stock-badge {
    position: absolute;
    top: 10px;
    left: 10px;
}

.producto-info {
    padding: 16px;
}

.producto-info h3 {
    font-size: 1rem;
    margin-bottom: 6px;
}

.producto-desc {
    color: var(--text-secondary);
    font-size: 0.85rem;
    margin-bottom: 12px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.producto-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.producto-precio {
    font-size: 1.1rem;
    font-weight: 700;
    color: var(--accent);
}

.btn-sm {
    padding: 7px 14px;
    font-size: 0.85rem;
}

/* SKELETON */
.loading-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
}

.skeleton-card {
    height: 300px;
    background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-hover) 50%, var(--bg-card) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
    border-radius: var(--radius-lg);
}

@keyframes shimmer {
    0% {
        background-position: -200% 0;
    }

    100% {
        background-position: 200% 0;
    }
}

/* CARRITO FAB */
.carrito-fab {
    position: fixed;
    bottom: 32px;
    right: 32px;
    background: var(--accent);
    color: white;
    padding: 14px 20px;
    border-radius: 999px;
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    box-shadow: var(--shadow-accent);
    font-weight: 600;
    z-index: 100;
    transition: var(--transition);
}

.carrito-fab:hover {
    transform: translateY(-2px);
    background: var(--accent-hover);
}

.carrito-count {
    background: white;
    color: var(--accent);
    border-radius: 999px;
    padding: 2px 8px;
    font-size: 0.8rem;
}

.carrito-total {
    font-size: 0.9rem;
}

/* MODAL */
.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.7);
    display: flex;
    align-items: flex-end;
    justify-content: flex-end;
    z-index: 200;
    padding: 24px;
}

.modal {
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: var(--radius-lg);
    width: 100%;
    max-width: 420px;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
}

.modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
    border-bottom: 1px solid var(--border);
}

.btn-close {
    background: none;
    color: var(--text-secondary);
    font-size: 1.2rem;
    padding: 4px 8px;
}

.modal-body {
    flex: 1;
    overflow-y: auto;
    padding: 16px 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.carrito-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px;
    background: var(--bg-secondary);
    border-radius: var(--radius);
}

.carrito-item-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.carrito-item-nombre {
    font-size: 0.9rem;
    font-weight: 500;
}

.carrito-item-precio {
    font-size: 0.85rem;
    color: var(--accent);
}

.carrito-item-actions {
    display: flex;
    align-items: center;
    gap: 8px;
}

.qty-btn {
    background: var(--bg-hover);
    color: var(--text-primary);
    border: 1px solid var(--border);
    border-radius: 6px;
    width: 28px;
    height: 28px;
    font-size: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-remove {
    background: none;
    font-size: 1rem;
}

.modal-footer {
    padding: 20px;
    border-top: 1px solid var(--border);
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.carrito-resumen {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: var(--text-secondary);
}

.total-grande {
    font-size: 1.4rem;
    font-weight: 800;
    color: var(--text-primary);
}

.empty-state {
    text-align: center;
    color: var(--text-muted);
    padding: 60px 0;
    font-size: 1.1rem;
}

.w-full {
    width: 100%;
}
</style>