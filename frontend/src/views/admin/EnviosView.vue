<template>
    <div>
        <NavbarAdmin />
        <div class="container page">
            <h1 class="page-title">Envíos</h1>

            <div class="tabla-card card">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Pedido</th>
                            <th>Dirección</th>
                            <th>Transportista</th>
                            <th>Fecha estimada</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="loading">
                            <td colspan="7" class="text-center">Cargando...</td>
                        </tr>
                        <tr v-for="e in envios" :key="e.id">
                            <td class="text-muted">#{{ e.id }}</td>
                            <td>#{{ e.pedidoId }}</td>
                            <td>{{ e.direccion }}</td>
                            <td>{{ e.transportista || '-' }}</td>
                            <td>{{ formatFecha(e.fechaEstimada) }}</td>
                            <td><span :class="badgeEnvio(e.estado)" class="badge">{{ e.estado }}</span></td>
                            <td>
                                <select class="estado-select" :value="e.estado"
                                    @change="cambiarEstado(e.id, $event.target.value)">
                                    <option value="PENDIENTE">PENDIENTE</option>
                                    <option value="ASIGNADO">ASIGNADO</option>
                                    <option value="EN_TRANSITO">EN_TRANSITO</option>
                                    <option value="ENTREGADO">ENTREGADO</option>
                                    <option value="INCIDENCIA">INCIDENCIA</option>
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
import { getEnvios, cambiarEstadoEnvio } from "../../services/api";
import "@/assets/styles/enviosview.css";
export default {
    components: { NavbarAdmin },
    data() {
        return { envios: [], loading: true };
    },
    async mounted() {
        try { this.envios = await getEnvios(); } catch { }
        this.loading = false;
    },
    methods: {
        async cambiarEstado(id, estado) {
            try {
                await cambiarEstadoEnvio(id, estado);
                const e = this.envios.find(e => e.id === id);
                if (e) e.estado = estado;
            } catch { alert("Error al cambiar estado"); }
        },
        badgeEnvio(e) {
            const m = { PENDIENTE: "badge-warning", ASIGNADO: "badge-accent", EN_TRANSITO: "badge-warning", ENTREGADO: "badge-success", INCIDENCIA: "badge-danger" };
            return m[e] || "badge-accent";
        },
        formatFecha(f) {
            if (!f) return "-";
            return new Date(f).toLocaleDateString("es-CL");
        }
    }
}
</script>