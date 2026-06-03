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

<style scoped>
.page-title {
    font-size: 2rem;
    margin-bottom: 32px;
}

.pedidos-lista {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.pedido-card {
    padding: 24px;
}

.pedido-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    margin-bottom: 20px;
}

.pedido-id {
    display: block;
    color: var(--text-muted);
    font-size: 0.8rem;
    margin-bottom: 4px;
}

.pedido-header h3 {
    font-size: 1.1rem;
}

.pedido-detalles {
    display: flex;
    gap: 32px;
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid var(--border);
}

.detalle-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.detalle-item span {
    color: var(--text-muted);
    font-size: 0.8rem;
}

.detalle-item strong {
    font-size: 0.95rem;
}

.pedido-progress {
    display: flex;
    align-items: flex-start;
    gap: 0;
}

.progress-step {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    position: relative;
}

.progress-step:not(:last-child)::after {
    content: '';
    position: absolute;
    top: 8px;
    left: 50%;
    width: 100%;
    height: 2px;
    background: var(--border);
    z-index: 0;
}

.progress-step.done:not(:last-child)::after {
    background: var(--accent);
}

.step-dot {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: var(--border);
    border: 2px solid var(--border);
    z-index: 1;
    transition: var(--transition);
}

.progress-step.active .step-dot {
    background: var(--accent);
    border-color: var(--accent);
    box-shadow: 0 0 0 4px var(--accent-glow);
}

.progress-step.done .step-dot {
    background: var(--accent);
    border-color: var(--accent);
}

.progress-step span {
    font-size: 0.7rem;
    color: var(--text-muted);
    text-align: center;
}

.progress-step.active span {
    color: var(--accent);
    font-weight: 600;
}

.progress-step.done span {
    color: var(--text-secondary);
}

.empty-state {
    text-align: center;
    padding: 80px 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
}

.empty-state span {
    font-size: 4rem;
}

.empty-state h3 {
    font-size: 1.3rem;
}

.empty-state p {
    color: var(--text-secondary);
    margin-bottom: 8px;
}

.loading-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.skeleton-row {
    height: 140px;
    background: var(--bg-card);
    border-radius: var(--radius-lg);
    animation: shimmer 1.5s infinite;
    background: linear-gradient(90deg, var(--bg-card) 25%, var(--bg-hover) 50%, var(--bg-card) 75%);
    background-size: 200% 100%;
}
</style>