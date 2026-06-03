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

.user-cell {
    display: flex;
    align-items: center;
    gap: 10px;
}

.user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: var(--accent);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.75rem;
    font-weight: 700;
    color: white;
    flex-shrink: 0;
}
</style>