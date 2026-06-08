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

            <div class="stats-grid">
                <div class="stat-card card" @click="$router.push('/admin/productos')">
                    <div class="stat-icon">🛍️</div>
                    <div class="stat-info">
                        <span class="stat-label">Productos</span>
                        <span class="stat-value">{{ stats.productos }}</span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
                <div class="stat-card card" @click="$router.push('/admin/pedidos')">
                    <div class="stat-icon">📦</div>
                    <div class="stat-info">
                        <span class="stat-label">Pedidos</span>
                        <span class="stat-value">{{ stats.pedidos }}</span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
                <div class="stat-card card" @click="$router.push('/admin/envios')">
                    <div class="stat-icon">🚚</div>
                    <div class="stat-info">
                        <span class="stat-label">Envíos</span>
                        <span class="stat-value">{{ stats.envios }}</span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
                <div class="stat-card card" @click="$router.push('/admin/clientes')">
                    <div class="stat-icon">👥</div>
                    <div class="stat-info">
                        <span class="stat-label">Clientes</span>
                        <span class="stat-value">{{ stats.clientes }}</span>
                    </div>
                    <span class="stat-arrow">→</span>
                </div>
            </div>

            <div class="dash-grid">
                <div class="card dash-section">
                    <h3>Últimos pedidos</h3>
                    <div v-if="pedidos.length === 0" class="empty-mini">Sin pedidos aún</div>
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
                    <div v-if="envios.length === 0" class="empty-mini">Sin envíos aún</div>
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
import "@/assets/styles/AdminDashboard.css";

export default {
    components: { NavbarAdmin },
    data() {
        return {
            nombre: localStorage.getItem("nombre") || "Admin",
            stats: { productos: 0, pedidos: 0, envios: 0, clientes: 0 },
            pedidos: [],
            envios: []
        };
    },
    async mounted() {
        try {
            const [productos, pedidos, envios, usuarios] = await Promise.all([
                getProductos(), getPedidos(), getEnvios(), getUsuarios()
            ]);
            this.stats.productos = productos.length;
            this.stats.pedidos = pedidos.length;
            this.stats.envios = envios.length;
            this.stats.clientes = usuarios.filter(u => u.rol === "CLIENTE").length;
            this.pedidos = pedidos.reverse();
            this.envios = envios.reverse();
        } catch { }
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
