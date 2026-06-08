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
import "@/assets/styles/productoview.css";

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
