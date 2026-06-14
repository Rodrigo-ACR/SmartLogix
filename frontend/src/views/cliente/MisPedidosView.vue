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
                        <span :class="badgeEstado(estadoFinal(p))" class="badge">
                            {{ estadoFinal(p) }}
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
                        <div class="detalle-item" v-if="envioDeP(p.id)">
                            <span>Transportista</span>
                            <strong>{{ envioDeP(p.id).transportista || '-' }}</strong>
                        </div>
                        <div class="detalle-item" v-if="envioDeP(p.id)">
                            <span>Entrega estimada</span>
                            <strong>{{ formatFecha(envioDeP(p.id).fechaEstimada) }}</strong>
                        </div>
                        <div class="detalle-item" v-if="envioDeP(p.id)">
                            <span>Dirección</span>
                            <strong>{{ envioDeP(p.id).direccion }}</strong>
                        </div>
                    </div>

                    <!-- PROGRESS BAR COMPLETO -->
                    <div class="pedido-progress">
                        <div v-for="(est, i) in todosLosEstados" :key="i" class="progress-step" :class="{
                            active: estadoFinal(p) === est.key,
                            done: esAnterior(estadoFinal(p), est.key),
                            disabled: !tieneEnvio(p) && est.tipo === 'envio',
                            'envio-step': est.tipo === 'envio',
                            'entregado-step': est.key === 'ENTREGADO'
                        }">
                            <div class="step-dot">
                                <span
                                    v-if="est.key === 'ENTREGADO' && (estadoFinal(p) === 'ENTREGADO' || esAnterior(estadoFinal(p), 'ENTREGADO'))"
                                    class="corona-emoji">👑</span>
                            </div>
                            <span>{{ est.label }}</span>
                        </div>
                    </div>

                    <!-- Banner celebración ENTREGADO -->
                    <div v-if="estadoFinal(p) === 'ENTREGADO'" class="entregado-banner">
                        🎉 ¡Tu pedido fue entregado exitosamente! 👑
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import NavbarCliente from "../../components/NavbarCliente.vue";
import { getPedidos, getEnvios } from "../../services/api";
import "@/assets/styles/mispedidosview.css";

const TODOS_ESTADOS = [
    { key: "CREADO", label: "Creado", tipo: "pedido" },
    { key: "VALIDADO", label: "Validado", tipo: "pedido" },
    { key: "APROBADO", label: "Aprobado", tipo: "pedido" },
    { key: "EN_PREPARACION", label: "En preparación", tipo: "pedido" },
    { key: "PENDIENTE", label: "Pendiente envío", tipo: "envio" },
    { key: "ASIGNADO", label: "Asignado", tipo: "envio" },
    { key: "EN_TRANSITO", label: "En tránsito", tipo: "envio" },
    { key: "ENTREGADO", label: "Entregado", tipo: "envio" },
];

const ORDEN = TODOS_ESTADOS.map(e => e.key);

export default {
    components: { NavbarCliente },
    data() {
        return {
            pedidos: [],
            envios: [],
            loading: true,
            todosLosEstados: TODOS_ESTADOS,
            clienteNombre: localStorage.getItem("nombre") || ""
        };
    },
    async mounted() {
        try {
            const [todos, envios] = await Promise.all([getPedidos(), getEnvios()]);
            this.pedidos = todos.filter(p =>
                p.cliente === this.clienteNombre
            ).reverse();
            this.envios = envios;
        } catch {
            this.pedidos = [];
            this.envios = [];
        }
        this.loading = false;
    },
    methods: {
        envioDeP(pedidoId) {
            return this.envios.find(e => e.pedidoId === pedidoId) || null;
        },
        tieneEnvio(p) {
            return !!this.envioDeP(p.id);
        },
        estadoFinal(p) {
            const envio = this.envioDeP(p.id);
            if (envio) return envio.estado;
            return p.estado;
        },
        esAnterior(estadoActual, key) {
            const idxActual = ORDEN.indexOf(estadoActual);
            const idxKey = ORDEN.indexOf(key);
            return idxKey < idxActual;
        },
        badgeEstado(estado) {
            const map = {
                CREADO: "badge-accent",
                VALIDADO: "badge-warning",
                APROBADO: "badge-success",
                EN_PREPARACION: "badge-warning",
                PENDIENTE: "badge-warning",
                ASIGNADO: "badge-accent",
                EN_TRANSITO: "badge-warning",
                ENTREGADO: "badge-success",
                RECHAZADO: "badge-danger",
                INCIDENCIA: "badge-danger"
            };
            return map[estado] || "badge-accent";
        },
        formatFecha(f) {
            if (!f) return "-";
            return new Date(f).toLocaleDateString("es-CL");
        }
    }
}
</script>

<style scoped>
.progress-step.disabled .step-dot {
    background: #1e293b;
    border-color: #334155;
}

.progress-step.disabled span {
    color: #334155;
}

.corona-emoji {
    font-size: 16px;
    line-height: 1;
    position: absolute;
    top: -18px;
    left: 50%;
    transform: translateX(-50%);
    animation: coronaFloat 1.5s ease-in-out infinite alternate;
    filter: drop-shadow(0 0 6px #ffd700);
    z-index: 10;
}

@keyframes coronaFloat {
    from {
        transform: translateX(-50%) translateY(0px);
    }

    to {
        transform: translateX(-50%) translateY(-4px);
    }
}
</style>