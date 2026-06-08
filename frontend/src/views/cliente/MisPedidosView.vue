<template>
    <div>
        <NavbarCliente />

        <div class="container page">
            <h1 class="page-title">Mis Pedidos</h1>

            <div v-if="loading" class="loading-list">
                <div v-for="i in 4" :key="i" class="skeleton-row"></div>
            </div>

            <div v-else-if="pedidos.length === 0" class="empty-state">
                <span>📦</span>
                <h3>Aún no tienes pedidos</h3>
                <p>¡Ve al catálogo y haz tu primera compra!</p>
                <button class="btn btn-primary" @click="$router.push('/inicio')">
                    Ver productos
                </button>
            </div>

            <div v-else class="pedidos-lista">
                <div v-for="p in pedidos" :key="p.id" class="pedido-card card">
                    <div class="pedido-header">
                        <div>
                            <span class="pedido-id">#{{ p.id }}</span>
                            <h3>{{ p.nombreProducto || 'Producto' }}</h3>
                        </div>
                        <span :class="badgeEstado(p.estado)" class="badge">
                            {{ p.estado }}
                        </span>
                    </div>
                    <div class="pedido-detalles">
                        <div class="detalle-item">
                            <span>Cantidad</span>
                            <strong>{{ p.cantidad }}</strong>
                        </div>
                        <div class="detalle-item">
                            <span>Fecha</span>
                            <strong>{{ formatFecha(p.fecha) }}</strong>
                        </div>
                        <div class="detalle-item">
                            <span>Estado</span>
                            <strong>{{ p.estado }}</strong>
                        </div>
                    </div>
                    <div class="pedido-progress">
                        <div v-for="(est, i) in estados" :key="i" class="progress-step"
                            :class="{ active: estaActivo(p.estado, est), done: estaDone(p.estado, est) }">
                            <div class="step-dot"></div>
                            <span>{{ est }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import NavbarCliente from "../../components/NavbarCliente.vue";
import { getPedidos } from "../../services/api";
import "@/assets/styles/mispedidosview.css";
const ESTADOS = ["CREADO", "VALIDADO", "APROBADO", "EN_PREPARACION"];

export default {
    components: { NavbarCliente },
    data() {
        return {
            pedidos: [],
            loading: true,
            estados: ESTADOS,
            clienteNombre: localStorage.getItem("nombre") || ""
        };
    },
    async mounted() {
        try {
            const todos = await getPedidos();
            this.pedidos = todos.filter(p =>
                p.cliente === this.clienteNombre
            ).reverse();
        } catch {
            this.pedidos = [];
        }
        this.loading = false;
    },
    methods: {
        badgeEstado(estado) {
            const map = {
                CREADO: "badge-accent",
                VALIDADO: "badge-warning",
                APROBADO: "badge-success",
                RECHAZADO: "badge-danger",
                EN_PREPARACION: "badge-warning"
            };
            return map[estado] || "badge-accent";
        },
        formatFecha(f) {
            if (!f) return "-";
            return new Date(f).toLocaleDateString("es-CL");
        },
        estaActivo(estadoActual, est) {
            return estadoActual === est;
        },
        estaDone(estadoActual, est) {
            const idx = ESTADOS.indexOf(est);
            const idxActual = ESTADOS.indexOf(estadoActual);
            return idxActual > idx;
        }
    }
}
</script>