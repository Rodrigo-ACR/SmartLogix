<template>
    <div>
        <NavbarAdmin />
        <div class="container page">
            <h1 class="page-title">Pedidos</h1>

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
                        <tr v-for="p in pedidos" :key="p.id">
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

export default {
    components: { NavbarAdmin },
    data() {
        return { pedidos: [], loading: true };
    },
    async mounted() {
        try { this.pedidos = await getPedidos(); } catch { }
        this.loading = false;
    },
    methods: {
        async cambiarEstado(id, estado) {
            try {
                await cambiarEstadoPedido(id, estado);
                const p = this.pedidos.find(p => p.id === id);
                if (p) p.estado = estado;
            } catch { alert("Error al cambiar estado"); }
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

<style scoped>
.page-title {
    font-size: 2rem;
    margin-bottom: 24px;
}

.tabla-card {
    overflow: hidden;
}

.tabla {
    width: 100%;
    border-collapse: collapse;
}

.tabla th {
    text-align: left;
    padding: 14px 20px;
    color: var(--text-muted);
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border-bottom: 1px solid var(--border);
}

.tabla td {
    padding: 14px 20px;
    border-bottom: 1px solid var(--border);
    font-size: 0.95rem;
}

.tabla tr:last-child td {
    border-bottom: none;
}

.tabla tr:hover td {
    background: var(--bg-hover);
}

.text-muted {
    color: var(--text-muted);
}

.text-center {
    text-align: center;
}

.estado-select {
    background: var(--bg-secondary);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: 6px 10px;
    color: var(--text-primary);
    font-size: 0.85rem;
    cursor: pointer;
}
</style>