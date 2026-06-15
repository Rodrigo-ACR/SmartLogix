<template>
    <div>
        <NavbarAdmin />
        <div class="container page">

            <div class="dash-header">
                <div>
                    <h1>Dashboard</h1>
                    <p class="text-muted">Bienvenido de vuelta, {{ nombre }}</p>
                </div>
            </div>

            <!-- Banner error si algún servicio falló -->
            <div v-if="serviciosCaidos.length > 0" class="error-banner-admin" style="margin-bottom:1.5rem">
                <span>🔴</span>
                <div>
                    <strong>⚠️ Servicios no disponibles: {{ serviciosCaidos.join(', ') }}</strong>
                    <p>Los contadores de esos servicios no están disponibles temporalmente.</p>
                </div>
                <button @click="$router.go(0)" class="btn-retry-admin">🔄 Reintentar</button>
            </div>

            <div class="stats-grid">
                <div class="stat-card card" @click="$router.push('/admin/productos')">
                    <div class="stat-icon">🛍️</div>
                    <div class="stat-info">
                        <span class="stat-label">Productos</span>
                        <span class="stat-value" :class="{ 'stat-error': errores.productos }">
                            {{ errores.productos ? '—' : stats.productos }}
                        </span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
                <div class="stat-card card" @click="$router.push('/admin/pedidos')">
                    <div class="stat-icon">📦</div>
                    <div class="stat-info">
                        <span class="stat-label">Pedidos</span>
                        <span class="stat-value" :class="{ 'stat-error': errores.pedidos }">
                            {{ errores.pedidos ? '—' : stats.pedidos }}
                        </span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
                <div class="stat-card card" @click="$router.push('/admin/envios')">
                    <div class="stat-icon">🚚</div>
                    <div class="stat-info">
                        <span class="stat-label">Envíos</span>
                        <span class="stat-value" :class="{ 'stat-error': errores.envios }">
                            {{ errores.envios ? '—' : stats.envios }}
                        </span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
                <div class="stat-card card" @click="$router.push('/admin/clientes')">
                    <div class="stat-icon">👥</div>
                    <div class="stat-info">
                        <span class="stat-label">Clientes</span>
                        <span class="stat-value" :class="{ 'stat-error': errores.clientes }">
                            {{ errores.clientes ? '—' : stats.clientes }}
                        </span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
            </div>

            <div class="dash-grid">
                <div class="card dash-section">
                    <h3>Últimos pedidos</h3>
                    <div v-if="errores.pedidos" class="empty-mini text-danger">⚠️ Servicio no disponible</div>
                    <div v-else-if="pedidos.length === 0" class="empty-mini">Sin pedidos aún</div>
                    <div v-for="p in pedidos.slice(0, 5)" :key="p.id" class="mini-row">
                        <div>
                            <span class="mini-id">#{{ p.id }}</span>
                            <span class="mini-nombre">{{ p.nombreProducto || 'Producto' }}</span>
                        </div>
                        <span :class="badgeEstado(p.estado)" class="badge">{{ p.estado }}</span>
                    </div>
                </div>
                <div class="card dash-section">
                    <h3>Últimos envíos</h3>
                    <div v-if="errores.envios" class="empty-mini text-danger">⚠️ Servicio no disponible</div>
                    <div v-else-if="envios.length === 0" class="empty-mini">Sin envíos aún</div>
                    <div v-for="e in envios.slice(0, 5)" :key="e.id" class="mini-row">
                        <div>
                            <span class="mini-id">#{{ e.id }}</span>
                            <span class="mini-nombre">{{ e.direccion }}</span>
                        </div>
                        <span :class="badgeEnvio(e.estado)" class="badge">{{ e.estado }}</span>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<script>
import NavbarAdmin from "../../components/NavbarAdmin.vue";
import { getProductos, getPedidos, getEnvios, getUsuarios } from "../../services/api";
import "@/assets/styles/admindashboardview.css";

export default {
    components: { NavbarAdmin },
    data() {
        return {
            nombre: localStorage.getItem("nombre") || "Admin",
            stats: { productos: 0, pedidos: 0, envios: 0, clientes: 0 },
            errores: { productos: false, pedidos: false, envios: false, clientes: false },
            pedidos: [],
            envios: []
        };
    },
    computed: {
        serviciosCaidos() {
            const nombres = { productos: "Inventario", pedidos: "Pedidos", envios: "Envíos", clientes: "Usuarios" };
            return Object.entries(this.errores)
                .filter(([, v]) => v)
                .map(([k]) => nombres[k]);
        }
    },
    async mounted() {
        // Cada llamada es independiente — si una falla, las demás siguen
        const [productos, pedidos, envios, usuarios] = await Promise.allSettled([
            getProductos(), getPedidos(), getEnvios(), getUsuarios()
        ]);

        if (productos.status === "fulfilled") {
            this.stats.productos = productos.value.length;
        } else { this.errores.productos = true; }

        if (pedidos.status === "fulfilled") {
            this.stats.pedidos = pedidos.value.length;
            this.pedidos = [...pedidos.value].reverse();
        } else { this.errores.pedidos = true; }

        if (envios.status === "fulfilled") {
            this.stats.envios = envios.value.length;
            this.envios = [...envios.value].reverse();
        } else { this.errores.envios = true; }

        if (usuarios.status === "fulfilled") {
            this.stats.clientes = usuarios.value.filter(u => u.rol === "CLIENTE").length;
        } else { this.errores.clientes = true; }
    },
    methods: {
        badgeEstado(e) {
            const m = { CREADO: "badge-accent", VALIDADO: "badge-warning", APROBADO: "badge-success", RECHAZADO: "badge-danger" };
            return m[e] || "badge-accent";
        },
        badgeEnvio(e) {
            const m = { PENDIENTE: "badge-warning", ASIGNADO: "badge-accent", EN_TRANSITO: "badge-warning", ENTREGADO: "badge-success" };
            return m[e] || "badge-accent";
        }
    }
}
</script>

<style scoped>
.stat-error {
    color: var(--text-muted);
    font-size: 1.2rem;
}

.text-danger {
    color: var(--danger);
}
</style>