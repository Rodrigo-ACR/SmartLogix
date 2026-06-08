<template>
    <div>
        <NavbarAdmin />
        <div class="container page">
            <h1 class="page-title">Clientes</h1>

            <div class="tabla-card card">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Dirección</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="loading">
                            <td colspan="6" class="text-center">Cargando...</td>
                        </tr>
                        <tr v-for="u in clientes" :key="u.id">
                            <td class="text-muted">#{{ u.id }}</td>
                            <td>
                                <div class="user-cell">
                                    <div class="user-avatar">{{ iniciales(u.nombre) }}</div>
                                    <span>{{ u.nombre }}</span>
                                </div>
                            </td>
                            <td>{{ u.correo }}</td>
                            <td>{{ u.telefono || '-' }}</td>
                            <td>{{ u.direccion || '-' }}</td>
                            <td>
                                <span :class="u.activo ? 'badge-success' : 'badge-danger'" class="badge">
                                    {{ u.activo ? 'Activo' : 'Inactivo' }}
                                </span>
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
import { getUsuarios } from "../../services/api";
import "@/assets/styles/clientesview.css";

export default {
    components: { NavbarAdmin },
    data() {
        return { clientes: [], loading: true };
    },
    async mounted() {
        try {
            const todos = await getUsuarios();
            this.clientes = todos.filter(u => u.rol === "CLIENTE");
        } catch { }
        this.loading = false;
    },
    methods: {
        iniciales(nombre) {
            if (!nombre) return "?";
            return nombre.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2);
        }
    }
}
</script>
