<template>
    <div>
        <NavbarAdmin />
        <div class="container page">
            <h1 class="page-title">Pedidos</h1>

            <div v-if="error" class="error-banner-admin">
                <span>🔴</span>
                <div>
                    <strong>{{ error }}</strong>
                    <p>El sistema se recuperará automáticamente cuando el servicio vuelva a estar disponible.</p>
                </div>
                <button @click="$router.go(0)" class="btn-retry-admin">🔄 Reintentar</button>
            </div>

            <!-- Barra búsqueda y filtro -->
            <div class="filtros-bar">
                <div class="filtro-search">
                    <span class="search-icon">🔍</span>
                    <input v-model="busqueda" type="text" placeholder="Buscar por cliente, producto o ID..."
                        class="search-input" />
                    <button v-if="busqueda" @click="busqueda = ''" class="search-clear">✕</button>
                </div>
                <select v-model="filtroEstado" class="filtro-select">
                    <option value="">Todos los estados</option>
                    <option value="CREADO">CREADO</option>
                    <option value="VALIDADO">VALIDADO</option>
                    <option value="APROBADO">APROBADO</option>
                    <option value="EN_PREPARACION">EN_PREPARACION</option>
                    <option value="RECHAZADO">RECHAZADO</option>
                </select>
                <span class="filtro-count">{{ pedidosFiltrados.length }} de {{ pedidos.length }}</span>
            </div>

            <div class="tabla-card card">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="loading">
                            <td colspan="7" class="text-center">Cargando...</td>
                        </tr>
                        <tr v-for="p in pedidosFiltrados" :key="p.id">
                            <td class="text-muted">#{{ p.id }}</td>
                            <td>{{ p.cliente }}</td>
                            <td>{{ p.nombreProducto || '-' }}</td>
                            <td>{{ p.cantidad }}</td>
                            <td>{{ formatFecha(p.fecha) }}</td>
                            <td><span :class="badgeEstado(p.estado)" class="badge">{{ p.estado }}</span></td>
                            <td>
                                <select class="estado-select" :value="p.estado"
                                    @change="cambiarEstado(p.id, $event.target.value)">
                                    <option value="CREADO">CREADO</option>
                                    <option value="VALIDADO">VALIDADO</option>
                                    <option value="APROBADO">APROBADO</option>
                                    <option value="RECHAZADO">RECHAZADO</option>
                                    <option value="EN_PREPARACION">EN_PREPARACION</option>
                                </select>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import NavbarAdmin from "../../components/NavbarAdmin.vue";
import { getPedidos, cambiarEstadoPedido } from "../../services/api";
import "@/assets/styles/pedidosview.css";
export default {
    components: { NavbarAdmin },
    data() {
        return { pedidos: [], loading: true, error: "", busqueda: "", filtroEstado: "" };
    },
    computed: {
        pedidosFiltrados() {
            return this.pedidos.filter(p => {
                const texto = this.busqueda.toLowerCase();
                const coincide = !texto ||
                    (p.cliente || "").toLowerCase().includes(texto) ||
                    (p.nombreProducto || "").toLowerCase().includes(texto) ||
                    String(p.id).includes(texto);
                const estado = !this.filtroEstado || p.estado === this.filtroEstado;
                return coincide && estado;
            });
        }
    },
    async mounted() {
        try { this.pedidos = await getPedidos(); } catch { this.error = "⚠️ No se pudieron cargar los pedidos. Servicio temporalmente no disponible."; }
        this.loading = false;
    },
    methods: {
        async cambiarEstado(id, estado) {
            try {
                await cambiarEstadoPedido(id, estado);
                const p = this.pedidos.find(p => p.id === id);
                if (p) p.estado = estado;
            } catch { window.$toast.mostrar("Error al cambiar estado del pedido", "error"); }
        },
        badgeEstado(e) {
            const m = { CREADO: "badge-accent", VALIDADO: "badge-warning", APROBADO: "badge-success", RECHAZADO: "badge-danger", EN_PREPARACION: "badge-warning" };
            return m[e] || "badge-accent";
        },
        formatFecha(f) {
            if (!f) return "-";
            return new Date(f).toLocaleDateString("es-CL");
        }
    }
}
</script>