<template>
    <div>
        <NavbarCliente />

        <div class="container page" v-if="producto">

            <button class="btn-back" @click="$router.back()">← Volver</button>

            <div class="producto-detalle">

                <!-- GALERÍA -->
                <div class="galeria">
                    <div class="img-principal">
                        <img v-if="imagenActiva" :src="imagenActiva" :alt="producto.nombre" />
                        <div v-else class="img-placeholder">🛍️</div>
                    </div>
                    <div class="img-thumbs" v-if="imagenes.length > 1">
                        <div v-for="(img, i) in imagenes" :key="i" class="thumb"
                            :class="{ active: imagenActiva === img }" @click="imagenActiva = img">
                            <img :src="img" />
                        </div>
                    </div>
                </div>

                <!-- INFO -->
                <div class="producto-info">
                    <span class="badge badge-accent">{{ producto.stock > 0 ? 'En stock' : 'Agotado' }}</span>
                    <h1>{{ producto.nombre }}</h1>
                    <p class="descripcion">{{ producto.descripcion || 'Sin descripción disponible.' }}</p>

                    <div class="precio-section">
                        <span class="precio">${{ formatPrecio(producto.precio) }}</span>
                        <span class="stock-info">{{ producto.stock }} disponibles</span>
                    </div>

                    <div class="cantidad-section">
                        <label>Cantidad</label>
                        <div class="cantidad-control">
                            <button @click="cantidad > 1 ? cantidad-- : null" class="qty-btn">−</button>
                            <span>{{ cantidad }}</span>
                            <button @click="cantidad < producto.stock ? cantidad++ : null" class="qty-btn">+</button>
                        </div>
                    </div>

                    <div class="acciones">
                        <button class="btn btn-primary" :disabled="producto.stock === 0 || comprando"
                            @click="comprarAhora">
                            {{ comprando ? 'Procesando...' : '⚡ Comprar ahora' }}
                        </button>
                    </div>

                    <div v-if="mensaje" class="mensaje-exito">
                        ✅ {{ mensaje }}
                    </div>
                </div>
            </div>
        </div>

        <div v-else-if="loading" class="container page">
            <div class="skeleton-detalle"></div>
        </div>
    </div>
</template>

<script>
import NavbarCliente from "../../components/NavbarCliente.vue";
import { getProductos, crearPedido } from "../../services/api";

export default {
    components: { NavbarCliente },
    data() {
        return {
            producto: null,
            imagenActiva: null,
            cantidad: 1,
            loading: true,
            comprando: false,
            mensaje: ""
        };
    },
    computed: {
        imagenes() {
            if (!this.producto) return [];
            return [this.producto.imagen1, this.producto.imagen2, this.producto.imagen3]
                .filter(Boolean);
        }
    },
    async mounted() {
        const id = this.$route.params.id;
        try {
            const productos = await getProductos();
            this.producto = productos.find(p => p.id == id);
            if (this.imagenes.length) this.imagenActiva = this.imagenes[0];
        } catch {
            this.producto = null;
        }
        this.loading = false;
    },
    methods: {
        async comprarAhora() {
            this.comprando = true;
            try {
                await crearPedido({
                    cliente: localStorage.getItem("nombre") || "Cliente",
                    productoId: this.producto.id,
                    nombreProducto: this.producto.nombre,
                    cantidad: this.cantidad
                });
                this.mensaje = "Pedido creado exitosamente";
                setTimeout(() => this.$router.push("/mis-pedidos"), 1500);
            } catch {
                this.mensaje = "Error al crear el pedido";
            }
            this.comprando = false;
        },
        formatPrecio(n) {
            return Number(n).toLocaleString("es-CL");
        }
    }
}
</script>

<style scoped>
.btn-back {
    background: none;
    color: var(--text-secondary);
    font-size: 0.9rem;
    margin-bottom: 32px;
    padding: 0;
    transition: var(--transition);
}

.btn-back:hover {
    color: var(--accent);
}

.producto-detalle {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 48px;
    align-items: start;
}

.galeria {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.img-principal {
    width: 100%;
    aspect-ratio: 1;
    border-radius: var(--radius-lg);
    overflow: hidden;
    background: var(--bg-card);
    border: 1px solid var(--border);
}

.img-principal img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.img-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 5rem;
}

.img-thumbs {
    display: flex;
    gap: 10px;
}

.thumb {
    width: 70px;
    height: 70px;
    border-radius: var(--radius);
    overflow: hidden;
    border: 2px solid var(--border);
    cursor: pointer;
    transition: var(--transition);
}

.thumb.active {
    border-color: var(--accent);
}

.thumb img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.producto-info {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.producto-info h1 {
    font-size: 2rem;
}

.descripcion {
    color: var(--text-secondary);
    line-height: 1.7;
}

.precio-section {
    display: flex;
    align-items: baseline;
    gap: 16px;
}

.precio {
    font-size: 2.5rem;
    font-weight: 800;
    color: var(--accent);
}

.stock-info {
    color: var(--text-muted);
    font-size: 0.9rem;
}

.cantidad-section {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.cantidad-section label {
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.cantidad-control {
    display: flex;
    align-items: center;
    gap: 16px;
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: 8px 16px;
    width: fit-content;
    font-size: 1.1rem;
    font-weight: 600;
}

.qty-btn {
    background: var(--bg-hover);
    color: var(--text-primary);
    border: 1px solid var(--border);
    border-radius: 6px;
    width: 32px;
    height: 32px;
    font-size: 1.1rem;
}

.acciones {
    display: flex;
    gap: 12px;
}

.acciones .btn {
    flex: 1;
    padding: 14px;
    font-size: 1rem;
}

.mensaje-exito {
    background: rgba(34, 197, 94, 0.1);
    border: 1px solid var(--success);
    color: var(--success);
    padding: 12px 16px;
    border-radius: var(--radius);
    font-size: 0.9rem;
}

.skeleton-detalle {
    height: 500px;
    background: var(--bg-card);
    border-radius: var(--radius-lg);
    animation: shimmer 1.5s infinite;
}

@media (max-width: 768px) {
    .producto-detalle {
        grid-template-columns: 1fr;
    }
}
</style>